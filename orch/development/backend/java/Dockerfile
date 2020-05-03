FROM openjdk:14-alpine

# Gradle
RUN wget https://services.gradle.org/distributions/gradle-6.3-bin.zip && \
    mkdir /opt/gradle && \
    unzip -d /opt/gradle /gradle-6.3-bin.zip

# Codebase
ADD codebase /app

# Supervisor
RUN apk update && apk add --no-cache supervisor
ADD supervisord.conf /etc/supervisord.conf

# Compile the app
RUN /opt/gradle/gradle-6.3/bin/gradle \
    --build-file /app/build.gradle \
    --no-daemon jar

WORKDIR /app

CMD ["/usr/bin/supervisord", "--nodaemon", "-c", "/etc/supervisord.conf"]