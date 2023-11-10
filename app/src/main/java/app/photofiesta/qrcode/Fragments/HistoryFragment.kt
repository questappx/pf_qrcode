package app.photofiesta.qrcode.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import app.photofiesta.qrcode.Adapters.HistoryAdapter
import app.photofiesta.qrcode.Models.MyAppDatabase
import app.photofiesta.qrcode.Models.ScanItem
import app.photofiesta.qrcode.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var myAppDatabase: MyAppDatabase

    private lateinit var noHistoryTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.history_fragment_layout, container, false)

        recyclerView = view.findViewById(R.id.historyRecyclerView)
        historyAdapter = HistoryAdapter(onFavoriteClick = { scanItem -> handleFavoriteClick(scanItem) },
            onShareClick = {scanItem -> onShareClick(scanItem)},
            onDeleteClick = {scanItem ->  onDeleteClick(scanItem)}
        )

        noHistoryTextView = view.findViewById(R.id.noHistoryTextView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = historyAdapter

        val scannedButton = view.findViewById<MaterialButton>(R.id.scannedItemBtn)
        val generatedButton = view.findViewById<MaterialButton>(R.id.generatedItemBtn)

        scannedButton.setOnClickListener { loadScanHistoryFromDatabase() }
        generatedButton.setOnClickListener { loadGeneratedHistoryFromDatabase() }

        loadScanHistoryFromDatabase()

        return view
    }

    private fun onShareClick(scanItem: ScanItem) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, scanItem.content)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun onDeleteClick(scanItem: ScanItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            myAppDatabase.scanItemDao().deleteScanItem(scanItem.id)
        }
    }

    private fun handleFavoriteClick(scanItem: ScanItem) {
        val isFavorite = !scanItem.isFavorite
        updateFavoriteStatus(scanItem.id, isFavorite)
    }

    private fun updateFavoriteStatus(itemId: Long, isFavorite: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            myAppDatabase.scanItemDao().updateFavoriteStatus(itemId, isFavorite)
        }
    }

    private fun loadScanHistoryFromDatabase() {
        myAppDatabase.scanItemDao().getAllScannedItems().observe(viewLifecycleOwner) { scanHistory ->
            historyAdapter.submitList(scanHistory)

            if (scanHistory.isEmpty()) {
                noHistoryTextView.visibility = View.VISIBLE
            } else {
                noHistoryTextView.visibility = View.GONE
            }
        }
    }

    private fun loadGeneratedHistoryFromDatabase() {
        myAppDatabase.scanItemDao().getAllGeneratedItems().observe(viewLifecycleOwner) { scanHistory ->
            historyAdapter.submitList(scanHistory)

            if (scanHistory.isEmpty()) {
                noHistoryTextView.visibility = View.VISIBLE
            } else {
                noHistoryTextView.visibility = View.GONE
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initialize your Room database
        myAppDatabase = Room.databaseBuilder(
            requireContext(),
            MyAppDatabase::class.java,
            R.string.scanPref.toString()
        ).build()
    }

    override fun onDestroy() {
        // Close the Room database when the fragment is destroyed
        myAppDatabase.close()
        super.onDestroy()
    }
}
