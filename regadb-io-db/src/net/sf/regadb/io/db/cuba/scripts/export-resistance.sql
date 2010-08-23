copy (
select
  p.patient_id as patient_id,
  vi.sample_id as sample_id,
  vi.sample_date as sample_date,
  t.description as algorithm,
  d.generic_id as drug,
  tr.value as gss
from
  regadbschema.test_result tr
  join regadbschema.patient p using(patient_ii)
  join regadbschema.viral_isolate vi using(viral_isolate_ii)
  join regadbschema.test t using(test_ii)
  join regadbschema.test_type tt using(test_type_ii)
  join regadbschema.drug_generic d using(generic_ii)
where
  tt.description = 'Genotypic Susceptibility Score (GSS)'
order by
  p.patient_id,
  vi.sample_date,
  d.generic_id,
  t.description
) to STDOUT with CSV HEADER;
