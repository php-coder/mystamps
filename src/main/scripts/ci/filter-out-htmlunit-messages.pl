#!/usr/bin/perl
#
# This script is filtering out log messages from htmlunit.
# It's a workaround for https://github.com/php-coder/mystamps/issues/538
#

use strict;
use warnings;

my @regexps = (
	# [WARNING] CSS error: 'http://127.0.0.1:8081/public/bootstrap/3.4.1/css/bootstrap.min.css' [5:56298] Error in expression; ':' found after identifier "progid".
	qr/\[WARNING\] CSS error: '[^']+' \[[^]]+\] Error in expression; [^\.]+\.\n/,
	
	# [WARNING] CSS error: 'http://127.0.0.1:8081/public/bootstrap/3.4.1/css/bootstrap.min.css' [5:115558] Invalid color "#000\9".
	qr/\[WARNING\] CSS error: '[^']+' \[[^]]+\] Invalid color "[^"]+"\.\n/,
	
	# [WARNING] CSS error: 'http://127.0.0.1:8081/public/selectize/0.12.3/css/selectize.bootstrap3.css' [176:3] Error in declaration. '*' is not allowed as first char of a property.
	qr/\[WARNING\] CSS error: '[^']+' \[[^]]+\] Error in declaration\. '\*' is not allowed as first char of a property\.\n/,
	
	# [ERROR] runtimeError: message=[An invalid or illegal selector was specified (selector: '*,:x' error: Invalid selector: :x).] sourceName=[http://127.0.0.1:8081/public/jquery/1.9.1/jquery.min.js] line=[4] lineSource=[null] lineOffset=[0]
	qr/\[ERROR\] runtimeError: message=\[[^]]+\] sourceName=\[[^]]+\] line=\[[^]]+\] lineSource=\[[^]]+\] lineOffset=\[[^]]+\]\n/,

	# [ERROR] runtimeError: message=[An invalid or illegal selector was specified (selector: '[id='sizzle-1487943406281'] [data-selectable]:first' error: Invalid selector: [id="sizzle-1487943406281"] [data-selectable]:first).] sourceName=[http://127.0.0.1:8081/public/jquery/1.9.1/jquery.min.js] line=[4] lineSource=[null] lineOffset=[0]
	qr/\[ERROR\] runtimeError: message=\[[^\(]+\(selector: '\[id='sizzle-[^']+'\] \[data-selectable\]:first' error: Invalid selector: \[id="sizzle-[^"]+"\] \[data-selectable\]:first\)\.\] sourceName=\[[^]]+\] line=\[[^]]+\] lineSource=\[[^]]+\] lineOffset=\[[^]]+\]\n/,
	
	# [INFO] Bad input type: "url", creating a text input
	# [INFO] Bad input type: "select-one", creating a text input
	qr/\[INFO\] Bad input type: "[^"]+", creating a text input\n/
);

foreach my $line (<STDIN>) {
	foreach my $regexp (@regexps) {
		my $count = $line =~ s/$regexp//;
		if ($count > 0) {
			# replacement were performed, skip other regexps
			last;
		}
	}
	
	print $line;
}
