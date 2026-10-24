package com.company.stock.ports.outbound;

import com.company.stock.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <h1>ProductRepositoryPort (Port de Sortie / Secondary Port)</h1>
 * <p>Interface définissant le contrat d'accès aux données de persistance des produits.</p>
 *
 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Inversion de dépendance : le domaine définit CETTE interface et exige que l'infrastructure (JPA, MongoDB, In-Memory) l'implémente.</li>
 *   <li>Le domaine manipule uniquement les entités métiers {@link Product}, jamais des entités ORM/JPA.</li>
 * </ul>
 */
public interface ProductRepositoryPort {

    /**
     * Sauvegarde ou met à jour un produit dans le support de stockage.
     */
    Product save(Product product);

    /**
     * Recherche un produit par son identifiant unique.
     */
    Optional<Product> findById(UUID id);

    /**
     * Recherche un produit par son code SKU.
     */
    Optional<Product> findBySku(String sku);

    /**
     * Récupère la totalité des produits.
     */
    List<Product> findAll();

    /**
     * Indique si un produit existe pour un SKU donné.
     */
    boolean existsBySku(String sku);
}
