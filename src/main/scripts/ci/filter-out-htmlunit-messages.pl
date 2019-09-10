#!/usr/bin/perl
#
# This script is filtering out log messages from htmlunit.
# It's a workaround for https://github.com/php-coder/mystamps/issues/538
#

use strict;
use warnings;

my @regexps = (
	# [WARNING] CSS error: 'http://127.0.0.1:8081/public/bootstrap/3.4.1/css/bootstrap.min.css' [5:115558] Invalid color "#000\9".
	qr/\[WARNING\] CSS error: '[^']+' \[[^]]+\] Invalid color "[^"]+"\.\n/,
	
	# [WARNING] CSS error: 'http://127.0.0.1:8081/public/selectize/0.12.3/css/selectize.bootstrap3.css' [176:3] Error in declaration. '*' is not allowed as first char of a property.
	qr/\[WARNING\] CSS error: '[^']+' \[[^]]+\] Error in declaration\. '\*' is not allowed as first char of a property\.\n/,
	
	# [INFO] Bad input type: "select-one", creating a text input
	qr/\[INFO\] Bad input type: "[^"]+", creating a text input\n/,
	
	# Google Charts:
	# [WARNING] Obsolete content type encountered: 'text/javascript'.
	qr/\[WARNING\] Obsolete content type encountered: 'text.javascript'\.\n/,
	
	# Google Charts:
	# [WARNING] CSS error: 'https://www.gstatic.com/charts/44/css/core/tooltip.css'
	# [3:38] Error in pseudo class or element. (Invalid token " ". Was expecting one of: <IDENT>, ":",
	# <FUNCTION_NOT>, <FUNCTION_LANG>, <FUNCTION>.)
	qr/\[WARNING\] CSS error: 'https:..www\.gstatic\..*\n/,
	
	# Google Charts:
	# [WARNING] CSS warning: 'https://www.gstatic.com/charts/44/css/core/tooltip.css' [3:38] Ignoring the whole rule.
	qr/\[WARNING\] CSS warning: 'https:..www\.gstatic\..*\n/
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
