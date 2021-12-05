#!/bin/bash

HOST=http://localhost:8080

rm postalcode*.json

curl --location --request GET "$HOST/postalcodes/" --header 'Accept: application/json' > postalcodes.json
COUNT=$(cat postalcodes.json | jq length)
echo "There are '$COUNT' postalcodes"

