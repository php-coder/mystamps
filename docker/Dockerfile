# Parents Dockerfiles:
# https://github.com/tianon/docker-brew-debian/blob/a3d2e76fdd618d1ca1b145c0a2268e828d547ea2/jessie/Dockerfile
# https://github.com/docker-library/buildpack-deps/blob/a0a59c61102e8b079d568db69368fb89421f75f2/jessie/curl/Dockerfile
# https://github.com/docker-library/openjdk/blob/445f8b8d18d7c61e2ae7fda76d8883b5d51ae0a5/8-jre/Dockerfile
FROM openjdk:8u121-jre

# Informs Docker that the container listens on the specified network ports at runtime.
# See also: https://docs.docker.com/engine/reference/builder/#expose
EXPOSE 8080

# Sets active Spring profile. Possible values are: test, prod and travis.
# See also: https://docs.docker.com/engine/reference/builder/#env
ENV SPRING_PROFILES_ACTIVE=test

# Creates base directories and unprivileged user.
# See also: https://docs.docker.com/engine/reference/builder/#run
RUN mkdir /data \
 && useradd mystamps --home-dir /data/mystamps --create-home --comment 'MyStamps' \
 && mkdir /data/uploads /data/heap-dumps \
 && chown mystamps:mystamps /data/uploads /data/heap-dumps

# Creates mount points and marks them as holding externally mounted volumes from native host.
# See also: https://docs.docker.com/engine/reference/builder/#volume
VOLUME /data/uploads /data/heap-dumps

# Sets the user name to use when running the image and for any subsequent RUN, CMD and ENTRYPOINT instructions.
# See also: https://docs.docker.com/engine/reference/builder/#user
USER mystamps

# Sets the working directory for any subsequent RUN, CMD, ENTRYPOINT, COPY and ADD instructions.
# See also: https://docs.docker.com/engine/reference/builder/#workdir
WORKDIR /data/mystamps

# Copies mystamps.war into destination directory. File is created with a UID and GID of 0.
# See also: https://docs.docker.com/engine/reference/builder/#copy
COPY mystamps.war /data/mystamps

# Sets the command to be executed during container startup.
# See also: https://docs.docker.com/engine/reference/builder/#cmd
CMD [ "java" \
	, "-XX:+HeapDumpOnOutOfMemoryError" \
	, "-XX:HeapDumpPath=/data/heap-dumps" \
	, "-XX:+UseCompressedOops" \
	, "-Dsun.rmi.dgc.client.gcInterval=86400000" \
	, "-Dsun.rmi.dgc.server.gcInterval=86400000" \
	, "-Djava.security.egd=file:/dev/./urandom" \
	, "-Xmx128m" \
	, "-Xss256k" \
	, "-Dserver.address=0.0.0.0" \
	, "-jar" \
	, "mystamps.war" \
]
