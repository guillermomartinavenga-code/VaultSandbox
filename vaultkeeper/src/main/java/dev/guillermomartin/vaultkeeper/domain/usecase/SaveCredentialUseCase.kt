package dev.guillermomartin.vaultkeeper.domain.usecase

import dev.guillermomartin.vaultkeeper.domain.model.Credential
import dev.guillermomartin.vaultkeeper.domain.repository.CredentialRepository

class SaveCredentialUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke(credential: Credential) = repository.save(credential)
}
