#!/bin/bash

# Algorithm:
# 1) sort by ticket and id
# 2) prepend a header (that will be used by GitHub for preview)
# 3) transform a list of objects to many object: [{}, {}] => {}, {}
# 4) transform each object to a list: {}, {} => [], []
# 4a) surround .body with quotes and escape double quotes inside by doubling them (this is what @csv filter does).
#     Required to be able to view at GitHub. See also:
#     https://docs.github.com/en/repositories/working-with-files/using-files/working-with-non-code-files#rendering-csv-and-tsv-data
# 5) transform lists to TSV rows

exec jq --raw-output '.puzzles | sort_by([ .ticket | tonumber ], .id) | [ { "id":"Id", "ticket":"Ticket", "body":"Title", "file":"File", "lines":"Lines" } ] + . | .[] | [ .id, .ticket, ([ .body ] | @csv), .file, .lines ] | @tsv'
