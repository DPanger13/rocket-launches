package io.mercury.android.movies.topmovies.topmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.mercury.android.movies.data.movies.MovieModule
import io.mercury.android.movies.data.movies.TopMovie
import io.mercury.android.movies.topmovies.R
import io.mercury.android.movies.topmovies.movie.MovieActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.topmovies_activity_top_movies.*
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

class TopMoviesActivity : AppCompatActivity() {

    @Inject
    internal lateinit var disposables: CompositeDisposable

    @Inject
    internal lateinit var uiHandler: TopMovieUiHandler

    @Inject
    internal lateinit var viewModel: TopMoviesViewModel

    private lateinit var movieAdapter: TopMovieAdapter
    private var movies: List<TopMovie>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createDependencyInjectionScope()

        super.onCreate(savedInstanceState)

        inject(scope)

        if (savedInstanceState != null) initFromSavedState(savedInstanceState)

        setContentView(R.layout.topmovies_activity_top_movies)
        initMovieList(movies)
    }

    private fun createDependencyInjectionScope(): Scope {
        val scope = Toothpick.openScope(INJECT_SCOPE)
        scope.installModules(MovieModule(), object: Module() {
            init {
                bind(CompositeDisposable::class.java).toProviderInstance { CompositeDisposable() }
            }
        })

        return scope
    }

    private fun inject(scope: Scope) {
        Toothpick.inject(this, scope)
    }

    private fun initFromSavedState(savedInstanceState: Bundle) {
        movies = savedInstanceState.getParcelableArrayList(KEY_MOVIES)
    }

    private fun initMovieList(movies: List<TopMovie>?) {
        val viewManager = LinearLayoutManager(this)
        movieAdapter = TopMovieAdapter(movies) { uiHandler.movieClicked(it) }
        topmovies_activity_top_movies_recyclerview.apply {
            layoutManager = viewManager
            adapter = movieAdapter
        }
    }

    override fun onStart() {
        super.onStart()

        observeUiHandler()

        if (movies == null) {
            fetchMovies()
        }
    }

    private fun observeUiHandler() {
        val disposable = uiHandler.actions.subscribe {
            when (it) {
                is ShowMovie -> {
                    val intent = MovieActivity.getStartIntent(this, it.movieId)
                    startActivity(intent)
                }
            }
        }
        disposables.add(disposable)
    }

    private fun fetchMovies() {
        val disposable = viewModel.topMovies
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                movies = it

                movieAdapter.movies = movies
            }, {
                topmovies_activity_top_movies_error.text = "Error"
            })
        disposables.add(disposable)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if (movies != null) {
            outState?.putParcelableArrayList(KEY_MOVIES, ArrayList(movies!!))
        }
    }

    override fun onStop() {
        disposables.clear()

        super.onStop()
    }

    override fun onDestroy() {
        Toothpick.closeScope(INJECT_SCOPE)

        super.onDestroy()
    }

    private companion object {
        private val INJECT_SCOPE = TopMoviesActivity::class.java

        private const val KEY_MOVIES = "key_movies"
    }

}

class TopMovieAdapter(
    movies: List<TopMovie>?,
    private val onMovieClicked: (TopMovie) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val picasso = Picasso.get()
    var movies: List<TopMovie>? = movies
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    class TopMovieViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.topmovies_item_top_movie_rank)
        val title: TextView = view.findViewById(R.id.topmovies_item_top_movie_title)
        val year: TextView = view.findViewById(R.id.topmovies_item_top_movie_year)
        val id: TextView = view.findViewById(R.id.topmovies_item_top_movie_id)
        val rating: TextView = view.findViewById(R.id.topmovies_item_top_movie_rating)
        val votes: TextView = view.findViewById(R.id.topmovies_item_top_movie_votes)
        val poster: ImageView = view.findViewById(R.id.topmovies_item_top_movie_poster)
        val imdbLink: TextView = view.findViewById(R.id.topmovies_item_top_movie_imdb_page)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.topmovies_item_top_movie, parent, false) as ViewGroup

        return TopMovieViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = movies?.get(position) ?: return
        val imdbInfo = movie.extendedImdbInfo

        val movieHolder = holder as TopMovieViewHolder
        movieHolder.rank.text = movie.rank.toString()
        movieHolder.title.text = movie.title
        movieHolder.year.text = movie.year.toString()
        movieHolder.id.text = movie.id
        movieHolder.rating.text = imdbInfo.rating.toString()
        movieHolder.votes.text = imdbInfo.votes.toString()
        movieHolder.imdbLink.text = imdbInfo.imdbPage?.toString()

        val posterUrlString = movie.poster?.toString()
        if (posterUrlString != null) picasso.load(posterUrlString).into(movieHolder.poster)

        movieHolder.itemView.setOnClickListener { onMovieClicked.invoke(movie) }
    }

    override fun getItemCount() = movies?.size ?: 0

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        holder.itemView.setOnClickListener(null)
    }

}
