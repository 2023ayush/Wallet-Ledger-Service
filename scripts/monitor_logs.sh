#!/bin/bash

echo "Monitoring Wallet Service logs..."
docker logs -f wallet-service | grep --line-buffered -i "ERROR\|WARN"
