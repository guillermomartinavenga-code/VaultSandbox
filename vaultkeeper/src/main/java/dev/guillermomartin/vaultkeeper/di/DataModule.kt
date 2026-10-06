package dev.guillermomartin.vaultkeeper.di

import androidx.room.Room
import dev.guillermomartin.vaultkeeper.data.local.CredentialRepositoryImpl
import dev.guillermomartin.vaultkeeper.data.local.DemoDataSeeder
import dev.guillermomartin.vaultkeeper.data.local.FaviconStore
import dev.guillermomartin.vaultkeeper.data.local.db.VaultDatabase
import dev.guillermomartin.vaultkeeper.data.local.db.VaultPassphraseProvider
import dev.guillermomartin.vaultkeeper.domain.repository.CredentialRepository
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.koin.dsl.module

val dataModule = module {
    single { VaultPassphraseProvider(get()) }
    single {
        val passphrase = get<VaultPassphraseProvider>().getOrCreatePassphrase()
        Room.databaseBuilder(get(), VaultDatabase::class.java, VaultDatabase.DATABASE_NAME)
            .openHelperFactory(SupportOpenHelperFactory(passphrase))
            .build()
    }
    single { get<VaultDatabase>().credentialDao() }
    single { FaviconStore(get()) }
    single<CredentialRepository> { CredentialRepositoryImpl(get(), get()) }
    single { DemoDataSeeder(get(), get()) }
}
