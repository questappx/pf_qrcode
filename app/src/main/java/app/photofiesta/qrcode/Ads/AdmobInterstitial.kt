package app.photofiesta.qrcode.Ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import app.photofiesta.qrcode.R

class AdmobInterstitial private constructor(private val context: Context) {

    private val TAG = "InterstitialAdImplement"
    private var interstitialAd: InterstitialAd? = null
    private var activityOpenAd = false
    private var isAdLoaded = false

    fun setActivityOpenAd(bool: Boolean) {
        activityOpenAd = bool
    }

    public fun loadAdMobInterstitial() {
        if (!isAdLoaded) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                context.resources.getString(R.string.googleInterstitial),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialad: InterstitialAd) {
                        interstitialAd = interstitialad
                        isAdLoaded = true
                        Log.i(TAG, "AdMob Interstitial Loaded")

                        if (activityOpenAd) {
//                            showInterstitial()
                            activityOpenAd = false
                        }

                        interstitialAd?.setFullScreenContentCallback(object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d(TAG, "AdMob Interstitial Dismissed")
                                isAdLoaded = false
                                interstitialAd = null
                                loadAdMobInterstitial()
                            }

                            override fun onAdShowedFullScreenContent() {
                                isAdLoaded = false
                                interstitialAd = null
                                Log.d(TAG, "AdMob Interstitial Shown")
                            }
                        })
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.i(TAG, "AdMob Interstitial Failed to Load: ${loadAdError.message}")
                        interstitialAd = null
                        isAdLoaded = false
                    }
                })
        }
    }

    fun showInterstitial(activity: Activity) {
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
        }
    }

    init {
        loadAdMobInterstitial()
    }

    companion object {
        private var staticInstance: AdmobInterstitial? = null

        fun getStaticInstance(context: Context): AdmobInterstitial {
            if (staticInstance == null) {
                staticInstance = AdmobInterstitial(context.applicationContext)
            }
            return staticInstance!!
        }
    }
}
