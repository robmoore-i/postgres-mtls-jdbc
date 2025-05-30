#!/bin/bash
docker compose down
./generate.sh
docker build -t postgres-mtls .
docker compose up -d
