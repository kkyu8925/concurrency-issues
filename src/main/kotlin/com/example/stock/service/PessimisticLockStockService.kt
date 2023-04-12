package com.example.stock.service

import com.example.stock.repository.StockRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PessimisticLockStockService(
    private val stockRepository: StockRepository
) {
    @Transactional
    fun decrease(id: Long, quantity: Long): Long {
        val stock = stockRepository.findByIdWithPessimisticLock(id)
        stock.decrease(quantity)
        stockRepository.saveAndFlush(stock)
        return stock.quantity
    }
}
