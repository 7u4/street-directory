#!/bin/bash

# usage:
# curl -o streets.json http://localhost:8080/streets/
# cat streets.json | post-streets.sh

BASE_URL=http://localhost:8080

args=( -s -S --output /dev/null -d @- -H "Content-Type: application/json" )

while IFS= read -r value; do
  #echo "Processing: $value" >&2
  #curl "${args[@]}" $BASE_URL/streets/ <<< "$value"
  curl "${args[@]}" $BASE_URL/streets/ <<< "$value" & echo test
done < <( jq --compact-output '.[] | { postalcode: .postalcode, centerLatitude:.centerLatitude, centerLongitude:.centerLongitude, streetname:.streetname }' )
