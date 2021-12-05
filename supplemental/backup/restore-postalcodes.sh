#!/bin/bash

HOST=http://localhost:8080

COUNT=$(cat postalcodes.json | jq length)
echo "There are '$COUNT' postalcodes"

for ((i=0; i<=COUNT-1; i++)); do
  echo "Processing array entry $i..."

  echo "  Reading data..."
  DATA=$(cat postalcodes.json | jq ".[$i]")

  echo "  Sending data..."
  curl --location --request POST "$HOST/postalcodes/" \
      --header 'Content-Type: application/json' \
      --silent --show-error -o /dev/null \
      --data "$DATA"
done
