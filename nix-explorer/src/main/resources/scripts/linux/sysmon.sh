#!/bin/sh

#awk_process_table

get_memory_usage(){
	cat /proc/meminfo|awk '{
		if($1=="MemTotal:"){
			memtotal=$2
		}
		if($1=="MemFree:"){
			memfree=$2	
		}
		if($1=="Cached:"){
			cached=$2	
		}
		if($1=="SwapTotal:"){
			swaptotal=$2
		}
		if($1=="SwapFree:"){
			swapfree=$2
		}
		if($1=="SwapCached:"){
			swapcached=$2	
		}
	}END{
		memused=memtotal-memfree-cached
		swapused=swaptotal-swapfree-swapcached
		if(memtotal>0){
			printf("MEMORY_USAGE=%d\n",(memused*100)/memtotal);	
		}
		if(swaptotal>0){
			printf("SWAP_USAGE=%d\n",(swapused*100)/swaptotal);
		}
	}'
}

get_cpu_usage(){
	cat /proc/stat|awk '
	BEGIN{ 
		#prev_idle={env}
		#prev_total={env}
	}{
		if(NR==1){
			idle=$5; 
			total=$1+$2+$3+$4+$5; 
		}
	}END{
		diff_idle=idle-prev_idle;
		diff_total=total-prev_total;
		diff_usage=(1000*(diff_total-diff_idle)/diff_total+5)/10;
		printf("prev_idle=%d\nprev_total=%d\nCPU_USAGE=%d\n",idle,total,diff_usage);
	}'
}

get_process_table(){
	UNIX95=1
	export UNIX95
	#show_all={env}
	#ps_options={env}
	if [ ! -z $show_all ];then
		ps_options=" $ps_options"
		echo "with extended options"
	fi
	ps $ps_options |awk -v ps_parse=1 'BEGIN{ 
		fields=0;
		text="";
	}
	{
		if(NR==1){
			fields=NF;
			for(i=1;i<=NF;i++){
				if(i>1){
					text=text "|";
				}
				text=text $i;		
			}
		}
		else{
			text=text ";";
			for(i=1;i<=fields;i++){
				if(i>NF)break;
					if(i>1){
						text=text "|";
					}
					text=text $i;
						}
						for(;i<=NF;i++){
							text=text " " $i;
						}
					}
	}
	END{
		print "PROCESS_TABLE=" text;
	}'
}

get_uptime(){
	UPTIME=`uptime`
	echo "UPTIME=$UPTIME"
}

get_stats(){
	get_cpu_usage
	get_memory_usage
	get_uptime
	#get_process_table
}

get_stats





