package com.idrisov.mycode

import ABTestType
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * Created by Tagir Idrisov on 09.07.2021
 */

object FirebaseAbTesting {

    val mFirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    val configSettings by lazy {
        FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
    }

    inline fun <reified T> fetchTestingGroup(type: ABTestType, crossinline callBack: (value: T) -> Unit) {

        mFirebaseRemoteConfig.run {

            setConfigSettingsAsync(configSettings)

            setDefaultsAsync(R.xml.firebase_remote_config)

            fetchAndActivate().addOnCompleteListener {
                when (T::class) {
                    Boolean::class -> callBack.invoke(getBoolean(type.key) as T)
                    else -> callBack.invoke(getString(type.key) as T)
                }
            }
        }
    }
}