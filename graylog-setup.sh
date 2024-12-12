#!/bin/bash

USERNAME="admin"
PASSWORD="password"
GRAYLOG_URL="http://graylog:9000/api"

# Ожидание запуска Graylog
until curl -s -u "$USERNAME:$PASSWORD" "$GRAYLOG_URL/system/inputs" &>/dev/null; do
  echo "Ждем запуска Graylog..."
  sleep 5
done

echo "Graylog доступен. Настраиваем входы..."

# Создание Gelf UDP input
curl -s -u "$USERNAME:$PASSWORD" \
  -X POST "$GRAYLOG_URL/system/inputs" \
  -H "Content-Type: application/json" \
  -H "X-Requested-By: setup-script" \
  -d '{
  "title": "GELF UDP Input",
  "type": "org.graylog2.inputs.gelf.udp.GELFUDPInput",
  "global": true,
  "configuration": {
    "bind_address": "0.0.0.0",
    "port": 12201,
    "recv_buffer_size": 262144,
    "decompress_size_limit": 8388608,
    "number_worker_threads": 2,
    "override_source": null,
    "tls_cert_file": null,
    "tls_key_file": null,
    "tls_key_password": null,
    "enable_tls": false
  },
  "node": null
}'

echo "Вход Gelf UDP добавлен."
