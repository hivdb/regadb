#!/usr/bin/python

import os
import sys

host	 = sys.argv[1]
user	 = sys.argv[2]
password = sys.argv[3]
database = sys.argv[4]
path	 = sys.argv[5]

showTablesCmd = 'mysql -u' + user + ' -p' + password + ' ' +  database + ' -B -e \"show tables;\" '

showTables = os.popen(showTablesCmd, "r")

#counter is kept to ignore the header
counter = 0
for table in showTables:
	if counter != 0 : 
		command = 'mysql -u' + user + ' -p' + password + ' ' + database + ' -B -e \"select * from \`' + table.lstrip().rstrip()  + '\`;\" '
		command += ' | sed \'s/\\t/\",\"/g\''
		command += ' | sed \'s/^/\"/\''	
		command += ' | sed \'s/$/"/\''
		command += ' | sed \'s/\\n//g\''
		command += ' > ' + path + '/' + table.lstrip().rstrip() + '.csv'
		os.system(command)
	counter += 1


