package com.example.exequiel_gutierrez.restaurant

class ItemMesa(
    val itemMenu: ItemMenu,
    var cantidad: Int
) {
    fun calcularSubtotal(): Int {
        return itemMenu.precio * cantidad
    }
}