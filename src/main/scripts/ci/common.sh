print_status() {
	local FAIL_FILE="$1"
	local MSG="$2"
	
	printf "* $MSG... "
	if [ -f "$FAIL_FILE" ]; then
		echo "\033[1;31mFAIL\033[0m"
	else
		echo "\033[1;32mSUCCESS\033[0m"
	fi
}

print_log() {
	local LOG_FILE="$1"
	local MSG="$2"
	
	echo
	echo "=====> \033[1;33m$MSG\033[0m"
	echo
	egrep -v '^\[INFO\] Download(ing|ed):' "$LOG_FILE"
}
