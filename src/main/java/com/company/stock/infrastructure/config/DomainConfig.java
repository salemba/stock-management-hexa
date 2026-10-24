package com.company.stock.infrastructure.config;

import com.company.stock.domain.service.StockManagementService;
import com.company.stock.ports.outbound.ProductRepositoryPort;
import com.company.stock.ports.outbound.StockNotifierPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * <h1>DomainConfig</h1>
 * <p>Configuration Spring instanciant le service du Domaine (pur Java) en tant que Bean Spring sans polluer le Domaine avec des annotations du framework.</p>
 */
@Configuration
public class DomainConfig {

    @Bean
    public StockManagementService stockManagementService(
            ProductRepositoryPort productRepositoryPort,
            Optional<StockNotifierPort> stockNotifierPort) {
        return new StockManagementService(productRepositoryPort, stockNotifierPort.orElse(null));
    }
}
