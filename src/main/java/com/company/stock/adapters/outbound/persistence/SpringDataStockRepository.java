package com.company.stock.adapters.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataStockRepository extends JpaRepository<StockEntity, UUID> {
    Optional<StockEntity> findBySku(String sku);
    boolean existsBySku(String sku);
}
