#!/bin/bash

HOST=http://localhost:8080

rm street*.json

curl --location --request GET "$HOST/streets/" --header 'Accept: application/json' > streets.json
COUNT=$(cat streets.json | jq length)
echo "There are '$COUNT' streets"
