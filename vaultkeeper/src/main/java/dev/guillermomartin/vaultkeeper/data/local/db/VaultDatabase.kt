package dev.guillermomartin.vaultkeeper.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CredentialEntity::class], version = 1, exportSchema = true)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun credentialDao(): CredentialDao

    companion object {
        const val DATABASE_NAME = "vault.db"

        init {
            // Required once before any SQLCipher-backed SQLiteDatabase is opened (v0.7 fix for
            // vuln #3) — the native core isn't loaded automatically like the stock Android driver.
            System.loadLibrary("sqlcipher")
        }
    }
}
