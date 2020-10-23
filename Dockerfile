#
# Build stage
#
FROM maven:3.6.3-openjdk-8 AS build

#copy pom
#COPY pom.xml .
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app


#resolve maven dependencies
#RUN mvn clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r target/
RUN mvn -f /usr/src/app/pom.xml clean package
#RUN mvn clean package

#copy source
#COPY src ./src

# build the app (no dependency download here)
#RUN mvn clean package  -Dmaven.test.skip


#
# Package stage
#
#FROM openjdk:8-jre-alpine
FROM openjdk:8-jdk
#FROM gcr.io/distroless/java

RUN apt update

# Install tesseract library
#RUN apk add --no-cache tesseract-ocr

# Download last language package
RUN mkdir -p /usr/share/tessdata
ADD https://github.com/tesseract-ocr/tessdata/raw/master/eng.traineddata /usr/share/tessdata/eng.traineddata
ADD https://github.com/tesseract-ocr/tessdata/raw/master/ces.traineddata /usr/share/tessdata/ces.traineddata
ADD https://github.com/tesseract-ocr/tessdata/raw/master/slk.traineddata /usr/share/tessdata/slk.traineddata

# Check the installation status
#RUN tesseract --list-langs
#RUN tesseract -v

# Set the location of the jar
#ENV MICROSERVICE_HOME /usr/microservices

# Set the name of the jar
#ENV APP_FILE ocrApi-0.0.1-SNAPSHOT-jar-with-dependencies.jar
ENV APP_FILE ocrApi-0.0.1-SNAPSHOT.jar

# Open the port
EXPOSE 8888

# Copy our JAR
#COPY target/$APP_FILE /app.jar
COPY --from=build /usr/src/app/target/ocrApi-0.0.1-SNAPSHOT.jar /usr/app/ocrApi-0.0.1-SNAPSHOT.jar

# Launch the Spring Boot application
#ENV APP_OPTS=""
#ENV JAVA_OPTS=""

#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar " ]
#ENTRYPOINT ["java", "-jar", "/app.jar"]
ENTRYPOINT ["java", "-jar", "/usr/app/ocrApi-0.0.1-SNAPSHOT.jar"]