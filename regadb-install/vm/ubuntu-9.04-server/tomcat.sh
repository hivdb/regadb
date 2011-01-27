#!/bin/bash

CONFFILE="/etc/default/tomcat6"
TIMEZONE="GMT"

RAMTOTAL=`free -m | grep Mem | sed -e "s/[ ]\+/,/g" | cut -d ',' -f 2`
RAMMAX=512
RAMMIN=128
RAM=$(( RAMTOTAL * 1/2  ))

if (( RAM > RAMMAX ))
then
  RAM=$RAMMAX
fi

if (( RAM < RAMMIN ))
then
  RAM=$RAMMING
fi

apt-get -y install tomcat6
sed -i 's/#TOMCAT6_SECURITY=yes/TOMCAT6_SECURITY=no/' "$CONFFILE"
sed -i "s/#JAVA_OPTS=\"/JAVA_OPTS=\"-Duser.timezone=$TIMEZONE /" "$CONFFILE"
echo sed -i "s/Xmx[0-9]\+M/Xmx$RAM\M/" "$CONFFILE"
