package app.photofiesta.qrcode

import app.photofiesta.qrcode.Adapters.GridItem

data class QRCodeData(
    val id: Int,
    val iconRes: Int,
    val title: String
)

object QRCodeDataProvider {

    object QRCodeType {
        const val TEXT = 1
        const val WEBSITE = 2
        const val WIFI = 3
        const val FACEBOOK = 4
        const val YOUTUBE = 5
        const val LOCATION = 6
        const val APP = 7
        const val TIKTOK = 8
        const val EMAIL = 9
        const val SMS = 10
        const val INSTAGRAM = 11
        const val TWITTER = 12
        const val MY_CARD = 13
        const val TELEGRAM = 14
        const val WHATSAPP = 15
        const val EVENT = 16
        const val CONTACTS = 17
        const val PAYPAL = 18

    }

    val qrCodeDataList = listOf(
        GridItem(QRCodeType.TEXT, R.drawable.ic_text, "Text"),
        GridItem(QRCodeType.WEBSITE, R.drawable.ic_website, "Website"),
        GridItem(QRCodeType.WIFI, R.drawable.ic_wifi, "Wi-Fi"),
        GridItem(QRCodeType.FACEBOOK, R.drawable.ic_facebook, "Facebook"),
        GridItem(QRCodeType.YOUTUBE, R.drawable.ic_youtube, "YouTube"),
        GridItem(QRCodeType.TIKTOK, R.drawable.ic_tiktok, "TikTok"),
        GridItem(QRCodeType.EMAIL, R.drawable.ic_email, "Email"),
        GridItem(QRCodeType.SMS, R.drawable.ic_sms, "SMS"),
        GridItem(QRCodeType.INSTAGRAM, R.drawable.ic_instagram, "Instagram"),
        GridItem(QRCodeType.TWITTER, R.drawable.ic_twitter, "Twitter"),
        GridItem(QRCodeType.TELEGRAM, R.drawable.ic_telegram, "Telegram"),
        GridItem(QRCodeType.WHATSAPP, R.drawable.ic_whatsapp, "WhatsApp"),
        GridItem(QRCodeType.CONTACTS, R.drawable.ic_contacts, "Contacts"),
        GridItem(QRCodeType.MY_CARD, R.drawable.ic_mycard, "My Card"),
        GridItem(QRCodeType.PAYPAL, R.drawable.ic_paypal, "PayPal"),
        GridItem(QRCodeType.EVENT, R.drawable.ic_calendar, "Event"),
        GridItem(QRCodeType.APP, R.drawable.ic_app, "App"),
        GridItem(QRCodeType.LOCATION, R.drawable.ic_location, "Location")
    )
}
