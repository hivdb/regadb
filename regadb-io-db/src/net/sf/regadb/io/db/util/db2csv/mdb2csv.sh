#!/bin/bash

db="$1"
tables="$2"

if [ $# -eq 1 ];
then
	for table in `mdb-tables "$1"`; do mdb-export "$db" "$table" > "$table.csv"; done;
else
	while read table; do mdb-export "$db" "$table" > "$table.csv"; done < "$tables";
fi
