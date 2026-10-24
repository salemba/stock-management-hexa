package com.company.stock.adapters.outbound.persistence;

import com.company.stock.domain.model.StockItem;
import com.company.stock.ports.outbound.StockRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StockPersistenceAdapter implements StockRepositoryPort {

    private final SpringDataStockRepository repository;

    public StockPersistenceAdapter(SpringDataStockRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StockItem> findAll() {
        return List.of();
    }

    @Override
    public Optional<StockItem> findBySku(String sku) {
        return Optional.empty();
    }

    @Override
    public StockItem save(StockItem stockItem) {
        return stockItem;
    }
}
