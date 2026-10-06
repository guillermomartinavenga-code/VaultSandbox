package dev.guillermomartin.vaultraider.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.guillermomartin.vaultraider.client.AttackResult
import dev.guillermomartin.vaultraider.client.VaultKeeperClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AttackViewModel(private val client: VaultKeeperClient) : ViewModel() {

    private val _log = MutableStateFlow("Ready. Enter a `selection` and tap \"Query credentials\".")
    val log: StateFlow<String> = _log.asStateFlow()

    fun queryCredentials(selection: String, selectionArgsCsv: String) {
        viewModelScope.launch {
            val args = selectionArgsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val result = withContext(Dispatchers.IO) {
                client.queryCredentials(
                    selection.ifBlank { null },
                    args.takeIf { it.isNotEmpty() }?.toTypedArray(),
                )
            }
            _log.value = result.render()
        }
    }

    fun readFavicon(filename: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { client.readFavicon(filename) }
            _log.value = result.render()
        }
    }

    private fun AttackResult.render(): String = when (this) {
        is AttackResult.Success -> if (rows.isEmpty()) {
            "0 rows."
        } else {
            rows.joinToString("\n") { row -> row.entries.joinToString(" | ") { "${it.key}=${it.value}" } }
        }
        is AttackResult.RawBytes -> "${bytes.size} bytes read:\n${bytes.toPreviewString()}"
        is AttackResult.Failure -> "Error: $reason"
    }

    private fun ByteArray.toPreviewString(): String =
        take(400).joinToString("") { b -> if (b in 32..126) b.toInt().toChar().toString() else "." }
}
