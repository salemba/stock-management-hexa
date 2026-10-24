package com.company.stock.adapters.outbound.notification;

import com.company.stock.domain.model.Product;
import com.company.stock.ports.outbound.StockNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <h1>ConsoleStockNotifierAdapter (Outbound Adapter)</h1>
 * <p>Adaptateur de notification basique enregistrant les alertes de stock dans les logs de l'application.</p>
 */
@Component
public class ConsoleStockNotifierAdapter implements StockNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(ConsoleStockNotifierAdapter.class);

    @Override
    public void notifyReorderNeeded(Product product) {
        log.warn("ALERTE STOCK : Le produit '{}' (SKU: {}) a atteint un niveau critique ! Stock actuel: {}, Seuil: {}",
                product.getName(),
                product.getSku(),
                product.getCurrentQuantity(),
                product.getReorderThreshold()
        );
    }
}
