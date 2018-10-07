## Run MyStamps in Docker

MyStamps application can be run as a Docker container. While there are many
possible configurations, we support only those that are typically used:

* **a single container** where the application is running with `test` profile.
  In this mode, it also runs in-memory database H2. This is roughly the same as
  executing `mvn spring-boot:run` command but inside a Docker container.

  * Create a WAR file and build the image `phpcoder/mystamps:latest`:
    ```console
    $ mvn package dockerfile:build
    ```
  * Run a container with `docker-compose`:
    ```console
    $ cd docker
    $ docker-compose up -d
    ```
    or with a plain Docker:
    ```console
    $ docker run -d -p 8080:8080 --cap-drop all phpcoder/mystamps:latest
    ```

