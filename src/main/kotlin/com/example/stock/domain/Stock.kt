package com.example.stock.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Stock(
    val productId: Long,

    var quantity: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    fun decrease(quantity: Long) {
        if (this.quantity - quantity < 0) {
            throw RuntimeException("foo")
        }
        this.quantity -= quantity
    }
}
