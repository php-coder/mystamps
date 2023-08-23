#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail


CURRENT_DIR="$(dirname "${0:-.}")"
INVENTORY="$CURRENT_DIR/ansible/mystamps.inventory"
PLAYBOOK="$CURRENT_DIR/ansible/deploy.yml"
PRIVATE_KEY="$CURRENT_DIR/ansible/mystamps_rsa"
VARS_FILE="$CURRENT_DIR/ansible/prod_vars.yml"
PASS_FILE="$CURRENT_DIR/vault-pass.txt"

cleanup() {
	rm -f "$PRIVATE_KEY" "$PASS_FILE" "$VARS_FILE"
	exit
}
trap 'cleanup' EXIT SIGHUP SIGINT SIGTERM

# Disable host key checking to suppress interactive prompt.
# See: https://docs.ansible.com/ansible/2.10/user_guide/connection_details.html#managing-host-key-checking
export ANSIBLE_HOST_KEY_CHECKING=False

# Make the output of a failed task human readable.
# See: https://docs.ansible.com/ansible/2.10/reference_appendices/config.html#envvar-ANSIBLE_STDOUT_CALLBACK
export ANSIBLE_STDOUT_CALLBACK=debug

if [ -z "${VAULT_PASSWORD:-}" ]; then
	echo >&2 "ERROR: env variable VAULT_PASSWORD is empty!"
	exit 1
fi

echo -n "$VAULT_PASSWORD" >"$PASS_FILE"

for FILE in "$PRIVATE_KEY" "$VARS_FILE"; do
	FILENAME="$(basename "$FILE")"
	echo "Decrypting ${FILENAME}.enc to $FILENAME"
	ansible-vault decrypt \
		--vault-password-file "$PASS_FILE" \
		--output "$FILE" \
		"${FILE}.enc"
done

ansible-playbook \
	--inventory="$INVENTORY" \
	"$PLAYBOOK" \
	--syntax-check

ansible-playbook \
	--inventory="$INVENTORY" \
	"$PLAYBOOK" \
	--private-key="$PRIVATE_KEY"
