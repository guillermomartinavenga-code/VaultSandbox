package dev.guillermomartin.vaultraider.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttackScreen(viewModel: AttackViewModel = koinViewModel()) {
    var selectionInput by remember { mutableStateOf("") }
    var selectionArgsInput by remember { mutableStateOf("") }
    var faviconInput by remember { mutableStateOf("example.test.png") }
    val log by viewModel.log.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("VaultRaider") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("content://dev.guillermomartin.vaultkeeper.provider/credentials", style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(
                value = selectionInput,
                onValueChange = { selectionInput = it },
                label = { Text("selection (WHERE)") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = selectionArgsInput,
                onValueChange = { selectionArgsInput = it },
                label = { Text("selectionArgs (comma-separated)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = { viewModel.queryCredentials(selectionInput, selectionArgsInput) }) {
                Text("Query credentials")
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = faviconInput,
                    onValueChange = { faviconInput = it },
                    label = { Text("favicon filename") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Button(onClick = { viewModel.readFavicon(faviconInput) }) {
                Text("Read favicon file")
            }

            Text(
                text = log,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
