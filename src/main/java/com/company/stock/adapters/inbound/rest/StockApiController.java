package com.company.stock.adapters.inbound.rest;

import com.company.stock.adapters.inbound.rest.model.CreateStockItemRequest;
import com.company.stock.adapters.inbound.rest.model.StockItemResponse;
import com.company.stock.adapters.inbound.rest.model.StockMovementRequest;
import com.company.stock.domain.model.Product;
import com.company.stock.domain.model.ProductCategory;
import com.company.stock.domain.model.StockQuantity;
import com.company.stock.ports.inbound.ConsultStockQuery;
import com.company.stock.ports.inbound.ManageStockUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * <h1>StockApiController (Inbound REST Adapter)</h1>
 * <p>Adaptateur Web REST exposant le contrat OpenAPI et traduisant les DTOs générés en appels de cas d'utilisation du domaine.</p>
 */
@RestController
@RequestMapping("/api/v1/stocks")
public class StockApiController {

    private final ManageStockUseCase manageStockUseCase;
    private final ConsultStockQuery consultStockQuery;

    public StockApiController(ManageStockUseCase manageStockUseCase, ConsultStockQuery consultStockQuery) {
        this.manageStockUseCase = manageStockUseCase;
        this.consultStockQuery = consultStockQuery;
    }

    @GetMapping
    public ResponseEntity<List<StockItemResponse>> getStockItems() {
        List<StockItemResponse> response = consultStockQuery.getAllProducts().stream()
                .map(this::toStockItemResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StockItemResponse> createStockItem(@Valid @RequestBody CreateStockItemRequest request) {
        ProductCategory category = parseCategory(request.getCategory());
        BigDecimal qty = request.getQuantity() != null ? request.getQuantity() : BigDecimal.ZERO;
        BigDecimal threshold = request.getReorderThreshold() != null ? request.getReorderThreshold() : BigDecimal.ZERO;

        Product productToCreate = new Product(
            UUID.randomUUID(),
            request.getSku(),
            request.getName(),
            category,
            request.getUnitPrice() != null ? request.getUnitPrice() : BigDecimal.ZERO,
            StockQuantity.of(qty),
            StockQuantity.of(threshold)
        );

        Product created = manageStockUseCase.createProduct(productToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toStockItemResponse(created));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<StockItemResponse> getStockBySku(@PathVariable("sku") String sku) {
        return consultStockQuery.getProductBySku(sku)
                .map(this::toStockItemResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{sku}/add")
    public ResponseEntity<StockItemResponse> addStock(
            @PathVariable("sku") String sku,
            @Valid @RequestBody StockMovementRequest request) {
        BigDecimal qty = request.getQuantity() != null ? request.getQuantity() : BigDecimal.ZERO;
        Product updated = manageStockUseCase.addStock(
            sku,
            StockQuantity.of(qty),
            request.getReason() != null ? request.getReason() : "Approvisionnement REST"
        );
        return ResponseEntity.ok(toStockItemResponse(updated));
    }

    @PostMapping("/{sku}/deduct")
    public ResponseEntity<StockItemResponse> deductStock(
            @PathVariable("sku") String sku,
            @Valid @RequestBody StockMovementRequest request) {
        BigDecimal qty = request.getQuantity() != null ? request.getQuantity() : BigDecimal.ZERO;
        Product updated = manageStockUseCase.deductStock(
            sku,
            StockQuantity.of(qty),
            request.getReason() != null ? request.getReason() : "Retrait REST"
        );
        return ResponseEntity.ok(toStockItemResponse(updated));
    }

    private StockItemResponse toStockItemResponse(Product product) {
        StockItemResponse response = new StockItemResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setCategory(product.getCategory() != null ? product.getCategory().name() : null);
        response.setUnitPrice(product.getUnitPrice());
        response.setQuantity(product.getCurrentQuantity() != null ? product.getCurrentQuantity().getValue() : null);
        response.setReorderThreshold(product.getReorderThreshold() != null ? product.getReorderThreshold().getValue() : null);
        response.setReorderNeeded(product.isReorderNeeded());
        return response;
    }

    private ProductCategory parseCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return ProductCategory.OTHER;
        }
        try {
            return ProductCategory.valueOf(categoryName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProductCategory.OTHER;
        }
    }
}
