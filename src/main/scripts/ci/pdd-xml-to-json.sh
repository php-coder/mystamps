#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail

if [ -z "${1:-}" ]; then
	echo >&2 "Usage: $(dirname "$0") /path/to/pdd.xml"
	exit 1
fi

# Yes, usage of `cat` is useless here, but it makes code more readable
cat "$1" \
	| sed \
		-e '/<?xml/d' \
		-e '/<estimate>/d' \
		-e '/<role>/d' \
		-e '/<time>/d' \
		-e '/<author>/d' \
		-e '/<email>/d' \
		-e 's|"|\\"|g' \
		-e 's|&quot;|"|g' \
		-e "s|&apos;|'|g" \
		-e 's|&amp;|&|g' \
		-e 's|<puzzles [^>]\+>|[|' \
	| paste -s -d' ' - \
	| sed \
		-E \
		-e 's|</(id\|ticket\|lines\|body\|file)>[ ]+</puzzle>[ ]+</puzzles>|" } ]|' \
		-e 's|</(id\|ticket\|lines\|body\|file)>[ ]+</puzzle>[ ]+<puzzle>|" }, {|g' \
		-e 's|<(id\|ticket\|lines\|body\|file)>|"\1": "|g' \
		-e 's|</(id\|ticket\|lines\|body\|file)>|",|g' \
		-e 's|<puzzle>|{|' \
		-e 's|</puzzles>|]|' \
		-e 's|&lt;|<|g' \
		-e 's|&gt;|>|g' \
		#
