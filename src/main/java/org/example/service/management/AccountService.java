package org.example.service.management;

import static spark.Spark.halt;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.model.dto.AccountDto;
import org.example.model.dto.TransferDto;
import org.example.model.dto.TransferPayload;
import org.example.repository.AccountDao;
import org.modelmapper.ModelMapper;

import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import spark.Request;
import spark.Response;

@Singleton
public class AccountService {
    AccountDao accountDao;

    ObjectMapper objectMapper;

    ModelMapper modelMapper;

    @Inject
    public AccountService(AccountDao accountDao, ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.accountDao = accountDao;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    public AccountDto ctrlGetAccount(Request request, Response response) {
        UUID accountId = null;
        try {
            accountId = getAccountId(request.params(":id"));
        } catch (Exception e) {
            halt(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter");
        }
        return accountDao.findById(accountId)
                .map(v -> modelMapper.map(v, AccountDto.class))
                .orElseGet(() -> {
                    halt(HttpServletResponse.SC_NOT_FOUND, "Account not found");
                    return null;
                });
    }

    private UUID getAccountId(String id) {
         return UUID.fromString(id);
    }

    public TransferDto ctrlAccountTransfer(Request request, Response response) {
        try {
            TransferPayload payload = objectMapper.readValue(request.body(), TransferPayload.class);
        } catch (Exception e) {

        }
        return null;
    }

}
