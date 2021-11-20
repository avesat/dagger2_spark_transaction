package org.example.model.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "id")
public class Account {
    private UUID id;
    private BigDecimal balance = BigDecimal.ZERO;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UUID getId() {
        return this.id;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }
}
