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
 * Exported (`exported="true"`) because the narrative is that a hypothetical Chrome extension
 * (via a companion app, never implemented in this repo — see docs/practical-evidence.md) would
 * query it to autofill credentials. `query()`'s SQL injection (vuln #1) was fixed in v0.3 by
 * validating `selection`/`sortOrder`/`projection` against an allowlist and binding values instead
 * of concatenating them. `openFile()`'s path traversal (vuln #2) was fixed in v0.5 by resolving
 * the requested file's canonical path and rejecting anything outside `faviconsDir`.
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
                // Fixed in v0.3 (was vuln #1 in v0.1/v0.2): `selection` is restricted to the
                // single shape `<allowlisted column> = ?`, with the value always bound through
                // selectionArgs — never spliced into the SQL text. `sortOrder` and `projection`
                // are validated against the same column allowlist instead of being trusted as-is.
                val columns = validateProjection(projection)
                val whereClause = validateSelection(selection, selectionArgs)
                val orderClause = validateSortOrder(sortOrder)

                val sql = buildString {
                    append("SELECT ").append(columns.joinToString(", "))
                    append(" FROM credentials")
                    if (whereClause != null) append(" WHERE $whereClause")
                    if (orderClause != null) append(" ORDER BY $orderClause")
                }
                val bindArgs: Array<Any?> = if (whereClause != null) arrayOf(selectionArgs!![0]) else emptyArray()
                dao.rawQuery(SimpleSQLiteQuery(sql, bindArgs))
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        return when (MATCHER.match(uri)) {
            FAVICON -> {
                // Fixed in v0.5 (was vuln #2 in v0.1/v0.4): the filename is still taken from the
                // URI's last path segment — including whatever `..`/`/` a %2F-encoded payload
                // decodes to — but the resulting file's canonical path is now resolved and
                // checked against the favicons directory's own canonical path before opening it.
                val filename = uri.lastPathSegment
                    ?: throw IllegalArgumentException("Missing filename in $uri")
                val file = resolveFaviconFile(faviconStore.faviconsDir, filename)
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

        private val ALLOWED_COLUMNS = listOf("id", "site", "username", "password")
        private val SELECTION_PATTERN = Regex("^(id|site|username|password)\\s*=\\s*\\?$")
        private val SORT_ORDER_PATTERN =
            Regex("^(id|site|username|password)(\\s+(ASC|DESC))?$", RegexOption.IGNORE_CASE)

        private fun validateProjection(projection: Array<out String>?): List<String> {
            if (projection.isNullOrEmpty()) return ALLOWED_COLUMNS
            val unknown = projection.filterNot { it in ALLOWED_COLUMNS }
            require(unknown.isEmpty()) { "Unknown column(s) in projection: $unknown" }
            return projection.toList()
        }

        private fun validateSelection(selection: String?, selectionArgs: Array<out String>?): String? {
            if (selection.isNullOrBlank()) return null
            val trimmed = selection.trim()
            require(SELECTION_PATTERN.matches(trimmed)) {
                "Unsupported selection — only '<column> = ?' against an allowlisted column is accepted: $selection"
            }
            require(selectionArgs?.size == 1) { "selection requires exactly one bind argument in selectionArgs" }
            return trimmed
        }

        private fun validateSortOrder(sortOrder: String?): String? {
            if (sortOrder.isNullOrBlank()) return null
            val trimmed = sortOrder.trim()
            require(SORT_ORDER_PATTERN.matches(trimmed)) {
                "Unsupported sortOrder — only an allowlisted column optionally followed by ASC/DESC is accepted: $sortOrder"
            }
            return trimmed
        }

        private fun resolveFaviconFile(faviconsDir: File, filename: String): File {
            val faviconsRoot = faviconsDir.canonicalFile
            val resolved = File(faviconsDir, filename).canonicalFile
            require(resolved == faviconsRoot || resolved.path.startsWith(faviconsRoot.path + File.separator)) {
                "Resolved path escapes the favicons directory: $filename"
            }
            return resolved
        }
    }
}
