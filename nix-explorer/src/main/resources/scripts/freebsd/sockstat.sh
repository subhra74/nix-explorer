#!/bin/sh

CMD_PATH=""

my_whereis(){
	for cmdpath in /bin/ /sbin/ /usr/bin/ /usr/local/bin/ /usr/local/sbin/
	do
		if [ -f "$cmdpath$1" ]
		then
			CMD_PATH="$cmdpath$1"
			return 0
		fi
	done
	return 1
}

#try lsof first

my_whereis lsof

if [ $? -eq 0 ]
then
	$CMD_PATH -b -n -i tcp|gzip|cat
else
	my_whereis netstat
	if [ $? -eq 0 ]
	then
		$CMD_PATH -a -p tcp|gzip|cat
	fi
fi







