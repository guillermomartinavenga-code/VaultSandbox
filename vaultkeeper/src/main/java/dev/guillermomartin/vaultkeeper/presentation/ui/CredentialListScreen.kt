package dev.guillermomartin.vaultkeeper.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.guillermomartin.vaultkeeper.domain.model.Credential
import org.koin.androidx.compose.koinViewModel
import dev.guillermomartin.vaultkeeper.presentation.viewmodel.CredentialListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialListScreen(viewModel: CredentialListViewModel = koinViewModel()) {
    val credentials by viewModel.credentials.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("VaultKeeper") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(credentials, key = { it.id }) { credential ->
                CredentialRow(credential)
            }
        }
    }
}

@Composable
private fun CredentialRow(credential: Credential) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = credential.site, style = MaterialTheme.typography.titleMedium)
            Text(text = credential.username, style = MaterialTheme.typography.bodyMedium)
            Text(text = "•".repeat(credential.password.length), style = MaterialTheme.typography.bodySmall)
        }
    }
}
