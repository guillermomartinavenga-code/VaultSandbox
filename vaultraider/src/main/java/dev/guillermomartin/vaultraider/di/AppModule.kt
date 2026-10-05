package dev.guillermomartin.vaultraider.di

import dev.guillermomartin.vaultraider.client.VaultKeeperClient
import dev.guillermomartin.vaultraider.presentation.AttackViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { VaultKeeperClient(get()) }
    viewModel { AttackViewModel(get()) }
}
