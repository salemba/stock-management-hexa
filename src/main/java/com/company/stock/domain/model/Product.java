package com.company.stock.domain.model;

import com.company.stock.domain.exception.InvalidStockOperationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * <h1>Product (Agrégat Métier)</h1>
 * <p>Entité racine de l'agrégat représentant un Produit en stock dans le domaine métier.</p>
 *
 * <p><strong>Règles de Gestion Embarquées :</strong></p>
 * <ul>
 *   <li>Le SKU et le nom doivent être valides.</li>
 *   <li>Toute modification de stock (ajout/retrait) enregistre un mouvement de stock dans l'historique de l'agrégat.</li>
 *   <li>Interdit tout retrait dépassant la quantité disponible.</li>
 *   <li>Évalue si le stock courant est inférieur au seuil d'alerte de réapprovisionnement.</li>
 * </ul>
 *
 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Cœur absolu du système. Ne contient aucune annotation Spring, JPA, ni Jackson.</li>
 *   <li>Toutes les mutations d'état passent par des méthodes métier explicites (ex: {@link #addStock}, {@link #deductStock}).</li>
 * </ul>
 */
public class Product {

    private final UUID id;
    private final String sku;
    private String name;
    private ProductCategory category;
    private BigDecimal unitPrice;
    private StockQuantity currentQuantity;
    private StockQuantity reorderThreshold;
    private final List<StockMovement> movements = new ArrayList<>();

    public Product(UUID id, String sku, String name, ProductCategory category, BigDecimal unitPrice, StockQuantity initialQuantity, StockQuantity reorderThreshold) {
        if (sku == null || sku.isBlank()) {
            throw new InvalidStockOperationException("Le SKU du produit ne peut pas être vide.");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidStockOperationException("Le nom du produit ne peut pas être vide.");
        }
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidStockOperationException("Le prix unitaire ne peut pas être négatif.");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.sku = sku.trim().toUpperCase();
        this.name = name.trim();
        this.category = category != null ? category : ProductCategory.OTHER;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.currentQuantity = initialQuantity != null ? initialQuantity : StockQuantity.ZERO;
        this.reorderThreshold = reorderThreshold != null ? reorderThreshold : StockQuantity.ZERO;

        if (this.currentQuantity.getValue().compareTo(BigDecimal.ZERO) > 0) {
            this.movements.add(new StockMovement(MovementType.INCOMING, this.currentQuantity, "Stock initial"));
        }
    }

    /**
     * Ajoute du stock au produit et enregistre un mouvement entrant.
     *
     * @param amount Quantité à ajouter.
     * @param reason Motif de l'ajout.
     */
    public void addStock(StockQuantity amount, String reason) {
        if (amount == null || amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockOperationException("La quantité d'ajout de stock doit être supérieure à 0.");
        }
        this.currentQuantity = this.currentQuantity.add(amount);
        this.movements.add(new StockMovement(MovementType.INCOMING, amount, reason));
    }

    /**
     * Retire du stock du produit et enregistre un mouvement sortant.
     *
     * @param amount Quantité à retirer.
     * @param reason Motif du retrait.
     */
    public void deductStock(StockQuantity amount, String reason) {
        if (amount == null || amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockOperationException("La quantité de retrait de stock doit être supérieure à 0.");
        }
        this.currentQuantity = this.currentQuantity.subtract(amount);
        this.movements.add(new StockMovement(MovementType.OUTGOING, amount, reason));
    }

    /**
     * Indique si le niveau de stock actuel nécessite un réapprovisionnement.
     *
     * @return true si le stock actuel est strictement inférieur au seuil de réapprovisionnement.
     */
    public boolean isReorderNeeded() {
        return this.currentQuantity.isLessThan(this.reorderThreshold);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public StockQuantity getCurrentQuantity() {
        return currentQuantity;
    }

    public StockQuantity getReorderThreshold() {
        return reorderThreshold;
    }

    public List<StockMovement> getMovements() {
        return Collections.unmodifiableList(movements);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidStockOperationException("Le nom du produit ne peut pas être vide.");
        }
        this.name = name.trim();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidStockOperationException("Le prix unitaire ne peut pas être négatif.");
        }
        this.unitPrice = unitPrice;
    }

    public void setReorderThreshold(StockQuantity reorderThreshold) {
        this.reorderThreshold = reorderThreshold != null ? reorderThreshold : StockQuantity.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
}
