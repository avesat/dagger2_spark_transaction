package org.example.util;

import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

@UtilityClass
public class DataBaseUtil {
    private final static String CREATE_TABLE_ACCOUNT =
            "CREATE TABLE IF NOT EXISTS account (\n" +
                    "  id            UUID NOT NULL,\n" +
                    "  balance       DECIMAL(10, 2),\n" +
                    "  CONSTRAINT account_pk PRIMARY KEY (id)\n" +
                    ");";

    public static void createTableAccount(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(CREATE_TABLE_ACCOUNT);
        } catch (SQLException e) {
//            throw new Exception();
            System.out.println("Can't create account table");
        }
    }
}
