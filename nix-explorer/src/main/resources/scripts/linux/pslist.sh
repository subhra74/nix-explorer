#!/bin/sh

get_process_table(){
	UNIX95=1
	export UNIX95
	#show_all={env}
	#ps_options={env}
	if [ ! -z $show_all ];then
		ps_options="-e $ps_options"
	else
		user=`whoami`
		ps_options="-u $user $ps_options"
	fi
	ps $ps_options
}

get_process_table|gzip|cat