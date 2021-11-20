package org.example.repository;

import org.example.model.entity.Account;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountDao {
    Optional<Account> findById(UUID id);

    void save(Account account);

    void transferTransaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount);
}