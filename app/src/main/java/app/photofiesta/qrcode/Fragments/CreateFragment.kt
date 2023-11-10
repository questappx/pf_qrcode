package app.photofiesta.qrcode.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.photofiesta.qrcode.Adapters.GridAdapter
import app.photofiesta.qrcode.Adapters.GridItem
import app.photofiesta.qrcode.QRCodeDataProvider
import app.photofiesta.qrcode.QRGenerateActivity
import app.photofiesta.qrcode.R

class CreateFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gridAdapter: GridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_fragment_layout, container, false)

        recyclerView = view.findViewById(R.id.gridRecyclerView)
        gridAdapter = GridAdapter(QRCodeDataProvider.qrCodeDataList) { item ->
            handleItemClick(item)
        }

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = gridAdapter

        return view
    }

    private fun handleItemClick(item: GridItem) {
        val intent = Intent(requireContext(), QRGenerateActivity::class.java)
        intent.putExtra("qrType", item.id)
        startActivity(intent)
    }



}
