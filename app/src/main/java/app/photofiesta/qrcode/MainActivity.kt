package app.photofiesta.qrcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import app.photofiesta.qrcode.Ads.AdmobBanner
import app.photofiesta.qrcode.Ads.AdmobInterstitial
import app.photofiesta.qrcode.Fragments.*
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var admobInterstitial: AdmobInterstitial

    private lateinit var adView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bindings()
        adsWorking()
    }

    private fun bindings() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val defaultFragment = ScanFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, defaultFragment).commit()

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_item1 -> {
                    val scanFragment = ScanFragment()
                    scanFragment.setAdmobInterstitial(admobInterstitial)
                    replaceFragment(scanFragment)
                    true
                }
                R.id.navigation_item2 -> {
                    replaceFragment(HistoryFragment())
                    true
                }
                R.id.navigation_item3 -> {
                    replaceFragment(FavoriteFragment())
                    true
                }
                R.id.navigation_item4 -> {
                    replaceFragment(CreateFragment())
                    true
                }
//                R.id.navigation_item5 -> {
//                    replaceFragment(SettingFragment())
//                true
//                }

                else -> false
            }
        }

    }

    private fun adsWorking() {

        adView = findViewById(R.id.adView)

        val admobBanner = AdmobBanner(this, adView)

        admobBanner.BannerAdLoad()

        admobInterstitial = AdmobInterstitial.getStaticInstance(this)

        admobInterstitial.setActivityOpenAd(false)
//        admobInterstitial.loadAdMobInterstitial()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }


}