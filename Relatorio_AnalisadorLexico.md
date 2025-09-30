# Relatório da Classe AnalisadorLexico

## Descrição Geral
A classe `AnalisadorLexico` é o núcleo do analisador léxico do compilador. Ela é responsável por processar o código fonte de entrada e identificar os tokens (unidades léxicas) que compõem a linguagem Pascal. Esta classe implementa um autômato finito que reconhece diferentes tipos de tokens como palavras reservadas, identificadores, números, operadores e símbolos especiais.

## Atributos da Classe

### Atributos Principais
- **`codigoFonte`** (String): Armazena o código fonte completo que será analisado
- **`posicaoAtual`** (int): Mantém a posição atual do cursor durante a análise do código
- **`tokenAnterior`** (Token): Referência para o token anteriormente processado, usado para análise contextual

### Atributos de Controle
- **`exibirFimDeArquivo`** (boolean): Controla se o token FIM_DE_ARQUIVO deve ser exibido (padrão: false)
- **`caracteresNaoIdentificados`** (List<Character>): Lista que armazena caracteres que não foram reconhecidos durante a análise

### Estruturas de Dados Estáticas
- **`PALAVRAS_RESERVADAS`** (Map<String, TipoToken>): Mapa que contém todas as palavras reservadas da linguagem Pascal
- **`SIMBOLOS_SIMPLES`** (Map<Character, Token>): Mapa que associa caracteres simples aos seus respectivos tokens

## Métodos da Classe

### Construtor
**`AnalisadorLexico(String codigoFonte)`**
- **Função**: Inicializa o analisador léxico com o código fonte fornecido
- **Parâmetros**: 
  - `codigoFonte`: String contendo o código fonte a ser analisado
- **Ação**: Define a posição inicial como 0 e inicializa o token anterior como null

### Métodos Principais de Análise

**`proximoToken()`** - Token
- **Função**: Método principal que retorna o próximo token encontrado no código fonte
- **Retorno**: Objeto Token representando a próxima unidade léxica, ou null se chegou ao fim
- **Funcionamento**:
  - Ignora espaços em branco
  - Verifica se chegou ao fim do código
  - Identifica o tipo de token baseado no primeiro caractere
  - Chama métodos específicos para reconhecer diferentes tipos de tokens
  - Atualiza o token anterior para análise contextual

**`reconhecerIdentificador()`** - Token (privado)
- **Função**: Reconhece identificadores e palavras reservadas
- **Funcionamento**:
  - Consome caracteres alfanuméricos sequenciais
  - Verifica se o lexema é uma palavra reservada
  - Retorna token do tipo IDENTIFICADOR ou PALAVRA_RESERVADA

**`reconhecerNumero()`** - Token (privado)
- **Função**: Reconhece números inteiros e reais, incluindo números negativos
- **Características reconhecidas**:
  - Números inteiros (ex: 123, -456)
  - Números reais com ponto decimal (ex: 12.34, -0.5)
  - Notação científica (ex: 1.23e-04, 2E+10)
- **Retorno**: Token do tipo NUMERO_INTEIRO ou NUMERO_REAL

**`reconhecerChar()`** - Token (privado)
- **Função**: Reconhece literais de caractere delimitados por aspas simples
- **Formato**: 'c' onde c é qualquer caractere
- **Retorno**: Token do tipo CHAR_LITERAL contendo apenas o caractere (sem as aspas)

**`reconhecerString()`** - Token (privado)
- **Função**: Reconhece literais de string delimitados por aspas duplas
- **Formato**: "texto" onde texto pode conter qualquer sequência de caracteres
- **Retorno**: Token do tipo STRING_LITERAL contendo apenas o conteúdo (sem as aspas)

### Métodos Auxiliares

**`ignorarEspacosEmBranco()`** (privado)
- **Função**: Avança a posição atual pulando todos os caracteres de espaço em branco
- **Caracteres ignorados**: espaços, tabs, quebras de linha, etc.

**`peek()`** - char (privado)
- **Função**: Retorna o próximo caractere sem avançar a posição atual
- **Retorno**: Próximo caractere ou '\0' se estiver no fim do código
- **Uso**: Verificar tokens de múltiplos caracteres (ex: :=, <=, >=, <>)

**`ignorarComentario()`** (privado)
- **Função**: Ignora comentários no formato /* ... */
- **Funcionamento**:
  - Avança além do /*
  - Procura pelo fechamento */
  - Se não encontrar o fechamento, ignora até o fim do código

### Métodos de Controle e Configuração

**`setExibirFimDeArquivo(boolean exibir)`**
- **Função**: Ativa ou desativa a exibição do token FIM_DE_ARQUIVO
- **Parâmetro**: `exibir` - true para exibir, false para não exibir

**`isExibindoFimDeArquivo()`** - boolean
- **Função**: Verifica se está configurado para exibir o token FIM_DE_ARQUIVO
- **Retorno**: Estado atual da configuração

**`getCaracteresNaoIdentificados()`** - List<Character>
- **Função**: Retorna uma cópia da lista de caracteres não identificados
- **Uso**: Para relatórios de erro e depuração

**`limparCaracteresNaoIdentificados()`**
- **Função**: Limpa a lista de caracteres não identificados
- **Uso**: Para reiniciar a análise ou limpar erros anteriores

### Métodos Auxiliares Privados

**`tokenAnteriorEhNumeroOuIdentificador()`** - boolean (privado)
- **Função**: Verifica se o token anterior é um número ou identificador
- **Uso**: Para análise contextual, especialmente no tratamento do operador menos (-)

## Características Especiais da Implementação

### Tratamento Contextual do Operador Menos (-)
A classe implementa uma lógica especial para o operador menos:
- Se precedido por número ou identificador: é tratado como operador aritmético
- Se não precedido por número/identificador e seguido por dígito: é tratado como parte de um número negativo
- Caso contrário: é tratado como operador aritmético

### Reconhecimento de Tokens Compostos
A classe reconhece corretamente tokens de múltiplos caracteres:
- `:=` (atribuição)
- `<=` (menor ou igual)
- `>=` (maior ou igual)
- `<>` (diferente)

### Tratamento Especial do Ponto após "end"
Quando um ponto (.) aparece após a palavra reservada "end", ele é classificado como token FIM em vez de SIMBOLO_ESPECIAL, indicando o fim do programa Pascal.

### Gerenciamento de Erros
- Caracteres não reconhecidos são coletados em uma lista para relatório
- Mensagens de erro são exibidas no console
- A análise continua mesmo após encontrar caracteres inválidos

## Palavras Reservadas Suportadas
A classe reconhece 47 palavras reservadas da linguagem Pascal, incluindo:
- Estruturas de controle: if, then, else, while, for, do, case, etc.
- Tipos de dados: integer, real, char, string, array, record, etc.
- Declarações: var, const, type, function, procedure, etc.
- Outras: begin, end, program, uses, etc.

## Símbolos e Operadores Reconhecidos
- **Símbolos especiais**: ( ) ; , . :
- **Operadores aritméticos**: + - * /
- **Operadores relacionais**: = < > <= >= <>
- **Atribuição**: :=
- **Literais**: caracteres ('c') e strings ("texto")

Esta classe forma a base do processo de compilação, convertendo o código fonte em uma sequência de tokens que será posteriormente processada pelo analisador sintático.