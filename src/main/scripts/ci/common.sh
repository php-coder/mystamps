print_status() {
	# $1 is empty if check has succeeded
	# $1 equals to 'fail' if check has failed
	local result="$1"
	local msg="$2"
	
	local status='SUCCESS'
	local color=32
	
	if [ "$result" = 'fail' ]; then
		status='FAIL'
		color=31
	fi
	printf "* %s... \033[1;%dm%s\033[0m\n" "$msg" "$color" "$status"
}

print_log() {
	local log_file="$1"
	local msg="$2"
	
	echo
	printf "=====> \033[1;33m%s\033[0m\n" "$msg"
	echo
	egrep -v '^\[INFO\] Download(ing|ed):' "$log_file" || :
}
