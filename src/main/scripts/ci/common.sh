print_status() {
	local we_failed="$1"
	local msg="$2"
	
	printf "* $msg... "
	if [ -n "$we_failed" ]; then
		echo "\033[1;31mFAIL\033[0m"
	else
		echo "\033[1;32mSUCCESS\033[0m"
	fi
}

print_log() {
	local log_file="$1"
	local msg="$2"
	
	echo
	echo "=====> \033[1;33m$msg\033[0m"
	echo
	egrep -v '^\[INFO\] Download(ing|ed):' "$log_file"
}
