package app.photofiesta.qrcode

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import app.photofiesta.qrcode.Ads.AdmobInterstitial
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class QRGenerateActivity : AppCompatActivity() {

    private lateinit var generateQRButton: MaterialButton
    private lateinit var saveImageQRButton: MaterialButton
    private lateinit var qrImageView: ImageView

    private lateinit var admobInterstitial: AdmobInterstitial
    private var scanCount = 3

    private lateinit var mainInputInflateLinearLayout: LinearLayout

    private val activityLauncherForSaveIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                saveImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerate)

        linkXml()

        admobInterstitial = AdmobInterstitial.getStaticInstance(this)


        val qrType = intent.getIntExtra("qrType",QRCodeDataProvider.QRCodeType.TEXT)
        handleQRCodeType(qrType)

    }

    private fun showRandomAd() {
        if (scanCount % 3 == 0) {
            admobInterstitial.showInterstitial(this);
        }
        scanCount++
    }

    private fun linkXml() {
        generateQRButton = findViewById(R.id.generateQRbutton)
        saveImageQRButton = findViewById(R.id.saveQRButton)
        qrImageView = findViewById(R.id.qrImageView)
        mainInputInflateLinearLayout = findViewById(R.id.mainInputInflateLinearlayout)


        saveImageQRButton.setOnClickListener{
            val drawable = qrImageView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                if (bitmap != null) {
                    createSaveIntent()
                } else {
                    Toast.makeText(this, "QR Code bitmap is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "QR Code image not ", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun handleQRCodeType(qrType: Int) {
        when (qrType) {
            QRCodeDataProvider.QRCodeType.TEXT -> {
                handleTextQRCode()
            }
            QRCodeDataProvider.QRCodeType.WEBSITE -> {
                handleWebsiteQRCode()
            }
            QRCodeDataProvider.QRCodeType.WIFI -> {
                handleWifiQRCode()
            }
            QRCodeDataProvider.QRCodeType.FACEBOOK -> {
                handleSocialMediaInputLayout(QRCodeDataProvider.QRCodeType.FACEBOOK)
            }
            QRCodeDataProvider.QRCodeType.YOUTUBE -> {
                handleSocialMediaInputLayout(QRCodeDataProvider.QRCodeType.YOUTUBE)
            }
            QRCodeDataProvider.QRCodeType.TIKTOK -> {
                handleSocialMediaInputLayout(QRCodeDataProvider.QRCodeType.TIKTOK)
            }
            QRCodeDataProvider.QRCodeType.EMAIL -> {
                handleEmailQRCode()
            }
            QRCodeDataProvider.QRCodeType.SMS -> {
                handleSMSQRCode()
            }
            QRCodeDataProvider.QRCodeType.INSTAGRAM -> {
                handleSocialMediaInputLayout(QRCodeDataProvider.QRCodeType.INSTAGRAM)
            }
            QRCodeDataProvider.QRCodeType.TWITTER -> {
                handleSocialMediaInputLayout(QRCodeDataProvider.QRCodeType.TWITTER)
            }
            QRCodeDataProvider.QRCodeType.TELEGRAM -> {
                handleSocialMediaInputLayout(QRCodeDataProvider.QRCodeType.TELEGRAM)
            }
            QRCodeDataProvider.QRCodeType.WHATSAPP -> {
                handleWhatsappQRCode()
            }
            QRCodeDataProvider.QRCodeType.CONTACTS -> {
                handleContactQRCode()
            }
            QRCodeDataProvider.QRCodeType.MY_CARD -> {
                handleContactCardQRCode()
            }
            QRCodeDataProvider.QRCodeType.PAYPAL -> {
                handlePayPalQRCode()
            }
            QRCodeDataProvider.QRCodeType.EVENT -> {
                handleEventQRCode()
            }
            QRCodeDataProvider.QRCodeType.APP -> {
                handleAppQRCode()
            }
            QRCodeDataProvider.QRCodeType.LOCATION -> {
                handleLocationQRCode()
            }
            else -> {
                // Handle the case when the QR code type is not recognized
            }
        }
    }

    private fun handleWhatsappQRCode() {

        val whatsappLayout = layoutInflater.inflate(R.layout.wp_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(whatsappLayout)

        val whatsappPhoneNumberTextInput = whatsappLayout.findViewById<TextInputEditText>(R.id.whatsappPhoneNumberTextInput)
        val whatsAppInputLayout = whatsappLayout.findViewById<TextInputLayout>(R.id.whatsappPhoneNumberInputLayout)
        whatsAppInputLayout.visibility = View.VISIBLE



        generateQRButton.setOnClickListener {
            val phoneNumber = whatsappPhoneNumberTextInput.text.toString()

            if (phoneNumber.isNotEmpty() && isValidPhoneNumber(phoneNumber)) {
                val wpString = "wa.me/" + phoneNumber
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateQRCodeBitmapAndInsertDatabase(wpString)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun handleTextQRCode() {
        val textInputLayout = layoutInflater.inflate(R.layout.text_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(textInputLayout)

        val clipboardTextInput = textInputLayout.findViewById<TextInputEditText>(R.id.edittextTextInput)

        textInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener{
            val clipboardText = clipboardTextInput.text.toString()

            if (clipboardText.isNotEmpty()) {
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateTextQRCode(clipboardText)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                Toast.makeText(this, "Clipboard text cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun createSaveIntent() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_TITLE, "${System.currentTimeMillis()}.png")
        activityLauncherForSaveIntent.launch(intent)
    }

    private fun saveImage(uri: Uri) {
        val drawable = qrImageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            try {
                bitmap.setHasAlpha(true)
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                Toast.makeText(applicationContext, "Image saved successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "First generate a QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleWifiQRCode() {
        val wifiInputLayout = layoutInflater.inflate(R.layout.wifi_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(wifiInputLayout)

        val wifiSsidInputLayout = wifiInputLayout.findViewById<TextInputLayout>(R.id.wifiSsidInputLayout)
        val wifiSsidTextInput = wifiSsidInputLayout.findViewById<TextInputEditText>(R.id.wifiSsidTextInput)
        val wifiPasswordInputLayout = wifiInputLayout.findViewById<TextInputLayout>(R.id.wifiPasswordInputLayout)
        val wifiPasswordTextInput = wifiPasswordInputLayout.findViewById<TextInputEditText>(R.id.wifiPasswordTextInput)
        val wifiTypeRadioGroup = wifiInputLayout.findViewById<RadioGroup>(R.id.wifiTypeRadioGroup)

        wifiInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val wifiSsid = wifiSsidTextInput.text.toString()
            val wifiPassword = wifiPasswordTextInput.text.toString()

            val selectedRadioButtonId = wifiTypeRadioGroup.checkedRadioButtonId
            if (wifiSsid.isNotEmpty() && wifiPassword.isNotEmpty() && selectedRadioButtonId != -1) {
                val wifiType = when (selectedRadioButtonId) {
                    R.id.wifiWpaWpa2RadioButton -> "WPA/WPA2"
                    R.id.wifiWepRadioButton -> "WEP"
                    R.id.wifiNoneRadioButton -> "None"
                    else -> "None"
                }
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateWiFiQRCode(wifiSsid, wifiPassword, wifiType)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                Toast.makeText(this, "SSID, Password, and Security Type are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleContactQRCode() {

        val contactsInputLayout = layoutInflater.inflate(R.layout.contacts_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(contactsInputLayout)

        val contactNameInputLayout = contactsInputLayout.findViewById<TextInputLayout>(R.id.contactsNameInputLayout)
        val contactNameTextInput = contactNameInputLayout.findViewById<TextInputEditText>(R.id.contactsNameTextInput)
        val contactPhoneNumberInputLayout = contactsInputLayout.findViewById<TextInputLayout>(R.id.contactsPhoneNumberInputLayout)
        val contactPhoneNumberTextInput = contactPhoneNumberInputLayout.findViewById<TextInputEditText>(R.id.contactsPhoneNumberTextInput)

        contactsInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val name = contactNameTextInput.text.toString()
            val phoneNumber = contactPhoneNumberTextInput.text.toString()

            if (name.isNotEmpty()) {
                if (isValidPhoneNumber(phoneNumber)) {
                    showRandomAd()
                    val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateContactsQRCode(name, phoneNumber)
                    qrImageView.setImageBitmap(qrCodeBitmap)
                } else {
                    Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Contact name is required", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun handleSocialMediaInputLayout(qrType : Int) {
        val socialInputLayout = layoutInflater.inflate(R.layout.social_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()
        mainInputInflateLinearLayout.addView(socialInputLayout)

        val socialTypeString = getQRTypeStrng(qrType)

        val socialTextInput = socialInputLayout.findViewById<EditText>(R.id.socialTextInput)
        val socialTextInputLayout = socialInputLayout.findViewById<TextInputLayout>(R.id.socialMediaInputLayout)
        val socialUrlBtn = socialInputLayout.findViewById<RadioButton>(R.id.socialUrlBtn)
        val socialUsernameBtn = socialInputLayout.findViewById<RadioButton>(R.id.socialUsernameBtn)

        socialInputLayout.visibility = View.VISIBLE

        socialTextInputLayout.hint = "Enter " + socialTypeString + " Link"

        socialUrlBtn.setOnClickListener{
            socialTextInputLayout.hint = "Enter " + socialTypeString + " Link"
        }
        socialUsernameBtn.setOnClickListener{
            socialTextInputLayout.hint = "Enter " + socialTypeString + " Username"
        }




        generateQRButton.setOnClickListener {
            val userInput = socialTextInput.text.toString().trim()

            if (userInput.isNotEmpty()) {
                if (socialUrlBtn.isChecked) {
                    if(isUrlValid(qrType, userInput))
                    {
                        showRandomAd()
                        val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateQrCodeForSocialLink(userInput)
                        qrImageView.setImageBitmap(qrCodeBitmap)
                    }
                    else
                    {
                        Toast.makeText(applicationContext, "Url is not valid", Toast.LENGTH_SHORT).show()
                    }
                }
                else if (socialUsernameBtn.isChecked) {
                    showRandomAd()
                    val usernameString = createUsernameForSocial(userInput, qrType)
                    val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateQrCodeForSocialLink(usernameString)
                    qrImageView.setImageBitmap(qrCodeBitmap)
                }
            } else {
                Toast.makeText(this, "Input is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUsernameForSocial(userInput: String, qrType: Int): String {
        val username = userInput.trim()

        return if (validateURL(username)) {
            username
        } else {
            val baseUrl = when (qrType) {
                QRCodeDataProvider.QRCodeType.FACEBOOK -> "https://www.facebook.com/"
                QRCodeDataProvider.QRCodeType.TWITTER -> "https://twitter.com/@"
                QRCodeDataProvider.QRCodeType.YOUTUBE -> "https://www.youtube.com/"
                QRCodeDataProvider.QRCodeType.INSTAGRAM -> "https://www.instagram.com/@"
                QRCodeDataProvider.QRCodeType.TIKTOK -> "https://www.tiktok.com/"
                else -> return ""
            }

            if (username.startsWith("http://") || username.startsWith("https://")) {
                return username
            }

            val fullLink = baseUrl + Uri.encode(username)

            return fullLink
        }
    }


    private fun validateURL(url: String): Boolean {
        val urlPattern = "^(https?://)?([\\da-zA-Z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$"
        return url.matches(Regex(urlPattern))
    }


    private fun getQRTypeStrng(qrType: Int):String {
        if(QRCodeDataProvider.QRCodeType.FACEBOOK == qrType)
        {
            return "Facebook"
        }
        else if(QRCodeDataProvider.QRCodeType.TWITTER == qrType)
        {
            return "Twitter"
        }
        else if(QRCodeDataProvider.QRCodeType.YOUTUBE == qrType)
        {
            return "YouTube"
        }
        else if(QRCodeDataProvider.QRCodeType.INSTAGRAM == qrType)
        {
            return "Instagram"
        }
        else if(QRCodeDataProvider.QRCodeType.TIKTOK == qrType)
        {
            return "Tik Tok"
        }
        else
        {
            return "Social"
        }
    }

    private fun isUrlValid(qrType: Int, userInput: String): Boolean {
        val facebookPattern = "^(https?://)?(www\\.)?facebook\\.com/.+".toRegex()
        val instagramPattern = "^(https?://)?(www\\.)?instagram\\.com/.+".toRegex()
        val twitterPattern = "^(https?://)?(www\\.)?twitter\\.com/.+".toRegex()
        val tiktokPattern = "^(https?://)?(www\\.)?tiktok\\.com/.+".toRegex()
        val youtubePattern = "^(https?://)?(www\\.)?youtube\\.com/.+".toRegex()

        val pattern = when (qrType) {
            QRCodeDataProvider.QRCodeType.FACEBOOK -> facebookPattern
            QRCodeDataProvider.QRCodeType.INSTAGRAM -> instagramPattern
            QRCodeDataProvider.QRCodeType.TWITTER -> twitterPattern
            QRCodeDataProvider.QRCodeType.TIKTOK -> tiktokPattern
            QRCodeDataProvider.QRCodeType.YOUTUBE -> youtubePattern
            else -> return false
        }

        return pattern.matches(userInput)
    }



    private fun handleEmailQRCode() {

        val emailInputLayout = layoutInflater.inflate(R.layout.email_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(emailInputLayout)


//        val emailInputLayout = findViewById<View>(R.id.email_input_include_layout)
        val emailAddressInputLayout = emailInputLayout.findViewById<TextInputLayout>(R.id.emailAddressInputLayout)
        val emailAddressTextInput = emailAddressInputLayout.findViewById<TextInputEditText>(R.id.emailAddressTextInput)
        val emailSubjectInputLayout = emailInputLayout.findViewById<TextInputLayout>(R.id.emailSubjectInputLayout)
        val emailSubjectTextInput = emailSubjectInputLayout.findViewById<TextInputEditText>(R.id.emailSubjectTextInput)
        val emailContentInputLayout = emailInputLayout.findViewById<TextInputLayout>(R.id.emailContentInputLayout)
        val emailContentTextInput = emailContentInputLayout.findViewById<TextInputEditText>(R.id.emailContentTextInput)

        emailInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val recipientEmail = emailAddressTextInput.text.toString()
            val subject = emailSubjectTextInput.text.toString()
            val content = emailContentTextInput.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(recipientEmail).matches()) {
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateEmailQRCode(recipientEmail, subject, content)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                Toast.makeText(this, "Invalid recipient's email address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleContactCardQRCode() {

        val cardInputLayout = layoutInflater.inflate(R.layout.card_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(cardInputLayout)


//        val cardInputLayout = findViewById<View>(R.id.card_input_include_layout)
        val cardNameInputLayout = cardInputLayout.findViewById<TextInputLayout>(R.id.myCardNameInputLayout)
        val cardNameTextInput = cardNameInputLayout.findViewById<TextInputEditText>(R.id.myCardNameTextInput)
        val cardNumberInputLayout = cardInputLayout.findViewById<TextInputLayout>(R.id.myCardNumberInputLayout)
        val cardNumberTextInput = cardNumberInputLayout.findViewById<TextInputEditText>(R.id.myCardNumberTextInput)
        val cardEmailInputLayout = cardInputLayout.findViewById<TextInputLayout>(R.id.myCardEmailInputLayout)
        val cardEmailTextInput = cardEmailInputLayout.findViewById<TextInputEditText>(R.id.myCardEmailTextInput)
        val cardAddressInputLayout = cardInputLayout.findViewById<TextInputLayout>(R.id.myCardAddressInputLayout)
        val cardAddressTextInput = cardAddressInputLayout.findViewById<TextInputEditText>(R.id.myCardAddressTextInput)
        val cardBirthdayInputLayout = cardInputLayout.findViewById<TextInputLayout>(R.id.myCardBirthdayInputLayout)
        val cardBirthdayTextInput = cardBirthdayInputLayout.findViewById<TextInputEditText>(R.id.myCardBirthdayTextInput)
        val cardNoteInputLayout = cardInputLayout.findViewById<TextInputLayout>(R.id.myCardNoteInputLayout)
        val cardNoteTextInput = cardNoteInputLayout.findViewById<TextInputEditText>(R.id.myCardNoteTextInput)

        cardInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val name = cardNameTextInput.text.toString()
            val number = cardNumberTextInput.text.toString()
            val email = cardEmailTextInput.text.toString()
            val address = cardAddressTextInput.text.toString()
            val birthday = cardBirthdayTextInput.text.toString()
            val note = cardNoteTextInput.text.toString()

            if (name.isNotEmpty()) {
                if ((number.isEmpty() || isValidPhoneNumber(number)) && (email.isEmpty() || isValidEmailAddress(email))) {
                    showRandomAd()
                    val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateCardQRCode(name, number, email, address, birthday, note)
                    qrImageView.setImageBitmap(qrCodeBitmap)
                } else {
                    Toast.makeText(this, "Invalid phone number or email", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Contact name is required", Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun isValidEmailAddress(email: String): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$")
        return emailPattern.matches(email)
    }



    private fun handleSMSQRCode() {
        val smsInputLayout = layoutInflater.inflate(R.layout.sms_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(smsInputLayout)

//        val smsInputLayout = findViewById<View>(R.id.sms_input_include_layout)
        val smsPhoneNumberInputLayout = smsInputLayout.findViewById<TextInputLayout>(R.id.smsPhoneNumberInputLayout)
        val smsPhoneNumberTextInput = smsPhoneNumberInputLayout.findViewById<TextInputEditText>(R.id.smsPhoneNumberTextInput)
        val smsMessageInputLayout = smsInputLayout.findViewById<TextInputLayout>(R.id.smsMessageInputLayout)
        val smsMessageTextInput = smsMessageInputLayout.findViewById<TextInputEditText>(R.id.smsMessageTextInput)

        smsInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val phoneNumber = smsPhoneNumberTextInput.text.toString()
            val message = smsMessageTextInput.text.toString()

            if (isValidPhoneNumber(phoneNumber) && message.isNotEmpty()) {
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateSMSQRCode(phoneNumber, message)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                if (!isValidPhoneNumber(phoneNumber)) {
                    Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "SMS message cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handlePayPalQRCode() {

        val paypalInputLayout = layoutInflater.inflate(R.layout.paypal_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(paypalInputLayout)


//        val paypalInputLayout = findViewById<View>(R.id.paypal_input_include_layout)
        val paypalUrlInputLayout = paypalInputLayout.findViewById<TextInputLayout>(R.id.paypalUrlInputLayout)
        val paypalUrlTextInput = paypalUrlInputLayout.findViewById<TextInputEditText>(R.id.paypalUrlTextInput)
        val paypalOptionsRadioGroup = paypalInputLayout.findViewById<RadioGroup>(R.id.paypalOptionsRadioGroup)

        val initialHint = paypalUrlInputLayout.hint

        paypalInputLayout.visibility = View.VISIBLE

        paypalOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedOption = findViewById<RadioButton>(checkedId)?.text.toString()
            paypalUrlInputLayout.hint = when (selectedOption) {
                "Link" -> "Enter PayPal URL*"
                "Username" -> "Username*"
                else -> initialHint
            }
        }

        generateQRButton.setOnClickListener {
            val selectedOptionId = paypalOptionsRadioGroup.checkedRadioButtonId
            val selectedOption = findViewById<RadioButton>(selectedOptionId)?.text.toString()

            if (selectedOption == "Link") {
                val paypalUrl = paypalUrlTextInput.text.toString()

                if (paypalUrl.isNotEmpty()) {
                    if (isValidPayPalURL(paypalUrl)) {
                        showRandomAd()
                        val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generatePaypalLinkQRCode(paypalUrl)
                        qrImageView.setImageBitmap(qrCodeBitmap)
                    } else {
                        Toast.makeText(this, "Invalid PayPal URL", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "PayPal URL is required", Toast.LENGTH_SHORT).show()
                }
            } else {
                val username = generatePayPalLink(paypalUrlTextInput.text.toString())
                if (username.isNotEmpty()) {
                    showRandomAd()
                    val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generatePaypalLinkQRCode(username)
                    qrImageView.setImageBitmap(qrCodeBitmap)
                } else {
                    Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun isValidPayPalURL(url: String): Boolean {
        val pattern = "^(https?://)?(www\\.)?paypal\\.com/paypalme/.*$".toRegex()

        return pattern.matches(url)
    }



    private fun generatePayPalLink(username: String): String {
        return "https://www.paypal.com/paypalme/$username"
    }


    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phonePattern = Regex("^\\d{6,14}$")
        return phonePattern.matches(phoneNumber)
    }

    private fun handleEventQRCode() {
        val eventInputLayout = layoutInflater.inflate(R.layout.event_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(eventInputLayout)



//        val eventInputLayout = findViewById<View>(R.id.event_input_include_layout)
        val calendarTitleInputLayout = eventInputLayout.findViewById<TextInputLayout>(R.id.calendarTitleInputLayout)
        val calendarTitleTextInput = calendarTitleInputLayout.findViewById<TextInputEditText>(R.id.calendarTitleTextInput)
        val calendarStartDateInputLayout = eventInputLayout.findViewById<TextInputLayout>(R.id.calendarStartDateInputLayout)
        val calendarStartDateTextInput = calendarStartDateInputLayout.findViewById<TextInputEditText>(R.id.calendarStartDateTextInput)
        val calendarEndDateInputLayout = eventInputLayout.findViewById<TextInputLayout>(R.id.calendarEndDateInputLayout)
        val calendarEndDateTextInput = calendarEndDateInputLayout.findViewById<TextInputEditText>(R.id.calendarEndDateTextInput)
        val calendarDescriptionInputLayout = eventInputLayout.findViewById<TextInputLayout>(R.id.calendarDescriptionInputLayout)
        val calendarDescriptionTextInput = calendarDescriptionInputLayout.findViewById<TextInputEditText>(R.id.calendarDescriptionTextInput)
        val calendarLocationInputLayout = eventInputLayout.findViewById<TextInputLayout>(R.id.calendarLocationInputLayout)
        val calendarLocationTextInput = calendarLocationInputLayout.findViewById<TextInputEditText>(R.id.calendarLocationTextInput)

        eventInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val title = calendarTitleTextInput.text.toString()
            val startDate = calendarStartDateTextInput.text.toString()
            val endDate = calendarEndDateTextInput.text.toString()
            val description = calendarDescriptionTextInput.text.toString()
            val location = calendarLocationTextInput.text.toString()

            if (title.isNotEmpty() && isValidDateFormat(startDate) && isValidDateFormat(endDate)) {
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateEventQRCode(title, location, startDate, endDate, description)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                Toast.makeText(this, "Title, start date & end date are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleAppQRCode() {

        val appInputLayout = layoutInflater.inflate(R.layout.app_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(appInputLayout)


        val appLinkInputLayout = appInputLayout.findViewById<TextInputLayout>(R.id.appLinkInputLayout)
        val appLinkTextInput = appLinkInputLayout.findViewById<TextInputEditText>(R.id.appLinkTextInput)

        appInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val appLink = appLinkTextInput.text.toString()

            if (appLink.isNotEmpty() && isValidUrl(appLink)) {
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateAppLinkQRCode(appLink)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        val urlPattern = "^(https?|ftp|file)://.+$"

        val pattern = Pattern.compile(urlPattern)
        val matcher = pattern.matcher(url)

        return matcher.matches()
    }



    private fun isValidDateFormat(date: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.isLenient = false
            dateFormat.parse(date)
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun handleLocationQRCode() {

        val locationInputLayout = layoutInflater.inflate(R.layout.location_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(locationInputLayout)


//        val locationInputLayout = findViewById<View>(R.id.location_input_include_layout)
        val locationNameInputLayout = locationInputLayout.findViewById<TextInputLayout>(R.id.locationNameInputLayout)
        val locationNameTextInput = locationNameInputLayout.findViewById<TextInputEditText>(R.id.locationNameTextInput)
        val locationLongitudeInputLayout = locationInputLayout.findViewById<TextInputLayout>(R.id.locationLongitudeInputLayout)
        val locationLongitudeTextInput = locationLongitudeInputLayout.findViewById<TextInputEditText>(R.id.locationLongitudeTextInput)
        val locationLatitudeInputLayout = locationInputLayout.findViewById<TextInputLayout>(R.id.locationLatitudeInputLayout)
        val locationLatitudeTextInput = locationLatitudeInputLayout.findViewById<TextInputEditText>(R.id.locationLatitudeTextInput)
        val locationDescriptionInputLayout = locationInputLayout.findViewById<TextInputLayout>(R.id.locationDescriptionInputLayout)
        val locationDescriptionTextInput = locationDescriptionInputLayout.findViewById<TextInputEditText>(R.id.locationDescriptionTextInput)

        locationInputLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener {
            val name = locationNameTextInput.text.toString()
            val longitude = locationLongitudeTextInput.text.toString()
            val latitude = locationLatitudeTextInput.text.toString()
            val description = locationDescriptionTextInput.text.toString()

            if (name.isNotEmpty() && longitude.isNotEmpty() && latitude.isNotEmpty()) {
                if (isValidLongitude(longitude) && isValidLatitude(latitude)) {
                    showRandomAd()
                    val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateLocationQRCode(name, description, longitude,latitude)
                    qrImageView.setImageBitmap(qrCodeBitmap)
                } else {
                    Toast.makeText(this, "Invalid longitude or latitude", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Name, Longitude, and Latitude are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidLongitude(longitude: String): Boolean {
        return try {
            val lon = longitude.toDouble()
            lon in -180.0..180.0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isValidLatitude(latitude: String): Boolean {
        return try {
            val lat = latitude.toDouble()
            lat in -90.0..90.0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun handleWebsiteQRCode() {
        val webIncludeLayout = layoutInflater.inflate(R.layout.web_input_layout, null)

        mainInputInflateLinearLayout.removeAllViews()

        mainInputInflateLinearLayout.addView(webIncludeLayout)



        val websiteInputLayout = findViewById<TextInputLayout>(R.id.webEdittextInputLayout)
        val websiteTextInput = websiteInputLayout.findViewById<TextInputEditText>(R.id.websiteTextInput)

        webIncludeLayout.visibility = View.VISIBLE

        generateQRButton.setOnClickListener{
            val websiteUrl = websiteTextInput.text.toString()

            if (Patterns.WEB_URL.matcher(websiteUrl).matches()) {
                showRandomAd()
                val qrCodeBitmap = QRCodeGenerator(this, lifecycleScope).generateWebsiteQRCode(websiteUrl)
                qrImageView.setImageBitmap(qrCodeBitmap)
            } else {
                Toast.makeText(this, "Invalid website URL", Toast.LENGTH_LONG).show()
            }
        }

    }


}