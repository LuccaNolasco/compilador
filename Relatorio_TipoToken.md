# Relatório da Enumeração TipoToken

## Descrição Geral
A enumeração `TipoToken` define todos os tipos de tokens (unidades léxicas) que podem ser reconhecidos pelo analisador léxico do compilador Pascal. Esta enumeração serve como um sistema de classificação que categoriza cada elemento léxico encontrado no código fonte, permitindo que o analisador sintático e outras fases do compilador processem adequadamente cada tipo de token.

## Estrutura da Enumeração

### Palavras Reservadas Específicas
A enumeração inclui constantes para palavras reservadas específicas da linguagem Pascal:

**Estruturas de Controle:**
- `IF`, `THEN`, `ELSE` - Estruturas condicionais
- `WHILE`, `FOR`, `DO` - Estruturas de repetição
- `CASE`, `OF` - Estrutura de seleção múltipla
- `REPEAT`, `UNTIL` - Estrutura de repetição pós-testada
- `GOTO` - Comando de salto incondicional

**Estruturas de Programa:**
- `PROGRAM` - Declaração de programa
- `BEGIN`, `END` - Delimitadores de bloco
- `MAIN` - Função principal
- `USES` - Declaração de unidades

**Declarações:**
- `VAR` - Declaração de variáveis
- `CONST` - Declaração de constantes
- `TYPE` - Declaração de tipos
- `LABEL` - Declaração de rótulos

**Subprogramas:**
- `FUNCTION`, `FUNC` - Declaração de função
- `PROC` - Declaração de procedimento
- `FORWARD` - Declaração antecipada
- `EXTERNAL` - Declaração externa

**Tipos de Dados:**
- `INTEGER` - Tipo inteiro
- `REAL` - Tipo real
- `CHAR` - Tipo caractere
- `STRING` - Tipo string
- `ARRAY` - Tipo array
- `RECORD` - Tipo registro
- `SET` - Tipo conjunto
- `FILE` - Tipo arquivo

**Modificadores e Especificadores:**
- `PACKED` - Modificador de empacotamento
- `ABSOLUTE` - Especificador de posição absoluta
- `INTERRUPT` - Especificador de interrupção

**Operadores Especiais:**
- `DIV` - Divisão inteira
- `SHL` - Deslocamento à esquerda
- `SHR` - Deslocamento à direita
- `XOR` - Ou exclusivo lógico
- `WITH` - Comando with

**Valores Especiais:**
- `NIL` - Valor nulo para ponteiros
- `NIT` - Valor especial (variante de NIL)

**Outras Palavras:**
- `IMPLEMENTATION` - Seção de implementação
- `INTERFACE` - Seção de interface
- `UNIT` - Declaração de unidade
- `TO`, `DOWTO` - Direções de loop

### Categorias Gerais de Tokens

**`PALAVRA_RESERVADA`**
- **Função**: Classificação geral para todas as palavras reservadas da linguagem
- **Uso**: Quando uma palavra reservada é reconhecida, mas tratada genericamente
- **Relação**: Complementa as constantes específicas de palavras reservadas

**`IDENTIFICADOR`**
- **Função**: Representa nomes definidos pelo usuário
- **Exemplos**: nomes de variáveis, funções, procedimentos, tipos personalizados
- **Características**: Deve começar com letra, pode conter letras e dígitos

**Tipos Numéricos:**
- **`NUMERO_INTEIRO`**: Representa valores inteiros (ex: 42, -17, 0)
- **`NUMERO_REAL`**: Representa valores reais (ex: 3.14, -2.5, 1.23e-4)

**Literais:**
- **`CHAR_LITERAL`**: Representa caracteres literais (ex: 'A', 'x', ' ')
- **`STRING_LITERAL`**: Representa strings literais (ex: "Hello", "Pascal")

**Operadores:**
- **`OPERADOR_ARITMETICO`**: Operadores matemáticos (+, -, *, /)
- **`OPERADOR_RELACIONAL`**: Operadores de comparação (=, <, >, <=, >=, <>)
- **`OPERADOR_LOGICO`**: Operadores lógicos (AND, OR, NOT)

**Símbolos Especiais:**
- **`SIMBOLO_ESPECIAL`**: Delimitadores e separadores ((, ), ;, ,, ., :)
- **`ATRIBUICAO`**: Operador de atribuição (:=)

**Tokens de Controle:**
- **`FIM`**: Indica o fim do programa (ponto após "end")
- **`FIM_DE_ARQUIVO`**: Indica o final do código fonte

## Funcionalidades e Características

### Sistema de Classificação Hierárquico
A enumeração implementa um sistema de duas camadas:
1. **Específica**: Constantes para palavras reservadas individuais
2. **Genérica**: Categorias amplas para tipos de tokens

### Flexibilidade de Uso
- **Análise específica**: Permite verificar palavras reservadas específicas
- **Análise categórica**: Permite agrupar tokens por categoria
- **Extensibilidade**: Fácil adição de novos tipos de tokens

### Integração com o Analisador Léxico
- **Mapeamento direto**: Palavras reservadas são mapeadas diretamente para suas constantes
- **Classificação automática**: O analisador escolhe entre classificação específica e genérica
- **Validação de tipos**: Garante que apenas tipos válidos sejam utilizados

## Exemplos de Uso

### Reconhecimento de Palavras Reservadas
```java
// No AnalisadorLexico
String lexema = "begin";
TipoToken tipo = PALAVRAS_RESERVADAS.getOrDefault(lexema.toLowerCase(), TipoToken.IDENTIFICADOR);
// Resultado: tipo = TipoToken.PALAVRA_RESERVADA (genérico)
// Ou poderia ser: tipo = TipoToken.BEGIN (específico)
```

### Classificação de Tokens
```java
// Verificação de categoria
if (token.tipo == TipoToken.NUMERO_INTEIRO || token.tipo == TipoToken.NUMERO_REAL) {
    // Processar número
}

// Verificação específica
if (token.tipo == TipoToken.BEGIN) {
    // Processar início de bloco
}
```

### Análise Sintática
```java
// No analisador sintático
switch (token.tipo) {
    case IF:
        processarComandoIf();
        break;
    case WHILE:
        processarComandoWhile();
        break;
    case IDENTIFICADOR:
        processarIdentificador();
        break;
    // ...
}
```

## Vantagens do Design

### Type Safety (Segurança de Tipos)
- **Prevenção de erros**: Impossível usar tipos inválidos
- **Verificação em tempo de compilação**: Erros detectados antes da execução
- **Autocompletar**: IDEs podem sugerir valores válidos

### Manutenibilidade
- **Centralização**: Todos os tipos de tokens em um local
- **Facilidade de modificação**: Adicionar/remover tipos é simples
- **Documentação implícita**: Os nomes são autodocumentados

### Performance
- **Comparação eficiente**: Comparações de enum são muito rápidas
- **Uso de memória**: Enums são eficientes em memória
- **Switch statements**: Otimizados pelo compilador

### Legibilidade
- **Código expressivo**: `token.tipo == TipoToken.IF` é mais claro que `token.tipo == 15`
- **Intenção clara**: O propósito de cada tipo é evidente
- **Padronização**: Nomenclatura consistente em todo o projeto

## Considerações de Implementação

### Compatibilidade com Pascal
- **Cobertura completa**: Inclui todas as palavras reservadas do Pascal padrão
- **Extensões**: Suporta algumas extensões comuns (como UNIT, IMPLEMENTATION)
- **Flexibilidade**: Permite adaptação para diferentes dialetos

### Estratégia de Mapeamento
- **Palavras específicas**: Para análise sintática detalhada
- **Categorias gerais**: Para análise léxica e classificação
- **Dupla funcionalidade**: Suporta ambos os níveis de granularidade

### Extensibilidade Futura
- **Novos operadores**: Fácil adição de novos tipos de operadores
- **Novas construções**: Suporte para futuras extensões da linguagem
- **Compatibilidade**: Mudanças não quebram código existente

## Relação com Outras Classes

### Com a Classe Token
- **Tipagem**: Fornece o tipo para objetos Token
- **Validação**: Garante que apenas tipos válidos sejam usados

### Com a Classe AnalisadorLexico
- **Classificação**: Usado para classificar tokens durante a análise
- **Decisões**: Influencia o comportamento do analisador

### Com Fases Posteriores
- **Análise Sintática**: Guia as decisões do parser
- **Análise Semântica**: Influencia verificações de tipo e escopo
- **Geração de Código**: Pode influenciar a geração de código específico

Esta enumeração é fundamental para o funcionamento correto de todo o sistema de compilação, fornecendo uma base sólida e extensível para a classificação de tokens léxicos.