copy (
select
  p.patient_id as patient_id,
  vi.sample_id as sample_id,
  vi.sample_date as sample_date,
  nt.label as sequence_label,
  t.description as algorithm,
  tr.value as subtype
from
  regadbschema.test_result tr
  join regadbschema.patient p using(patient_ii)
  join regadbschema.nt_sequence nt using(nt_sequence_ii)
  join regadbschema.viral_isolate vi on (nt.viral_isolate_ii = vi.viral_isolate_ii)
  join regadbschema.test t using(test_ii)
  join regadbschema.test_type tt using(test_type_ii)
where
  tt.description = 'Subtype Test'
order by
  p.patient_id,
  vi.sample_date,
  nt.label,
  t.description
) to STDOUT with CSV HEADER;
