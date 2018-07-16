#!/bin/bash

set -o nounset
set -o errexit

UPLOADS_DST='{{ uploads_target_url }}'
MYSQL_BACKUPS_DST='{{ mysql_backups_target_url }}'
PASSPHRASE='{{ gpg_passphrase }}'

case "${1:-}" in
	'uploads')
		su \
			mystamps \
			-c "duplicity --name=uploads --no-compression --no-encryption /data/uploads ${UPLOADS_DST}" \
			2>&1
		;;
	'mysql-backups')
		PASSPHRASE="$PASSPHRASE" su \
			mystamps \
			-c "duplicity --name=mysql-backups --no-compression /data/backups ${MYSQL_BACKUPS_DST}" \
			2>&1
		;;
	*)
		echo 2>&1 "Usage: $(dirname "$0") (uploads|mysql-backups)"
		exit 1
esac
