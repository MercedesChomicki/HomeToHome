#!/bin/sh
set -e

echo "Esperando a Eureka en http://eureka-server:8761/eureka/apps..."
until curl -s http://eureka-server:8761/eureka/apps > /dev/null; do
  echo "Eureka aún no está disponible. Esperando 10 segundos..."
  sleep 10
done

echo "Eureka está disponible. Iniciando el servicio..."
exec java -jar chat-service.jar
