package dev.guillermomartin.vaultkeeper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.guillermomartin.vaultkeeper.domain.model.Credential
import dev.guillermomartin.vaultkeeper.domain.usecase.GetCredentialsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CredentialListViewModel(
    getCredentials: GetCredentialsUseCase,
) : ViewModel() {

    val credentials: StateFlow<List<Credential>> = getCredentials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
