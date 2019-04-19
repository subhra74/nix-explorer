#!/bin/sh

CMD_PATH=""

my_whereis(){
	for cmdpath in /usr/sbin/ /bin/ /sbin/ /usr/bin/ /usr/local/bin/ /usr/local/sbin/
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

my_whereis lsof

if [ "$?" -eq 0 ]
then
	"$CMD_PATH" -b -n -i tcp
else
	my_whereis ss
	if [ $? -eq 0 ]
	then
		"$CMD_PATH" -l -n -t -p
	else
		my_whereis netstat
		if [ $? -eq 0 ]
		then
			"$CMD_PATH" -l -n -t
		fi
	fi
fi