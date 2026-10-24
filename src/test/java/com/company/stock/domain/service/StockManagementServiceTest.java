package com.company.stock.domain.service;

import com.company.stock.domain.exception.InvalidStockOperationException;
import com.company.stock.domain.model.Product;
import com.company.stock.domain.model.ProductCategory;
import com.company.stock.domain.model.StockQuantity;
import com.company.stock.ports.outbound.ProductRepositoryPort;
import com.company.stock.ports.outbound.StockNotifierPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StockManagementServiceTest {

    private InMemoryProductRepository repository;
    private MockStockNotifier notifier;
    private StockManagementService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
        notifier = new MockStockNotifier();
        service = new StockManagementService(repository, notifier);
    }

    @Test
    @DisplayName("Devrait créer un produit et l'enregistrer dans le dépôt")
    void shouldCreateProductSuccessfully() {
        Product product = new Product(
            UUID.randomUUID(),
            "LAPTOP-01",
            "Laptop Workstation",
            ProductCategory.ELECTRONICS,
            BigDecimal.valueOf(1200.00),
            StockQuantity.of(10),
            StockQuantity.of(2)
        );

        Product created = service.createProduct(product);

        assertNotNull(created);
        assertEquals("LAPTOP-01", created.getSku());
        assertTrue(repository.findBySku("LAPTOP-01").isPresent());
    }

    @Test
    @DisplayName("Devrait ajouter du stock et mettre à jour le produit")
    void shouldAddStockSuccessfully() {
        Product product = new Product(
            UUID.randomUUID(),
            "PHONE-01",
            "Smartphone",
            ProductCategory.ELECTRONICS,
            BigDecimal.valueOf(800.00),
            StockQuantity.of(5),
            StockQuantity.of(2)
        );
        service.createProduct(product);

        Product updated = service.addStock("PHONE-01", StockQuantity.of(10), "Réapprovisionnement fournisseur");

        assertEquals(BigDecimal.valueOf(15), updated.getCurrentQuantity().getValue());
        assertEquals(2, updated.getMovements().size());
    }

    @Test
    @DisplayName("Devrait déstocker et notifier si le seuil d'alerte est atteint")
    void shouldDeductStockAndTriggerNotificationWhenBelowThreshold() {
        Product product = new Product(
            UUID.randomUUID(),
            "TABLE-01",
            "Table de bureau",
            ProductCategory.FURNITURE,
            BigDecimal.valueOf(150.00),
            StockQuantity.of(5),
            StockQuantity.of(3)
        );
        service.createProduct(product);

        service.deductStock("TABLE-01", StockQuantity.of(3), "Vente client");

        assertTrue(notifier.notifiedProducts.contains("TABLE-01"));
    }

    @Test
    @DisplayName("Devrait lever une exception lors d'une tentative de déstockage supérieur au stock disponible")
    void shouldThrowExceptionWhenDeductingMoreThanAvailable() {
        Product product = new Product(
            UUID.randomUUID(),
            "CHAIR-01",
            "Chaise ergonomique",
            ProductCategory.FURNITURE,
            BigDecimal.valueOf(90.00),
            StockQuantity.of(2),
            StockQuantity.of(1)
        );
        service.createProduct(product);

        assertThrows(InvalidStockOperationException.class, () ->
            service.deductStock("CHAIR-01", StockQuantity.of(5), "Vente excessive")
        );
    }

    // Faux objets de test sans framework de mock (In-Memory Fake Repository)
    private static class InMemoryProductRepository implements ProductRepositoryPort {
        private final Map<String, Product> storage = new HashMap<>();

        @Override
        public Product save(Product product) {
            storage.put(product.getSku(), product);
            return product;
        }

        @Override
        public Optional<Product> findById(UUID id) {
            return storage.values().stream().filter(p -> p.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Product> findBySku(String sku) {
            return Optional.ofNullable(storage.get(sku));
        }

        @Override
        public List<Product> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public boolean existsBySku(String sku) {
            return storage.containsKey(sku);
        }
    }

    private static class MockStockNotifier implements StockNotifierPort {
        private final List<String> notifiedProducts = new ArrayList<>();

        @Override
        public void notifyReorderNeeded(Product product) {
            notifiedProducts.add(product.getSku());
        }
    }
}
