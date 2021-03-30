#!/bin/bash

# usage:
# curl -o backup.json http://localhost:8080/postalcodes/
# cat postalcodes.json | post-postalcodes.sh

BASE_URL=http://localhost:8080

args=( -d @- -H "Content-Type: application/json" )

while IFS= read -r value; do
  echo "Processing: $value" >&2
  curl "${args[@]}" $BASE_URL/postalcodes/ <<< "$value"
done < <( jq --compact-output '.[] | { code: .code, centerLatitude:.centerLatitude, centerLongitude:.centerLongitude, note:.note }' )