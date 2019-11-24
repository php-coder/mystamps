#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail


UPLOADS_DST='{{ uploads_target_url }}'
MYSQL_BACKUPS_DST='{{ mysql_backups_target_url }}'
PASSPHRASE='{{ gpg_passphrase }}'
DUPLICITY_CMD='duplicity --no-compression --full-if-older-than 1M'

case "${1:-}" in
	'uploads')
		su mystamps 2>&1 \
			-c "${DUPLICITY_CMD} --name=uploads --no-encryption /data/uploads ${UPLOADS_DST}"
		su mystamps 2>&1 \
			-c "duplicity remove-all-but-n-full 3 --force ${UPLOADS_DST}"
		;;
	'mysql-backups')
		PASSPHRASE="$PASSPHRASE" su mystamps 2>&1 \
			-c "${DUPLICITY_CMD} --name=mysql-backups /data/backups ${MYSQL_BACKUPS_DST}"
		su mystamps 2>&1 \
			-c "duplicity remove-all-but-n-full 3 --force ${MYSQL_BACKUPS_DST}"
		;;
	*)
		echo 2>&1 "Usage: $(dirname "$0") (uploads|mysql-backups)"
		exit 1
esac
