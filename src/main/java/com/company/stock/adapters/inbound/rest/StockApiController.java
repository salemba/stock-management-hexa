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

import java.util.List;
import java.util.UUID;

/**
 * <h1>StockApiController (Inbound REST Adapter)</h1>
 * <p>Adaptateur Web REST exposant le contrat OpenAPI et traduisant les DTOs Web en appels de cas d'utilisation du domaine.</p>
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
                .map(StockItemResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StockItemResponse> createStockItem(@Valid @RequestBody CreateStockItemRequest request) {
        ProductCategory category = parseCategory(request.category());
        Product productToCreate = new Product(
            UUID.randomUUID(),
            request.sku(),
            request.name(),
            category,
            request.unitPrice(),
            StockQuantity.of(request.quantity()),
            StockQuantity.of(request.reorderThreshold())
        );

        Product created = manageStockUseCase.createProduct(productToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(StockItemResponse.fromDomain(created));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<StockItemResponse> getStockBySku(@PathVariable("sku") String sku) {
        return consultStockQuery.getProductBySku(sku)
                .map(StockItemResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{sku}/add")
    public ResponseEntity<StockItemResponse> addStock(
            @PathVariable("sku") String sku,
            @Valid @RequestBody StockMovementRequest request) {
        Product updated = manageStockUseCase.addStock(
            sku,
            StockQuantity.of(request.quantity()),
            request.reason() != null ? request.reason() : "Approvisionnement REST"
        );
        return ResponseEntity.ok(StockItemResponse.fromDomain(updated));
    }

    @PostMapping("/{sku}/deduct")
    public ResponseEntity<StockItemResponse> deductStock(
            @PathVariable("sku") String sku,
            @Valid @RequestBody StockMovementRequest request) {
        Product updated = manageStockUseCase.deductStock(
            sku,
            StockQuantity.of(request.quantity()),
            request.reason() != null ? request.reason() : "Retrait REST"
        );
        return ResponseEntity.ok(StockItemResponse.fromDomain(updated));
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
