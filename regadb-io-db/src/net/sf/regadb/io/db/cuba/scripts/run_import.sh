#!/bin/bash

REGADB_USER="import_user"
REGADB_PASS="ksIG87re"
REGADB_DATASET="Cuba"

IMPORT_NEW="/home/garcia/import/new"
IMPORT_OLD="/home/garcia/import/old"
BACKUP_DIR="/home/garcia/backup"

JAVA_DIR="/home/garcia/cron/java"

CD="$IMPORT_NEW/cd4text.txt"
VL="$IMPORT_NEW/cvtext.txt"
TH="$IMPORT_NEW/ttotext.txt"
DR="$IMPORT_NEW/combtext.txt"
PT="$IMPORT_NEW/pvihtext.txt"
XML="$IMPORT_NEW/patients.xml"

OUT="$IMPORT_NEW/out.txt"

if [[    -f "$CD"
      && -f "$VL"
      && -f "$TH"
      && -f "$DR"
      && -f "$PT" ]]
then

  echo "creating backup" > "$OUT"
  "$BACKUP_DIR/create_backup.sh"

  echo "importing new files from $IMPORT_NEW" >> "$OUT"

  echo "creating xml" >> "$OUT"
  java -Xmx512m -Duser.timezone="GMT" -jar "$JAVA_DIR/regadb-io-db/regadb-import-cuba.jar" "$IMPORT_NEW" "$JAVA_DIR" "$IMPORT_NEW" 1>> "$OUT" 2>> "$OUT"

  echo "importing xml" >> "$OUT"
  java -Xmx512m -Duser.timezone="GMT" -jar "$JAVA_DIR/regadb-io-db/regadb-io-patient-xmlimport.jar" -dont-delete ViralIsolate "$XML" "$REGADB_USER" "$REGADB_PASS" "$REGADB_DATASET" 1>> "$OUT" 2>> "$OUT"

  OLD_DIR="$IMPORT_OLD/`date +'%Y-%m-%d_%H-%M'`"
  echo "moving import files to $OLD_DIR" >> "$OUT"
  mkdir -p "$OLD_DIR"
  mv "$CD" "$VL" "$TH" "$DR" "$PT" "$XML" "$OUT" "$OLD_DIR"
  
else
  echo "no new import files found in $IMPORT_NEW"
fi

