#!/bin/sh
set -e

# Función genérica para esperar un servicio
wait_for_service() {
  local name=$1
  local url=$2
  local max_retries=${3:-30} # default 30 intentos
  local count=0

  echo "Esperando a $name en $url ..."
  until curl -s "$url" | grep -q "UP" > /dev/null; do
    count=$((count+1))
    if [ $count -ge $max_retries ]; then
      echo "⛔ Timeout esperando a $name después de $((count*2)) segundos."
      exit 1
    fi
    echo "$name aún no está disponible. Reintentando en 2s..."
    sleep 2
  done

  echo "✅ $name está listo."
}

# --- Esperas específicas ---

# Esperar a Eureka
wait_for_service "Eureka" "${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE}/apps"

# Esperar a Config Server
wait_for_service "Config Server" "${SPRING_CLOUD_CONFIG_URI}/actuator/health"

# --- Iniciar el microservicio ---
echo "🚀 Todos los servicios están disponibles. Iniciando el gateway-server..."
exec java -jar gateway-server.jar