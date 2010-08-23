#!/bin/bash
WTSDIR="/etc/wt/wts"

cd `echo $0 | sed 's/\/[^\/]*$/\//g'`

mkdir -p "$WTSDIR"
cp wts.xml "$WTSDIR"

git clone http://git.regaweb.med.kuleuven.be/wts /tmp/wts
cd /tmp/wts/wts
mkdir build
ant clean
ant

cp dist/wts.war /var/lib/tomcat6/webapps

