package dev.guillermomartin.vaultkeeper.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.sqlite.db.SimpleSQLiteQuery
import dev.guillermomartin.vaultkeeper.data.local.FaviconStore
import dev.guillermomartin.vaultkeeper.data.local.db.CredentialDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * v0.1 — deliberately naive starting state. Exported (`exported="true"`) because the narrative
 * is that a hypothetical Chrome extension (via a companion app, never implemented in this repo —
 * see docs/practical-evidence.md) would query it to autofill credentials. Neither query() nor
 * openFile() are hardened yet: that's exactly what the next stages (v0.2–v0.5) exploit and fix.
 */
class VaultContentProvider : ContentProvider(), KoinComponent {

    private val dao: CredentialDao by inject()
    private val faviconStore: FaviconStore by inject()

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        return when (MATCHER.match(uri)) {
            CREDENTIALS -> {
                // Deliberately vulnerable (vuln #1, v0.1): concatenates `selection`/`sortOrder`
                // straight into the SQL and ignores `selectionArgs` entirely — no value ever
                // reaches the database parametrized. Fixed in v0.3.
                val sql = buildString {
                    append("SELECT * FROM credentials")
                    if (!selection.isNullOrBlank()) append(" WHERE $selection")
                    if (!sortOrder.isNullOrBlank()) append(" ORDER BY $sortOrder")
                }
                dao.rawQuery(SimpleSQLiteQuery(sql))
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        return when (MATCHER.match(uri)) {
            FAVICON -> {
                // Deliberately vulnerable (vuln #2, v0.1): the filename comes straight from the
                // last path segment of the URI without validating the resulting canonical path
                // against the allowed favicons directory. Fixed in v0.5.
                val filename = uri.lastPathSegment
                    ?: throw IllegalArgumentException("Missing filename in $uri")
                val file = File(faviconStore.faviconsDir, filename)
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? = when (MATCHER.match(uri)) {
        CREDENTIALS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.credentials"
        FAVICON -> "image/png"
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0

    companion object {
        const val AUTHORITY = "dev.guillermomartin.vaultkeeper.provider"

        private const val CREDENTIALS = 1
        private const val FAVICON = 2

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "credentials", CREDENTIALS)
            addURI(AUTHORITY, "favicons/*", FAVICON)
        }

        fun credentialsUri(): Uri = Uri.parse("content://$AUTHORITY/credentials")
        fun faviconUri(filename: String): Uri = Uri.parse("content://$AUTHORITY/favicons/$filename")
    }
}
