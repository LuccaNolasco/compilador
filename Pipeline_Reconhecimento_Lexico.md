# Pipeline de Reconhecimento Léxico - Analisador Léxico

## Visão Geral do Pipeline

O pipeline de reconhecimento léxico é o processo fundamental pelo qual o analisador léxico converte uma sequência de caracteres (código fonte) em uma sequência de tokens. Este documento explica detalhadamente como funciona esse pipeline no analisador léxico implementado, incluindo todas as funções envolvidas e sua ordem de chamada.

## Arquitetura do Pipeline

### Componentes Principais

1. **AnalisadorLexico** - Classe principal que coordena todo o processo
2. **Token** - Estrutura de dados que representa cada unidade léxica
3. **TipoToken** - Enumeração que classifica os tipos de tokens
4. **Estruturas de Dados Auxiliares**:
   - `PALAVRAS_RESERVADAS` - Mapa de palavras reservadas
   - `SIMBOLOS_SIMPLES` - Mapa de símbolos simples
   - `caracteresNaoIdentificados` - Lista de caracteres não reconhecidos

### Fluxo Básico do Pipeline

```
Código Fonte → Análise Léxica → Sequência de Tokens
```

## Pipeline Detalhado de Reconhecimento

### 1. Inicialização do Analisador

**Função**: `AnalisadorLexico(String codigoFonte)`
- **Entrada**: String contendo o código fonte
- **Ações**:
  - Armazena o código fonte
  - Inicializa `posicaoAtual = 0`
  - Inicializa `tokenAnterior = null`
  - Prepara estruturas de controle

### 2. Loop Principal de Reconhecimento

**Função**: `proximoToken()`
- **Responsabilidade**: Função central do pipeline
- **Processo**:
  1. Ignora espaços em branco
  2. Verifica fim do código
  3. Identifica tipo do próximo token
  4. Chama função específica de reconhecimento
  5. Atualiza estado interno
  6. Retorna token reconhecido

### 3. Funções de Suporte ao Pipeline

#### 3.1 Preparação
**Função**: `ignorarEspacosEmBranco()`
- **Propósito**: Remove espaços, tabs, quebras de linha
- **Processo**: Avança `posicaoAtual` enquanto encontrar whitespace

#### 3.2 Reconhecimento de Identificadores
**Função**: `reconhecerIdentificador()`
- **Entrada**: Caractere inicial é letra
- **Processo**:
  1. Marca posição inicial
  2. Avança enquanto encontrar letras ou dígitos
  3. Extrai lexema
  4. Consulta `PALAVRAS_RESERVADAS`
  5. Retorna Token com tipo apropriado

#### 3.3 Reconhecimento de Números
**Função**: `reconhecerNumero()`
- **Entrada**: Caractere inicial é dígito ou '-' seguido de dígito
- **Processo**:
  1. Trata sinal negativo (se presente)
  2. Consome parte inteira
  3. Verifica parte decimal
  4. Verifica notação científica
  5. Determina tipo (INTEIRO ou REAL)
  6. Retorna Token apropriado

#### 3.4 Reconhecimento de Caracteres Literais
**Função**: `reconhecerChar()`
- **Entrada**: Aspas simples (')
- **Processo**:
  1. Consome aspa inicial
  2. Captura caractere interno
  3. Verifica aspa final
  4. Retorna Token CHAR_LITERAL

#### 3.5 Reconhecimento de Strings Literais
**Função**: `reconhecerString()`
- **Entrada**: Aspas duplas (")
- **Processo**:
  1. Consome aspa inicial
  2. Captura conteúdo até aspa final
  3. Verifica fechamento
  4. Retorna Token STRING_LITERAL

#### 3.6 Funções Auxiliares
- **`peek()`**: Examina próximo caractere sem consumir
- **`ignorarComentario()`**: Pula comentários /* */
- **`tokenAnteriorEhNumeroOuIdentificador()`**: Verifica contexto para operador '-'

## Ordem de Chamadas de Função no Pipeline

### Sequência Típica para Reconhecimento de Token

1. **Chamada Externa** → `proximoToken()`
2. **`proximoToken()`** → `ignorarEspacosEmBranco()`
3. **`proximoToken()`** → Análise do caractere atual
4. **`proximoToken()`** → Função específica de reconhecimento:
   - `reconhecerIdentificador()` (se letra)
   - `reconhecerNumero()` (se dígito)
   - `reconhecerChar()` (se ')
   - `reconhecerString()` (se ")
   - Reconhecimento direto (símbolos simples)
5. **Função específica** → Construção do Token
6. **`proximoToken()`** → Atualização de `tokenAnterior`
7. **`proximoToken()`** → Retorno do Token

### Chamadas Auxiliares Durante o Processo

- **`peek()`**: Chamada quando necessário examinar próximo caractere
- **`ignorarComentario()`**: Chamada quando encontrado '/*'
- **`proximoToken()`** (recursiva): Chamada após ignorar comentários

## Exemplo Prático: Reconhecimento de "x := 4 - b"

### Análise Passo a Passo

#### Estado Inicial
- **Código fonte**: "x := 4 - b"
- **posicaoAtual**: 0
- **tokenAnterior**: null

#### Token 1: Reconhecimento de "x"

**Chamadas de Função:**
1. `proximoToken()` - Função principal chamada
2. `ignorarEspacosEmBranco()` - Não há espaços no início
3. `proximoToken()` - Identifica 'x' como letra
4. `reconhecerIdentificador()` - Chamada para reconhecer identificador

**Processo em `reconhecerIdentificador()`:**
- **posicaoInicial**: 0
- **Loop**: Avança enquanto `Character.isLetterOrDigit()` retorna true
  - Posição 0: 'x' → é letra, avança para posição 1
  - Posição 1: ' ' → não é letra nem dígito, para o loop
- **Lexema extraído**: "x" (substring de 0 a 1)
- **Consulta PALAVRAS_RESERVADAS**: "x" não encontrado
- **Tipo determinado**: TipoToken.IDENTIFICADOR
- **Token criado**: Token(IDENTIFICADOR, "x")

**Estado após Token 1:**
- **posicaoAtual**: 1
- **tokenAnterior**: Token(IDENTIFICADOR, "x")

#### Token 2: Reconhecimento de ":="

**Chamadas de Função:**
1. `proximoToken()` - Função principal chamada
2. `ignorarEspacosEmBranco()` - Avança da posição 1 para 3 (pula espaço)
3. `proximoToken()` - Identifica ':' na posição 3
4. `peek()` - Examina próximo caractere (posição 4)

**Processo no switch case ':':**
- **Caractere atual**: ':' (posição 3)
- **`peek()`** retorna: '=' (posição 4)
- **Condição**: `peek() == '='` é verdadeira
- **Ação**: `posicaoAtual += 2` (avança para posição 5)
- **Token criado**: Token(ATRIBUICAO, ":=")

**Estado após Token 2:**
- **posicaoAtual**: 5
- **tokenAnterior**: Token(ATRIBUICAO, ":=")

#### Token 3: Reconhecimento de "4"

**Chamadas de Função:**
1. `proximoToken()` - Função principal chamada
2. `ignorarEspacosEmBranco()` - Avança da posição 5 para 6 (pula espaço)
3. `proximoToken()` - Identifica '4' como dígito
4. `reconhecerNumero()` - Chamada para reconhecer número

**Processo em `reconhecerNumero()`:**
- **posicaoInicial**: 6
- **Verificação de sinal**: Não há '-' no início
- **Loop parte inteira**: 
  - Posição 6: '4' → é dígito, avança para posição 7
  - Posição 7: ' ' → não é dígito, para o loop
- **Verificação parte decimal**: Próximo caractere não é '.'
- **Verificação notação científica**: Próximo caractere não é 'e' ou 'E'
- **Lexema extraído**: "4"
- **Tipo determinado**: TipoToken.NUMERO_INTEIRO
- **Token criado**: Token(NUMERO_INTEIRO, "4")

**Estado após Token 3:**
- **posicaoAtual**: 7
- **tokenAnterior**: Token(NUMERO_INTEIRO, "4")

#### Token 4: Reconhecimento de "-"

**Chamadas de Função:**
1. `proximoToken()` - Função principal chamada
2. `ignorarEspacosEmBranco()` - Avança da posição 7 para 8 (pula espaço)
3. `proximoToken()` - Identifica '-' na posição 8
4. **Análise contextual do operador '-'**

**Processo no switch case '-':**
- **Caractere atual**: '-' (posição 8)
- **Verificação de contexto**: `tokenAnterior != null` E
  - `tokenAnterior.tipo == TipoToken.NUMERO_INTEIRO` (verdadeiro)
- **Condição**: Token anterior é número → operador aritmético
- **Ação**: `posicaoAtual++` (avança para posição 9)
- **Token criado**: Token(OPERADOR_ARITMETICO, "-")

**Estado após Token 4:**
- **posicaoAtual**: 9
- **tokenAnterior**: Token(OPERADOR_ARITMETICO, "-")

#### Token 5: Reconhecimento de "b"

**Chamadas de Função:**
1. `proximoToken()` - Função principal chamada
2. `ignorarEspacosEmBranco()` - Avança da posição 9 para 10 (pula espaço)
3. `proximoToken()` - Identifica 'b' como letra
4. `reconhecerIdentificador()` - Chamada para reconhecer identificador

**Processo em `reconhecerIdentificador()`:**
- **posicaoInicial**: 10
- **Loop**: Avança enquanto `Character.isLetterOrDigit()` retorna true
  - Posição 10: 'b' → é letra, avança para posição 11
  - Posição 11: fim da string → para o loop
- **Lexema extraído**: "b"
- **Consulta PALAVRAS_RESERVADAS**: "b" não encontrado
- **Tipo determinado**: TipoToken.IDENTIFICADOR
- **Token criado**: Token(IDENTIFICADOR, "b")

**Estado após Token 5:**
- **posicaoAtual**: 11 (fim da string)
- **tokenAnterior**: Token(IDENTIFICADOR, "b")

#### Finalização

**Próxima chamada a `proximoToken()`:**
1. `ignorarEspacosEmBranco()` - Sem efeito (já no fim)
2. **Verificação**: `posicaoAtual >= codigoFonte.length()` é verdadeira
3. **Retorno**: `null` (indica fim da análise)

### Resumo da Sequência de Tokens Gerada

```
1. Token(IDENTIFICADOR, "x")
2. Token(ATRIBUICAO, ":=")
3. Token(NUMERO_INTEIRO, "4")
4. Token(OPERADOR_ARITMETICO, "-")
5. Token(IDENTIFICADOR, "b")
```

### Diagrama de Chamadas de Função para "x := 4 - b"

```
Análise de "x := 4 - b"
│
├── Token 1 ("x"):
│   ├── proximoToken()
│   ├── ignorarEspacosEmBranco()
│   ├── reconhecerIdentificador()
│   └── new Token(IDENTIFICADOR, "x")
│
├── Token 2 (":="):
│   ├── proximoToken()
│   ├── ignorarEspacosEmBranco()
│   ├── peek() → '='
│   └── new Token(ATRIBUICAO, ":=")
│
├── Token 3 ("4"):
│   ├── proximoToken()
│   ├── ignorarEspacosEmBranco()
│   ├── reconhecerNumero()
│   └── new Token(NUMERO_INTEIRO, "4")
│
├── Token 4 ("-"):
│   ├── proximoToken()
│   ├── ignorarEspacosEmBranco()
│   ├── [análise contextual do token anterior]
│   └── new Token(OPERADOR_ARITMETICO, "-")
│
└── Token 5 ("b"):
    ├── proximoToken()
    ├── ignorarEspacosEmBranco()
    ├── reconhecerIdentificador()
    └── new Token(IDENTIFICADOR, "b")
```

## Características Especiais do Pipeline

### 1. Análise Contextual
O pipeline implementa análise contextual, especialmente para o operador '-':
- **Contexto de operador**: Quando precedido por número ou identificador
- **Contexto de sinal**: Quando não precedido ou seguido de dígito

### 2. Lookahead
Utiliza a função `peek()` para examinar o próximo caractere sem consumi-lo:
- **Operadores compostos**: ":=", "<=", ">=", "<>"
- **Comentários**: Detecção de "/*"
- **Números decimais**: Verificação de dígito após '.'

### 3. Tratamento de Erros
- **Caracteres não identificados**: Armazenados em lista separada
- **Tokens malformados**: Tratamento gracioso com mensagens de erro
- **Continuidade**: Pipeline continua após encontrar erros

### 4. Estado Interno
Mantém estado entre chamadas:
- **posicaoAtual**: Posição no código fonte
- **tokenAnterior**: Contexto para análise
- **caracteresNaoIdentificados**: Histórico de erros

## Vantagens da Arquitetura do Pipeline

### 1. Modularidade
- Cada tipo de token tem sua função específica de reconhecimento
- Fácil manutenção e extensão
- Responsabilidades bem definidas

### 2. Eficiência
- Análise em uma única passada
- Lookahead mínimo (apenas 1 caractere)
- Processamento linear O(n)

### 3. Robustez
- Tratamento de erros sem interrupção
- Recuperação automática após erros
- Validação em múltiplos níveis

### 4. Flexibilidade
- Suporte a diferentes tipos de tokens
- Análise contextual quando necessária
- Configurações ajustáveis (como exibição de FIM_DE_ARQUIVO)

## Integração com o Sistema Maior

### Interface com a GUI
O pipeline é utilizado pela `TelaAnalisadorLexico` através do seguinte fluxo:

1. **Inicialização**: `new AnalisadorLexico(codigoFonte)`
2. **Configuração**: `setExibirFimDeArquivo(false)`
3. **Loop de análise**:
   ```java
   while ((token = analisador.proximoToken()) != null) {
       // Processa token
   }
   ```
4. **Coleta de erros**: `getCaracteresNaoIdentificados()`
5. **Limpeza**: `limparCaracteresNaoIdentificados()`

### Preparação para Fases Posteriores
Os tokens gerados pelo pipeline servem como entrada para:
- **Analisador Sintático**: Sequência de tokens para parsing
- **Analisador Semântico**: Tokens com informações de tipo
- **Gerador de Código**: Tokens para tradução final

Este pipeline representa a base fundamental do compilador, convertendo texto bruto em estruturas de dados organizadas que podem ser processadas pelas fases subsequentes da compilação.