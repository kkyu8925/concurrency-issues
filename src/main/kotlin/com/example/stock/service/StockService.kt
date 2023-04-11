package com.example.stock.service

import com.example.stock.repository.StockRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockRepository: StockRepository
) {
    @Transactional
    fun decrease(id: Long, quantity: Long) {
        val stock = stockRepository.findById(id).orElseThrow()
        stock.decrease(quantity)
        stockRepository.saveAndFlush(stock)
    }
}
