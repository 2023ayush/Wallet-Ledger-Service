#!/bin/bash

# Create Docker network if it doesn't exist
docker network inspect wallet-net >/dev/null 2>&1 || docker network create wallet-net

echo "Starting Wallet MySQL container..."
docker run -d --name wallet-mysql --network wallet-net \
  -e MYSQL_ROOT_PASSWORD=test -e MYSQL_DATABASE=walletdb mysql:8.0

echo "Waiting for MySQL to initialize..."
sleep 15

echo "Starting Wallet Service container..."
docker run -d --name wallet-service --network wallet-net -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://wallet-mysql:3306/walletdb \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=test \
  ayush4857/wallet-service:latest

echo "Tailing Wallet Service logs..."
docker logs -f wallet-service
