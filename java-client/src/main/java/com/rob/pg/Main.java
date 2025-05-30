package com.rob.pg;

import org.postgresql.PGProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class Main {

    private static final String CERTS = Objects.requireNonNull(System.getProperty("certs.dir"), "Missing system property 'certs.dir'.");
    private static final String CERTS_PROVIDER = Objects.requireNonNull(System.getProperty("certs.provider"), "Missing system property 'certs.provider'.");

    public static void main(String[] args) throws SQLException, IOException {
        var certs = new File(CERTS);
        if (!certs.isDirectory()) {
            throw new IllegalArgumentException("Certs directory '" + CERTS + "' is not a directory.");
        }
        if (args.length == 0) {
            throw new IllegalArgumentException("First argument must be the name of the database user to connect as.");
        }
        var dbUser = args[0];
        var dbHost = Optional.ofNullable(System.getenv("DATABASE_HOST")).orElse("127.0.0.1");
        var dbPort = Optional.ofNullable(System.getenv("DATABASE_PORT")).orElse("15432");
        var url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/postgres?user=" + dbUser + "&ssl=true&sslmode=verify-full";

        var properties = new Properties();
        setCertificateProperties(certs, properties);

        try (var conn = DriverManager.getConnection(url, properties); var statement = conn.createStatement()) {
            var query = "SELECT current_user";
            System.out.println("Running query: '" + query + "'");
            try (var results = statement.executeQuery(query)) {
                results.next();
                System.out.println("Current user is: " + results.getString(1));
            }
        }
    }

    /**
     * Three options demonstrated here for setting up the JDBC driver:
     * 1. Use PEM format root and client certificates and a PKCS#8 format client private key.
     * 2. Use PEM format root certificate and a PKCS#12 format file containing both client certificate and private key, with a mandatory passphrase.
     * 3. If you are configuring the driver without having any ability to set properties on the JDBC driver programmatically,
     * you can still configure it by writing the TLS material files to the correct locations, where they will be picked up automatically.
     */
    private static void setCertificateProperties(File certs, Properties properties) throws IOException {
        switch (CERTS_PROVIDER) {
            case "pkcs8":
                System.out.println("Using PEM root and client certificates and PKCS#8 private key in directory '" + CERTS + "'.");
                properties.setProperty(PGProperty.SSL_CERT.getName(), new File(certs, "client.crt").getAbsolutePath());
                properties.setProperty(PGProperty.SSL_KEY.getName(), new File(certs, "client.pk8").getAbsolutePath());
                properties.setProperty(PGProperty.SSL_ROOT_CERT.getName(), new File(certs, "ca.crt").getAbsolutePath());
                break;
            case "pkcs12":
                System.out.println("Using PEM root certificate and PKCS#12 combined client certificate and private key in directory '" + CERTS + "'.");
                properties.setProperty(PGProperty.SSL_PASSWORD.getName(), "changeit");
                properties.setProperty(PGProperty.SSL_KEY.getName(), new File(certs, "client.p12").getAbsolutePath());
                properties.setProperty(PGProperty.SSL_ROOT_CERT.getName(), new File(certs, "ca.crt").getAbsolutePath());
                break;
            case "user.home":
                var userHome = new File(System.getProperty("user.home"));
                var postgresDefaultDir = new File(userHome, ".postgresql");
                System.out.println("Using certificates under user home directory '" + postgresDefaultDir.getAbsolutePath() + "'.");
                //noinspection ResultOfMethodCallIgnored
                postgresDefaultDir.mkdirs();
                copy(new File(certs, "client.crt"), new File(postgresDefaultDir, "postgresql.crt"));
                copy(new File(certs, "client.pk8"), new File(postgresDefaultDir, "postgresql.pk8"));
                copy(new File(certs, "ca.crt"), new File(postgresDefaultDir, "root.crt"));
                break;
            default:
                throw new IllegalArgumentException("Unsupported certs.provider system property value '" + CERTS_PROVIDER + "'.");
        }
    }

    private static void copy(File fromFile, File toFile) throws IOException {
        Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
