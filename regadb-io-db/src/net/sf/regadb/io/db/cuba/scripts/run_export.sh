#!/bin/bash

USER="regadb_user"
DATABASE="regadb"
OUTPUT_DIR="/home/garcia/export"

CRON_PATH=/home/garcia/cron

rm "$OUTPUT_DIR/errors.txt"
psql $DATABASE -U $USER -f "$CRON_PATH/export-resistance.sql" 2> "$OUTPUT_DIR/errors.txt" 1> "$OUTPUT_DIR/resistance.csv"
psql $DATABASE -U $USER -f "$CRON_PATH/export-subtypes.sql" 2>> "$OUTPUT_DIR/errors.txt" 1> "$OUTPUT_DIR/subtypes.csv"

