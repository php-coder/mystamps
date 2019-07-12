# Robot Framework

## Faster Iterations

As we already know, `./mvnw verify` runs all the integration tests. But let's
suppose that we are working on a single test and we need to run it many times
until it will work as desired. In this case, we would like to iterate faster
and at least don't run all the tests, but the only one that we are working on.

In this case we can (temporary) mark the test with a unique tag, let’s say
`testme`, and ask `robotframework-maven-plugin` to execute only tests with such
a tag:
```console
./mvnw verify -Dincludes=testme
```

Now we run much less tests but could we make the execution even faster? Sure,
we can! Let's look on what exactly `./mvnw verify` does:
1) it runs the application (this in turn includes many steps, like code
   compilation, preparation of the resources, running unit tests and so on)
2) it runs [Wiremock server](wiremock.md) that is required by some tests
3) it executes the integration tests

As we modify only one (or more) of the integration tests, we don't need to
re-run the whole application every time. Also, if our test doesn't use
Wiremock, we might also skip this part entirely. To do so, we should manually
execute all the steps that we need:

1) run the application:
   ```console
   ./mvnw spring-boot:run
   ```
2) in another terminal, run Wiremock server, only if your test need it:
   ```console
   ./mvnw wiremock:run -DkeepRunning=true
   ```
3) in another terminal, run integration tests as many times as you need:
   ```console
   ./mvnw robotframework:run -Dincludes=testme
   ```

