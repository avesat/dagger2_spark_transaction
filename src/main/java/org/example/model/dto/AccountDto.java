package org.example.model.dto;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AccountDto {
    private UUID id;
    private BigDecimal balance;

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
