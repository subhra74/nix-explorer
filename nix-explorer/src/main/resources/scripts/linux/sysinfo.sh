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

sysinfo(){
echo "System information:"
uname -a

echo " "

#echo "Uptime:"
#uptime
#
#echo " "

echo "Distro info:"
cat /etc/*release

echo " "

echo "Network information:"

my_whereis ip

if [ $? -eq 0 ]
then
	$CMD_PATH addr
fi

}

sysinfo



