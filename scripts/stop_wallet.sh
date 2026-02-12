#!/bin/bash

echo "Stopping Wallet Service and MySQL containers..."
docker stop wallet-service wallet-mysql
docker rm wallet-service wallet-mysql
echo "Containers removed."
