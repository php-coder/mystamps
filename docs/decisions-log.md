# Bugs, workarounds and project's decisions

| Date/Type | Description |
| --------- | ----------- |
| 09.02.2020 Workaround | **pdd** fails to parse files in charsets other than UTF-8 with error `invalid byte sequence in UTF-8`. Workaround: exclude `src/test/wiremock` directory. Bug: [yegor256/pdd#143](https://github.com/yegor256/pdd/issues/143) Commits: [30aab7d](https://github.com/php-coder/mystamps/commit/30aab7dc8c265804efef382422e7cae9e53187f3), [ab563b6](https://github.com/php-coder/mystamps/commit/ab563b653279625120121babccdfd915181ee46d) |
