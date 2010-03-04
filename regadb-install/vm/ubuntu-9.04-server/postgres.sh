#! /bin/sh

#quitely install postgres
apt-get -y install postgresql

#create user
sudo -u postgres psql -c "create user regadb_user password 'regadb_password'"
#create database
sudo -u postgres psql -c "create database regadb owner regadb_user"

