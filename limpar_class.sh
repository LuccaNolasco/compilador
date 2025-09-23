#!/bin/bash

# Script para limpar todos os arquivos .class do projeto
echo "🧹 Limpando arquivos .class..."

# Contar quantos arquivos .class existem antes da limpeza
TOTAL=$(find . -name "*.class" | wc -l)

if [ $TOTAL -gt 0 ]; then
    echo "📁 Encontrados $TOTAL arquivo(s) .class"
    
    # Remover todos os arquivos .class recursivamente
    find . -name "*.class" -delete
    
    echo "✅ Todos os arquivos .class foram removidos!"
else
    echo "ℹ️  Nenhum arquivo .class encontrado."
fi

echo "🎯 Limpeza concluída!"
