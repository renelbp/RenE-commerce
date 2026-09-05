package com.reneprojects.feature.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reneprojects.feature.products.interactor.ProductsInteractor
import com.reneprojects.feature.products.model.ProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
        interactor.observeProducts().onEach { products ->
            _uiState.update { currentState ->
                currentState.copy(products = products)
            }
        }.catch { error ->
            if (error is CancellationException) {
                throw error
            }
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = error.message ?: "Unable to get load Products"
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadProductData(forceRefresh: Boolean = false) {
        if (loadProductsJob?.isActive == true) {
            return
        }

        loadProductsJob = viewModelScope.launch {
            updateLoadingState(forceRefresh)

            try {
                interactor.loadProductData(
                    forceRefresh = forceRefresh,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                updateErrorState(
                    message = error.message
                        ?: getDefaultErrorMessage(forceRefresh),
                )
            } finally {
                finishLoading()
            }
        }
    }

    private fun getDefaultErrorMessage(
        forceRefresh: Boolean,
    ): String {
        return if (forceRefresh) {
            "No fue posible actualizar los productos."
        } else {
            "No fue posible cargar los productos. Revisa tu conexión."
        }
    }

    private fun updateErrorState(
        message: String,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = message,
            )
        }
    }

    private fun updateLoadingState(
        forceRefresh: Boolean,
    ) {
        _uiState.update { currentState ->
            currentState.copy(

                isLoading =
                    !forceRefresh &&
                            currentState.products.isEmpty(),
                isRefreshing = forceRefresh,
                errorMessage = null,
            )
        }
    }

    private fun finishLoading() {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                isRefreshing = false,
            )
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