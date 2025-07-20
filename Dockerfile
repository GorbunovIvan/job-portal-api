FROM openjdk:21

WORKDIR /app

COPY build/libs/job-portal-api-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]

# Run:
#   'docker build -t ivangorbunovv/job-portal-api-image .'
