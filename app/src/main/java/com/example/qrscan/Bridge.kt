package com.example.qrscan

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import com.eyestorm.services.ServicesManager

class Bridge private constructor() {
    private val servicesManager = ServicesManager.getInstance()

    // List Product IAP
    var COIN_50: String = "appservicestest_50_coin"
    var NOADS: String = "appservicestest_noads"
    var SUBSCRIPTION: String = "appservicestest_vip_subscription"

    private val nonConsumablesList = listOf(NOADS)
    private val consumablesList = listOf(COIN_50)
    private val subsList = listOf(SUBSCRIPTION)

    private var isInitServices: Boolean = false

    companion object {
        @Volatile
        private var instance: Bridge? = null

        fun getInstance(): Bridge {
            return instance ?: synchronized(this) {
                instance ?: Bridge().also { instance = it }
            }
        }
    }

    fun setCurrentActivity(activity: Activity) {
        servicesManager.setCurrentActivity(activity)
        initServices()
        servicesManager.showBannerAd()
        servicesManager.loadNativeAds()
    }

    private fun initServices() {
        if (isInitServices) return
        isInitServices = true
        servicesManager.setupListProductPurchase(nonConsumablesList, consumablesList, subsList)
        servicesManager.initServices()
    }

    fun sendEventFirebase(eventName: String, parameter: Bundle?) {
        servicesManager.logEventFirebase(eventName, parameter)
    }

    fun setNoAds() {
        servicesManager.setNoAds()
    }

    fun showInterstitialAd() {
        servicesManager.showInterstitialAd()
    }

    fun showRewardedAd(onUserEarnedReward: () -> Unit) {
        servicesManager.showRewardedAd(onUserEarnedReward)
    }

    fun showAppOpenAd(onShowAdCompleted: () -> Unit) {
        servicesManager.showAppOpenAd(onShowAdCompleted)
    }

    fun clearNativeAdsLayouts() {
        servicesManager.clearNativeAdsLayouts()
    }

    fun addNativeAdLayout(layout: FrameLayout) {
        servicesManager.addNativeAdLayout(layout)
    }

    fun openTerms(activity: Activity) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://eyestormglobal.com/terms-of-use/")
            )
        )
    }

    fun openPrivacy(activity: Activity) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://eyestormglobal.com/privacy-policy/")
            )
        )
    }

    fun buyProduct(sku: String, onSucceedPurchase: () -> Unit) {
        servicesManager.buyProduct(sku, onSucceedPurchase)
    }

    fun buySubscription(sku: String, onSucceedPurchase: () -> Unit) {
        servicesManager.buySubscription(sku, onSucceedPurchase)
    }

    fun isPurchased(productId: String): Boolean {
        return servicesManager.isPurchased(productId)
    }

    fun isSubscribed(subscriptionId: String): Boolean {
        return servicesManager.isSubscribed(subscriptionId)
    }
}