package org.example.repository;

import org.example.config.DataSourceModule;
import org.example.model.entity.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.sql.DataSource;

public class AccountDaoImpl implements AccountDao {
    private final static String SELECT_FROM_ACCOUNT_BY_ID = "SELECT * FROM account WHERE account.id = ?;";
    private final static String INSERT_INTO_ACCOUNT = "INSERT INTO account(id, balance) VALUES(?, ?);";
    private final static String UPDATE_ACCOUNT = "UPDATE account SET balance = ? WHERE id = ?;";

    private final DataSource dataSource;

    @Inject
    public AccountDaoImpl(DataSourceModule dataSourceModule) {
        this.dataSource = dataSourceModule.dataSource();
    }

    @Override
    public Optional<Account> findById(UUID id) {
        try (Connection connection = dataSource.getConnection()) {
            Account account = getAccountById(connection, id);
            return Optional.of(account);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    private Account getAccountById(Connection connection, UUID id) throws SQLException {
        try (PreparedStatement selectByIdStatement = connection.prepareStatement(SELECT_FROM_ACCOUNT_BY_ID)) {
            selectByIdStatement.setObject(1, id);
            return parseAccountSelectResult(selectByIdStatement);
        }
    }

    private Account parseAccountSelectResult(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
            Account account = new Account();
            account.setId(resultSet.getObject("id", java.util.UUID.class));
            account.setBalance(resultSet.getBigDecimal(2));
            return account;
        }
    }

    @Override
    public void save(Account account) {
        validateAccount(account);
        findById(account.getId()).ifPresentOrElse(v -> updateAccount(account), () -> insertAccount(account));
    }

    private void validateAccount(Account account) {
    }

    public void updateAccount(Account account) {
        try (Connection connection = dataSource.getConnection()) {
            executeUpdateQuery(connection, account);
        } catch (SQLException e) {
//            throw new Exception();
            System.out.println("Error update account");
        }
    }

    private void executeUpdateQuery(Connection connection, Account account) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ACCOUNT)) {
            preparedStatement.setBigDecimal(1, account.getBalance());
            preparedStatement.setObject(2, account.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void insertAccount(Account account) {
        try (Connection connection = dataSource.getConnection()) {
            if (account.getId() == null) {
                account.setId(UUID.randomUUID());
            }
            executeInsertQuery(connection, account);
        } catch (SQLException e) {
//            throw new Exception();
            System.out.println("Error insertAccount");
        }
    }

    private void executeInsertQuery(Connection connection, Account account) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_ACCOUNT)) {
            preparedStatement.setObject(1, account.getId());
            preparedStatement.setBigDecimal(2, account.getBalance());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void transferTransaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        try (Connection connection = dataSource.getConnection()) {
            transferData(connection, fromAccountId, toAccountId, amount);
        } catch (SQLException e) {
//            throw new Exception();
            System.out.println("Error transferTransaction");
        }
    }

    synchronized private void transferData(Connection connection, UUID fromAccountId, UUID toAccountId, BigDecimal amount)
            throws SQLException {
        try {
            connection.setAutoCommit(false);

            Account toAccount = getAccountById(connection, toAccountId);
            System.out.println(Thread.currentThread().getName() + " to Account Balance: " + toAccount.getBalance().toPlainString());
            Account fromAccount = getAccountById(connection, fromAccountId);
            System.out.println(Thread.currentThread().getName() + " from Account Balance: " + fromAccount.getBalance().toPlainString());
            if (fromAccount.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                return;
            }
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            updateAccount(fromAccount);
            toAccount.setBalance(toAccount.getBalance().add(amount));
            updateAccount(toAccount);
            connection.commit();

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Error rollback");
            connection.rollback();
        }
    }

}
