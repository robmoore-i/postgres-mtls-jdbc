# Postgres mTLS

1. `./generate.sh` need openssl >= 3
2. `docker build -t postgres-mtls .`
3. `docker-compose up -d`
4. `psql "host=127.0.0.1 port=5432 user=postgres dbname=postgres sslmode=verify-full sslcert=certs/client.crt sslkey=certs/client.key sslrootcert=certs/ca.crt"`
