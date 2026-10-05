package dev.guillermomartin.vaultkeeper.di

import dev.guillermomartin.vaultkeeper.presentation.viewmodel.CredentialListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { CredentialListViewModel(get()) }
}
