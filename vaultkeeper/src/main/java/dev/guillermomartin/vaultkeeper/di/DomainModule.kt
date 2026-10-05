package dev.guillermomartin.vaultkeeper.di

import dev.guillermomartin.vaultkeeper.domain.usecase.GetCredentialsUseCase
import dev.guillermomartin.vaultkeeper.domain.usecase.GetFaviconFileUseCase
import dev.guillermomartin.vaultkeeper.domain.usecase.SaveCredentialUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetCredentialsUseCase(get()) }
    factory { SaveCredentialUseCase(get()) }
    factory { GetFaviconFileUseCase(get()) }
}
