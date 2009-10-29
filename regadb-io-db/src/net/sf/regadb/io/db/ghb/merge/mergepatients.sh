#!/bin/bash

#select a.patient_id, b.patient_id from patient a, patient b where a.patient_ii != b.patient_ii and a.patient_id !~ '^[0-9]+$' and b.patient_id = regexp_replace(a.patient_id,'[^0-9]','','g');

DB=$1

function rd { #remove duplicates from table(table, key, from_ii, to_ii)
  psql $DB -c "delete from regadbschema.$1 where $2 in (select $2 from regadbschema.$1 where patient_ii = $4) and patient_ii = $3";
}
function ii { #table, from_ii, to_ii
  psql $DB -c "update regadbschema.$1 set patient_ii = $3 where patient_ii = $2 "
}
function rp { #delete patient from table (table, from_ii)
  psql $DB -c "delete from regadbschema.$1 where patient_ii = $2" 
}

function merge {
  rd patient_attribute_value attribute_ii $1 $2
  ii patient_attribute_value $1 $2
  rd patient_event_value event_ii $1 $2
  ii patient_event_value $1 $2
  ii test_result $1 $2
  ii viral_isolate $1 $2
  ii therapy $1 $2
  rp patient_dataset $1
  rp patient $1
}


for i in `psql -A -t -F ',' $DB -c "select a.patient_ii, a.patient_id, b.patient_ii, b.patient_id from regadbschema.patient a, regadbschema.patient b where a.patient_ii != b.patient_ii and a.patient_id "'!~'" '^[0-9]+\$' and b.patient_id = regexp_replace(a.patient_id,'[^0-9]','','g')"`
do
  fii=`echo $i | cut -d , -f 1`
  fid=`echo $i | cut -d , -f 2`
  tii=`echo $i | cut -d , -f 3`
  tid=`echo $i | cut -d , -f 4`
  echo "$fid ($fii) to $tid ($tii)"
  merge $fii $tii
done

