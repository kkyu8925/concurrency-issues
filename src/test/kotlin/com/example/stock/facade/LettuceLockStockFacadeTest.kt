package com.example.stock.facade

import com.example.stock.domain.Stock
import com.example.stock.repository.StockRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class LettuceLockStockFacadeTest @Autowired constructor(
    private val lettuceLockStockFacade: LettuceLockStockFacade,
    private val stockRepository: StockRepository
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
    fun `동시에 100개의요청`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)
        for (i in 0 until threadCount) {
            executorService.submit {
                try {
                    lettuceLockStockFacade.decrease(1L, 1L)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
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
