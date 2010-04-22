#! /bin/sh

cd `echo $0 | sed 's/\/[^\/]*$/\//g'`

mkdir -p /soft/wts

mkdir /soft/wts/sessions
mkdir /soft/wts/users
cp wts-users.pwd /soft/wts/users/users.pwd

cp -R ../../../regadb-wts-services/ /soft/wts/services

chown -R tomcat6:tomcat6 /soft/wts
