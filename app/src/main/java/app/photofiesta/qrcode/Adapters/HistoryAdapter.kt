package app.photofiesta.qrcode.Adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.photofiesta.qrcode.Models.ScanItem
import app.photofiesta.qrcode.R
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter (
    private val onFavoriteClick: (ScanItem) -> Unit,
    private val onShareClick : (ScanItem) -> Unit,
    private val onDeleteClick : (ScanItem) -> Unit
        )
    : ListAdapter<ScanItem, HistoryAdapter.ScanItemViewHolder>(ScanItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ScanItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScanItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ScanItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        private val qrTypeImageView: ImageView = itemView.findViewById(R.id.qrTypeImageView)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val optionsMenuButton: ImageButton = itemView.findViewById(R.id.optionsMenuButton)

        fun bind(item: ScanItem) {
            contentTextView.text = item.content

            timestampTextView.text = formatTimestamp(item.timestamp)
            if(item.qrType == 1)
            {
                qrTypeImageView.setImageResource(R.drawable.baseline_qr_code_scanner_24)
            }
            else
            {
                qrTypeImageView.setImageResource(R.drawable.baseline_bar_code_scanner_24)
            }

            val favoriteImageResource = if (item.isFavorite) {
                R.drawable.baseline_favorite_24
            } else {
                R.drawable.baseline_favorite_border_24
            }
            favoriteButton.setImageResource(favoriteImageResource)

            favoriteButton.setOnClickListener {
                onFavoriteClick(item)
            }

            optionsMenuButton.setOnClickListener {
                showPopupMenu(item, optionsMenuButton)
            }

            contentTextView.setOnClickListener{
                copyToClipboard(contentTextView.context, item.content)
            }
        }
    }

    private fun copyToClipboard(context: Context, content: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", content)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showPopupMenu(scanItem: ScanItem, anchorView: View) {
        val popupMenu = PopupMenu(anchorView.context, anchorView)
        popupMenu.menuInflater.inflate(R.menu.item_history_popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_share -> {
                    // Handle the share action
                    onShareClick(scanItem)
                    true
                }
                R.id.menu_delete -> {
                    // Handle the delete action
                    onDeleteClick(scanItem)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(timestamp)
        return formattedDate.toString()
    }


}

class ScanItemDiffCallback : DiffUtil.ItemCallback<ScanItem>() {
    override fun areItemsTheSame(oldItem: ScanItem, newItem: ScanItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ScanItem, newItem: ScanItem): Boolean {
        return oldItem == newItem
    }
}
