#!/bin/bash

HOST=http://localhost:8080

DATA=$(cat)

echo "Sending data..."
curl --location --request POST "$HOST/streets/" \
    --header 'Content-Type: application/json' \
    --silent --show-error -o /dev/null \
    --data "$DATA"
