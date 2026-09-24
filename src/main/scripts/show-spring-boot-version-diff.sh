#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail

CURRENT_DIR="$(dirname "$0")"
PROJECT_POM="$CURRENT_DIR/../../../pom.xml"

(
	printf 'Dependency\tSpring Boot\tProject\tResolution\n';
	spring-boot-dependency-checker "$PROJECT_POM" |
		jq --raw-output '
			.packages
			| sort_by(.group, .name)
			| .[]
			| select(.versionComparison != "same")
			| [ "\(.group):\(.name)", .bootVersion, .inputFileVersion, .versionComparison ]
			| @tsv
			'
) | column -t -s $'\t'

