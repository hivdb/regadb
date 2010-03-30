#! /bin/sh
cd `echo $0 | sed 's/\/[^\/]*$/\//g'`

cd ../../../

apt-get -y install ant
ant clean
ant build

TOMCAT="/var/lib/tomcat6"

cp regadb-ui/dist/regadb-ui.war "$TOMCAT/webapps/regadb.war"

