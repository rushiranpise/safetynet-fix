package dev.kdrag0n.safetynetfix.proxy

import android.app.Application
import android.os.Build
import android.os.SystemProperties
import android.util.Log

import java.lang.reflect.Field
import java.util.HashMap

class ProxyProvider {
    companion object {
        private const val TAG = "ProxyProvider"
        private const val DEBUG = true

        private val propsToChangePixel7Pro = mutableMapOf(
            "FINGERPRINT" to "google/cheetah/cheetah:13/TQ3A.230605.012/10204971:user/release-keys"
        )
        private val propsToChangePixel5 = mutableMapOf(
            "BRAND" to "google",
            "MANUFACTURER" to "Google",
            "DEVICE" to "redfin",
            "PRODUCT" to "redfin",
            "MODEL" to "Pixel 5",
            "FINGERPRINT" to "google/redfin/redfin:13/TQ3A.230605.011/10161073:user/release-keys"
        )
        private val propsToChangePixelXL = mutableMapOf(
            "BRAND" to "google",
            "MANUFACTURER" to "Google",
            "DEVICE" to "marlin",
            "PRODUCT" to "marlin",
            "MODEL" to "Pixel XL",
            "FINGERPRINT" to "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys"
        )
        private val propsToChangeROG6 = mutableMapOf(
            "BRAND" to "asus",
            "MANUFACTURER" to "asus",
            "DEVICE" to "AI2201",
            "MODEL" to "ASUS_AI2201"
        )
        private val propsToChangeXP5 = mutableMapOf(
            "MODEL" to "SO-52A",
            "MANUFACTURER" to "Sony"
        )
        private val propsToChangeOP8P = mutableMapOf(
            "MODEL" to "IN2020",
            "MANUFACTURER" to "OnePlus"
        )
        private val propsToChangeOP9P = mutableMapOf(
            "MODEL" to "LE2123",
            "MANUFACTURER" to "OnePlus"
        )
        private val propsToChangeMI11T = mutableMapOf(
            "MODEL" to "21081111RG",
            "MANUFACTURER" to "Xiaomi"
        )
        private val propsToChangeMI13P = mutableMapOf(
            "BRAND" to "Xiaomi",
            "MANUFACTURER" to "Xiaomi",
            "MODEL" to "2210132C"
        )
        private val propsToChangeF5 = mutableMapOf(
            "MODEL" to "23049PCD8G",
            "MANUFACTURER" to "Xiaomi"
        )
        private val propsToChangeK30U = mutableMapOf(
            "MODEL" to "M2006J10C",
            "MANUFACTURER" to "Xiaomi"
        )

        private val packagesToKeep = arrayOf(
            // Add package names to keep here
        )

        private val pixelCodenames = arrayOf(
            // Add pixel codenames here
        )

        private val extraPackagesToChange = arrayOf(
            // Add extra packages to change here
        )

        private val packagesToChangePixel7Pro = arrayOf(
            // Add package names to change for Pixel 7 Pro here
        )

        private val packagesToChangePixel5 = arrayOf(
            // Add package names to change for Pixel 5 here
        )

        private val packagesToChangePixelXL = arrayOf(
            // Add package names to change for Pixel XL here
        )

        private val packagesToChangeROG6 = arrayOf(
            // Add package names to change for ROG 6 here
        )

        private val packagesToChangeXP5 = arrayOf(
            // Add package names to change for Xperia 5 here
        )

        private val packagesToChangeOP8P = arrayOf(
            // Add package names to change for OnePlus 8 Pro here
        )

        private val packagesToChangeOP9P = arrayOf(
            // Add package names to change for OnePlus 9 Pro here
        )

        private val packagesToChangeMI11T = arrayOf(
            // Add package names to change for Mi 11T here
        )

        private val packagesToChangeMI13P = arrayOf(
            // Add package names to change for Mi 13 Pro here
        )

        private val packagesToChangeF5 = arrayOf(
            // Add package names to change for F5 here
        )

        private val packagesToChangeK30U = arrayOf(
            // Add package names to change for K30 Ultra here
        )

        private val propMap = HashMap<String, MutableMap<String, String>>()

        init {
            propMap["pixel7pro"] = propsToChangePixel7Pro
            propMap["pixel5"] = propsToChangePixel5
            propMap["pixelxl"] = propsToChangePixelXL
            propMap["rog6"] = propsToChangeROG6
            propMap["xperia5"] = propsToChangeXP5
            propMap["op8pro"] = propsToChangeOP8P
            propMap["op9pro"] = propsToChangeOP9P
            propMap["mi11t"] = propsToChangeMI11T
            propMap["mi13pro"] = propsToChangeMI13P
            propMap["f5"] = propsToChangeF5
            propMap["k30u"] = propsToChangeK30U
        }

        fun fixProxyProperties(app: Application) {
            val deviceProp = getDeviceProp(app)
            if (deviceProp != null) {
                if (DEBUG) {
                    Log.d(TAG, "Device: ${deviceProp.deviceName}")
                    Log.d(TAG, "Codename: ${deviceProp.codename}")
                }

                if (shouldChangeDeviceProps(deviceProp.codename)) {
                    changeSystemProperties(deviceProp.codename)
                }
            }
        }

        private fun getDeviceProp(app: Application): DeviceProperties? {
            val model = Build.MODEL ?: return null
            val manufacturer = Build.MANUFACTURER ?: return null
            val deviceName = "$manufacturer $model"
            val codename = SystemProperties.get("ro.product.device")

            if (DEBUG) {
                Log.d(TAG, "getDeviceProp: $deviceName ($codename)")
            }

            return DeviceProperties(deviceName, codename)
        }

        private fun shouldChangeDeviceProps(codename: String?): Boolean {
            return codename != null && pixelCodenames.contains(codename)
        }

        private fun changeSystemProperties(codename: String) {
            val propsToChange = propMap[codename]
            if (propsToChange == null) {
                if (DEBUG) {
                    Log.d(TAG, "No properties to change for codename: $codename")
                }
                return
            }

            val sysPropsClass = SystemProperties::class.java
            val systemPropsField: Field
            try {
                systemPropsField = sysPropsClass.getDeclaredField("sSystemProperties")
                systemPropsField.isAccessible = true
                val systemProps = systemPropsField.get(null) as HashMap<*, *>

                for ((key, value) in propsToChange) {
                    if (value.isEmpty()) {
                        if (DEBUG) {
                            Log.d(TAG, "Clearing property: $key")
                        }
                        systemProps.remove(key)
                    } else {
                        if (DEBUG) {
                            Log.d(TAG, "Setting property: $key=$value")
                        }
                        systemProps[key] = value
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to change system properties", e)
            }
        }
    }

    private data class DeviceProperties(val deviceName: String, val codename: String?)
}
