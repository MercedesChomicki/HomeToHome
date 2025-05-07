#!/bin/sh
set -e

echo "Esperando a Eureka en http://eureka-server:8761/eureka/apps..."
until curl -s http://eureka-server:8761/eureka/apps > /dev/null; do
  echo "Eureka aún no está disponible. Esperando 5 segundos..."
  sleep 5
done

echo "Eureka está disponible. Iniciando el servicio..."
exec java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar user-service.jar
