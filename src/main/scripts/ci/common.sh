print_status() {
	local we_failed="$1"
	local msg="$2"
	local status='SUCCESS'
	
	if [ -n "$we_failed" ]; then
		status='FAIL'
	fi
	printf "* %s... \033[1;32m%s\033[0m\n" "$msg" "$status"
}

print_log() {
	local log_file="$1"
	local msg="$2"
	
	echo
	printf "=====> \033[1;33m%s\033[0m\n" "$msg"
	echo
	egrep -v '^\[INFO\] Download(ing|ed):' "$log_file" || :
}
