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

get_uptime(){
	UPTIME=`uptime`
	SYSTEM_TIME=`date`
	echo "UPTIME=$UPTIME  System date: $SYSTEM_TIME"
}

get_stats(){
	get_cpu_usage
	get_memory_usage
	get_uptime
}

get_stats





