#!/bin/bash

URL="http://localhost:8080/actuator/health"
STATUS=$(curl -s $URL | jq '.status')

if [ "$STATUS" == "\"UP\"" ]; then
    echo "Wallet Service is UP ✅"
else
    echo "Wallet Service is DOWN ❌"
fi
