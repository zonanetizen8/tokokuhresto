package com.example.ui.cashier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.MenuDao
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.MenuEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderType
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PaymentStatus
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.data.local.entities.TableEntity
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CartItem(
    val menu: MenuEntity,
    val quantity: Int = 1,
    val note: String = "",
    val discount: Long = 0L
) {
    val subtotal: Long
        get() = (menu.sellPrice * quantity) - discount
}

data class CashierUiState(
    val cartItems: List<CartItem> = emptyList(),
    val orderType: OrderType = OrderType.DINE_IN,
    val selectedTable: TableEntity? = null,
    val customerName: String = "Pelanggan",
    val customerPhone: String = "",
    val generalDiscountPercent: Double = 0.0,
    val note: String = "",
    val isCheckingOut: Boolean = false,
    val lastCompletedOrder: OrderEntity? = null,
    val lastCompletedItems: List<OrderItemEntity> = emptyList(),
    val showReceiptDialog: Boolean = false,
    val errorMessage: String? = null
)

class CashierViewModel(
    private val menuDao: MenuDao,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = restaurantRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tables: StateFlow<List<TableEntity>> = restaurantRepository.getAllTables()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<RestaurantProfileEntity?> = restaurantRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    val availableMenus: StateFlow<List<MenuEntity>> = combine(
        menuDao.getAvailableMenus(),
        _searchQuery,
        _selectedCategoryId
    ) { menus, query, catId ->
        menus.filter { m ->
            val matchQuery = query.isBlank() || m.name.contains(query, ignoreCase = true) || m.code.contains(query, ignoreCase = true)
            val matchCat = catId == null || m.categoryId == catId
            matchQuery && matchCat
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CashierUiState())
    val uiState: StateFlow<CashierUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun onCategorySelect(catId: Long?) { _selectedCategoryId.value = catId }

    fun addToCart(menu: MenuEntity) {
        val currentList = _uiState.value.cartItems.toMutableList()
        val index = currentList.indexOfFirst { it.menu.id == menu.id }
        if (index >= 0) {
            val item = currentList[index]
            currentList[index] = item.copy(quantity = item.quantity + 1)
        } else {
            currentList.add(CartItem(menu = menu, quantity = 1))
        }
        _uiState.value = _uiState.value.copy(cartItems = currentList)
    }

    fun incrementQty(menuId: Long) {
        val currentList = _uiState.value.cartItems.toMutableList()
        val index = currentList.indexOfFirst { it.menu.id == menuId }
        if (index >= 0) {
            val item = currentList[index]
            currentList[index] = item.copy(quantity = item.quantity + 1)
            _uiState.value = _uiState.value.copy(cartItems = currentList)
        }
    }

    fun decrementQty(menuId: Long) {
        val currentList = _uiState.value.cartItems.toMutableList()
        val index = currentList.indexOfFirst { it.menu.id == menuId }
        if (index >= 0) {
            val item = currentList[index]
            if (item.quantity > 1) {
                currentList[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentList.removeAt(index)
            }
            _uiState.value = _uiState.value.copy(cartItems = currentList)
        }
    }

    fun removeFromCart(menuId: Long) {
        val currentList = _uiState.value.cartItems.filterNot { it.menu.id == menuId }
        _uiState.value = _uiState.value.copy(cartItems = currentList)
    }

    fun setOrderType(type: OrderType) {
        _uiState.value = _uiState.value.copy(orderType = type)
    }

    fun setSelectedTable(table: TableEntity?) {
        _uiState.value = _uiState.value.copy(selectedTable = table)
    }

    fun setCustomerName(name: String) {
        _uiState.value = _uiState.value.copy(customerName = name)
    }

    fun clearCart() {
        _uiState.value = _uiState.value.copy(cartItems = emptyList())
    }

    fun dismissReceipt() {
        _uiState.value = _uiState.value.copy(
            showReceiptDialog = false,
            lastCompletedOrder = null,
            lastCompletedItems = emptyList(),
            cartItems = emptyList()
        )
    }

    fun checkout(
        paymentMethod: PaymentMethod,
        paidAmount: Long
    ) {
        val state = _uiState.value
        if (state.cartItems.isEmpty()) return

        val prof = profile.value
        val taxRate = (prof?.defaultTaxPercent ?: 10.0) / 100.0
        val serviceRate = (prof?.defaultServicePercent ?: 5.0) / 100.0

        val subtotal = state.cartItems.sumOf { it.subtotal }
        val discountAmount = (subtotal * (state.generalDiscountPercent / 100.0)).toLong()
        val afterDiscount = subtotal - discountAmount
        val taxAmount = (afterDiscount * taxRate).toLong()
        val serviceAmount = (afterDiscount * serviceRate).toLong()
        val totalAmount = afterDiscount + taxAmount + serviceAmount

        val actualPaid = if (paidAmount < totalAmount && paymentMethod == PaymentMethod.CASH) totalAmount else paidAmount
        val changeAmount = if (actualPaid >= totalAmount) actualPaid - totalAmount else 0L
        val payStatus = if (actualPaid >= totalAmount) PaymentStatus.PAID else PaymentStatus.PARTIAL

        val timeSdf = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val orderNumber = "ORD-${timeSdf.format(Date())}"

        val order = OrderEntity(
            orderNumber = orderNumber,
            orderType = state.orderType,
            tableId = state.selectedTable?.id,
            tableName = state.selectedTable?.tableNumber,
            customerName = state.customerName.ifBlank { "Pelanggan" },
            cashierName = "Kasir Resto",
            status = OrderStatus.PENDING,
            subtotal = subtotal,
            discountAmount = discountAmount,
            discountPercent = state.generalDiscountPercent,
            taxAmount = taxAmount,
            taxPercent = prof?.defaultTaxPercent ?: 10.0,
            serviceAmount = serviceAmount,
            servicePercent = prof?.defaultServicePercent ?: 5.0,
            totalAmount = totalAmount,
            paidAmount = actualPaid,
            changeAmount = changeAmount,
            paymentStatus = payStatus,
            paymentMethod = paymentMethod,
            note = state.note,
            completedAt = System.currentTimeMillis()
        )

        val items = state.cartItems.map { ci ->
            OrderItemEntity(
                orderId = 0L,
                menuId = ci.menu.id,
                menuName = ci.menu.name,
                quantity = ci.quantity,
                unitPrice = ci.menu.sellPrice,
                buyPrice = ci.menu.buyPrice,
                discount = ci.discount,
                subtotal = ci.subtotal,
                note = ci.note
            )
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isCheckingOut = true, errorMessage = null)
            try {
                val orderId = restaurantRepository.checkoutTransaction(order, items)
                val savedOrder = order.copy(id = orderId)
                _uiState.value = _uiState.value.copy(
                    isCheckingOut = false,
                    lastCompletedOrder = savedOrder,
                    lastCompletedItems = items,
                    showReceiptDialog = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCheckingOut = false,
                    errorMessage = "Gagal memproses transaksi: ${e.message}"
                )
            }
        }
    }

    class Factory(
        private val menuDao: MenuDao,
        private val restaurantRepository: RestaurantRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CashierViewModel(menuDao, restaurantRepository) as T
        }
    }
}
