package dev.guillermomartin.vaultkeeper.di

import androidx.room.Room
import dev.guillermomartin.vaultkeeper.data.local.CredentialRepositoryImpl
import dev.guillermomartin.vaultkeeper.data.local.DemoDataSeeder
import dev.guillermomartin.vaultkeeper.data.local.FaviconStore
import dev.guillermomartin.vaultkeeper.data.local.db.VaultDatabase
import dev.guillermomartin.vaultkeeper.domain.repository.CredentialRepository
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(get(), VaultDatabase::class.java, VaultDatabase.DATABASE_NAME)
            .build()
    }
    single { get<VaultDatabase>().credentialDao() }
    single { FaviconStore(get()) }
    single<CredentialRepository> { CredentialRepositoryImpl(get(), get()) }
    single { DemoDataSeeder(get(), get()) }
}
