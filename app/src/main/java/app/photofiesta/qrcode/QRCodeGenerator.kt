package app.photofiesta.qrcode

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.Room
import app.photofiesta.qrcode.Models.MyAppDatabase
import app.photofiesta.qrcode.Models.ScanItem
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QRCodeGenerator(private val context: Context,private val lifecycleScope: LifecycleCoroutineScope){


    val width = 600;
    val height = 600;

    fun generateQrOrBarcode(scanItem : ScanItem):Bitmap
    {
        if(scanItem.qrType == 1)
        {
            return generateQRCodeBitmap(scanItem.content)
        }
        else
        {
            return generateBarcodeBitmap(scanItem.content)
        }
    }

    fun generateBarcodeBitmap(text: String): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val barcodeWriter = MultiFormatWriter()
        val bitMatrix = barcodeWriter.encode(text, BarcodeFormat.CODE_128, width, height, hints)

        val barcodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                barcodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }

        return barcodeBitmap
    }


    fun generateQRCodeBitmap(text: String): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        val qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }

        return qrCodeBitmap
    }

    fun generateQRCodeBitmapAndInsertDatabase(text: String): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        val qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }

        val generatedItem = ScanItem(content = text, timestamp = System.currentTimeMillis(), qrType = R.drawable.baseline_qr_code_scanner_24, isFavorite = false, isGenerated = true)
        insertGeneratedItemIntoDatabase(generatedItem)

        return qrCodeBitmap
    }

    private fun insertGeneratedItemIntoDatabase(item: ScanItem) {
        Log.d("Insertion",item.toString())
        val myAppDatabase = Room.databaseBuilder(
            context,
            MyAppDatabase::class.java,
            R.string.scanPref.toString()
        ).build()
        lifecycleScope.launch(Dispatchers.IO) {
            myAppDatabase.scanItemDao().insertScanItem(item)
        }
    }

    fun generateTextQRCode(text: String): Bitmap {
        return generateQRCodeBitmapAndInsertDatabase(text)
    }

    fun generateWebsiteQRCode(url: String): Bitmap {
        // Handle website QR code generation
        return generateQRCodeBitmapAndInsertDatabase(url)
    }

    fun generateEmailQRCode(
        email: String,
        subject: String,
        message: String,
    ): Bitmap {
        // Handle email QR code generation
        val data = "mailto:$email?subject=$subject&body=$message"
        return generateQRCodeBitmapAndInsertDatabase(data)
    }


    fun generateWiFiQRCode(ssid: String, password: String, encryptionType: String): Bitmap {
        // Handle Wi-Fi QR code generation
        val wifiData = "WIFI:S:$ssid;T:$encryptionType;P:$password;;"
        return generateQRCodeBitmapAndInsertDatabase(wifiData)
    }

    fun generateContactsQRCode(name: String, number: String): Bitmap {
        // Handle contacts QR code generation
        val contactData = "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "FN:$name\n" +
                "TEL:$number\n" +
                "END:VCARD"
        return generateQRCodeBitmapAndInsertDatabase(contactData)
    }

    fun generateTelephoneQRCode(number: String): Bitmap {
        // Handle telephone QR code generation
        val telData = "tel:$number"
        return generateQRCodeBitmapAndInsertDatabase(telData)
    }

    fun generateSMSQRCode(number: String, message: String): Bitmap {
        // Handle SMS QR code generation
        val smsData = "smsto:$number:$message"
        return generateQRCodeBitmapAndInsertDatabase(smsData)
    }

    fun generateCardQRCode(name: String, number: String, mail: String, address: String, birthday: String, note: String): Bitmap {
        // Handle My Card QR code generation
        val cardData = "MECARD:N:$name;TEL:$number;EMAIL:$mail;ADR:$address;BDAY:$birthday;NOTE:$note;;"
        return generateQRCodeBitmapAndInsertDatabase(cardData)
    }

    fun generatePaypalLinkQRCode(link: String): Bitmap {
        // Handle PayPal Link QR code generation
        return generateQRCodeBitmapAndInsertDatabase(link)
    }


    fun generateEventQRCode(title: String, location: String, startDate: String, endDate: String, description: String): Bitmap {
        // Handle Event QR code generation
        val eventData = "BEGIN:VEVENT\n" +
                "SUMMARY:$title\n" +
                "LOCATION:$location\n" +
                "DTSTART:$startDate\n" +
                "DTEND:$endDate\n" +
                "DESCRIPTION:$description\n" +
                "END:VEVENT"
        return generateQRCodeBitmapAndInsertDatabase(eventData)
    }

    fun generateSpotifyQRCode(artistName: String, songName: String): Bitmap {
        val spotifyData = "spotify:search:$artistName $songName"
        return generateQRCodeBitmapAndInsertDatabase(spotifyData)
    }

    fun generateAppLinkQRCode(link: String): Bitmap {
        return generateQRCodeBitmapAndInsertDatabase(link)
    }

    fun generateLocationQRCode(name: String, description:String, longitude: String, latitude: String): Bitmap? {
        val locationData = "Name: $name\nLongitude: $longitude\nLatitude: $latitude\nDescription: $description"
        return generateQRCodeBitmapAndInsertDatabase(locationData)
    }

    fun generateQrCodeForSocialLink(userInput: String): Bitmap? {
        return generateQRCodeBitmapAndInsertDatabase(userInput)
    }


}
