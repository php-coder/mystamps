#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail

CURRENT_DIR="$(dirname "$0")"
PROJECT_POM="$CURRENT_DIR/../../../pom.xml"
SPRING_VERSION="$(grep -FA1 '<artifactId>spring-boot-starter-parent' "$PROJECT_POM" | awk -F'[<>]' '/<version>/{print $3}')"
SPRING_POM="https://raw.githubusercontent.com/spring-projects/spring-boot/v$SPRING_VERSION/spring-boot-dependencies/pom.xml"

printf "Comparing with Spring Boot %s (project vs spring versions)\\n\\n" "$SPRING_VERSION"

# I know about useless cat below, but it's here for better readability.
# shellcheck disable=SC2002
join \
	<(cat    "$PROJECT_POM" | awk -F'[<>]' -v OFS='\t' '$2~/\.version/{print $2, $3}' | sort) \
	<(curl -s "$SPRING_POM" | awk -F'[<>]' -v OFS='\t' '$2~/\.version/{print $2, $3}' | sort) \
	| awk '
			{
				if ($2 != $3){
					sub(/\.version$/, "", $1);
					printf("%35s:\t%20s\t->\t%s\n", $1, $2, $3);
					cnt++
				}
			}
			END {
				printf("\nTotal:\t%d dependencies differ by versions\n", cnt);
				exit cnt
			}'

