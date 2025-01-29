FROM openjdk:21-jdk-slim

# Definir las variables de entorno
ENV CONFIG_SERVER_URI="http://localhost:8888"
ENV EUREKA_URI="http://localhost:8761/eureka/"
ENV MUSIC_SERVER_PORT="8084"
ENV NEXTCLOUD_URI="lb://nextcloud"
ENV DEPLOY="local"

# Copiar el archivo JAR al contenedor
COPY ./music/target/*.jar /app.jar

# Comando para ejecutar el JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]