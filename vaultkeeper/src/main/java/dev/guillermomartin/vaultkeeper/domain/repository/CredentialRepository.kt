package dev.guillermomartin.vaultkeeper.domain.repository

import dev.guillermomartin.vaultkeeper.domain.model.Credential
import kotlinx.coroutines.flow.Flow
import java.io.File

interface CredentialRepository {
    fun observeAll(): Flow<List<Credential>>
    suspend fun getAll(): List<Credential>
    suspend fun save(credential: Credential)
    fun getFaviconFile(site: String): File?
}
