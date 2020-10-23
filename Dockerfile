#FROM openjdk:8-jre-alpine
FROM openjdk:8-jdk

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
ENV MICROSERVICE_HOME /usr/microservices

# Set the name of the jar
#ENV APP_FILE ocrApi-0.0.1-SNAPSHOT-jar-with-dependencies.jar
ENV APP_FILE ocrApi-0.0.1-SNAPSHOT.jar

# Open the port
EXPOSE 8080

# Copy our JAR
COPY target/$APP_FILE /app.jar

# Launch the Spring Boot application
#ENV APP_OPTS=""
ENV JAVA_OPTS=""

#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar " ]
ENTRYPOINT ["java", "-jar", "/app.jar"]