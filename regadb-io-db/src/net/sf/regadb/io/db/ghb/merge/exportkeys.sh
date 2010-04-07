#!/bin/bash

DB=$1
rm -r $DB
mkdir $DB

function exp {
  psql $DB -c "set search_path to regadbschema; COPY ($2) TO STDOUT WITH CSV HEADER" > $DB/$1.csv
}

exp patient "select patient_id from patient order by patient_id"
exp therapy "select patient_id, start_date, stop_date, comment, value from patient join therapy using(patient_ii) left outer join therapy_motivation using(therapy_motivation_ii) order by patient_id, start_date, stop_date, comment, value"
exp therapy_commercial "select patient_id, start_date, stop_date, name, day_dosage_units, placebo, blind, frequency from patient join therapy using(patient_ii) join therapy_commercial using(therapy_ii) join drug_commercial using (commercial_ii) order by patient_id, start_date, stop_date, name"
exp therapy_generic "select patient_id, start_date, stop_date, generic_id, day_dosage_mg, placebo, blind, frequency from patient join therapy using(patient_ii) join therapy_generic using(therapy_ii) join drug_generic using (generic_ii) order by patient_id, start_date, stop_date, generic_id"
exp attribute "select patient_id, name, pav.value, anv.value from patient_attribute_value pav join patient using(patient_ii) join attribute a using (attribute_ii) left outer join attribute_nominal_value anv on (pav.nominal_value_ii = anv.nominal_value_ii) order by patient_id, name, pav.value, pav.nominal_value_ii"
exp event "select patient_id, start_date, end_date, name, pev.value, env.value from patient_event_value pev join patient using(patient_ii) join event e using (event_ii) left outer join event_nominal_value env on (pev.nominal_value_ii = env.nominal_value_ii) order by patient_id, start_date, end_date, name, pev.value, env.value"
exp viralisolate "select patient_id, sample_id, sample_date from viral_isolate join patient using(patient_ii) order by patient_id, sample_date"
exp testresult "select patient_id, tt.description, t.description, organism_name, tr.test_date, tr.value, tnv.value, tr.sample_id from test_result tr join patient p using(patient_ii) join test t using(test_ii) join test_type tt using(test_type_ii) left outer join genome g using(genome_ii) left outer join test_nominal_value tnv using(nominal_value_ii) where tr.viral_isolate_ii is null and tr.nt_sequence_ii is null order by patient_id, tt.description, t.description, organism_name, tr.test_date, tr.value, tnv.value, sample_id"
