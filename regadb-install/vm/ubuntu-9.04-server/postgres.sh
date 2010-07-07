#! /bin/sh

#quitely install postgres
apt-get -y install postgresql

#create user
sudo -u postgres psql -c "create user regadb_user password 'regadb_password'"
#create database
sudo -u postgres psql -c "create database regadb owner regadb_user"

cp pg_hba.conf /etc/postgresql/8.3/main/ 

/etc/init.d/postgresql-8.3 restart

psql -U regadb_user regadb -f ../../../regadb-install/src/net/sf/regadb/install/ddl/schema/postgresSchema.sql
