#!/bin/bash

# this script requires mdbtools

db="$1"
tables="$2"

if [ $# -eq 1 ];
then
	for table in `mdb-tables "$1" -1 | sed -e 's/ /---/g' `;
	do
		tablename=`echo $table | sed -e 's/---/ /g'`;
		tablefile=`echo $table | sed -e 's/---/_/g'`;
		mdb-export "$db" "$tablename"  -D '%Y-%m-%d %H:%M:%S' > "$tablefile.csv";
	done;
else
	while read table; do mdb-export "$db" "$table" > "$table.csv"; done < "$tables";
fi
