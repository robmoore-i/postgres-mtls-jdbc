package com.rob.pg;

import org.postgresql.PGProperty;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    private static final String CERTS = System.getProperty("certs.dir");

    public static void main(String[] args) throws SQLException {
        if (CERTS == null) {
            throw new IllegalArgumentException("Missing system property 'certs.dir'.");
        }
        var certs = new File(CERTS);
        if (!certs.isDirectory()) {
            throw new IllegalArgumentException("Certs directory '" + CERTS + "' is not a directory.");
        }
        if (args.length == 0) {
            throw new IllegalArgumentException("First argument must be the name of the database user to connect as.");
        }
        var dbUser = args[0];

        var url = "jdbc:postgresql://127.0.0.1:15432/postgres";
        var properties = new Properties();
        properties.setProperty(PGProperty.USER.getName(), dbUser);
        properties.setProperty(PGProperty.SSL.getName(), "true");
        properties.setProperty(PGProperty.SSL_MODE.getName(), "verify-full");
        properties.setProperty(PGProperty.SSL_CERT.getName(), new File(certs, "client.crt").getAbsolutePath());
        properties.setProperty(PGProperty.SSL_KEY.getName(), new File(certs, "client.pk8").getAbsolutePath());
        properties.setProperty(PGProperty.SSL_ROOT_CERT.getName(), new File(certs, "ca.crt").getAbsolutePath());
        try (var conn = DriverManager.getConnection(url, properties); var statement = conn.createStatement()) {
            var query = "SELECT current_user";
            System.out.println("Running query: '" + query + "'");
            try (var results = statement.executeQuery(query)) {
                results.next();
                System.out.println("Current user is: " + results.getString(1));
            }
        }
    }
}
