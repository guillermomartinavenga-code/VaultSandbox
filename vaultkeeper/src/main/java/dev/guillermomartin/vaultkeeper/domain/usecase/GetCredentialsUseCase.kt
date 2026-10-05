package dev.guillermomartin.vaultkeeper.domain.usecase

import dev.guillermomartin.vaultkeeper.domain.model.Credential
import dev.guillermomartin.vaultkeeper.domain.repository.CredentialRepository
import kotlinx.coroutines.flow.Flow

class GetCredentialsUseCase(private val repository: CredentialRepository) {
    operator fun invoke(): Flow<List<Credential>> = repository.observeAll()
}
