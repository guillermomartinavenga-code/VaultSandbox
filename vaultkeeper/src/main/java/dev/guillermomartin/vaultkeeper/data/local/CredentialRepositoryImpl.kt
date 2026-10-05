package dev.guillermomartin.vaultkeeper.data.local

import dev.guillermomartin.vaultkeeper.data.local.db.CredentialDao
import dev.guillermomartin.vaultkeeper.data.local.db.CredentialEntity
import dev.guillermomartin.vaultkeeper.domain.model.Credential
import dev.guillermomartin.vaultkeeper.domain.repository.CredentialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class CredentialRepositoryImpl(
    private val dao: CredentialDao,
    private val faviconStore: FaviconStore,
) : CredentialRepository {

    override fun observeAll(): Flow<List<Credential>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAll(): List<Credential> =
        dao.getAll().map { it.toDomain() }

    override suspend fun save(credential: Credential) {
        dao.insertAll(listOf(credential.toEntity()))
    }

    override fun getFaviconFile(site: String): File? =
        faviconStore.fileFor(site).takeIf { it.exists() }
}

internal fun CredentialEntity.toDomain() = Credential(id = id, site = site, username = username, password = password)
internal fun Credential.toEntity() = CredentialEntity(id = id, site = site, username = username, password = password)
