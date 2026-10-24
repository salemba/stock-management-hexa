package com.company.stock.ports.inbound;

import com.company.stock.domain.model.Product;
import java.util.List;
import java.util.Optional;

/**
 * <h1>ConsultStockQuery (Port d'Entrée / Primary Port)</h1>
 * <p>Interface définissant les cas d'utilisation de consultation et lecture du stock.</p>
 *
 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Séparation de la responsabilité de lecture (Query) par rapport aux modifications (Command).</li>
 * </ul>
 */
public interface ConsultStockQuery {

    /**
     * Recherche un produit par son code SKU.
     */
    Optional<Product> getProductBySku(String sku);

    /**
     * Liste tous les produits.
     */
    List<Product> getAllProducts();

    /**
     * Récupère la liste des produits dont le stock actuel est inférieur au seuil de réapprovisionnement.
     */
    List<Product> getProductsNeedingReorder();
}
