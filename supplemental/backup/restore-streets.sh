#!/bin/bash

HOST=http://localhost:8080

COUNT=$(cat streets.json | jq length)
echo "There are '$COUNT' streets"

for ((i=0; i<=COUNT-1; i++)); do
  echo "Processing array entry $i..."

  echo "  Reading data..."
  DATA=$(cat streets.json | jq ".[$i]")

  echo "  Sending data..."
  curl --location --request POST "$HOST/streets/" \
      --header 'Content-Type: application/json' \
      --silent --show-error -o /dev/null \
      --data "$DATA"
done
