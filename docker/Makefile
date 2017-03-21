build:
	ln -v ../target/mystamps.war mystamps.war && \
	docker build --tag mystamps:0.3.0 .; \
	rm -fv mystamps.war

start:
	docker run --name mystamps --publish 8080:8080 --detach mystamps:0.3.0

stop:
	docker stop mystamps

remove:
	docker rm mystamps

remove-completely:
	docker rm -v mystamps

.PHONY: build start stop remove remove-completely
