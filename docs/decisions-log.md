# Bugs, workarounds and project's decisions

| Date | Type | Description | Comment | References |
| ---- | ---- | ----------- | ------- | ---------- |
| 09.02.2020 | Workaround | pdd fails to parse files in non-UTF-8 charset with error `invalid byte sequence in UTF-8` | Exclude `src/test/wiremock` directory | yegor256/pdd#143, 30aab7dc8c265804efef382422e7cae9e53187f3, ab563b653279625120121babccdfd915181ee46d |
