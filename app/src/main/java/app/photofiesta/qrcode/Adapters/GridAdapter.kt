package app.photofiesta.qrcode.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.photofiesta.qrcode.R

class GridAdapter(
    private val items: List<GridItem>,
    private val onItemClick: (GridItem) -> Unit
) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_layout, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        fun bind(item: GridItem) {
            iconImageView.setImageResource(item.iconRes)
            titleTextView.text = item.title
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}

data class GridItem(val id: Int, val iconRes: Int, val title: String)
