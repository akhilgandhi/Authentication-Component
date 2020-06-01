FROM adoptopenjdk/openjdk11:alpine-slim as build

ENV spring-profile=dev

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw
#download the dependency if needed or if pom is changed
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

#Production stage for spring boot application image
FROM adoptopenjdk/openjdk11:alpine-jre as production
ARG DEPENDENCY=/app/target/dependency

#Copy the dependency application file from build stage artifact
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

#Run the spring boot application
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "-Dspring.profiles.active=dev", "com.organization.webapp.WebApplication"]