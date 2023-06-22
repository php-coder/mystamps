#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail

if [ -z "${1:-}" ]; then
	echo >&2 "Usage: $(dirname "$0") /path/to/todos-in-code.tsv"
	exit 1
fi

debug() {
    [ -z "$ENABLE_DEBUG" ] || printf 'DEBUG: %s\n' "$1"
}

info() {
    printf 'INFO : %s\n' "$1"
}

warn() {
    printf 'WARN : %s\n' "$1"
}

error() {
    printf >&2 'ERROR: %s\n' "$1"
}

fatal() {
    error "$1"
    exit 1
}

# GH_NO_UPDATE_NOTIFIER: set to any value to disable update notifications.
# By default, gh checks for new releases once every 24 hours and displays an upgrade notice
# on standard error if a newer version was found.
export GH_NO_UPDATE_NOTIFIER=yes

# NOTE: requires `gh auth login --hostname github.com --git-protocol https --web` prior executing the script
export GH_TOKEN="$(gh auth token)"
[ -n "$GH_TOKEN" ] ||  fatal 'gh auth token returns an empty string'

# DEBUG (deprecated): set to "1", "true", or "yes" to enable verbose output on standard error.
# GH_DEBUG: set to a truthy value to enable verbose output on standard error. Set to "api" to additionally log details of HTTP traffic.
#export GH_DEBUG=api

# NO_COLOR: set to any value to avoid printing ANSI escape sequences for color output.
#export NO_COLOR=yes

# a non-empty string enabled debug output
ENABLE_DEBUG=

DIR="$(dirname "$1")"
MAPPING_FILE="$DIR/todos-on-github.tsv"

if [ ! -f "$MAPPING_FILE" ]; then
    info "$MAPPING_FILE doesn't exist. Creating..."
    printf 'Id\tIssue\tState\tCreated\n' >> "$MAPPING_FILE"
else
    info "$MAPPING_FILE exists"
fi

PUZZLES_COUNT=0
NEW_ISSUES_COUNT=0

while IFS=$'\t' read PUZZLE_ID TICKET TITLE REST; do
    PUZZLES_COUNT=$[PUZZLES_COUNT + 1]

    # unescape CSV: "a ""quoted"" string" => a "quoted" string
    TITLE="$(echo "$TITLE" | sed -e 's|^"||;' -e 's|"$||' -e 's|""|"|g')"

    debug "$PUZZLE_ID: has title: '$TITLE'"
    MAPPING="$(grep --max-count=1 '^'$PUZZLE_ID'[[:space:]]' "$MAPPING_FILE" || :)"
    if [ -n "$MAPPING" ]; then
        IFS=$'\t' read ID ISSUE_ID REST <<<"$MAPPING"
        info "$PUZZLE_ID => #$ISSUE_ID: is already linked"
        continue
    fi

    debug "$PUZZLE_ID: isn't linked to any issues. Will search on GitHub..."
    # https://cli.github.com/manual/gh_search_issues
    # Brief example of API output:
    # {
    # "total_count": 1,
    # "incomplete_results": false,
    # "items": [
    #     {
    #       "html_url": "https://github.com/php-coder/mystamps/issues/760",
    #       "number": 760,
    #       "title": "Check src/main/config/nginx/503.*html by html5validator",
    #       "state": "open",
    #       "body": "The puzzle `109-a721e051` (from #109) in [`src/main/scripts/ci/check-build-and-verify.sh`] ...",
    #     }
    #   ]
    # }
    # NB: we search by body as a title isn't reliable: it might be modified after issue creation and don't match with code
    # (but later, we check a title anyway)
    debug "$PUZZLE_ID: search issues with a body that contain puzzle id '$PUZZLE_ID' or a title that is equal to '$TITLE'"
    SEARCH_BY_BODY="$(gh search issues --repo php-coder/mystamps --json number,state,url,title,body --match body "$PUZZLE_ID")"
    ISSUES_BY_BODY_COUNT="$(echo "$SEARCH_BY_BODY" | jq '. | length')"
    debug "$PUZZLE_ID: found $ISSUES_BY_BODY_COUNT issue(s) by body"

    SEARCH_BY_TITLE="$(gh search issues --repo php-coder/mystamps --json number,state,url,title,body --match title "$TITLE")"
    ISSUES_BY_TITLE_COUNT="$(echo "$SEARCH_BY_TITLE" | jq '. | length')"
    debug "$PUZZLE_ID: found $ISSUES_BY_TITLE_COUNT issue(s) by title"

    # KNOWN ISSUES:
    # 1) As there is no way to search in title with exact match, it's possible to find more than one issue if their titles are similar.
    #    For example, when lookup for "Add validation", it find an issue with the title "Add validation" and "Add validation for e-mail".
    #    In this case, we let the user to choose which one is needed.
    # 2) For each puzzle id we have to make 2 search requests instead of one because there is no possibility to use logical OR
    #    (body contains OR title equals) in a search query. As result, we might get "HTTP 403: API rate limit exceeded" error more often
    #    if we have a lot of issues to process or we re-run the script frequently.
    JSON="$(echo "$SEARCH_BY_BODY\n$SEARCH_BY_TITLE" | sed -e 's|\\n| |g' -e 's|\\r||g' -e 's|`||g' | jq --slurp 'add | unique_by(.number)')"
    ISSUES_COUNT="$(echo "$JSON" | jq '. | length')"
    debug "$PUZZLE_ID: found $ISSUES_COUNT issue(s) overall"
    debug "$PUZZLE_ID: result=$JSON"
    if [ $ISSUES_COUNT -gt 1 ]; then
        # LATER: include in the output type of match -- in:title or in:body
        error ''
        error "$PUZZLE_ID: found $ISSUES_COUNT issues that match the criterias:"
        CANDIDATES="$(echo "$JSON" | jq --raw-output '.[] | [ .number, .state, .url, .title ] | @tsv')"
        echo >&2 "$CANDIDATES"
        error "Ways to resolve:"
        error "    1) modify a body of one of the tickets to not contain puzzle id (or to have a different title)"
        error "    2) manually create a mapping between this puzzle and one of the issues:"
        echo "$CANDIDATES" | while read ISSUE_ID ISSUE_STATE REST; do
            error "       echo '$PUZZLE_ID\t$ISSUE_ID\t$ISSUE_STATE\tmanually' >>$MAPPING_FILE"
        done
        fatal ''
    fi

    if [ $ISSUES_COUNT -le 0 ]; then
        info "$PUZZLE_ID: no related issues found. Need to create a new issue: $TITLE"
        NEW_ISSUES_COUNT=$[NEW_ISSUES_COUNT + 1]
        continue
    fi

    if [ $ISSUES_COUNT -eq 1 ]; then
        ISSUE_ID="$(echo "$JSON" | jq '.[0] | .number')"
        ISSUE_URL="$(echo "$JSON" | jq --raw-output '.[0] | .url')"

        ISSUE_TITLE="$(echo "$JSON" | jq --raw-output '.[0] | .title')"
        if [ "$TITLE" != "$ISSUE_TITLE" ]; then
            warn ''
            warn "$PUZZLE_ID => #$ISSUE_ID: $ISSUE_URL looks identical but titles don't match!"
            warn "Perhaps, the issue's title was modified after issue creation"
            warn "Expected: $TITLE"
            warn "Found:    $ISSUE_TITLE"
            warn ''
        else
            debug "$PUZZLE_ID => #$ISSUE_ID: titles match"
        fi

        ISSUE_STATE="$(echo "$JSON" | jq --raw-output '.[0] | .state')"
        if [ "$ISSUE_STATE" = 'closed' ]; then
            error ''
            error "$PUZZLE_ID => #$ISSUE_ID: $ISSUE_URL is closed!"
            error "Either the issue isn't related to a puzzle or the issues was closed manually but a puzzle left in code"
            error "Ways to resolve:"
            error "    1) remove the puzzle with id $PUZZLE_ID from code"
            error "    2) investigate and manually resolve this collision"
            fatal ''

        elif [ "$ISSUE_STATE" != 'open' ]; then
            error ''
            error "$PUZZLE_ID => #$ISSUE_ID: $ISSUE_URL has unknown state"
            error "Expected: 'open' or 'closed'"
            error "Found:    $ISSUE_STATE"
            fatal ''
        fi

        ISSUE_BODY="$(echo "$JSON" | jq --raw-output '.[0] | .body')"
        if ! echo "$ISSUE_BODY" | grep -q "$PUZZLE_ID"; then
            error ''
            error "$PUZZLE_ID => #$ISSUE_ID: issue looks identical but its body doesn't contain the puzzle id ($PUZZLE_ID)!"
            error "Perhaps, the puzzle id got changed after issue creation"
            error "Body: $ISSUE_BODY"
            error "Ways to resolve:"
            error "    1) edit $ISSUE_URL and modify its body to contain $PUZZLE_ID"
            error "    2) manually create a mapping between this puzzle and the issue:"
            error "       echo '$PUZZLE_ID\t$ISSUE_ID\t$ISSUE_STATE\tmanually' >>$MAPPING_FILE"
            fatal ''
        else
            debug "$PUZZLE_ID => #$ISSUE_ID: body contains puzzle id"
        fi

        info "$PUZZLE_ID => #$ISSUE_ID: link with $ISSUE_ID ($ISSUE_STATE)"
        printf '%s\t%s\t%s\tautomatically\n' "$PUZZLE_ID" "$ISSUE_ID" "$ISSUE_STATE" >> "$MAPPING_FILE"
    fi
done <<< "$(grep -v '^Id' "$1")"

info ''
info 'DONE'
info "Puzzles   : $PUZZLES_COUNT"
info "New issues: $NEW_ISSUES_COUNT"
