#!/bin/sh
set -e

EUREKA_HEALTH_URL="http://eureka-server:8761/actuator/health"

echo "Esperando a que Eureka esté listo en $EUREKA_HEALTH_URL ..."
until curl -s "$EUREKA_HEALTH_URL" | grep '"status":"UP"' > /dev/null; do
  echo "Eureka aún no está listo. Esperando 10 segundos..."
  sleep 10
done

echo "Eureka está listo. Iniciando Config Server..."
exec java -jar config-server.jar