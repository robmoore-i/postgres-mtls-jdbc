# Postgres mTLS

1. `./generate.sh` need openssl >= 3
2. `docker build -t postgres-mtls .`
3. `docker compose up -d`
4. `psql "host=127.0.0.1 port=15432 user=postgres dbname=postgres sslmode=verify-full sslcert=certs/client.crt sslkey=certs/client.key sslrootcert=certs/ca.crt"`
5. `cd java-client`
6. `./gradlew run --args "postgres"`

#### Multiple roles

```
CREATE USER app LOGIN;
```

`psql "host=127.0.0.1 port=15432 user=app dbname=postgres sslmode=verify-full sslcert=certs/client.crt sslkey=certs/client.key sslrootcert=certs/ca.crt"`

`./gradlew run --args "app"`

