package io.mercury.android.movies.topmovies.movie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.mercury.android.movies.data.movies.DetailedMovieInfo
import io.mercury.android.movies.data.movies.MovieModule
import io.mercury.android.movies.topmovies.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.topmovies_activity_movie.*
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

class MovieActivity : AppCompatActivity() {

    @Inject
    internal lateinit var disposables: CompositeDisposable

    @Inject
    internal lateinit var viewModel: MovieViewModel

    private lateinit var movieId: String
    private var movie: DetailedMovieInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createDependencyInjectionScope()

        super.onCreate(savedInstanceState)

        inject(scope)

        if (savedInstanceState == null) initFromExtras(intent.extras!!)
        else initFromSavedState(savedInstanceState)

        setContentView(R.layout.topmovies_activity_movie)
    }

    private fun createDependencyInjectionScope(): Scope {
        val scope = Toothpick.openScope(INJECT_SCOPE)
        scope.installModules(MovieModule(), object : Module() {
            init {
                bind(CompositeDisposable::class.java).toProviderInstance { CompositeDisposable() }
            }
        })

        return scope
    }

    private fun inject(scope: Scope) {
        Toothpick.inject(this, scope)
    }

    private fun initFromExtras(extras: Bundle) {
        movieId = extras.getString(EXTRA_ID_MOVIE)!!
    }

    private fun initFromSavedState(savedInstanceState: Bundle) {
        movieId = savedInstanceState.getString(KEY_ID_MOVIE)!!

        movie = savedInstanceState.getParcelable(KEY_MOVIE)
    }

    override fun onStart() {
        super.onStart()

        if (movie == null) {
            fetchMovie()
        } else {
            updateUiWithMovieInfo(movie!!)
        }
    }

    private fun fetchMovie() {
        val disposable = viewModel.getMovieInfo(movieId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                movie = it

                updateUiWithMovieInfo(it)
            }, {
                topmovies_activity_movie_error.setText(R.string.error)
            })
        disposables.add(disposable)
    }

    private fun updateUiWithMovieInfo(movie: DetailedMovieInfo) {
        val imdbInfo = movie.imdbInfo
        topmovies_activity_movie_rating.text = imdbInfo.rating.toString()
        topmovies_activity_movie_votes.text = imdbInfo.votes.toString()

        topmovies_activity_movie_id.text = movie.id
        topmovies_activity_movie_title.text = movie.title
        topmovies_activity_movie_year.text = movie.year.toString()
        topmovies_activity_movie_content_rating.text = movie.contentRating.toString()
        topmovies_activity_movie_release_date.text = movie.releaseDate?.toString()
        topmovies_activity_movie_runtime.text = movie.runtimeInMinutes?.toString()
        topmovies_activity_movie_genres.text = movie.genres.toString()
        topmovies_activity_movie_director.text = movie.director
        topmovies_activity_movie_actors.text = movie.actors.toString()
        topmovies_activity_movie_plot.text = movie.plot
        topmovies_activity_movie_writers.text = movie.writers.toString()
        topmovies_activity_movie_languages.text = movie.languages.toString()
        topmovies_activity_movie_country.text = movie.country
        topmovies_activity_movie_awards.text = movie.awards
        topmovies_activity_movie_poster.text = movie.poster?.toString()
        topmovies_activity_movie_metascore.text = movie.metascore?.toString()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString(KEY_ID_MOVIE, movieId)

        if (movie != null) outState?.putParcelable(KEY_MOVIE, movie)
    }

    override fun onStop() {
        disposables.clear()

        super.onStop()
    }

    override fun onDestroy() {
        Toothpick.closeScope(INJECT_SCOPE)

        super.onDestroy()
    }

    companion object {
        private val INJECT_SCOPE = MovieActivity::class.java

        private const val EXTRA_ID_MOVIE = "extra_id_movie"
        private const val KEY_ID_MOVIE = "key_id_movie"
        private const val KEY_MOVIE = "key_movie"

        fun getStartIntent(context: Context, movieId: String): Intent {
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra(EXTRA_ID_MOVIE, movieId)

            return intent
        }

    }

}
