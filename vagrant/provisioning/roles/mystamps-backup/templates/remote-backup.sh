#!/bin/bash

set -o nounset
set -o errexit

UPLOADS_DST='{{ uploads_target_url }}'
MYSQL_BACKUPS_DST='{{ mysql_backups_target_url }}'
PASSPHRASE='{{ gpg_passphrase }}'
DUPLICITY_CMD='duplicity --no-compression'

case "${1:-}" in
	'uploads')
		su \
			mystamps \
			-c "${DUPLICITY_CMD} --name=uploads --no-encryption /data/uploads ${UPLOADS_DST}" \
			2>&1
		;;
	'mysql-backups')
		PASSPHRASE="$PASSPHRASE" su \
			mystamps \
			-c "${DUPLICITY_CMD} --name=mysql-backups /data/backups ${MYSQL_BACKUPS_DST}" \
			2>&1
		;;
	*)
		echo 2>&1 "Usage: $(dirname "$0") (uploads|mysql-backups)"
		exit 1
esac
