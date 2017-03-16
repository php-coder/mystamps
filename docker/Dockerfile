# Parents Dockerfiles:
# https://github.com/tianon/docker-brew-debian/blob/a3d2e76fdd618d1ca1b145c0a2268e828d547ea2/jessie/Dockerfile
# https://github.com/docker-library/buildpack-deps/blob/a0a59c61102e8b079d568db69368fb89421f75f2/jessie/curl/Dockerfile
# https://github.com/docker-library/openjdk/blob/445f8b8d18d7c61e2ae7fda76d8883b5d51ae0a5/8-jre/Dockerfile
FROM openjdk:8u121-jre
EXPOSE 8080
COPY mystamps.war /tmp
CMD [ "java", "-Dserver.address=0.0.0.0", "-jar", "/tmp/mystamps.war" ]
