#!/bin/bash

# run.sh - Script corregido para ejecutar la aplicación For Honor

# Cambiar al directorio del script
cd "$(dirname "$0")"

# Configurar opciones de Maven
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# Clase principal por defecto
DEFAULT_MAIN_CLASS="cat.iesesteveterradas.Main"

# Usar la clase proporcionada o la por defecto
if [ -z "$1" ]; then
    MAIN_CLASS="$DEFAULT_MAIN_CLASS"
    echo "Usando clase principal por defecto: $MAIN_CLASS"
else
    MAIN_CLASS="$1"
    echo "Clase principal: $MAIN_CLASS"
    shift  # Remover el primer argumento
fi

# Argumentos para Java (si los hay)
JAVA_ARGS="$@"

echo "========================================"
echo "Configuración:"
echo "========================================"
echo "MAVEN_OPTS: $MAVEN_OPTS"
echo "Clase principal: $MAIN_CLASS"
echo "Argumentos Java: $JAVA_ARGS"
echo "========================================"

# Construir comando Maven
MAVEN_CMD="mvn clean compile exec:java -Dexec.mainClass=\"$MAIN_CLASS\""

# Agregar argumentos Java si existen
if [ -n "$JAVA_ARGS" ]; then
    MAVEN_CMD="$MAVEN_CMD -Dexec.args=\"$JAVA_ARGS\""
fi

echo "Ejecutando: $MAVEN_CMD"
echo "========================================"

# Ejecutar Maven
eval $MAVEN_CMD