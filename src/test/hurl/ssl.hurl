#
# Tests of SSL cerificate
#
# How to run:
# hurl --test ssl.hurl
#
# See also: https://hurl.dev and https://github.com/Orange-OpenSource/hurl
#

# https://hurl.dev/docs/asserting-response.html#ssl-certificate-assert
GET https://my-stamps.ru
HTTP 200
[Asserts]
certificate "Subject" == "CN = my-stamps.ru"
certificate "Issuer" contains "Let's Encrypt"
certificate "Expire-Date" daysAfterNow > 10

GET https://www.my-stamps.ru
HTTP 200
[Asserts]
certificate "Subject" == "CN = my-stamps.ru"
certificate "Issuer" contains "Let's Encrypt"
certificate "Expire-Date" daysAfterNow > 10

GET https://stamps.filezz.ru
HTTP 301
[Asserts]
certificate "Subject" == "CN = my-stamps.ru"
certificate "Issuer" contains "Let's Encrypt"
certificate "Expire-Date" daysAfterNow > 10

