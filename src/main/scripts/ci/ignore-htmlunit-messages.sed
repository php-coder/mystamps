#
# This script is filtering out log messages from htmlunit.
# It's a workaround for https://github.com/php-coder/mystamps/issues/538
#
# IMPORTANT: script should be executed with `sed -z` to be able to replace new line character.
#

# [INFO] Bad input type: "url", creating a text input
s|\[INFO\] Bad input type: "url", creating a text input\n||g

# [WARNING] CSS error: 'http://127.0.0.1:8081/public/bootstrap/3.3.7/css/bootstrap.min.css' [5:56298] Error in expression; ':' found after identifier "progid".
s|\[WARNING\] CSS error: '[^']\+' \[[^]]\+\] Error in expression; [^\.]\+\.\n||g

# [WARNING] CSS error: 'http://127.0.0.1:8081/public/bootstrap/3.3.7/css/bootstrap.min.css' [5:115558] Invalid color "#000\9".
s|\[WARNING\] CSS error: '[^']\+' \[[^]]\+\] Invalid color "[^"]\+"\.\n||g

# [WARNING] CSS error: 'http://127.0.0.1:8081/public/selectize/0.12.3/css/selectize.bootstrap3.css' [176:3] Error in declaration. '*' is not allowed as first char of a property.
s|\[WARNING\] CSS error: '[^']\+' \[[^]]\+\] Error in declaration\. '\*' is not allowed as first char of a property\.\n||g

# Simplify error message and make it match the next filter.
# [ERROR] runtimeError: message=[An invalid or illegal selector was specified (selector: '[id='sizzle-1487943406281'] [data-selectable]:first' error: Invalid selector: [id="sizzle-1487943406281"] [data-selectable]:first).] sourceName=[http://127.0.0.1:8081/public/jquery/1.9.1/jquery.min.js] line=[4] lineSource=[null] lineOffset=[0]
s|\(selector: '\[id='sizzle-[^']\+'\] \[data-selectable\]:first' error: Invalid selector: \[id="sizzle-[^"]\+"\] \[data-selectable\]:first\)||g

# [ERROR] runtimeError: message=[An invalid or illegal selector was specified (selector: '*,:x' error: Invalid selector: :x).] sourceName=[http://127.0.0.1:8081/public/jquery/1.9.1/jquery.min.js] line=[4] lineSource=[null] lineOffset=[0]
s|\[ERROR\] runtimeError: message=\[[^]]\+\] sourceName=\[[^]]\+\] line=\[[^]]\+\] lineSource=\[[^]]\+\] lineOffset=\[[^]]\+\]\n||g
