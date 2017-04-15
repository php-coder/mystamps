print_status() {
	local we_failed="$1"
	local msg="$2"
	local status='SUCCESS'
	local color=32
	
	if [ -n "$we_failed" ]; then
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
