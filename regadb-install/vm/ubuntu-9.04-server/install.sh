#!/bin/bash

cd `echo $0 | sed 's/\/[^\/]*$/\//g'`

echo Installing 32bit binaries support
apt-get install -y ia32-libs

echo Installing Postgres
./postgres.sh

echo Installing Sequencetool
./sequencetool.sh

echo Installing Tomcat 6
./tomcat.sh

echo Stopping Tomcat
/etc/init.d/tomcat6 stop

echo Installing RegaDB
./regadb.sh

echo Installing WTS
./wts.sh

echo Creatings WTS content
./wts-server.sh

echo Starting Tomcat
/etc/init.d/tomcat6 start

echo Done
