#!/bin/bash

# Script para executar o compilador com configurações brasileiras

echo "Configurando ambiente para layout brasileiro..."

# Configurar variáveis de ambiente para localização brasileira
export LANG=pt_BR.UTF-8
export LC_ALL=pt_BR.UTF-8
export LC_CTYPE=pt_BR.UTF-8
export LC_MESSAGES=pt_BR.UTF-8

# Configurar propriedades Java para layout brasileiro
JAVA_OPTS="-Duser.language=pt"
JAVA_OPTS="$JAVA_OPTS -Duser.country=BR"
JAVA_OPTS="$JAVA_OPTS -Duser.region=BR"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
JAVA_OPTS="$JAVA_OPTS -Dsun.jnu.encoding=UTF-8"
JAVA_OPTS="$JAVA_OPTS -Djava.awt.im.style=on-the-spot"
JAVA_OPTS="$JAVA_OPTS -Djavafx.platform.locale=pt-BR"
JAVA_OPTS="$JAVA_OPTS -Dnative.encoding=UTF-8"

echo "Compilando arquivos Java..."
# Compilar todos os arquivos Java
find . -name "*.java" -exec javac -encoding UTF-8 {} \;

if [ $? -eq 0 ]; then
    echo "Compilação bem-sucedida!"
    echo "Executando aplicação com configurações brasileiras..."
    
    # Executar a aplicação com as configurações brasileiras
    java $JAVA_OPTS Main
else
    echo "Erro na compilação!"
    exit 1
fi