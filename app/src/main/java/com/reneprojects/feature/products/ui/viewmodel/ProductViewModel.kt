package com.reneprojects.feature.products.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.feature.products.domain.interactor.ProductsInteractor
import com.reneprojects.feature.products.model.LandingUiState
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
    val uiState: StateFlow<LandingUiState>
    fun refreshProducts()
    fun retry()
    fun dismissError()

    /**
     * Applies a single-selection category filter, toggling it off when the same chip is tapped again.
     *
     * @param slug Category slug tapped in the filter row.
     */
    fun onFilterSelected(slug: String?)
}

@HiltViewModel
internal class ProductViewModelImpl @Inject constructor(
    private val interactor: ProductsInteractor,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ProductViewModel {

    private companion object Keys {
        private const val SELECTED_CATEGORY = "selected_category"
    }

    private val _uiState = MutableStateFlow(LandingUiState())
    override val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()
    private var loadProductsJob: Job? = null

    private val selectedCategory = savedStateHandle.getStateFlow<String?>(
        key = SELECTED_CATEGORY,
        initialValue = null
    )

    init {
        observeProducts()
        observeSelectedCategory()
        loadProductData()
    }

    private fun observeSelectedCategory() {
        selectedCategory.onEach { slug ->
            _uiState.update { currentState ->
                currentState.copy(selectedCategory = slug)
            }
        }.launchIn(viewModelScope)
    }

    private fun observeProducts() {
        interactor.observeProductSections().onEach { data ->
            _uiState.update { currentState ->
                currentState.copy(landingPageData = data)
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
                updateErrorState(message = "")//TODO GET CUSTOM ERROR DATA
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

    override fun onFilterSelected(slug: String?) {
        val newSelection = if (selectedCategory.value == slug) null else slug
        savedStateHandle[SELECTED_CATEGORY] = newSelection
    }

}