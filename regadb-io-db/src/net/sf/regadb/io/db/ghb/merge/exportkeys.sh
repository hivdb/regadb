#!/bin/bash

DB=$1
rm -r $DB
mkdir $DB

function exp {
  psql $DB -c "COPY ($2) TO STDOUT WITH CSV HEADER" > $DB/$1.csv
}

exp patient "select patient_id from regadbschema.patient order by patient_id"
exp therapy "select patient_id, start_date, stop_date, comment, value from regadbschema.patient join regadbschema.therapy using(patient_ii) left outer join regadbschema.therapy_motivation using(therapy_motivation_ii) order by patient_id, start_date, stop_date"
exp therapy_commercial "select patient_id, start_date, stop_date, name, day_dosage_units, placebo, blind, frequency from regadbschema.patient join regadbschema.therapy using(patient_ii) join regadbschema.therapy_commercial using(therapy_ii) join regadbschema.drug_commercial using (commercial_ii) order by patient_id, start_date, stop_date, name"
exp therapy_generic "select patient_id, start_date, stop_date, generic_id, day_dosage_mg, placebo, blind, frequency from regadbschema.patient join regadbschema.therapy using(patient_ii) join regadbschema.therapy_generic using(therapy_ii) join regadbschema.drug_generic using (generic_ii) order by patient_id, start_date, stop_date, generic_id"
exp attribute "select patient_id, name, pav.value, anv.value from regadbschema.patient_attribute_value pav join regadbschema.patient using(patient_ii) join regadbschema.attribute a using (attribute_ii) left outer join regadbschema.attribute_nominal_value anv on (pav.nominal_value_ii = anv.nominal_value_ii) order by patient_id, name, pav.value, pav.nominal_value_ii"
exp event "select patient_id, start_date, end_date, name, pev.value, env.value from regadbschema.patient_event_value pev join regadbschema.patient using(patient_ii) join regadbschema.event e using (event_ii) left outer join regadbschema.event_nominal_value env on (pev.nominal_value_ii = env.nominal_value_ii) order by patient_id, start_date, end_date, name, pev.value, env.value"
exp viralisolate "select patient_id, sample_id, sample_date from regadbschema.viral_isolate join regadbschema.patient using(patient_ii) order by patient_id, sample_date"
