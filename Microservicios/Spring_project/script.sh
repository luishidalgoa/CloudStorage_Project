#!/bin/bash

# Navegar al directorio del proyecto y ejecutar mvn clean y package
mvn clean package

# Construir los contenedores Docker para cada servicio
docker buildx build --platform linux/amd64,linux/arm64 -t luishidalgoa/hidalgo-music:latest --push ./music/

docker build -f ./Gateway/Dockerfile -t gateway .
docker build -f ./Eureka/Dockerfile -t eureka .
docker build -f ./Config/Dockerfile -t config .
