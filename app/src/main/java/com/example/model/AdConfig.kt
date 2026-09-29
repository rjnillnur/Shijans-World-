package com.example.model

enum class AdType {
    REWARDED,
    REWARDED_INTERSTITIAL,
    BANNER,
    INTERSTITIAL,
    NATIVE,
    APP_OPEN,
    ADSTERRA_WEB
}

data class AdMobUnitIds(
    val bannerId: String = "ca-app-pub-6866789810993294/9207261358",
    val interstitialId: String = "ca-app-pub-6866789810993294/1494180450",
    val rewardedInterstitialId: String = "ca-app-pub-6866789810993294/9914761809",
    val rewardedId: String = "ca-app-pub-6866789810993294/4343705071",
    val nativeId: String = "ca-app-pub-6866789810993294/5943552191",
    val appOpenId: String = "ca-app-pub-6866789810993294/4630470525",
    val testBannerId: String = "ca-app-pub-3940256099942544/6300978111",
    val testInterstitialId: String = "ca-app-pub-3940256099942544/1033173712",
    val testRewardedId: String = "ca-app-pub-3940256099942544/5224354917"
)

data class AdsterraConfig(
    val isEnabled: Boolean = true,
    val scriptUrl: String = "https://pl31578566.profitableratecpmnetwork.com/d7/8d/4c/d78d4c146535bfdeb917ba25ed4a96d4.js",
    val enableOnHome: Boolean = true,
    val enableOnTasks: Boolean = true,
    val enableOnGames: Boolean = false,
    val dailyLimit: Int = 15
)

data class AdSettings(
    val isTestMode: Boolean = true, // Safe default during development/testing
    val isAdsGloballyEnabled: Boolean = true,
    val isRewardedAdsEnabled: Boolean = true,
    val isBannerEnabled: Boolean = true,
    val isInterstitialEnabled: Boolean = true,
    val isNativeEnabled: Boolean = true,
    val isAppOpenEnabled: Boolean = true,
    val rewardedPoints: Long = 20L,
    val dailyRewardedLimit: Int = 20,
    val minimumSecondsBetweenAds: Int = 15,
    val adMobUnits: AdMobUnitIds = AdMobUnitIds(),
    val adsterra: AdsterraConfig = AdsterraConfig()
)

data class AdRewardEvent(
    val eventId: String = "",
    val userId: String = "",
    val adType: AdType = AdType.REWARDED,
    val rewardPoints: Long = 0L,
    val clientToken: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val verified: Boolean = true
)
