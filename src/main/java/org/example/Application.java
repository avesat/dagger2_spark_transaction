package org.example;

import org.example.service.web.DaggerWebApplication;


public class Application {

    public static void main(String[] args) {
        DaggerWebApplication.create()
                .webServer()
                .run();
    }
}
