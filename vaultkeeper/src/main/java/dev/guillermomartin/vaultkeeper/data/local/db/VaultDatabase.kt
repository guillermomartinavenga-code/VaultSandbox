package dev.guillermomartin.vaultkeeper.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CredentialEntity::class], version = 1, exportSchema = true)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun credentialDao(): CredentialDao

    companion object {
        const val DATABASE_NAME = "vault.db"
    }
}
