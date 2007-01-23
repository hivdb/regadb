-- test_object stuff

CREATE TABLE test_object (
    test_object_ii serial NOT NULL,
    description character varying(50) NOT NULL
);

REVOKE ALL ON TABLE test_object FROM PUBLIC;
GRANT SELECT ON TABLE test_object TO GROUP researchers;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE test_object TO GROUP clinicians;

REVOKE ALL ON TABLE test_object_test_object_ii_seq FROM PUBLIC;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE test_object_test_object_ii_seq TO GROUP clinicians;
GRANT SELECT ON TABLE test_object_test_object_ii_seq TO GROUP researchers;

COPY test_object (test_object_ii, description) FROM stdin;
1	Patient test
2	Resistance test
3	Sequence analysis
4	Generic drug test
\.

ALTER TABLE test_type ADD COLUMN test_object_ii integer;

UPDATE test_type SET test_object_ii = 1 WHERE resistance_test = FALSE;
UPDATE test_type SET test_object_ii = 2 WHERE resistance_test = TRUE;
ALTER TABLE test_type ALTER COLUMN test_object_ii SET NOT NULL;

ALTER TABLE ONLY test_object
    ADD CONSTRAINT test_object_pkey PRIMARY KEY (test_object_ii);
ALTER TABLE ONLY test_type ADD CONSTRAINT "FK_test_type_test_object" FOREIGN KEY (test_object_ii) REFERENCES test_object(test_object_ii) ON UPDATE CASCADE;
ALTER TABLE ONLY test_type DROP COLUMN resistance_test;

-- isolate stuff

ALTER TABLE ONLY nt_sequence DROP CONSTRAINT "FK_nt_sequence_patient";

CREATE TABLE viral_isolate (
    viral_isolate_ii serial NOT NULL,
    patient_ii integer,
    sample_id character varying(50),
    sample_date date
);

REVOKE ALL ON TABLE viral_isolate FROM PUBLIC;
GRANT SELECT ON TABLE viral_isolate TO GROUP researchers;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE viral_isolate TO GROUP clinicians;

REVOKE ALL ON TABLE viral_isolate_viral_isolate_ii_seq FROM PUBLIC;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE viral_isolate_viral_isolate_ii_seq TO GROUP clinicians;
GRANT SELECT ON TABLE viral_isolate_viral_isolate_ii_seq TO GROUP researchers;

ALTER TABLE nt_sequence ADD COLUMN viral_isolate_ii integer;

DELETE FROM nt_sequence WHERE nt_sequence_ii IN ( SELECT v1.nt_sequence_ii FROM nt_sequence v1 natural join patient, nt_sequence v2 where v1.nt_sequence_ii != v2.nt_sequence_ii and v1.sample_id = v2.sample_id and v1.sample_date = v2.sample_date and v2.patient_ii = v2.patient_ii AND v1.nt_sequence_ii > v2.nt_sequence_ii AND v1.nucleotides = v2.nucleotides );

INSERT INTO viral_isolate (patient_ii, sample_id, sample_date) SELECT patient_ii, sample_id, sample_date FROM nt_sequence;

UPDATE nt_sequence SET viral_isolate_ii = ( SELECT viral_isolate_ii FROM viral_isolate v WHERE v.patient_ii = nt_sequence.patient_ii AND v.sample_id = nt_sequence.sample_id AND v.sample_date = nt_sequence.sample_date );

ALTER TABLE ONLY nt_sequence DROP COLUMN sample_date CASCADE;
ALTER TABLE ONLY nt_sequence DROP COLUMN sample_id CASCADE;
ALTER TABLE ONLY nt_sequence DROP COLUMN patient_ii CASCADE;

ALTER TABLE viral_isolate ALTER COLUMN viral_isolate_ii SET NOT NULL;
ALTER TABLE viral_isolate ALTER COLUMN patient_ii SET NOT NULL;
ALTER TABLE nt_sequence ALTER COLUMN viral_isolate_ii SET NOT NULL;

ALTER TABLE ONLY viral_isolate
    ADD CONSTRAINT viral_isolate_pkey PRIMARY KEY (viral_isolate_ii);

ALTER TABLE ONLY nt_sequence ADD CONSTRAINT "FK_nt_sequence_viral_isolate" FOREIGN KEY (viral_isolate_ii) REFERENCES viral_isolate(viral_isolate_ii) ON UPDATE CASCADE;
ALTER TABLE ONLY viral_isolate ADD CONSTRAINT "FK_viral_isolate_patient" FOREIGN KEY (patient_ii) REFERENCES patient(patient_ii) ON UPDATE CASCADE;

-- test

ALTER TABLE nominal_value RENAME TO attribute_nominal_value;

ALTER TABLE ONLY test_result ADD COLUMN nominal_value_ii integer;

CREATE TABLE test_nominal_value (
    nominal_value_ii serial NOT NULL,
    test_type_ii     integer NOT NULL,
    value            character varying(100) NOT NULL
);

REVOKE ALL ON TABLE test_nominal_value FROM PUBLIC;
GRANT SELECT ON TABLE test_nominal_value TO GROUP researchers;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE test_nominal_value TO GROUP clinicians;

REVOKE ALL ON TABLE test_nominal_value_nominal_value_ii_seq FROM PUBLIC;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE test_nominal_value_nominal_value_ii_seq TO GROUP clinicians;
GRANT SELECT ON TABLE test_nominal_value_nominal_value_ii_seq TO GROUP researchers;

ALTER TABLE ONLY test_nominal_value
    ADD CONSTRAINT test_nominal_value_pkey PRIMARY KEY (nominal_value_ii);
ALTER TABLE ONLY test_nominal_value ADD CONSTRAINT "FK_test_nominal_value_test_type" FOREIGN KEY (test_type_ii) REFERENCES test_type(test_type_ii) ON UPDATE CASCADE;

-- attribute

CREATE TABLE patient_attribute_value (
    patient_ii       integer NOT NULL,
    attribute_ii     integer NOT NULL,
    value            character varying(100),
    nominal_value_ii integer
);

REVOKE ALL ON TABLE patient_attribute_value FROM PUBLIC;
GRANT SELECT ON TABLE patient_attribute_value TO GROUP researchers;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE patient_attribute_value TO GROUP clinicians;

ALTER TABLE ONLY patient_attribute_value
    ADD CONSTRAINT patient_attribute_value_pkey PRIMARY KEY(patient_ii, attribute_ii);
ALTER TABLE ONLY patient_attribute_value ADD CONSTRAINT "FK_patient_attribute_value_patient" FOREIGN KEY (patient_ii) REFERENCES patient(patient_ii) ON UPDATE CASCADE;
ALTER TABLE ONLY patient_attribute_value ADD CONSTRAINT "FK_patient_attribute_value_attribute" FOREIGN KEY (attribute_ii) REFERENCES attribute(attribute_ii) ON UPDATE CASCADE;

INSERT INTO patient_attribute_value (patient_ii, attribute_ii, value) SELECT patient_ii, attribute_ii, value FROM patient_other_attribute_value;
INSERT INTO patient_attribute_value (patient_ii, attribute_ii, nominal_value_ii) SELECT patient_ii, attribute_ii, nominal_value_ii FROM patient_nominal_attribute_value;

-- expand test result to also sequence results, drug results, resistance result

ALTER TABLE ONLY test_result ADD COLUMN viral_isolate_ii integer;
ALTER TABLE ONLY test_result ADD COLUMN generic_ii  integer;

ALTER TABLE ONLY test_result ADD CONSTRAINT "FK_test_result_viral_isolate" FOREIGN KEY (viral_isolate_ii) REFERENCES viral_isolate(viral_isolate_ii) ON UPDATE CASCADE;
ALTER TABLE ONLY test_result ADD CONSTRAINT "FK_test_result_drug_generic" FOREIGN KEY (generic_ii) REFERENCES drug_generic(generic_ii) ON UPDATE CASCADE;

-- alter test to support services

ALTER TABLE ONLY test DROP COLUMN value_type_ii;
ALTER TABLE ONLY test RENAME COLUMN resistance_software to service_class;
ALTER TABLE ONLY test RENAME COLUMN resistance_software_data to service_data;
ALTER TABLE ONLY test ADD COLUMN service_config text;

-- drop stupid tables

DROP TABLE resistance_test_position;
DROP TABLE resistance_result;

-- ranged values and value lists

ALTER TABLE ONLY value_type ADD COLUMN min double precision;
ALTER TABLE ONLY value_type ADD COLUMN max double precision;
ALTER TABLE ONLY value_type ADD COLUMN multiple boolean;
--ALTER TABLE ONLY nt_sequence DROP COLUMN hiv_type;
--ALTER TABLE ONLY nt_sequence DROP COLUMN hiv_subtype;

-- add dataset stuff

CREATE TABLE dataset (
    dataset_ii serial NOT NULL,
    description character varying(50) NOT NULL,
    creation_date date NOT NULL,
    closed_date date
);

REVOKE ALL ON TABLE dataset FROM PUBLIC;
GRANT SELECT ON TABLE dataset TO GROUP researchers;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE dataset TO GROUP clinicians;

REVOKE ALL ON TABLE dataset_dataset_ii_seq FROM PUBLIC;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE dataset_dataset_ii_seq TO GROUP clinicians;
GRANT SELECT ON TABLE dataset_dataset_ii_seq TO GROUP researchers;

ALTER TABLE ONLY dataset
    ADD CONSTRAINT dataset_pkey PRIMARY KEY(dataset_ii);

CREATE TABLE patient_dataset (
    dataset_ii integer NOT NULL,
    patient_ii integer NOT NULL
);

REVOKE ALL ON TABLE patient_dataset FROM PUBLIC;
GRANT SELECT ON TABLE patient_dataset TO GROUP researchers;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE patient_dataset TO GROUP clinicians;

ALTER TABLE ONLY patient_dataset
    ADD CONSTRAINT patient_dataset_pkey PRIMARY KEY(dataset_ii, patient_ii);

ALTER TABLE ONLY patient_dataset ADD CONSTRAINT "FK_patient_dataset_patient" FOREIGN KEY (patient_ii) REFERENCES patient(patient_ii) ON UPDATE CASCADE;
ALTER TABLE ONLY patient_dataset ADD CONSTRAINT "FK_patient_dataset_dataset" FOREIGN KEY (dataset_ii) REFERENCES dataset(dataset_ii) ON UPDATE CASCADE;

-- user settings

ALTER TABLE ONLY settings_user ADD COLUMN source_dataset_ii integer;

ALTER TABLE ONLY settings_user ADD CONSTRAINT "FK_source_dataset_dataset" FOREIGN KEY (source_dataset_ii) REFERENCES dataset(dataset_ii) ON UPDATE CASCADE;

CREATE TABLE dataset_access (
    uid character varying(50) NOT NULL,
    dataset_ii integer NOT NULL,
    permissions integer NOT NULL
);

REVOKE ALL ON TABLE dataset_access FROM PUBLIC;
GRANT SELECT ON TABLE dataset_access TO GROUP researchers;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE dataset_access TO GROUP clinicians;

ALTER TABLE ONLY dataset_access
    ADD CONSTRAINT dataset_access_pkey PRIMARY KEY(uid, dataset_ii);

ALTER TABLE ONLY dataset_access ADD CONSTRAINT "FK_dataset_access_settings_user" FOREIGN KEY (uid) REFERENCES settings_user(uid) ON UPDATE CASCADE;
ALTER TABLE ONLY dataset_access ADD CONSTRAINT "FK_dataset_access_dataset" FOREIGN KEY (dataset_ii) REFERENCES dataset(dataset_ii) ON UPDATE CASCADE;

INSERT INTO dataset (description, creation_date) SELECT DISTINCT source, CURRENT_DATE FROM patient;

INSERT INTO patient_dataset (dataset_ii, patient_ii) SELECT dataset_ii, patient_ii FROM patient, dataset WHERE source = description;

ALTER TABLE patient DROP COLUMN source;

-- version stuff

ALTER TABLE ONLY patient ADD COLUMN version integer;
ALTER TABLE ONLY aa_insertion ADD COLUMN version integer;
ALTER TABLE ONLY aa_mutation ADD COLUMN version integer;
ALTER TABLE ONLY aa_sequence ADD COLUMN version integer;
ALTER TABLE ONLY attribute ADD COLUMN version integer;
ALTER TABLE ONLY attribute_nominal_value ADD COLUMN version integer;
ALTER TABLE ONLY dataset_access ADD COLUMN version integer;
ALTER TABLE ONLY drug_class ADD COLUMN version integer;
ALTER TABLE ONLY drug_commercial ADD COLUMN version integer;
ALTER TABLE ONLY drug_generic ADD COLUMN version integer;
ALTER TABLE ONLY gender ADD COLUMN version integer;
ALTER TABLE ONLY nt_sequence ADD COLUMN version integer;
ALTER TABLE ONLY patient_attribute_value ADD COLUMN version integer;
ALTER TABLE ONLY protein ADD COLUMN version integer;
ALTER TABLE ONLY settings_user ADD COLUMN version integer;
ALTER TABLE ONLY test ADD COLUMN version integer;
ALTER TABLE ONLY test_nominal_value ADD COLUMN version integer;
ALTER TABLE ONLY test_object ADD COLUMN version integer;
ALTER TABLE ONLY test_result ADD COLUMN version integer;
ALTER TABLE ONLY test_type ADD COLUMN version integer;
ALTER TABLE ONLY therapy ADD COLUMN version integer;
ALTER TABLE ONLY therapy_commercial ADD COLUMN version integer;
ALTER TABLE ONLY therapy_drugs ADD COLUMN version integer;
ALTER TABLE ONLY therapy_generic ADD COLUMN version integer;
ALTER TABLE ONLY value_type ADD COLUMN version integer;
ALTER TABLE ONLY viral_isolate ADD COLUMN version integer;
ALTER TABLE ONLY dataset ADD COLUMN revision integer;

UPDATE aa_mutation SET version = 0;
UPDATE aa_sequence SET version = 0;
UPDATE attribute SET version = 0;
UPDATE attribute_nominal_value SET version = 0;
UPDATE dataset SET version = 0;
UPDATE dataset_access SET version = 0;
UPDATE drug_class SET version = 0;
UPDATE drug_commercial SET version = 0;
UPDATE drug_generic SET version = 0;
UPDATE gender SET version = 0;
UPDATE nt_sequence SET version = 0;
UPDATE patient SET version = 0;
UPDATE patient_attribute_value SET version = 0;
UPDATE protein SET version = 0;
UPDATE settings_user SET version = 0;
UPDATE test SET version = 0;
UPDATE test_nominal_value SET version = 0;
UPDATE test_object SET version = 0;
UPDATE test_result SET version = 0;
UPDATE test_type SET version = 0;
UPDATE therapy SET version = 0;
UPDATE therapy_commercial SET version = 0;
UPDATE therapy_drugs SET version = 0;
UPDATE therapy_generic SET version = 0;
UPDATE value_type SET version = 0;
UPDATE viral_isolate SET version = 0;

-- make gender a nominal attribute

SELECT pg_catalog.setval('attribute_nominal_value_nominal_value_ii_seq', 100, true);

INSERT INTO attribute(name, value_type_ii, patient_attribute, sequence_attribute, version) VALUES ('Gender', 4, true, false, 0);

INSERT INTO attribute_nominal_value(attribute_ii, value, version) VALUES (18, 'Male', 0);
INSERT INTO attribute_nominal_value(attribute_ii, value, version) VALUES (18, 'Female', 0);

INSERT INTO patient_attribute_value(patient_ii, attribute_ii, nominal_value_ii, version) SELECT patient_ii, 18, 102, 0 FROM patient WHERE gender_ii = 2;

DROP TABLE gender CASCADE;
ALTER TABLE patient DROP COLUMN gender_ii;

-- attribute group

ALTER TABLE attribute ADD COLUMN attribute_group character varying(100);

alter table only test_result alter column value drop not null;
alter table only test_result alter column patient_ii set not null;

-- add a password field to the user settings table
ALTER TABLE ONLY settings_user ADD COLUMN password character varying(50);

-- remove these tables, they're replace with the patient_attribute_value table
DROP TABLE patient_nominal_attribute_value;
DROP TABLE patient_other_attribute_value;

-- fix primary key in attribute_nominal_value
ALTER TABLE ONLY attribute_nominal_value DROP CONSTRAINT nominal_value_pkey;
ALTER TABLE ONLY attribute_nominal_value
    ADD CONSTRAINT attribute_nominal_value_pkey PRIMARY KEY(nominal_value_ii);
    
-- make nominal_values_ii in test_result a foreign key to test_nominal_values
ALTER TABLE ONLY test_result ADD CONSTRAINT "FK_test_result__nominal_value" FOREIGN KEY (nominal_value_ii) REFERENCES test_nominal_value(nominal_value_ii) ON UPDATE CASCADE;

-- make nominal values for attributes accessible as a class reference in a PatientAttributeValue
ALTER TABLE ONLY patient_attribute_value ADD CONSTRAINT "FK_patient_attribute_value_attribute_nominal_value" FOREIGN KEY (nominal_value_ii) REFERENCES attribute_nominal_value(nominal_value_ii) ON UPDATE CASCADE;

-- remove obsolete attribute columns
ALTER TABLE ONLY attribute DROP COLUMN patient_attribute;
ALTER TABLE ONLY attribute DROP COLUMN sequence_attribute;