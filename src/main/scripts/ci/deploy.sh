#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -e errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail


CURRENT_DIR="$(dirname "${0:-.}")"
INVENTORY="$CURRENT_DIR/ansible/mystamps.inventory"
PLAYBOOK="$CURRENT_DIR/ansible/deploy.yml"
PRIVATE_KEY="$CURRENT_DIR/ansible/mystamps_rsa"

cleanup() {
	rm -f "$PRIVATE_KEY"
	exit
}
trap 'cleanup' EXIT SIGHUP SIGINT SIGTERM

# Disable host key checking to suppress interactive prompt.
# See: http://docs.ansible.com/ansible/intro_getting_started.html#host-key-checking
export ANSIBLE_HOST_KEY_CHECKING=False

# Decrypt private key
openssl aes-256-cbc -K $encrypted_bf07cb25089f_key -iv $encrypted_bf07cb25089f_iv -in "$PRIVATE_KEY.enc" -out "$PRIVATE_KEY" -d
chmod 600 "$PRIVATE_KEY"

ansible-playbook --inventory-file="$INVENTORY" "$PLAYBOOK" --syntax-check

exec ansible-playbook --inventory-file="$INVENTORY" "$PLAYBOOK" --private-key="$PRIVATE_KEY"
