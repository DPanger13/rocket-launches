package co.raverapp.base.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.raverapp.base.R

class BottomFabSpacerViewHolder(
    context: Context,

    /**
     * Parent view to inflate this view holder against.
     */
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(context).inflate(R.layout.base_item_spacer_fab_bottom, parent, false)
)
