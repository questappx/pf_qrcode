package app.photofiesta.qrcode.Fragments

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.icu.number.IntegerWidth
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import app.photofiesta.qrcode.Ads.AdmobInterstitial
import app.photofiesta.qrcode.Models.MyAppDatabase
import app.photofiesta.qrcode.Models.ScanItem
import app.photofiesta.qrcode.QRCodeDataProvider
import app.photofiesta.qrcode.QRCodeGenerator
import app.photofiesta.qrcode.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScanFragment : Fragment(), DecoratedBarcodeView.TorchListener {

    private var scanCount = 3



    private lateinit var myAppDatabase: MyAppDatabase
    private lateinit var captureManager: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var qrScanLayout: RelativeLayout
    private lateinit var scanButton: Button
    private var isTorchOn : Boolean = false
    private lateinit var barcodeCallback : BarcodeCallback

    private lateinit var admobInterstitial: AdmobInterstitial

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted: Boolean ->
            if(isGranted)
            {
                showCamera()
            }
            else
            {
                Toast.makeText(requireContext(), "Camera permission is required to scan codes", Toast.LENGTH_LONG).show()
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()){
                result : ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
                }
                else {
                    setResult(result.contents, if(result.formatName.equals("QR_CODE")) 1 else 2 )
                }
            }
        }

    private fun setResult(string : String, format : Int) {

        qrScanLayout.visibility = View.GONE
        scanButton.visibility = View.VISIBLE
        barcodeScannerView.stopDecoding()
        val scannedQrImage = view?.findViewById<ImageView>(R.id.scanedQRImageView)
        val scannedTextTv = view?.findViewById<TextView>(R.id.scannedTextTv)
        scannedTextTv?.text = string

        Log.d("Test", "Format we got in result is $format")


        val scanItem = ScanItem(content = string, timestamp = System.currentTimeMillis(), qrType = format, isFavorite = false, isGenerated = false)
        scannedQrImage?.setImageBitmap(view?.context?.let { QRCodeGenerator(it, lifecycleScope).generateQrOrBarcode(scanItem) })
        insertScanItem(scanItem)
        showRandomAd()

        scannedTextTv?.setOnClickListener{
            copyToClipboard(requireContext(), string)
        }
    }

    private fun copyToClipboard(context: Context, content: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", content)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showRandomAd() {
        if (scanCount % 3 == 0) {
            admobInterstitial.showInterstitial(requireActivity());
        }
        scanCount++
    }

    private fun insertScanItem(scanItem: ScanItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            myAppDatabase.scanItemDao().insertScanItem(scanItem)
        }
    }

    private fun otherOptions(view:View)
    {
        val qrCodeScannerIv = view.findViewById<ImageView>(R.id.qrScannerIv)
        val barCodeScannerIv = view.findViewById<ImageView>(R.id.barcodeIv)
        val torchIv = view.findViewById<ImageView>(R.id.torchIv)

        qrCodeScannerIv.setOnClickListener{
            qrCodeScannerIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.mainAppColor));
            barCodeScannerIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.black));
            if(isTorchOn)
                torchIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.mainAppColor));
            else
                torchIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.black));


        }


        barCodeScannerIv.setOnClickListener{
            qrCodeScannerIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.black));
            barCodeScannerIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.mainAppColor));
            if(isTorchOn)
                torchIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.mainAppColor));
            else
                torchIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.black));


        }

        torchIv.setOnClickListener{
            if(isTorchOn)
                torchIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.black));
            else
                torchIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.mainAppColor));

            if(isTorchOn) turnOffTorch(view.context) else turnOnTorch(view.context)
            qrCodeScannerIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.black));
            barCodeScannerIv.setImageTintList(ContextCompat.getColorStateList(view.context, R.color.black));
        }

        scanButton.setOnClickListener{

            barcodeScannerView.startDecoding()
            barcodeScannerView.decodeContinuous(barcodeCallback)
            barcodeScannerView.resume()
//                barcodeScannerView.decodeContinuous(barcodeCallback)
            qrScanLayout.visibility = View.VISIBLE
            scanButton.visibility = View.GONE
        }


    }

    private fun showCamera() {
        val options = ScanOptions()
//        val formats = mutableListOf<BarcodeFormat>()
//        formats.add(BarcodeFormat.QR_CODE)
//        formats.add(BarcodeFormat.DATA_MATRIX)
//        formats.add(BarcodeFormat.UPC_A)
//        formats.add(BarcodeFormat.CODE_128)

        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)

        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun checkPermissionCamera(context : Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scan_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myAppDatabase = Room.databaseBuilder(
            view.context,
            MyAppDatabase::class.java,
            R.string.scanPref.toString()
        ).build()

        qrScanLayout = view.findViewById(R.id.qrScanLayout)
        scanButton = view.findViewById(R.id.scanButton)

        barcodeCallback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                val format = when (result.barcodeFormat) {
                    BarcodeFormat.QR_CODE -> 1
//                    BarcodeFormat.DATA_MATRIX -> 2
//                    BarcodeFormat.UPC_A -> 3
//                    BarcodeFormat.CODE_128 -> 4
                    else -> 2
                }
                Log.d("Test", when (format) {
                    1 -> "QR"
//                    2 -> "Data Matrix"
//                    3 -> "UPC-A"
//                    4 -> "Code 128"
                    else -> "Barcode"
                })
                setResult(result.text, format)
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
                // Handle possible result points if needed
            }
        }

        barcodeScannerView = view.findViewById(R.id.zxing_barcode_scanner)
        captureManager = CaptureManager(requireActivity(), barcodeScannerView,
        ) {
                result->
            var qrType : Int = if(result.barcodeFormat.name.equals("QR_CODE")) 1 else 2
            Log.d("Test",if(qrType==1) "QR" else "format type is Barcode")


            setResult(result.text, qrType)
        }
        captureManager.initializeFromIntent(activity?.intent, savedInstanceState)
        captureManager.decode()

        otherOptions(view)

        admobInterstitial = AdmobInterstitial.getStaticInstance(requireContext())
    }



    private fun turnOnTorch(context: Context) {
        isTorchOn = true;
        barcodeScannerView.setTorchOn()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
        barcodeScannerView.pause()
    }



    private fun turnOffTorch(context: Context) {
        isTorchOn = false;
        barcodeScannerView.setTorchOff()
    }

    fun setAdmobInterstitial(admobInterstitial: AdmobInterstitial) {
//        this.admobInterstitial = admobInterstitial
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            captureManager.onResume()
        }
        else {
            requestCameraPermission()
        }
    }


    override fun onTorchOn() {
    }

    override fun onTorchOff() {
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureManager.onResume()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to scan codes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }


    override fun onDestroy() {
        myAppDatabase.close()
        super.onDestroy()
    }
}