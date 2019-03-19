#!/bin/sh

#awk_process_table

get_sys_info(){
	upt=`uptime`
	sysinfo=`uname -a`
	echo "UPTIME=$upt"
	echo "SYSINFO=$sysinfo"
}

get_disto_info(){
	cat /etc/*release
}

get_disk_usage(){
	df -P|awk 'BEGIN{ 
		text="";
	}{ 
		for(i=1;i<=NF;i++){ 
			if(i==NF){
				text=text $i ";";
			}
			else{
				text=text $i "|";
			}
		}
	}
	END{
		printf("DISK_USAGE_TABLE=%s\n",text);
	}'
}

get_memory_usage(){
	vmstat -s|awk '{ 
		if($0 ~ /pages free$/){ 
			free=$1;
		} 
		if($0 ~ /pages active$/){ 
			active=$1;
		} 
	
		if($0 ~ /swap pages$/){ 
			total_swap=$1;
		} 
		if($0 ~ /swap pages in use$/){ 
			swap_use=$1;
		} 
	}END{
		total=free+active; 
		if(total>0){
			mem_used=(active*100)/total;
			printf("MEMORY_USAGE=%d\n",mem_used);
		}
		if(total_swap>0){
			swap_used=(swap_use*100)/total_swap;
			printf("SWAP_USAGE=%d\n",swap_used);
		}
	}'
}

get_cpu_usage(){
	vmstat 1 2|awk '
	{
		if(NR==2)
		{
			for(i=1;i<=NF;i++)
			{
				if($i=="id")
				{
					fn=i;
				}
			}
		}
		if(NR==4)
		{
			print "CPU_USAGE=" (100-$fn)
		}
	}'
}


get_socket_table(){
	LSOF=`which lsof`
	
	if [ $? -eq 0 ];then
		lsof -b -i tcp|awk 'BEGIN{ 
			text="";
		}{ 
			for(i=1;i<=NF;i++){ 
				if(i==NF){
					text=text $i ";";
				}
				else{
					text=text $i "|";
				}
			}
		}
		END{
			printf("SOCKET_TABLE=%s\n",text);
		}'
	else
		netstat -a -p tcp|grep tcp|awk 'BEGIN{ 
			text="";
		}{ 
			if(NR==1){
				for(i=1;i<=NF;i++){ 
					if(i==NF){
						text=text " ;";
					}
					else{
						text=text " |";
					}
				}
			}
			
			for(i=1;i<=NF;i++){ 
				if(i==NF){
					text=text $i ";";
				}
				else{
					text=text $i "|";
				}
			}
		}
		END{
			printf("SOCKET_TABLE=%s\n",text);
		}'
	fi
}

get_process_table(){
	UNIX95=1
	export UNIX95
	ps -A -o comm=PROCESS -o pcpu=CPU -o vsz=MEMORY -o pid=PID -o ruser -o user -o rgroup -o group -o ppid -o pgid -o nice -o etime -o time -o tty -o args|awk 'BEGIN{
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
	get_process_table
	get_sys_info
}

get_stats|gzip|cat





