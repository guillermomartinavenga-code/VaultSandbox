package dev.guillermomartin.vaultkeeper.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credentials")
data class CredentialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val site: String,
    val username: String,
    val password: String,
)
