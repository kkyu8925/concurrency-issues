package com.example.stock.repository

import com.example.stock.domain.Stock
import com.example.stock.domain.StockWithVersion
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface StockRepository : JpaRepository<Stock, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id=:id")
    fun findByIdWithPessimisticLock(id: Long): Stock
}

interface StockWithVersionRepository : JpaRepository<StockWithVersion, Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from StockWithVersion s where s.id = :id")
    fun findByIdWithOptimisticLock(id: Long): StockWithVersion
}
