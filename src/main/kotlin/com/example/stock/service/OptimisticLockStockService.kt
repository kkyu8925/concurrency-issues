package com.example.stock.service

import com.example.stock.repository.StockWithVersionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OptimisticLockStockService(
    private val stockWithVersionRepository: StockWithVersionRepository
) {
    @Transactional
    fun decrease(id: Long, quantity: Long) {
        val stock = stockWithVersionRepository.findByIdWithOptimisticLock(id)
        stock.decrease(quantity)
        stockWithVersionRepository.saveAndFlush(stock)
    }
}
