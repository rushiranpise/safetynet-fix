package dev.kdrag0n.safetynetfix.proxy

import android.os.Build
import dev.kdrag0n.safetynetfix.SecurityHooks
import dev.kdrag0n.safetynetfix.logDebug
import java.security.Provider
import kotlin.concurrent.thread

// This is mostly just a pass-through provider that exists to change the provider's ClassLoader.
// This works because Service looks up the class by name from the *provider* ClassLoader, not
// necessarily the bootstrap one.
class ProxyProvider(
    orig: Provider,
) : Provider(orig.name, orig.version, orig.info) {
    init {
        logDebug("Init proxy provider - wrapping $orig")

        putAll(orig)
        this["KeyStore.${SecurityHooks.PROVIDER_NAME}"] = ProxyKeyStoreSpi::class.java.name
    }

    override fun getService(type: String?, algorithm: String?): Service? {
        logDebug("Provider: get service - type=$type algorithm=$algorithm")
        val host = Build.HOST
        if (type == "KeyStore" && host != "xiaomi.eu") {

            val origProduct = Build.PRODUCT
            val patchedProduct = "walleye"

            val origDevice = Build.DEVICE
            val patchedDevice = "walleye"

            val origModel = Build.MODEL
            val patchedModel = "Pixel 2"

            val origBRAND = Build.BRAND
            val patchedBRAND = "google"

            val origMANUFACTURER = Build.MANUFACTURER
            val patchedMANUFACTURER = "Google"

            val origFingerprint = Build.FINGERPRINT
            val patchedFingerprint = "google/walleye/walleye:8.1.0/OPM1.171019.011/4448085:user/release-keys"

            // val origDEVICE_INITIAL_SDK_INT = Build.VERSION.DEVICE_INITIAL_SDK_INT
            // val patchedDEVICE_INITIAL_SDK_INT = "25"

            logDebug("Patch PRODUCT for KeyStore $origProduct -> $patchedProduct")
            Build::class.java.getDeclaredField("PRODUCT").let { field ->
                field.isAccessible = true
                field.set(null, patchedProduct)
            }
            logDebug("Patch DEVICE for KeyStore $origDevice -> $patchedDevice")
            Build::class.java.getDeclaredField("DEVICE").let { field ->
                field.isAccessible = true
                field.set(null, patchedDevice)
            }
            logDebug("Patch MODEL for KeyStore $origModel -> $patchedModel")
            Build::class.java.getDeclaredField("MODEL").let { field ->
                field.isAccessible = true
                field.set(null, patchedModel)
            }
            logDebug("Patch BRAND for KeyStore $origBRAND -> $patchedBRAND")
            Build::class.java.getDeclaredField("BRAND").let { field ->
                field.isAccessible = true
                field.set(null, patchedBRAND)
            }
            logDebug("Patch MANUFACTURER for KeyStore $origMANUFACTURER -> $patchedMANUFACTURER")
            Build::class.java.getDeclaredField("MANUFACTURER").let { field ->
                field.isAccessible = true
                field.set(null, patchedMANUFACTURER)
            }
            logDebug("Patch FINGERPRINT for KeyStore $origFingerprint -> $patchedFingerprint")
            Build::class.java.getDeclaredField("FINGERPRINT").let { field ->
                field.isAccessible = true
                field.set(null, patchedFingerprint)
            }
            // logDebug("Patch DEVICE_INITIAL_SDK_INT for KeyStore $origDEVICE_INITIAL_SDK_INT -> $patchedDEVICE_INITIAL_SDK_INT")
            // Build::class.java.getDeclaredField("DEVICE_INITIAL_SDK_INT").let { field ->
            //     field.isAccessible = true
            //     field.set(null, patchedDEVICE_INITIAL_SDK_INT)
            // }
        }
        return super.getService(type, algorithm)
    }

    override fun getServices(): MutableSet<Service>? {
        logDebug("Get services")
        return super.getServices()
    }
}
