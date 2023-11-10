package app.photofiesta.qrcode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import app.photofiesta.qrcode.Ads.AdmobInterstitial
import app.photofiesta.qrcode.Ads.AppOpenManager
import com.google.android.gms.ads.MobileAds

class SplashActivity : AppCompatActivity() {

    private lateinit var admobInterstitial: AdmobInterstitial
    private lateinit var appOpenManager: AppOpenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        splashWorking()
        adsWorking()
    }

    private fun adsWorking() {
        MobileAds.initialize(this)
        appOpenManager = AppOpenManager(this)

        admobInterstitial = AdmobInterstitial.getStaticInstance(this)

        admobInterstitial.setActivityOpenAd(false)
    }

    private fun splashWorking() {
        Handler().postDelayed({
            appOpenManager.showAdIfAvailable()
            startAnotherActivity()
        }, 4500)
    }

    private fun startAnotherActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Show App Open Ad if available
    }
}