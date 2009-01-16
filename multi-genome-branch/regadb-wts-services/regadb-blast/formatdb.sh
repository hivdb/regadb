#!/bin/bash

tmpdir=tmp
dbbasedir=db
speciesdir=species

rm -rf "$tmpdir"
mkdir "$tmpdir"
touch "$tmpdir/all.fasta"
cat "$speciesdir"/*.fasta > "$tmpdir/all.fasta"
formatdb -i "$tmpdir/all.fasta" -p F -n all

mv all.* formatdb.log "$tmpdir/"
dbdir="$dbbasedir/"`date +'%Y%m%d'`

mv "$tmpdir" "$dbdir"