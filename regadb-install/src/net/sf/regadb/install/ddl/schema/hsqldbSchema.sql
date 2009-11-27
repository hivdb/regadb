create table regadbschema.aa_insertion (insertion_position smallint not null, aa_sequence_ii integer not null, insertion_order smallint not null, version integer not null, aa_insertion varchar(30) not null, nt_insertion_codon varchar(3) not null, primary key (insertion_position, aa_sequence_ii, insertion_order));
create table regadbschema.aa_mutation (mutation_position smallint not null, aa_sequence_ii integer not null, version integer not null, aa_reference varchar(1) not null, aa_mutation varchar(30), nt_reference_codon varchar(3) not null, nt_mutation_codon varchar(3), primary key (mutation_position, aa_sequence_ii));
create table regadbschema.aa_sequence (aa_sequence_ii integer generated by default as identity (start with 1), version integer not null, nt_sequence_ii integer not null, protein_ii integer not null, first_aa_pos smallint not null, last_aa_pos smallint not null, primary key (aa_sequence_ii));
create table regadbschema.analysis (analysis_ii integer generated by default as identity (start with 1), analysis_type_ii integer not null, url varchar(100), account varchar(50), password varchar(100), baseinputfile varchar(50), baseoutputfile varchar(50), service_name varchar(100), dataoutputfile varchar(50), primary key (analysis_ii));
create table regadbschema.analysis_data (analysis_data_ii integer generated by default as identity (start with 1), analysis_ii integer not null, name varchar(50), data varbinary(255), mimetype varchar(50) not null, primary key (analysis_data_ii));
create table regadbschema.analysis_type (analysis_type_ii integer generated by default as identity (start with 1), type varchar(50), primary key (analysis_type_ii));
create table regadbschema.attribute (attribute_ii integer generated by default as identity (start with 1), version integer not null, value_type_ii integer, attribute_group_ii integer, name varchar(50) not null, primary key (attribute_ii));
create table regadbschema.attribute_group (attribute_group_ii integer generated by default as identity (start with 1), version integer not null, group_name varchar(50), primary key (attribute_group_ii));
create table regadbschema.attribute_nominal_value (nominal_value_ii integer generated by default as identity (start with 1), version integer not null, attribute_ii integer not null, value varchar(100) not null, primary key (nominal_value_ii));
create table regadbschema.combined_query (combined_query_ii integer generated by default as identity (start with 1), name varchar(50) not null, primary key (combined_query_ii));
create table regadbschema.combined_query_definition (combined_query_ii integer not null, query_definition_ii integer not null, number integer not null, name varchar(50), primary key (combined_query_ii, query_definition_ii));
create table regadbschema.commercial_generic (generic_ii integer not null, commercial_ii integer not null, primary key (commercial_ii, generic_ii));
create table regadbschema.dataset (dataset_ii integer generated by default as identity (start with 1), version integer not null, uid varchar(50) not null, description varchar(50) not null, creation_date date not null, closed_date date, revision integer, primary key (dataset_ii));
create table regadbschema.dataset_access (uid varchar(50) not null, dataset_ii integer not null, version integer not null, permissions integer not null, provider varchar(50) not null, primary key (uid, dataset_ii));
create table regadbschema.drug_class (drug_class_ii integer generated by default as identity (start with 1), version integer not null, class_id varchar(10) not null, class_name varchar(100) not null, resistance_table_order integer, primary key (drug_class_ii));
create table regadbschema.drug_commercial (commercial_ii integer generated by default as identity (start with 1), version integer not null, name varchar(100) not null, atc_code varchar(50), primary key (commercial_ii));
create table regadbschema.drug_generic (generic_ii integer generated by default as identity (start with 1), version integer not null, drug_class_ii integer not null, generic_id varchar(10) not null, generic_name varchar(50) not null, resistance_table_order integer, atc_code varchar(50), primary key (generic_ii));
create table regadbschema.event (event_ii integer generated by default as identity (start with 1), version integer not null, value_type_ii integer, name varchar(50) not null, primary key (event_ii));
create table regadbschema.event_nominal_value (nominal_value_ii integer generated by default as identity (start with 1), version integer not null, event_ii integer not null, value varchar(500) not null, primary key (nominal_value_ii));
create table regadbschema.genome (genome_ii integer generated by default as identity (start with 1), version integer not null, organism_name varchar(50) not null, organism_description varchar(500) not null, genbank_number varchar(50), primary key (genome_ii));
create table regadbschema.genome_drug_generic (genome_ii integer not null, generic_ii integer not null, primary key (generic_ii, genome_ii));
create table regadbschema.nt_sequence (nt_sequence_ii integer generated by default as identity (start with 1), version integer not null, viral_isolate_ii integer not null, label varchar(50), sequence_date date, nucleotides longvarchar, aligned bit, primary key (nt_sequence_ii));
create table regadbschema.open_reading_frame (open_reading_frame_ii integer generated by default as identity (start with 1), version integer not null, genome_ii integer not null, name varchar(50) not null, description varchar(500) not null, reference_sequence longvarchar not null, primary key (open_reading_frame_ii));
create table regadbschema.patient (patient_ii integer generated by default as identity (start with 1), version integer not null, patient_id varchar(50) not null, primary key (patient_ii));
create table regadbschema.patient_attribute_value (patient_attribute_value_ii integer generated by default as identity (start with 1), version integer not null, attribute_ii integer not null, patient_ii integer not null, nominal_value_ii integer, value varchar(100), primary key (patient_attribute_value_ii));
create table regadbschema.patient_dataset (dataset_ii integer not null, patient_ii integer not null, primary key (dataset_ii, patient_ii));
create table regadbschema.patient_event_value (patient_event_value_ii integer generated by default as identity (start with 1), version integer not null, patient_ii integer not null, nominal_value_ii integer, event_ii integer not null, value varchar(100), start_date date, end_date date, primary key (patient_event_value_ii));
create table regadbschema.protein (protein_ii integer generated by default as identity (start with 1), version integer not null, open_reading_frame_ii integer not null, abbreviation varchar(50) not null, full_name varchar(50), start_position integer not null, stop_position integer not null, primary key (protein_ii));
create table regadbschema.query_definition (query_definition_ii integer generated by default as identity (start with 1), uid varchar(50), name varchar(50), description longvarchar, query longvarchar, query_type_ii integer not null, primary key (query_definition_ii));
create table regadbschema.query_definition_parameter (query_definition_parameter_ii integer generated by default as identity (start with 1), query_definition_parameter_type_ii integer, query_definition_ii integer, name varchar(50), primary key (query_definition_parameter_ii));
create table regadbschema.query_definition_parameter_type (query_definition_parameter_type_ii integer generated by default as identity (start with 1), name varchar(100) not null, id integer not null, primary key (query_definition_parameter_type_ii), unique (name), unique (id));
create table regadbschema.query_definition_run (query_definition_run_ii integer generated by default as identity (start with 1), query_definition_ii integer, uid varchar(50), startdate date, enddate date, status integer, name varchar(100) not null, result varchar(100), primary key (query_definition_run_ii));
create table regadbschema.query_definition_run_parameter (query_definition_run_parameter_ii integer generated by default as identity (start with 1), query_definition_parameter_ii integer, query_definition_run_ii integer, value varchar(50), primary key (query_definition_run_parameter_ii));
create table regadbschema.resistance_interpretation_template (template_ii integer generated by default as identity (start with 1), name varchar(100), document varbinary(255), filename varchar(100), primary key (template_ii));
create table regadbschema.settings_user (uid varchar(50) not null, version integer not null, test_ii integer, dataset_ii integer, chart_width integer not null, chart_height integer not null, password varchar(50), email varchar(100), first_name varchar(50), last_name varchar(50), role varchar(50), primary key (uid));
create table regadbschema.splicing_position (splicing_position_ii integer generated by default as identity (start with 1), version integer not null, protein_ii integer not null, nt_position integer not null, primary key (splicing_position_ii));
create table regadbschema.test (test_ii integer generated by default as identity (start with 1), version integer not null, analysis_ii integer, test_type_ii integer not null, description varchar(50) not null, primary key (test_ii), unique (analysis_ii));
create table regadbschema.test_nominal_value (nominal_value_ii integer generated by default as identity (start with 1), version integer not null, test_type_ii integer not null, value varchar(100) not null, primary key (nominal_value_ii));
create table regadbschema.test_object (test_object_ii integer generated by default as identity (start with 1), version integer not null, description varchar(50) not null, test_object_id integer, primary key (test_object_ii));
create table regadbschema.test_result (test_result_ii integer generated by default as identity (start with 1), version integer not null, test_ii integer not null, generic_ii integer, viral_isolate_ii integer, nominal_value_ii integer, patient_ii integer, nt_sequence_ii integer, value varchar(50), test_date date, sample_id varchar(50), data varbinary(255), primary key (test_result_ii));
create table regadbschema.test_type (test_type_ii integer generated by default as identity (start with 1), version integer not null, value_type_ii integer, genome_ii integer, test_object_ii integer not null, description varchar(50) not null, primary key (test_type_ii));
create table regadbschema.therapy (therapy_ii integer generated by default as identity (start with 1), version integer not null, therapy_motivation_ii integer, patient_ii integer not null, start_date date not null, stop_date date, comment varchar(50), primary key (therapy_ii));
create table regadbschema.therapy_commercial (therapy_ii integer not null, commercial_ii integer not null, version integer not null, day_dosage_units double, placebo bit not null, blind bit not null, frequency bigint, primary key (therapy_ii, commercial_ii));
create table regadbschema.therapy_generic (therapy_ii integer not null, generic_ii integer not null, version integer not null, day_dosage_mg double, placebo bit not null, blind bit not null, frequency bigint, primary key (therapy_ii, generic_ii));
create table regadbschema.therapy_motivation (therapy_motivation_ii integer generated by default as identity (start with 1), version integer not null, value varchar(50) not null, primary key (therapy_motivation_ii));
create table regadbschema.user_attribute (user_attribute_ii integer generated by default as identity (start with 1), value_type_ii integer, uid varchar(50), name varchar(50), value varchar(100), data varbinary(255), primary key (user_attribute_ii));
create table regadbschema.value_type (value_type_ii integer generated by default as identity (start with 1), version integer not null, description varchar(50) not null, minimum double, maximum double, multiple bit, primary key (value_type_ii));
create table regadbschema.viral_isolate (viral_isolate_ii integer generated by default as identity (start with 1), version integer not null, patient_ii integer not null, sample_id varchar(50), sample_date date, genome_ii integer, primary key (viral_isolate_ii));
alter table regadbschema.aa_insertion add constraint FKE54E0D50CBB9BE97 foreign key (aa_sequence_ii) references regadbschema.aa_sequence;
alter table regadbschema.aa_mutation add constraint FK33795BE8CBB9BE97 foreign key (aa_sequence_ii) references regadbschema.aa_sequence;
alter table regadbschema.aa_sequence add constraint FK50514100608572E3 foreign key (nt_sequence_ii) references regadbschema.nt_sequence;
alter table regadbschema.aa_sequence add constraint FK5051410062F3E068 foreign key (protein_ii) references regadbschema.protein;
alter table regadbschema.analysis add constraint FKC2F032DC647DD22F foreign key (analysis_type_ii) references regadbschema.analysis_type;
alter table regadbschema.analysis_data add constraint FK6232DF2D46EE23D6 foreign key (analysis_ii) references regadbschema.analysis;
alter table regadbschema.attribute add constraint FKC7AA9C5FC38F0B foreign key (value_type_ii) references regadbschema.value_type;
alter table regadbschema.attribute add constraint FKC7AA9C6B7C6CBD foreign key (attribute_group_ii) references regadbschema.attribute_group;
alter table regadbschema.attribute_nominal_value add constraint FK558F63EB28201F88 foreign key (attribute_ii) references regadbschema.attribute;
alter table regadbschema.combined_query_definition add constraint FKA51227245D802F7D foreign key (combined_query_ii) references regadbschema.combined_query;
alter table regadbschema.combined_query_definition add constraint FKA512272468B43D19 foreign key (query_definition_ii) references regadbschema.query_definition;
alter table regadbschema.commercial_generic add constraint FK4E6E81E219F977A8 foreign key (generic_ii) references regadbschema.drug_generic;
alter table regadbschema.commercial_generic add constraint FK4E6E81E2A152DB96 foreign key (commercial_ii) references regadbschema.drug_commercial;
alter table regadbschema.dataset add constraint FK5605B478F76A62F5 foreign key (uid) references regadbschema.settings_user;
alter table regadbschema.dataset_access add constraint FK1C0C870BB165C048 foreign key (dataset_ii) references regadbschema.dataset;
alter table regadbschema.dataset_access add constraint FK1C0C870BF76A62F5 foreign key (uid) references regadbschema.settings_user;
alter table regadbschema.drug_generic add constraint FK241E40388ED79C07 foreign key (drug_class_ii) references regadbschema.drug_class;
alter table regadbschema.event add constraint FK5C6729A5FC38F0B foreign key (value_type_ii) references regadbschema.value_type;
alter table regadbschema.event_nominal_value add constraint FK7D59576933594348 foreign key (event_ii) references regadbschema.event;
alter table regadbschema.genome_drug_generic add constraint FKCA02968019F977A8 foreign key (generic_ii) references regadbschema.drug_generic;
alter table regadbschema.genome_drug_generic add constraint FKCA0296805A4F23B6 foreign key (genome_ii) references regadbschema.genome;
alter table regadbschema.nt_sequence add constraint FK161BA1A3C3F2CE1 foreign key (viral_isolate_ii) references regadbschema.viral_isolate;
alter table regadbschema.open_reading_frame add constraint FK6BDEF0C55A4F23B6 foreign key (genome_ii) references regadbschema.genome;
alter table regadbschema.patient_attribute_value add constraint FKDA05D0D428201F88 foreign key (attribute_ii) references regadbschema.attribute;
alter table regadbschema.patient_attribute_value add constraint FKDA05D0D471F1932B foreign key (nominal_value_ii) references regadbschema.attribute_nominal_value;
alter table regadbschema.patient_attribute_value add constraint FKDA05D0D4CC6FF868 foreign key (patient_ii) references regadbschema.patient;
alter table regadbschema.patient_dataset add constraint FKE7A4713EB165C048 foreign key (dataset_ii) references regadbschema.dataset;
alter table regadbschema.patient_dataset add constraint FKE7A4713ECC6FF868 foreign key (patient_ii) references regadbschema.patient;
alter table regadbschema.patient_event_value add constraint FK11B8755233594348 foreign key (event_ii) references regadbschema.event;
alter table regadbschema.patient_event_value add constraint FK11B875526A5E9AA9 foreign key (nominal_value_ii) references regadbschema.event_nominal_value;
alter table regadbschema.patient_event_value add constraint FK11B87552CC6FF868 foreign key (patient_ii) references regadbschema.patient;
alter table regadbschema.protein add constraint FKED94D783E79F51C foreign key (open_reading_frame_ii) references regadbschema.open_reading_frame;
alter table regadbschema.query_definition add constraint FK9CE2A52AF76A62F5 foreign key (uid) references regadbschema.settings_user;
alter table regadbschema.query_definition_parameter add constraint FK5ADD66D468B43D19 foreign key (query_definition_ii) references regadbschema.query_definition;
alter table regadbschema.query_definition_parameter add constraint FK5ADD66D46C0CA9F9 foreign key (query_definition_parameter_type_ii) references regadbschema.query_definition_parameter_type;
alter table regadbschema.query_definition_run add constraint FK7384E9F668B43D19 foreign key (query_definition_ii) references regadbschema.query_definition;
alter table regadbschema.query_definition_run add constraint FK7384E9F6F76A62F5 foreign key (uid) references regadbschema.settings_user;
alter table regadbschema.query_definition_run_parameter add constraint FK34B91CA06634A1B0 foreign key (query_definition_parameter_ii) references regadbschema.query_definition_parameter;
alter table regadbschema.query_definition_run_parameter add constraint FK34B91CA0F97E9F30 foreign key (query_definition_run_ii) references regadbschema.query_definition_run;
alter table regadbschema.settings_user add constraint FKDBFE06679BD8A396 foreign key (test_ii) references regadbschema.test;
alter table regadbschema.settings_user add constraint FKDBFE0667B165C048 foreign key (dataset_ii) references regadbschema.dataset;
alter table regadbschema.splicing_position add constraint FK52F9C66F62F3E068 foreign key (protein_ii) references regadbschema.protein;
alter table regadbschema.test add constraint FK36449246EE23D6 foreign key (analysis_ii) references regadbschema.analysis;
alter table regadbschema.test add constraint FK36449291E3B81B foreign key (test_type_ii) references regadbschema.test_type;
alter table regadbschema.test_nominal_value add constraint FK37D6576191E3B81B foreign key (test_type_ii) references regadbschema.test_type;
alter table regadbschema.test_result add constraint FKEF1E986A19F977A8 foreign key (generic_ii) references regadbschema.drug_generic;
alter table regadbschema.test_result add constraint FKEF1E986A3C3F2CE1 foreign key (viral_isolate_ii) references regadbschema.viral_isolate;
alter table regadbschema.test_result add constraint FKEF1E986A4596F6EF foreign key (nominal_value_ii) references regadbschema.test_nominal_value;
alter table regadbschema.test_result add constraint FKEF1E986A608572E3 foreign key (nt_sequence_ii) references regadbschema.nt_sequence;
alter table regadbschema.test_result add constraint FKEF1E986A9BD8A396 foreign key (test_ii) references regadbschema.test;
alter table regadbschema.test_result add constraint FKEF1E986ACC6FF868 foreign key (patient_ii) references regadbschema.patient;
alter table regadbschema.test_type add constraint FKB9A90EC7264DC6FB foreign key (test_object_ii) references regadbschema.test_object;
alter table regadbschema.test_type add constraint FKB9A90EC75A4F23B6 foreign key (genome_ii) references regadbschema.genome;
alter table regadbschema.test_type add constraint FKB9A90EC75FC38F0B foreign key (value_type_ii) references regadbschema.value_type;
alter table regadbschema.therapy add constraint FKAF8F6C692F95783B foreign key (therapy_motivation_ii) references regadbschema.therapy_motivation;
alter table regadbschema.therapy add constraint FKAF8F6C69CC6FF868 foreign key (patient_ii) references regadbschema.patient;
alter table regadbschema.therapy_commercial add constraint FKE158F8E0A152DB96 foreign key (commercial_ii) references regadbschema.drug_commercial;
alter table regadbschema.therapy_commercial add constraint FKE158F8E0AC63EEA8 foreign key (therapy_ii) references regadbschema.therapy;
alter table regadbschema.therapy_generic add constraint FKE73DA40119F977A8 foreign key (generic_ii) references regadbschema.drug_generic;
alter table regadbschema.therapy_generic add constraint FKE73DA401AC63EEA8 foreign key (therapy_ii) references regadbschema.therapy;
alter table regadbschema.user_attribute add constraint FK5E2F8F285FC38F0B foreign key (value_type_ii) references regadbschema.value_type;
alter table regadbschema.user_attribute add constraint FK5E2F8F28F76A62F5 foreign key (uid) references regadbschema.settings_user;
alter table regadbschema.viral_isolate add constraint FK8C5625F65A4F23B6 foreign key (genome_ii) references regadbschema.genome;
alter table regadbschema.viral_isolate add constraint FK8C5625F6CC6FF868 foreign key (patient_ii) references regadbschema.patient;
create index aa_sequence_nt_sequence_ii_idx on regadbschema.aa_sequence (nt_sequence_ii);
create index aa_sequence_protein_ii_idx on regadbschema.aa_sequence (protein_ii);
create index analysis_analysis_type_ii_idx on regadbschema.analysis (analysis_type_ii);
create index analysis_data_analysis_ii_idx on regadbschema.analysis_data (analysis_ii);
create index attribute_attribute_group_ii_idx on regadbschema.attribute (attribute_group_ii);
create index attribute_nominal_value_attribute_ii_idx on regadbschema.attribute_nominal_value (attribute_ii);
create index attribute_value_type_ii_idx on regadbschema.attribute (value_type_ii);
create index combined_query_definition_combined_query_ii_idx on regadbschema.combined_query_definition (combined_query_ii);
create index combined_query_definition_query_definition_ii_idx on regadbschema.combined_query_definition (query_definition_ii);
create index dataset_uid_idx on regadbschema.dataset (uid);
create index drug_generic_drug_class_ii_idx on regadbschema.drug_generic (drug_class_ii);
create index event_nominal_value_event_ii_idx on regadbschema.event_nominal_value (event_ii);
create index event_value_type_ii_idx on regadbschema.event (value_type_ii);
create index nt_sequence_viral_isolate_ii_idx on regadbschema.nt_sequence (viral_isolate_ii);
create index open_reading_frame_genome_ii_idx on regadbschema.open_reading_frame (genome_ii);
create index open_reading_frame_genome_ii_idx on regadbschema.viral_isolate (genome_ii);
create index patient_attribute_value_attribute_ii_idx on regadbschema.patient_attribute_value (attribute_ii);
create index patient_attribute_value_nominal_value_ii_idx on regadbschema.patient_attribute_value (nominal_value_ii);
create index patient_attribute_value_patient_ii_idx on regadbschema.patient_attribute_value (patient_ii);
create index patient_event_value_event_ii_idx on regadbschema.patient_event_value (event_ii);
create index patient_event_value_nominal_value_ii_idx on regadbschema.patient_event_value (nominal_value_ii);
create index patient_event_value_patient_ii_idx on regadbschema.patient_event_value (patient_ii);
create index protein_open_reading_frame_ii_idx on regadbschema.protein (open_reading_frame_ii);
create index query_definition_parameter_query_definition_ii_idx on regadbschema.query_definition_parameter (query_definition_ii);
create index query_definition_parameter_query_definition_parameter_type_ii_idx on regadbschema.query_definition_parameter (query_definition_parameter_type_ii);
create index query_definition_run_parameter_query_definition_parameter_ii_idx on regadbschema.query_definition_run_parameter (query_definition_parameter_ii);
create index query_definition_run_parameter_query_definition_run_ii_idx on regadbschema.query_definition_run_parameter (query_definition_run_ii);
create index query_definition_run_query_definition_ii_idx on regadbschema.query_definition_run (query_definition_ii);
create index query_definition_run_uid_idx on regadbschema.query_definition_run (uid);
create index query_definition_uid_idx on regadbschema.query_definition (uid);
create index settings_user_dataset_ii_idx on regadbschema.settings_user (dataset_ii);
create index settings_user_test_ii_idx on regadbschema.settings_user (test_ii);
create index splicing_position_protein_ii_idx on regadbschema.splicing_position (protein_ii);
create index test_analysis_ii_idx on regadbschema.test (analysis_ii);
create index test_nominal_value_test_type_ii_idx on regadbschema.test_nominal_value (test_type_ii);
create index test_result_generic_ii_idx on regadbschema.test_result (generic_ii);
create index test_result_nominal_value_ii_idx on regadbschema.test_result (nominal_value_ii);
create index test_result_nt_sequence_ii_idx on regadbschema.test_result (nt_sequence_ii);
create index test_result_patient_ii_idx on regadbschema.test_result (patient_ii);
create index test_result_test_ii_idx on regadbschema.test_result (test_ii);
create index test_result_viral_isolate_ii_idx on regadbschema.test_result (viral_isolate_ii);
create index test_test_type_ii_idx on regadbschema.test (test_type_ii);
create index test_type_genome_ii_idx on regadbschema.test_type (genome_ii);
create index test_type_test_object_ii_idx on regadbschema.test_type (test_object_ii);
create index test_type_value_type_ii_idx on regadbschema.test_type (value_type_ii);
create index therapy_patient_ii_idx on regadbschema.therapy (patient_ii);
create index therapy_therapy_motivation_ii_idx on regadbschema.therapy (therapy_motivation_ii);
create index user_attribute_uid_idx on regadbschema.user_attribute (uid);
create index user_attribute_value_type_ii_idx on regadbschema.user_attribute (value_type_ii);
create index viral_isolate_patient_ii_idx on regadbschema.viral_isolate (patient_ii);
