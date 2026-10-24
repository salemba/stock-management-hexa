package com.company.stock.ports.outbound;

import com.company.stock.domain.model.Product;

/**
 * <h1>StockNotifierPort (Port de Sortie / Secondary Port)</h1>
 * <p>Interface définissant le contrat de notification lorsque le niveau de stock atteint un seuil d'alerte.</p>

 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Permet au domaine de déclencher une alerte sans dépendre du moyen d'envoi (Email, Slack, Webhook, Kafka).</li>
 * </ul>
 */
public interface StockNotifierPort {

    /**
     * Envoie une alerte de réapprovisionnement nécessaire pour un produit donné.
     *
     * @param product Produit nécessitant un réapprovisionnement.
     */
    void notifyReorderNeeded(Product product);
}
