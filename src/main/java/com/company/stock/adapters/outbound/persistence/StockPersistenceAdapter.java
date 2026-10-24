package com.company.stock.adapters.outbound.persistence;

import com.company.stock.domain.model.Product;
import com.company.stock.domain.model.ProductCategory;
import com.company.stock.domain.model.StockQuantity;
import com.company.stock.ports.outbound.ProductRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <h1>StockPersistenceAdapter (Outbound Persistence Adapter)</h1>
 * <p>Adaptateur de persistance traduisant les entités JPA {@link StockEntity} vers/depuis l'agrégat du domaine {@link Product}.</p>
 */
@Component
public class StockPersistenceAdapter implements ProductRepositoryPort {

    private final SpringDataStockRepository repository;

    public StockPersistenceAdapter(SpringDataStockRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        StockEntity entity = toEntity(product);
        StockEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return repository.findBySku(sku).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySku(String sku) {
        return repository.existsBySku(sku);
    }

    private StockEntity toEntity(Product product) {
        return new StockEntity(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getCategory() != null ? product.getCategory().name() : ProductCategory.OTHER.name(),
            product.getUnitPrice(),
            product.getCurrentQuantity() != null ? product.getCurrentQuantity().getValue() : null,
            product.getReorderThreshold() != null ? product.getReorderThreshold().getValue() : null
        );
    }

    private Product toDomain(StockEntity entity) {
        ProductCategory category;
        try {
            category = entity.getCategory() != null ? ProductCategory.valueOf(entity.getCategory()) : ProductCategory.OTHER;
        } catch (IllegalArgumentException e) {
            category = ProductCategory.OTHER;
        }

        return new Product(
            entity.getId(),
            entity.getSku(),
            entity.getName(),
            category,
            entity.getUnitPrice(),
            entity.getQuantity() != null ? StockQuantity.of(entity.getQuantity()) : StockQuantity.ZERO,
            entity.getReorderThreshold() != null ? StockQuantity.of(entity.getReorderThreshold()) : StockQuantity.ZERO
        );
    }
}
