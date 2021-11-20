package org.example.service.web;

import static spark.Spark.halt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.service.management.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import spark.Route;
import spark.Service;

@Singleton
public class WebServer {
    AccountService accountService;

    ObjectMapper objectMapper;

    @Inject
    WebServer(AccountService accountService, ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    public void run() {
        Service server = Service.ignite().port(8080);

        server.get("management/v1/accounts/:id", "application/json",
                response(accountService::ctrlGetAccount));

        server.post("management/v1/accounts/:id/transfer", "application/json",
                response(accountService::ctrlAccountTransfer));
    }

    public Route response(Route route) {
        return (request, response) -> {
            try {
                Object returnObject = route.handle(request, response);
                return objectMapper.writeValueAsString(returnObject);
            } catch (JsonProcessingException e) {
                return halt(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
            }
        };
    }

}
