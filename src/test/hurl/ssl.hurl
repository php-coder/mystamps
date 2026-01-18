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
certificate "Issuer" == "C = US, O = Let's Encrypt, CN = R13"
certificate "Expire-Date" daysAfterNow > 10

GET https://stamps.filezz.ru
HTTP 200
[Asserts]
certificate "Subject" == "CN = stamps.filezz.ru"
certificate "Issuer" == "C = US, O = Let's Encrypt, CN = R13"
certificate "Expire-Date" daysAfterNow > 10

