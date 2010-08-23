#!/bin/bash

USER=regadb_user
DATABASE=regadb
OUTPUT_DIR=/home/garcia/backup

FILENAME="$DATABASE_`date +'%Y-%m-%d_%H-%M'`.pg"
FULLPATH="$OUTPUT_DIR/$FILENAME"

pg_dump -U $USER $DATABASE -F custom -f "$FULLPATH"

echo "$FULLPATH"
