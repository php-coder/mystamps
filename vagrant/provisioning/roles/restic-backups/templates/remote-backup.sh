#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail



case "${1:-}" in
	'uploads')
		restic -r rclone:{{ rclone_repo_name }}:{{ restic_prefix }}uploads --password-command "echo {{ restic_uploads_password }}" /data/uploads
		;;
	'mysql-backups')
		restic -r rclone:{{ rclone_repo_name }}:{{ restic_prefix }}mysql --password-command "echo {{ restic_mysql_password }}" /data/backups/mysql_backup_mystamps.sql.bz2
		;;
	*)
		echo 2>&1 "Usage: $(dirname "$0") (uploads|mysql-backups)"
		exit 1
esac
