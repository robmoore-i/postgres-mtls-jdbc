# Postgres mTLS

1. `./bounce.sh` (requires openssl >= 3)
2. `psql "host=127.0.0.1 port=15432 user=postgres dbname=postgres sslmode=verify-full sslcert=certs/client.crt sslkey=certs/client.key sslrootcert=certs/ca.crt"`
3. `cd java-client`
4. `./gradlew run --args "postgres"`

#### Multiple roles

1. `CREATE USER app LOGIN;`
2. `psql "host=127.0.0.1 port=15432 user=app dbname=postgres sslmode=verify-full sslcert=certs/client.crt sslkey=certs/client.key sslrootcert=certs/ca.crt"`
3. `./gradlew run --args "app"`

#### From a container

1. `./gradlew dockerBuild`
2. `docker run --rm --network postgres_mtls postgres-mtls-jdbc app`
