package dev.guillermomartin.vaultkeeper.domain.usecase

import dev.guillermomartin.vaultkeeper.domain.repository.CredentialRepository
import java.io.File

class GetFaviconFileUseCase(private val repository: CredentialRepository) {
    operator fun invoke(site: String): File? = repository.getFaviconFile(site)
}
