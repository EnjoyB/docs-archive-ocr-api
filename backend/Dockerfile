#
# Build stage
#
FROM maven:3.6.3-openjdk-11 AS build

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

RUN mvn -f /usr/src/app/pom.xml clean package

#
# Package stage
#
FROM gcr.io/distroless/java-debian10

# Download last language package
ADD https://github.com/tesseract-ocr/tessdata/blob/master/eng.traineddata /usr/share/tessdata/eng.traineddata
ADD https://github.com/tesseract-ocr/tessdata/blob/master/ces.traineddata /usr/share/tessdata/ces.traineddata
ADD https://github.com/tesseract-ocr/tessdata/blob/master/slk.traineddata /usr/share/tessdata/slk.traineddata

# Set the name of the jar
ENV APP_FILE ocrApi-0.0.1-SNAPSHOT.jar

# Open the port, inside docker network
EXPOSE 8086

# Copy our JAR
#COPY target/$APP_FILE /app.jar
COPY --from=build /usr/src/app/target/ocrApi-0.0.1-SNAPSHOT.jar /usr/app/ocrApi-0.0.1-SNAPSHOT.jar

# Launch the Spring Boot application
#ENV JAVA_OPTS=""

#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar " ]
#ENTRYPOINT ["java", "-jar", "/app.jar"]
ENTRYPOINT ["java", "-jar", "/usr/app/ocrApi-0.0.1-SNAPSHOT.jar"]
