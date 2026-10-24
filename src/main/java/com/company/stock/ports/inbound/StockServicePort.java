package com.company.stock.ports.inbound;

import com.company.stock.domain.model.StockItem;
import java.util.List;
import java.util.Optional;

public interface StockServicePort {
    List<StockItem> getAllStockItems();
    Optional<StockItem> getStockBySku(String sku);
    StockItem createStockItem(StockItem stockItem);
}
