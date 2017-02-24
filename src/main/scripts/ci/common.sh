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
	# yes, it's a hard coded workaround for #538
	if [ "$log_file" = 'verify.log' ]; then
		# we need -z option to be able to replace new line character
		egrep -v '^\[INFO\] Download(ing|ed):' "$log_file" | sed -z -f "$(dirname "$0")/ignore-htmlunit-messages.sed"
	else
		egrep -v '^\[INFO\] Download(ing|ed):' "$log_file"
	fi
}
