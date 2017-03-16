build-image:
	ln -v ../target/mystamps.war mystamps.war && \
	docker build --tag mystamps:0.3.0 .; \
	rm -fv mystamps.war

.PHONY: build-image
