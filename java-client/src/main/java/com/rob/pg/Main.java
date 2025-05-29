package com.rob.pg;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws SQLException {
        var certs = new File(args[0]);
        if (!certs.isDirectory()) {
            throw new IllegalArgumentException("First argument must be the certs directory.");
        }

        var url = "jdbc:postgresql://127.0.0.1:15432/postgres";
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("ssl", "true");
        properties.setProperty("sslmode", "verify-full");
        properties.setProperty("sslcert", new File(certs, "client.crt").getAbsolutePath());
        properties.setProperty("sslkey", new File(certs, "client.pk8").getAbsolutePath());
        properties.setProperty("sslrootcert", new File(certs, "ca.crt").getAbsolutePath());
        try (var conn = DriverManager.getConnection(url, properties); var statement = conn.createStatement()) {
            var query = "SELECT current_user";
            System.out.println("Running query: '" + query + "'");
            try(var results = statement.executeQuery(query)) {
                results.next();
                System.out.println("Current user is: " + results.getString(1));
            }
        }
    }
}
