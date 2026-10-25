# Compila con Maven y ejecuta la aplicación en una imagen que solo lleva el JRE.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd --system clinica && useradd --system --gid clinica clinica
COPY --from=build /app/target/clinica-demo-*.jar app.jar
USER clinica
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
