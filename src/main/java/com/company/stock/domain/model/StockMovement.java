package com.company.stock.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * <h1>StockMovement</h1>
 * <p>Objet Valeur représentant un historique de mouvement de stock (Entrée/Sortie/Ajustement).</p>
 *
 * <p><strong>Rôle dans l'Architecture Hexagonale :</strong></p>
 * <ul>
 *   <li>Conserve la traçabilité des opérations métiers sur le stock sans dépendre d'un ORM/JPA.</li>
 * </ul>
 */
public final class StockMovement {

    private final UUID id;
    private final Instant timestamp;
    private final MovementType type;
    private final StockQuantity quantity;
    private final String reason;

    public StockMovement(MovementType type, StockQuantity quantity, String reason) {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.type = Objects.requireNonNull(type, "Le type de mouvement ne peut pas être nul.");
        this.quantity = Objects.requireNonNull(quantity, "La quantité du mouvement ne peut pas être nulle.");
        this.reason = reason != null ? reason : "";
    }

    public UUID getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public MovementType getType() {
        return type;
    }

    public StockQuantity getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement movement = (StockMovement) o;
        return Objects.equals(id, movement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
