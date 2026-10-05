package dev.guillermomartin.vaultkeeper.domain.model

data class Credential(
    val id: Long = 0,
    val site: String,
    val username: String,
    val password: String,
)
