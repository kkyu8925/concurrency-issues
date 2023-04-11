package com.example.stock.service

import com.example.stock.domain.Stock
import com.example.stock.repository.StockRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StockServiceTest @Autowired constructor(
    private val stockService: StockService, private val stockRepository: StockRepository
) {
    @BeforeEach
    fun insert() {
        val stock = Stock(1L, 100L)
        stockRepository.saveAndFlush(stock)
    }

    @AfterEach
    fun delete() {
        stockRepository.deleteAll()
    }

    @Test
    fun decrease_test() {
        stockService.decrease(1L, 1L)
        val stock = stockRepository.findById(1L).orElseThrow()
        assertEquals(99, stock.quantity)
    }
}
