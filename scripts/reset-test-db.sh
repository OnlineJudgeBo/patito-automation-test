#!/usr/bin/env bash
set -e

DB_CONTAINER="patito-db"
DB_NAME="jol"
DB_USER="root"
DB_PASSWORD="supertest"
DB_DIR="scripts/database"

docker exec "$DB_CONTAINER" mariadb -u"$DB_USER" -p"$DB_PASSWORD" -e "
DROP DATABASE IF EXISTS \`$DB_NAME\`;
CREATE DATABASE \`$DB_NAME\` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
"

for file in "$DB_DIR"/*.sql; do
    echo "Importando $file"
    docker exec -i "$DB_CONTAINER" mariadb \
        --default-character-set=utf8mb4 \
        -u"$DB_USER" \
        -p"$DB_PASSWORD" \
        "$DB_NAME" < "$file"
done

echo "OK"