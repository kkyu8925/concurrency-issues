package com.example.stock.service

import com.example.stock.domain.Stock
import com.example.stock.repository.StockRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class StockServiceTest @Autowired constructor(
    private val stockService: StockService,
    private val stockRepository: StockRepository,
    private val pessimisticLockStockService: PessimisticLockStockService
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

    @Test
    fun `동시에 100명이 주문`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executorService.submit {
                try {
                    stockService.decrease(1L, 1L)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        val stock = stockRepository.findById(1L).orElseThrow()
        assertNotEquals(0, stock.quantity)
    }

    @Test
    fun `동시에 100명이 주문, Pessimistic Lock`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executorService.submit {
                try {
                    pessimisticLockStockService.decrease(1L, 1L)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        val stock = stockRepository.findById(1L).orElseThrow()
        assertEquals(0, stock.quantity)
    }
}
