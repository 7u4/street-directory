#!/bin/bash

PARALLEL_JOBS=2

COUNT=$(cat streets.json | jq length)
echo "There are '$COUNT' streets"

cat streets.json | jq --unbuffered -c ".[]" | parallel -j $PARALLEL_JOBS --progress --block 1 --pipe ./restore-street.sh
