## Run MyStamps in Docker

MyStamps application can be run as a Docker container. While there are many
possible configurations, we support only those that are typically used:

* **a single container** where the application is running with `test` profile.
  In this mode, it also runs in-memory database H2. This is roughly the same as
  executing `mvn spring-boot:run` command but inside a Docker container.

  * Create a WAR file that will be used for building an image:
    ```console
    $ mvn package
    $ cd docker
    $ ln -fv ../target/mystamps.war mystamps.war
    ```
    By the way, don't forget to remove this hard link after using.
  * Build an image and run a container with `docker-compose`:
    ```console
    $ docker-compose up -d
    ```
  * The same but with a plain Docker:
    ```console
    $ docker build -t mystamps:latest .
    $ docker run -d -p 8080:8080 mystamps:latest
    ```

