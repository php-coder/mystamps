#!/bin/sh

FILES="
../src/main/java/ru/mystamps/web/service/SiteService.java
../src/main/java/ru/mystamps/web/service/UserService.java
../src/main/java/ru/mystamps/web/service/ImageService.java
../src/main/java/ru/mystamps/web/service/CountryService.java
../src/main/java/ru/mystamps/web/service/CronService.java
../src/main/java/ru/mystamps/web/service/SeriesService.java
../src/main/java/ru/mystamps/web/service/dto/AddCountryDto.java
../src/main/java/ru/mystamps/web/service/dto/AddSeriesDto.java
../src/main/java/ru/mystamps/web/service/dto/RegisterAccountDto.java
../src/main/java/ru/mystamps/web/service/dto/ActivateAccountDto.java
../src/main/java/ru/mystamps/web/service/AuthService.java
../src/main/java/ru/mystamps/web/util/CatalogUtils.java
../src/test/java/ru/mystamps/web/tests/fest/DateAssert.java
../src/test/java/ru/mystamps/web/service/SeriesServiceTest.java
../src/test/java/ru/mystamps/web/service/CronServiceTest.java
../src/test/java/ru/mystamps/web/service/SiteServiceTest.java
../src/test/java/ru/mystamps/web/service/CountryServiceTest.java
../src/test/java/ru/mystamps/web/service/UserServiceTest.java
../src/test/java/ru/mystamps/web/service/ImageServiceTest.java
../src/test/java/ru/mystamps/web/util/CatalogUtilsTest.java
"

for F in $FILES; do
	DST=$(dirname $(echo "$F" | sed 's|^\.\./||'))
	[ -d "$DST" ] || mkdir -p "$DST"
	cp "$F" "$DST"
done
