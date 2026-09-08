package com.reneprojects.feature.products.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.feature.products.domain.interactor.ProductsInteractor
import com.reneprojects.feature.products.model.ProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal interface ProductViewModel {
    val uiState: StateFlow<ProductsUiState>
    fun refreshProducts()
    fun retry()
    fun dismissError()
}

@HiltViewModel
internal class ProductViewModelImpl @Inject constructor(
    private val interactor: ProductsInteractor,
) : ViewModel(), ProductViewModel {

    private val _uiState = MutableStateFlow(ProductsUiState())
    override val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()
    private var loadProductsJob: Job? = null

    init {
        observeProducts()
        loadProductData()
    }

    private fun observeProducts() {
        interactor.observeProductSections().onEach { sections ->
            _uiState.update { currentState ->
                currentState.copy(productCarousels = sections)
            }
        }.catch { error ->
            updateErrorState(error.message ?: "Unable to get load Products")
        }.launchIn(viewModelScope)
    }

    private fun loadProductData(forceRefresh: Boolean = false) {
        if (loadProductsJob?.isActive == true) {
            return
        }

        loadProductsJob = viewModelScope.launch {
            updateLoadingState()

            val result = interactor.loadProductData(forceRefresh = forceRefresh)

            if (result is RenEcommerceResult.Error) {
                updateErrorState(message = result.message)
            }
            finishLoading()
        }
    }

    private fun updateErrorState(
        message: String,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }

    private fun updateLoadingState() {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                errorMessage = null,
            )
        }
    }

    private fun finishLoading() {
        _uiState.update { currentState ->
            currentState.copy(isLoading = false)
        }
    }

    override fun refreshProducts() {
        loadProductData(true)
    }

    override fun retry() {
        loadProductData(forceRefresh = false)
    }

    override fun dismissError() {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = null,
            )
        }
    }

}