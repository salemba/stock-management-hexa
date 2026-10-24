package com.company.stock.ports.outbound;

import com.company.stock.domain.model.StockItem;
import java.util.List;
import java.util.Optional;

public interface StockRepositoryPort {
    List<StockItem> findAll();
    Optional<StockItem> findBySku(String sku);
    StockItem save(StockItem stockItem);
}
