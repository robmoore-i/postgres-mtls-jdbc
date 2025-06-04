#!/bin/bash

#  create certs directory if not exists
mkdir -p certs

# generate root private key
openssl ecparam -name prime256v1 -genkey -noout -out certs/ca.key

# create root certificate
openssl req -x509 -key certs/ca.key -subj "/CN=root" -days 3650 -out certs/ca.crt

# generate server private key
openssl ecparam -name prime256v1 -genkey -noout -out certs/server.key

# create server CSR
openssl req -new -key certs/server.key -subj "/CN=server" -addext "subjectAltName=DNS:127.0.0.1,DNS:postgres-mtls,IP:127.0.0.1" -out certs/server.csr

# create server certificate
openssl x509 -req -in certs/server.csr -days 3650 -CA certs/ca.crt -CAkey certs/ca.key -CAcreateserial -copy_extensions copy -out certs/server.crt

# generate client private key
openssl ecparam -name prime256v1 -genkey -noout -out certs/client.key

# create client CSR
openssl req -new -key certs/client.key -subj "/CN=postgres" -out certs/client.csr

# create client certificate
openssl x509 -req -in certs/client.csr -days 3650 -CA certs/ca.crt -CAkey certs/ca.key -CAcreateserial -out certs/client.crt

# convert client private key to DER
openssl pkcs8 -topk8 -inform PEM -outform DER -in certs/client.key -out certs/client.pk8 -nocrypt

# create client pkcs12 file
openssl pkcs12 -export -in certs/client.crt -inkey certs/client.key -out certs/client.p12 -CAfile certs/ca.crt -name user -passout "pass:changeit"
