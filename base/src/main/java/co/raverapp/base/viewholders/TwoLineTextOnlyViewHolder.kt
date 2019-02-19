package co.raverapp.base.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.raverapp.base.R

class TwoLineTextOnlyViewHolder(
    context: Context,

    /**
     * Parent view to inflate this view holder against.
     */
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(context).inflate(R.layout.base_item_two_line_text_only, parent, false)
) {

    val title: TextView = itemView.findViewById(R.id.base_item_two_line_text_only_title)
    val subtitle: TextView = itemView.findViewById(R.id.base_item_two_line_text_only_subtitle)

}
