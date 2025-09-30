# Relatório da Classe Token

## Descrição Geral
A classe `Token` é uma estrutura de dados fundamental no analisador léxico que representa uma unidade léxica identificada no código fonte. Cada token encapsula duas informações essenciais: o tipo do token (classificação) e o lexema (a sequência de caracteres que forma o token). Esta classe serve como a unidade básica de comunicação entre o analisador léxico e as fases subsequentes do compilador.

## Estrutura da Classe

### Atributos
A classe possui dois atributos públicos e finais:

**`tipo`** (TipoToken)
- **Função**: Armazena a classificação do token
- **Tipo**: Enumeração TipoToken
- **Modificadores**: public final
- **Descrição**: Define a categoria léxica do token (ex: IDENTIFICADOR, NUMERO_INTEIRO, PALAVRA_RESERVADA, etc.)

**`lexema`** (String)
- **Função**: Armazena a sequência de caracteres que forma o token
- **Tipo**: String
- **Modificadores**: public final
- **Descrição**: Contém o texto literal extraído do código fonte que representa este token

### Construtor

**`Token(TipoToken tipo, String lexema)`**
- **Função**: Cria uma nova instância de Token com o tipo e lexema especificados
- **Parâmetros**:
  - `tipo`: A classificação do token (do enum TipoToken)
  - `lexema`: A sequência de caracteres que representa o token
- **Características**: 
  - Inicializa os atributos finais da classe
  - Garante que cada token seja imutável após a criação

## Métodos da Classe

### Método toString()

**`toString()`** - String
- **Função**: Fornece uma representação textual padronizada do token
- **Retorno**: String no formato "<lexema, tipo>"
- **Exemplo de saída**: 
  - `<begin, PALAVRA_RESERVADA>`
  - `<variavel, IDENTIFICADOR>`
  - `<123, NUMERO_INTEIRO>`
  - `<+, OPERADOR_ARITMETICO>`
- **Uso**: 
  - Depuração e logging
  - Exibição de resultados da análise léxica
  - Geração de relatórios de tokens

## Características de Design

### Imutabilidade
- **Atributos finais**: Ambos os atributos são declarados como `final`, garantindo que não possam ser modificados após a criação
- **Benefícios**: 
  - Segurança thread-safe
  - Prevenção de modificações acidentais
  - Clareza sobre o ciclo de vida do objeto

### Simplicidade
- **Estrutura minimalista**: A classe contém apenas os elementos essenciais para representar um token
- **Acesso direto**: Os atributos são públicos, permitindo acesso direto sem necessidade de getters/setters
- **Foco único**: A classe tem uma única responsabilidade: representar um token

### Flexibilidade
- **Tipo genérico**: Pode representar qualquer tipo de token definido no enum TipoToken
- **Lexema variável**: Pode armazenar qualquer sequência de caracteres como lexema

## Exemplos de Uso

### Criação de Tokens
```java
// Token para palavra reservada
Token tokenBegin = new Token(TipoToken.PALAVRA_RESERVADA, "begin");

// Token para identificador
Token tokenVar = new Token(TipoToken.IDENTIFICADOR, "minhaVariavel");

// Token para número
Token tokenNum = new Token(TipoToken.NUMERO_INTEIRO, "42");

// Token para operador
Token tokenMais = new Token(TipoToken.OPERADOR_ARITMETICO, "+");
```

### Acesso aos Dados
```java
Token token = new Token(TipoToken.IDENTIFICADOR, "contador");

// Acesso direto aos atributos
TipoToken tipo = token.tipo;        // IDENTIFICADOR
String lexema = token.lexema;       // "contador"

// Representação textual
String representacao = token.toString(); // "<contador, IDENTIFICADOR>"
```

## Integração com o Sistema

### Relacionamento com AnalisadorLexico
- **Produção**: A classe AnalisadorLexico cria instâncias de Token durante o processo de análise
- **Retorno**: O método `proximoToken()` retorna objetos Token
- **Armazenamento**: Tokens são temporariamente armazenados para análise contextual

### Relacionamento com TipoToken
- **Dependência**: A classe Token depende do enum TipoToken para classificação
- **Tipagem forte**: O uso do enum garante que apenas tipos válidos sejam utilizados

### Uso em Fases Posteriores
- **Análise Sintática**: Os tokens são consumidos pelo analisador sintático
- **Análise Semântica**: Informações dos tokens são utilizadas para verificações semânticas
- **Geração de Código**: Tokens podem influenciar a geração do código final

## Vantagens do Design

### Performance
- **Estrutura leve**: Classe simples com overhead mínimo
- **Criação rápida**: Construtor simples e direto
- **Acesso eficiente**: Atributos públicos eliminam chamadas de método

### Manutenibilidade
- **Código claro**: Estrutura simples e fácil de entender
- **Extensibilidade**: Fácil de modificar se necessário
- **Testabilidade**: Simples de testar devido à simplicidade

### Robustez
- **Imutabilidade**: Previne modificações acidentais
- **Tipagem forte**: Uso de enum previne erros de tipo
- **Representação consistente**: Método toString() padronizado

## Considerações de Implementação

### Thread Safety
- A classe é thread-safe devido à imutabilidade dos atributos
- Múltiplas threads podem acessar o mesmo token simultaneamente sem problemas

### Uso de Memória
- Estrutura eficiente em termos de memória
- Apenas dois campos por instância
- Strings são reutilizadas quando possível pelo pool de strings do Java

### Padrões de Design
- **Value Object**: A classe representa um valor imutável
- **Data Transfer Object**: Transporta dados entre componentes do sistema
- **Flyweight implícito**: Strings idênticas são automaticamente compartilhadas

Esta classe, embora simples, é fundamental para o funcionamento correto do analisador léxico e serve como base para toda a comunicação entre as diferentes fases do compilador.