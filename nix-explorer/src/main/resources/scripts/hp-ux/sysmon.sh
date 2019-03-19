#!/bin/sh

#awk_process_table

get_disk_usage(){
	RESULT=`df -P /|awk '{if(NR==2) { gsub("%","");print $5}}'`
	echo "DISKSPACE_USAGE=$RESULT"
}

get_network_usage(){
	cat /proc/net/dev|awk '
	BEGIN{ 
		#CURRENT_TIME={env}
		#BYTES_IN={env}
		#BYTES_OUT={env}
		#BYTES_TOTAL={env}

		inbyte=0; 
		outbyte=0; 
	}
	{
		if($0 ~ /:/) { 
			inbyte=inbyte+$2; 
			outbyte=outbyte+$10;
		}
	}
	END{
		total=inbyte+outbyte
		cmd="date +%s"
		(cmd|getline current_time1)
		close(cmd)
		time_diff=current_time1-CURRENT_TIME
		in_diff=inbyte-BYTES_IN
		out_diff=outbyte-BYTES_OUT

		in_speed=(in_diff/time_diff)/1024
		out_speed=(out_diff/time_diff)/1024
		total_speed=((total-BYTES_TOTAL)/time_diff)/1024
		printf("BYTES_IN=%d\nBYTES_OUT=%d\nBYTES_TOTAL=%d\n",inbyte,outbyte,total);
		printf("CURRENT_TIME=%d\nIN_SPEED=%d\nOUT_SPEED=%d\nTOTAL_SPEED=%d\n",current_time1,in_speed,out_speed,total_speed); 
	}
	'

	cat /proc/net/tcp|awk 'BEGIN{
		c1=0;
		c2=0;
	}{ 
		if(NR>1) { 
			if($4=="0A"){
				c1=c1+1
			} 
			if($4=="01"){
				c2=c2+1
			} 
		}
	} END{
		print("TOTAL_LISTENING=" c1); 
		print("TOTAL_CONNECTED=" c2)
	}'

}

get_memory_usage(){
	cat /proc/meminfo|awk '{
		if($1=="MemTotal:"){
			memtotal=$2
		}
		if($1=="MemFree:"){
			memfree=$2	
		}
		if($1=="SwapTotal:"){
			swaptotal=$2
		}
		if($1=="SwapFree:"){
			swapfree=$2
		}
	}END{
		memused=memtotal-memfree
		swapused=swaptotal-swapfree
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
	ps -e -o comm=PROCESS -o pcpu=CPU -o vsz=MEMORY -o pid=PID -o ruser -o user -o rgroup -o group -o ppid -o pgid -o nice -o etime -o time -o tty -o args|awk -v pid=$$ -v key='awk_process_table' 'BEGIN{
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
			if($0 !~ "awk_process_table" && $0 !~ "ignore_script_process"){
				if($6!=pid){
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
			}
	}
	END{
		print "PROCESS_TABLE=" text;
	}'
}

get_stats(){
	get_cpu_usage
	get_memory_usage
	get_disk_usage
	get_network_usage
	get_process_table
}

get_stats|gzip|cat





