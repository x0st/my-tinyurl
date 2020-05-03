PROJECT_ROOT=$(shell pwd)/../..

.PHONY: up down logs enter-java build

build:
	# Clean up
	rm -rf backend/java/codebase
	# Copy project
	mkdir backend/java/codebase
	cp -r ${PROJECT_ROOT}/codebase/backend/src backend/java/codebase/src
	cp ${PROJECT_ROOT}/codebase/backend/{settings.gradle,build.gradle} backend/java/codebase/
	# Docker build
	docker build -t tinyurl-backend-java backend/java
	docker build -t tinyurl-gateway-nginx gateway/nginx
	# Clean up
	rm -rf backend/java/codebase

enter-java:
	docker-compose exec backend-java sh

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

hotswap:
	# docker cp ${PROJECT_ROOT}/codebase/backend/src $(shell docker-compose ps -q backend-java):/app/
	# docker cp ${PROJECT_ROOT}/codebase/backend/settings.gradle $(shell docker-compose ps -q backend-java):/app/settings.gradle
	# docker cp ${PROJECT_ROOT}/codebase/backend/build.gradle $(shell docker-compose ps -q backend-java):/app/build.gradle
	docker exec $(shell docker-compose ps -q backend-java) /opt/gradle/gradle-6.3/bin/gradle --build-file /app/build.gradle jar
	docker exec $(shell docker-compose ps -q backend-java) supervisorctl restart app
	/usr/bin/osascript -e "display notification \"Compiled\" with title \"tinyurl\""