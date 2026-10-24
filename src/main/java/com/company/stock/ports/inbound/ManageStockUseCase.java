package com.company.stock.ports.inbound;

import com.company.stock.domain.model.Product;
import com.company.stock.domain.model.StockQuantity;

/**
 * <h1>ManageStockUseCase (Port d'Entrée / Primary Port)</h1>
 * <p>Interface définissant les cas d'utilisation de modification du stock et de gestion des produits.</p>
 *
 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Définit le contrat d'exposition du domaine pour les acteurs extérieurs (Contrôleurs REST, CLI, Handlers d'événements).</li>
 *   <li>Assure l'isolation : les acteurs appellent ce port sans connaître l'implémentation du service ni l'infrastructure.</li>
 * </ul>
 */
public interface ManageStockUseCase {

    /**
     * Crée un nouveau produit dans le domaine.
     *
     * @param product Produit à enregistrer.
     * @return Le produit créé.
     */
    Product createProduct(Product product);

    /**
     * Approvisionne le stock d'un produit (Entrée).
     *
     * @param sku Identifiant SKU du produit.
     * @param amount Quantité à ajouter.
     * @param reason Motif de l'approvisionnement.
     * @return Le produit mis à jour.
     */
    Product addStock(String sku, StockQuantity amount, String reason);

    /**
     * Déstocke une quantité d'un produit (Sortie).
     *
     * @param sku Identifiant SKU du produit.
     * @param amount Quantité à retirer.
     * @param reason Motif du déstockage.
     * @return Le produit mis à jour.
     */
    Product deductStock(String sku, StockQuantity amount, String reason);
}
