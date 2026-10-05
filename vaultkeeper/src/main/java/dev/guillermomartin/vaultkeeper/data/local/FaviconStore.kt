package dev.guillermomartin.vaultkeeper.data.local

import android.content.Context
import java.io.File

/**
 * Caches one icon per site in the app's internal storage (files/favicons/<site>.png).
 * This is the real functional reason VaultContentProvider needs to expose openFile(): without
 * it, there'd be no legitimate "open a file" vector on the provider at all.
 */
class FaviconStore(private val context: Context) {

    val faviconsDir: File
        get() = File(context.filesDir, "favicons").apply { mkdirs() }

    fun fileFor(site: String): File = File(faviconsDir, "$site.png")

    fun ensureSeeded(sites: List<String>) {
        for (site in sites) {
            val file = fileFor(site)
            if (!file.exists()) {
                file.writeBytes(PLACEHOLDER_PNG_1X1)
            }
        }
    }

    companion object {
        // 1x1 transparent PNG — placeholder content, just so a real file exists for the
        // ContentProvider to serve via openFile().
        private val PLACEHOLDER_PNG_1X1 = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, 0xC4.toByte(), 0x89.toByte(),
            0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,
            0x78, 0x9C.toByte(), 0x63, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x01,
            0x0D, 0x0A, 0x2D, 0xB4.toByte(),
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE.toByte(), 0x42, 0x60, 0x82.toByte(),
        )
    }
}
