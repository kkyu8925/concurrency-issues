package com.example.stock.facade

import com.example.stock.domain.StockWithVersion
import com.example.stock.repository.StockWithVersionRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class OptimisticLockStockFacadeTest@Autowired constructor(
    private val optimisticLockStockFacade: OptimisticLockStockFacade,
    private val stockRepository: StockWithVersionRepository,
) {
    @BeforeEach
    fun insert() {
        val stock = StockWithVersion(1L, 100L)
        stockRepository.saveAndFlush(stock)
    }

    @AfterEach
    fun delete() {
        stockRepository.deleteAll()
    }

    @Test
    fun `동시에 100명이 주문`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executorService.submit {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        val stock = stockRepository.findById(1L).orElseThrow()
        Assertions.assertEquals(0, stock.quantity)
    }
}
