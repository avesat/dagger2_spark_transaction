package org.example.service.management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.config.DataSourceModule;
import org.example.model.dto.AccountDto;
import org.example.model.entity.Account;
import org.example.repository.AccountDao;
import org.example.repository.AccountDaoImpl;
import org.example.util.DataBaseUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import spark.Request;
import spark.Response;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountServiceTest {
    @Mock
    Request request;

    @Mock
    Response response;

    @Spy
    DataSourceModule dataSourceModule = new DataSourceModule();

    @Spy
    AccountDao accountDao = new AccountDaoImpl(dataSourceModule);

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    AccountService accountService;

    Account account;

    private static final String TEST_UUID_STRING = "bb81d979-3a89-49ae-9d8b-e228b055ccd6";
    private static final BigDecimal TEST_INIT_BALANCE = new BigDecimal("100.00");

    @BeforeAll
    void initDatabase() {
        DataBaseUtil.createTableAccount(dataSourceModule.dataSource());

        account = new Account();
        account.setId(UUID.fromString(TEST_UUID_STRING));
        account.setBalance(TEST_INIT_BALANCE);

        accountDao.save(account);
    }

    @Test
    public void ctrlGetAccountInvalidIdParamException() {
        when(request.params(any())).thenReturn("abc");

        assertThrows(spark.HaltException.class,
                () -> accountService.ctrlGetAccount(request, response));
    }

    @Test
    public void ctrlGetAccountAccountNotFoundException() {
        when(request.params(any())).thenReturn(UUID.randomUUID().toString());

        assertThrows(spark.HaltException.class,
                () -> accountService.ctrlGetAccount(request, response));
    }

    @Test
    public void ctrlGetAccountSuccess() {
        when(request.params(any())).thenReturn(TEST_UUID_STRING);

        AccountDto accountDto = accountService.ctrlGetAccount(request, response);

        assertEquals(account.getId(), accountDto.getId());
        assertEquals(account.getBalance(), accountDto.getBalance());
    }

    @Test
    public void TransferTransactionGetAccountSuccess() throws InterruptedException {
        UUID fromAccountId = UUID.fromString("bb81d979-3a89-49ae-9d8b-e228b055cc01");
        Account fromAccount = new Account();
        fromAccount.setId(fromAccountId);
        fromAccount.setBalance(new BigDecimal("200.00"));
        accountDao.save(fromAccount);

        UUID toAccountId = UUID.fromString("bb81d979-3a89-49ae-9d8b-e228b055cc02");
        Account toAccount = new Account();
        toAccount.setId(toAccountId);
        toAccount.setBalance(new BigDecimal("100.00"));
        accountDao.save(toAccount);


        ExecutorService es = Executors.newFixedThreadPool(10);
        List<Callable<Object>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tasks.add(Executors.callable(() -> accountDao.transferTransaction(fromAccountId, toAccountId, new BigDecimal("1"))));
        }

        List<Future<Object>> answers = es.invokeAll(tasks);

        Optional<Account> actualFromAccount = accountDao.findById(fromAccountId);
        Optional<Account> actualToAccount = accountDao.findById(toAccountId);


        assertEquals(100, actualFromAccount.get().getBalance().intValue());
        assertEquals(200, actualToAccount.get().getBalance().intValue());
    }
}
