#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail

CURRENT_DIR="$(dirname "$0")"
PROJECT_POM="$CURRENT_DIR/../../../pom.xml"

# Workaround for https://github.com/mrbusche/spring-boot-dependency-checker/issues/188
cp "$PROJECT_POM" .

(
	printf 'Dependency\tProject\tSpring Boot\n';
	# Workarounds for https://github.com/mrbusche/spring-boot-dependency-checker/issues/189
	# and https://github.com/mrbusche/spring-boot-dependency-checker/issues/190
	spring-boot-dependency-checker pom.xml |
		sed -e '/^Processing xml file/d' \
			-e '/^Detected Spring Boot Version/d' \
			-e '/^Declared Pom Package Count/d' \
			-e '/^Spring Boot default versions URL no longer exists/d' \
			-e 's/^Declared Pom Packages - //' \
			-e 's/Package {/{/' \
			-e "s/'/\"/g" \
			-e 's/\(group\|name\|inputFileVersion\|bootVersion\)/"\1"/' |
		jq --raw-output '
			sort_by(.group, .name)
			| .[]
			| select(.bootVersion != .inputFileVersion)
			| [ "\(.group):\(.name)", .inputFileVersion, .bootVersion ]
			| @tsv
			'
) | column -t -s $'\t'

