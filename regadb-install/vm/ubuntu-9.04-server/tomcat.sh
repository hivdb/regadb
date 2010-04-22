#!/bin/bash

apt-get -y install tomcat6
sed -i 's/#TOMCAT6_SECURITY=yes/TOMCAT6_SECURITY=no/' /etc/default/tomcat6
