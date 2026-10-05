package dev.guillermomartin.vaultkeeper.data.local

import dev.guillermomartin.vaultkeeper.data.local.db.CredentialDao
import dev.guillermomartin.vaultkeeper.data.local.db.CredentialEntity

/**
 * Seeds fictional credentials the first time the app starts. Reserved domains
 * (.test/.example, RFC 2606) and obviously made-up passwords — no real data lives in this
 * public repo.
 */
class DemoDataSeeder(
    private val dao: CredentialDao,
    private val faviconStore: FaviconStore,
) {
    suspend fun ensureSeeded() {
        if (dao.getAll().isNotEmpty()) return
        val seed = listOf(
            Triple("example.test", "demo@example.test", "Tr0ub4dor&3-fake"),
            Triple("mail.example", "demo.mail@example.test", "Hunter2-fake"),
            Triple("shop.example", "demo.shop@example.test", "Cassette-Battery-Staple-fake"),
        )
        dao.insertAll(seed.map { (site, username, password) ->
            CredentialEntity(site = site, username = username, password = password)
        })
        faviconStore.ensureSeeded(seed.map { it.first })
    }
}
