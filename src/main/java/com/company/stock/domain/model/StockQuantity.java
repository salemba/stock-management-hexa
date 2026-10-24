package com.company.stock.domain.model;

import com.company.stock.domain.exception.InvalidStockOperationException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * <h1>StockQuantity</h1>
 * <p>Objet Valeur (Value Object) immuable encapsulant la quantité d'un produit en stock.</p>
 *
 * <p><strong>Règles de Gestion Encapsulées :</strong></p>
 * <ul>
 *   <li>La quantité ne peut jamais être nulle ou négative.</li>
 *   <li>Les opérations d'addition et de soustraction retournent de nouvelles instances (immuabilité).</li>
 *   <li>Empêche le stock de devenir négatif lors d'un retrait.</li>
 * </ul>
 *
 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Garantit l'intégrité des données du domaine à tout moment.</li>
 *   <li>Isole la logique arithmétique métier hors des services et adaptateurs.</li>
 * </ul>
 */
public final class StockQuantity {

    public static final StockQuantity ZERO = new StockQuantity(BigDecimal.ZERO);

    private final BigDecimal value;

    /**
     * Crée une quantité de stock.
     *
     * @param value Valeur décimale de la quantité (doit être >= 0).
     * @throws InvalidStockOperationException si la valeur est nulle ou négative.
     */
    public StockQuantity(BigDecimal value) {
        if (value == null) {
            throw new InvalidStockOperationException("La quantité de stock ne peut pas être nulle.");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidStockOperationException("La quantité de stock ne peut pas être négative : " + value);
        }
        this.value = value;
    }

    /**
     * Méthode de fabrique statique pour créer une quantité à partir d'un entier.
     */
    public static StockQuantity of(long value) {
        return new StockQuantity(BigDecimal.valueOf(value));
    }

    /**
     * Méthode de fabrique statique pour créer une quantité à partir d'un BigDecimal.
     */
    public static StockQuantity of(BigDecimal value) {
        return new StockQuantity(value);
    }

    /**
     * Ajoute une quantité à la quantité actuelle.
     *
     * @param amount Quantité à ajouter.
     * @return Une nouvelle instance de {@link StockQuantity}.
     */
    public StockQuantity add(StockQuantity amount) {
        if (amount == null) {
            return this;
        }
        return new StockQuantity(this.value.add(amount.value));
    }

    /**
     * Soustrait une quantité de la quantité actuelle.
     *
     * @param amount Quantité à soustraire.
     * @return Une nouvelle instance de {@link StockQuantity}.
     * @throws InvalidStockOperationException si le solde devient négatif.
     */
    public StockQuantity subtract(StockQuantity amount) {
        if (amount == null) {
            return this;
        }
        BigDecimal result = this.value.subtract(amount.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidStockOperationException(
                String.format("Stock insuffisant : impossible de soustraire %s à %s", amount.value, this.value)
            );
        }
        return new StockQuantity(result);
    }

    /**
     * Vérifie si cette quantité est inférieure à un seuil donné.
     */
    public boolean isLessThan(StockQuantity threshold) {
        if (threshold == null) {
            return false;
        }
        return this.value.compareTo(threshold.value) < 0;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockQuantity that = (StockQuantity) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
