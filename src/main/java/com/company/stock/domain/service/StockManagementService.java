package com.company.stock.domain.service;

import com.company.stock.domain.exception.InvalidStockOperationException;
import com.company.stock.domain.exception.ProductNotFoundException;
import com.company.stock.domain.model.Product;
import com.company.stock.domain.model.StockQuantity;
import com.company.stock.ports.inbound.ConsultStockQuery;
import com.company.stock.ports.inbound.ManageStockUseCase;
import com.company.stock.ports.outbound.ProductRepositoryPort;
import com.company.stock.ports.outbound.StockNotifierPort;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <h1>StockManagementService (Service Métier Purement Java)</h1>
 * <p>Implémentation du cœur métier orchestrant les règles de gestion du stock.</p>
 *
 * <p><strong>Règles d'Architecture Hexagonale Respectées :</strong></p>
 * <ul>
 *   <li><strong>AUCUNE annotation Spring</strong> (ex: pas de {@code @Service}, pas de {@code @Autowired}).</li>
 *   <li>Dépend uniquement des entités du domaine et des ports de sortie (interfaces {@link ProductRepositoryPort} et {@link StockNotifierPort}).</li>
 *   <li>Implémente les cas d'utilisation (ports d'entrée {@link ManageStockUseCase} et {@link ConsultStockQuery}).</li>
 *   <li>L'injection des dépendances se fait via le constructeur standard Java. La configuration Spring (si nécessaire dans l'infra) se fera via un {@code @Bean} dans le package infrastructure.</li>
 * </ul>
 */
public class StockManagementService implements ManageStockUseCase, ConsultStockQuery {

    private final ProductRepositoryPort productRepositoryPort;
    private final StockNotifierPort stockNotifierPort;

    /**
     * Constructeur pur Java pour l'injection des adaptateurs de sortie.
     *
     * @param productRepositoryPort Port de persistance.
     * @param stockNotifierPort     Port de notification d'alerte.
     */
    public StockManagementService(ProductRepositoryPort productRepositoryPort, StockNotifierPort stockNotifierPort) {
        this.productRepositoryPort = Objects.requireNonNull(productRepositoryPort, "ProductRepositoryPort ne peut pas être nul.");
        this.stockNotifierPort = stockNotifierPort;
    }

    @Override
    public Product createProduct(Product product) {
        if (product == null) {
            throw new InvalidStockOperationException("Le produit à créer ne peut pas être nul.");
        }
        if (productRepositoryPort.existsBySku(product.getSku())) {
            throw new InvalidStockOperationException("Un produit existe déjà avec le SKU : " + product.getSku());
        }

        Product savedProduct = productRepositoryPort.save(product);

        // Vérification du seuil d'alerte dès la création si stock initial bas
        checkAndNotifyReorder(savedProduct);

        return savedProduct;
    }

    @Override
    public Product addStock(String sku, StockQuantity amount, String reason) {
        Product product = getProductOrThrow(sku);
        product.addStock(amount, reason);

        Product updatedProduct = productRepositoryPort.save(product);
        return updatedProduct;
    }

    @Override
    public Product deductStock(String sku, StockQuantity amount, String reason) {
        Product product = getProductOrThrow(sku);
        product.deductStock(amount, reason);

        Product updatedProduct = productRepositoryPort.save(product);

        // Vérification du seuil de réapprovisionnement après déstockage
        checkAndNotifyReorder(updatedProduct);

        return updatedProduct;
    }

    @Override
    public Optional<Product> getProductBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            return Optional.empty();
        }
        return productRepositoryPort.findBySku(sku.trim().toUpperCase());
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepositoryPort.findAll();
    }

    @Override
    public List<Product> getProductsNeedingReorder() {
        return productRepositoryPort.findAll().stream()
                .filter(Product::isReorderNeeded)
                .toList();
    }

    private Product getProductOrThrow(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new InvalidStockOperationException("Le SKU fourni est invalide.");
        }
        return productRepositoryPort.findBySku(sku.trim().toUpperCase())
                .orElseThrow(() -> new ProductNotFoundException(sku, true));
    }

    private void checkAndNotifyReorder(Product product) {
        if (stockNotifierPort != null && product.isReorderNeeded()) {
            stockNotifierPort.notifyReorderNeeded(product);
        }
    }
}
