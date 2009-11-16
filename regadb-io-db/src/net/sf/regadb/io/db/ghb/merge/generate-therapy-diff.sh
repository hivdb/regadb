#!/bin/bash

function printUsage {
  echo "Usage: [-e] db1 db2" >&2
}

if [ $# -lt 2 ]
then
  printUsage
  exit 1
fi

if [ $# -gt 2 ]
then
  if [ "$1" = "-e" ]
  then
    DB1="$2"
    DB2="$3"
    ./exportkeys.sh "$2"
    ./exportkeys.sh "$3"
  else
    printUsage
    exit 1
  fi
else
  DB1="$1"
  DB2="$2"
fi

DB1="`echo $DB1 | sed -e 's/\/$//'`"
DB2="`echo $DB2 | sed -e 's/\/$//'`"

diff "$DB1/therapy.csv" "$DB2/therapy.csv" > "$DB1-$DB2-therapy.diff"
diff "$DB1/therapy_commercial.csv" "$DB2/therapy_commercial.csv" > "$DB1-$DB2-therapy_commercial.diff"
diff "$DB1/therapy_generic.csv" "$DB2/therapy_generic.csv" > "$DB1-$DB2-therapy_generic.diff"

cut -d , -f 1,2,3 "$DB1-$DB2-therapy.diff" "$DB1-$DB2-therapy_commercial.diff" "$DB1-$DB2-therapy_generic.diff" | grep '[><]' | sort -u > "$DB1-$DB2-all-therapy.diff"


