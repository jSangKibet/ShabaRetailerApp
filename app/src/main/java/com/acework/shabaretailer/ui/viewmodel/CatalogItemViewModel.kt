package com.acework.shabaretailer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.acework.shabaretailer.model.Item
import com.acework.shabaretailer.atlas.items
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CatalogItemViewModel(private val sku: String) : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogItemUiState())
    val uiState: StateFlow<CatalogItemUiState> = _uiState.asStateFlow()

    init {
        loadItem()
    }

    private fun loadItem() {
        items[sku]?.let {
            _uiState.update { state -> state.copy(item = it) }
        }
    }

    fun showBasic() {
        _uiState.update { state -> state.copy(showingBasic = true) }
    }

    fun showMore() {
        _uiState.update { state -> state.copy(showingBasic = false) }
    }
}

data class CatalogItemUiState(
    val item: Item = Item(),
    val showingBasic: Boolean = true,
)

class CatalogItemViewModelFactory(sku: String) {
    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return CatalogItemViewModel(sku) as T
        }
    }
}