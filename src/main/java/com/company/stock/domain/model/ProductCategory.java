package com.company.stock.domain.model;

/**
 * <h1>ProductCategory</h1>
 * <p>Objet valeur (Enumération) représentant les catégories de produits gérées par le domaine.</p>
 *
 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Fait partie intégrante du modèle du domaine pur.</li>
 *   <li>Aucune dépendance vers une base de données ou un framework Web.</li>
 * </ul>
 */
public enum ProductCategory {
    ELECTRONICS("Électronique"),
    FURNITURE("Mobilier"),
    CLOTHING("Vêtements"),
    FOOD("Alimentation"),
    OTHER("Autre");

    private final String label;

    ProductCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
