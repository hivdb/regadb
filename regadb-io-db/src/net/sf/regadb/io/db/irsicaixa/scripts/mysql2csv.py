import os
import sys

host = 'localhost'
user = 'plibin0'
password = 'plibin0'
database = 'virolab_spain'

path = sys.argv[1]

showTablesCmd = 'mysql -u' + user + ' -p' + user + ' ' +  database + ' -B -e \"show tables;\" '

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


