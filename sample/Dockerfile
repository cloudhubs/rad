FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/rad-sample-0.0.5.jar

ADD ${JAR_FILE} rad-sample.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "-Dserver.port=8090", "/rad-sample.jar"]

# docker build -t diptadas/rad-sample .
# docker push diptadas/rad-sample
