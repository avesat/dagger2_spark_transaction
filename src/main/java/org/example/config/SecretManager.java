package org.example.config;

public class SecretManager {
    private static final String DB_URL = "jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}
