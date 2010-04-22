#! /bin/sh
cd `echo $0 | sed 's/\/[^\/]*$/\//g'`

cd ../../../

apt-get -y install ant
ant clean
ant 

TOMCAT="/var/lib/tomcat6"

REGADBDIR="/etc/rega_institute/regadb"
LOGDIR="$REGADBDIR/log"
QUERYDIR="$REGADBDIR/query"
IMPORTDIR="$REGADBDIR/import"

mkdir -p "$REGADBDIR"
mkdir "$LOGDIR"
mkdir "$QUERYDIR"
mkdir "$IMPORTDIR"

cp regadb-doc/xml/global-conf.xml "$REGADBDIR"
#sed -i -e 's///' "$REGADBDIR/global-conf.xml"

chown -R tomcat6:tomcat6 "$REGADBDIR"

cp regadb-ui/dist/regadb-ui.war "$TOMCAT/webapps/regadb.war"

