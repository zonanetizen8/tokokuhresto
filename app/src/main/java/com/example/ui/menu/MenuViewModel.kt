package com.example.ui.menu

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.MenuDao
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.MenuEntity
import com.example.data.util.ImageStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuViewModel(
    private val menuDao: MenuDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    val filteredMenus: StateFlow<List<MenuEntity>> = combine(
        menuDao.getAllMenus(),
        _searchQuery,
        _selectedCategoryId
    ) { menus, query, categoryId ->
        menus.filter { menu ->
            val matchesQuery = query.isBlank() ||
                    menu.name.contains(query, ignoreCase = true) ||
                    menu.code.contains(query, ignoreCase = true)
            val matchesCategory = categoryId == null || menu.categoryId == categoryId
            matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun toggleAvailability(menuId: Long, current: Boolean) {
        viewModelScope.launch {
            menuDao.updateAvailability(menuId, !current)
        }
    }

    fun saveMenu(
        id: Long = 0,
        code: String,
        name: String,
        description: String,
        categoryId: Long?,
        buyPrice: Long,
        sellPrice: Long,
        stock: Double,
        unit: String,
        isAvailable: Boolean,
        isFavorite: Boolean,
        imagePath: String?
    ) {
        viewModelScope.launch {
            val autoCode = if (code.isBlank()) {
                val count = menuDao.getMenuCount() + 1
                "MNU-%03d".format(count)
            } else code

            val entity = MenuEntity(
                id = id,
                code = autoCode,
                name = name.trim(),
                description = description.trim(),
                categoryId = categoryId,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                stock = stock,
                unit = unit.ifBlank { "Porsi" },
                isAvailable = isAvailable,
                isFavorite = isFavorite,
                imagePath = imagePath
            )

            if (id == 0L) {
                menuDao.insertMenu(entity)
            } else {
                menuDao.updateMenu(entity)
            }
        }
    }

    fun deleteMenu(menu: MenuEntity) {
        viewModelScope.launch {
            menuDao.deleteMenu(menu.id)
            menu.imagePath?.let { path ->
                ImageStorageManager.deleteImageFile(path)
            }
        }
    }

    suspend fun saveImage(context: Context, uri: Uri): String? {
        return ImageStorageManager.saveImageFromUri(context, uri)
    }

    class Factory(
        private val menuDao: MenuDao,
        private val categoryDao: CategoryDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MenuViewModel(menuDao, categoryDao) as T
        }
    }
}
