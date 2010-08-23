#!/bin/bash

USER="regadb_user"
DATABASE="regadb"

FULLPATH="$1"

if [[ -f "$FULLPATH" ]]
then
  pg_restore -U $USER -d $DATABASE -c "$FULLPATH"
else
  echo "usage: $0 <database_dump.pg>"
fi

