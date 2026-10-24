package com.company.stock.adapters.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stocks")
public class StockEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "reorder_threshold", precision = 12, scale = 2)
    private BigDecimal reorderThreshold;

    public StockEntity() {}

    public StockEntity(UUID id, String sku, String name, String category, BigDecimal unitPrice, BigDecimal quantity, BigDecimal reorderThreshold) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(BigDecimal reorderThreshold) { this.reorderThreshold = reorderThreshold; }
}
