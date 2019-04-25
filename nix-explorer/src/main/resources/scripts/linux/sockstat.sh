export PATH=$PATH:/usr/sbin:/bin:/sbin:/usr/bin:/usr/local/bin:/usr/local/sbin

lsof -b -n -i tcp 2>/dev/null
if [ "$?" -eq 0 ];then
exit 0
fi

ss  -l -n -t -p 2>/dev/null
if [ "$?" -eq 0 ];then
exit 0
fi

netstat -l -n -t 2>/dev/null
if [ "$?" -eq 0 ];then
exit 0
fi

echo "None of the command found: lsof,ss,netstat"