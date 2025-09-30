# Relatório da Classe TelaAnalisadorLexico

## Descrição Geral
A classe `TelaAnalisadorLexico` é responsável por implementar a interface gráfica completa do analisador léxico do compilador. Esta classe gerencia toda a interação do usuário com o analisador léxico, incluindo seleção de arquivos, entrada de código, execução da análise e exibição dos resultados. A interface permite tanto a entrada manual de código quanto o carregamento de arquivos, com opções para salvar os resultados da análise.

## Estrutura da Classe

### Atributos de Interface
**`arquivoEntradaSelecionado`** (File)
- **Função**: Armazena o arquivo de entrada selecionado pelo usuário
- **Uso**: Referência para carregamento do código fonte
- **Estado inicial**: null

**`arquivoSaidaSelecionado`** (File)
- **Função**: Armazena o arquivo de saída opcional para salvar resultados
- **Uso**: Destino para exportação dos tokens encontrados
- **Estado inicial**: null

**`textAreaCodigo`** (TextArea)
- **Função**: Área de texto para exibição e edição do código fonte
- **Características**: Editável, com scroll automático
- **Dimensões**: 600x300 pixels

**`textAreaTokens`** (TextArea)
- **Função**: Área de texto para exibição dos tokens encontrados
- **Características**: Somente leitura, com scroll automático
- **Dimensões**: 600x200 pixels

### Atributos de Controle
**`primaryStage`** (Stage)
- **Função**: Referência para a janela principal da aplicação
- **Uso**: Necessário para diálogos de arquivo e alertas
- **Escopo**: Privado

**`mainApp`** (CompiladorGUI)
- **Função**: Referência para a classe principal da aplicação
- **Uso**: Permite navegação de volta à tela inicial
- **Escopo**: Privado

### Construtor
**`TelaAnalisadorLexico(Stage primaryStage, CompiladorGUI mainApp)`**
- **Função**: Inicializa a tela do analisador léxico
- **Parâmetros**:
  - `primaryStage`: Janela principal da aplicação
  - `mainApp`: Instância da classe controladora principal
- **Ações**: 
  - Armazena referências
  - Configura localização brasileira
  - Inicializa componentes de interface

## Métodos da Classe

### Método Principal de Criação

**`criarCena()`** - Scene
- **Função**: Cria e configura toda a interface gráfica do analisador léxico
- **Retorno**: Objeto Scene configurado e pronto para exibição
- **Estrutura criada**:
  - Layout principal (VBox)
  - Título e botão de volta
  - Grade de seleção de arquivos
  - Área de código fonte
  - Botão de análise
  - Área de resultados (tokens)

### Métodos de Configuração

**`configurarLocalizacaoBrasileira()`** - void (privado)
- **Função**: Configura a localização para português brasileiro
- **Ação**: Define `Locale.setDefault(new Locale("pt", "BR"))`
- **Propósito**: Garante formatação correta de números e datas

### Métodos de Seleção de Arquivos

**`selecionarArquivoEntrada()`** - void (privado)
- **Função**: Abre diálogo para seleção do arquivo de entrada
- **Processo**:
  1. Cria FileChooser configurado
  2. Define filtros para arquivos de texto
  3. Abre diálogo de seleção
  4. Armazena arquivo selecionado
  5. Carrega conteúdo automaticamente
- **Filtros suportados**: 
  - Arquivos de texto (*.txt)
  - Todos os arquivos (*.*)

**`selecionarArquivoSaida()`** - void (privado)
- **Função**: Abre diálogo para seleção do arquivo de saída (opcional)
- **Processo**:
  1. Cria FileChooser configurado
  2. Define filtros para arquivos de texto
  3. Abre diálogo de salvamento
  4. Armazena destino selecionado
- **Características**: Permite criar novos arquivos ou sobrescrever existentes

### Métodos de Manipulação de Arquivos

**`carregarArquivo(File arquivo)`** - void (privado)
- **Função**: Carrega o conteúdo de um arquivo para a área de texto
- **Parâmetros**: `arquivo` - arquivo a ser carregado
- **Processo**:
  1. Verifica se o arquivo existe
  2. Lê todo o conteúdo usando Files.readString()
  3. Exibe conteúdo na textAreaCodigo
  4. Trata exceções de I/O
- **Tratamento de erros**: Exibe alerta em caso de falha na leitura
- **Codificação**: UTF-8 (padrão do Java)

**`salvarResultados(String conteudo)`** - void (privado)
- **Função**: Salva os resultados da análise em arquivo
- **Parâmetros**: `conteudo` - texto dos tokens a ser salvo
- **Condições**: Só executa se arquivo de saída foi selecionado
- **Processo**:
  1. Verifica se há arquivo de saída definido
  2. Escreve conteúdo usando Files.writeString()
  3. Exibe confirmação de sucesso
  4. Trata exceções de I/O
- **Codificação**: UTF-8 (padrão do Java)

### Método Principal de Análise

**`analisarCodigo()`** - void (privado)
- **Função**: Executa a análise léxica do código fonte
- **Processo detalhado**:
  1. **Validação**: Verifica se há código para analisar
  2. **Preparação**: Limpa área de resultados e obtém código
  3. **Inicialização**: Cria instância do AnalisadorLexico
  4. **Análise**: Loop de reconhecimento de tokens
  5. **Formatação**: Organiza resultados para exibição
  6. **Exibição**: Mostra tokens e caracteres não identificados
  7. **Salvamento**: Salva resultados se arquivo de saída definido

### Detalhamento do Processo de Análise

**Etapa 1: Validação de Entrada**
```
- Verifica se textAreaCodigo não está vazia
- Exibe aviso se não há código para analisar
```

**Etapa 2: Inicialização do Analisador**
```
- Cria AnalisadorLexico com o código fonte
- Prepara StringBuilder para resultados
- Inicializa contador de tokens
```

**Etapa 3: Loop de Reconhecimento**
```
- Chama proximoToken() até encontrar FIM_DE_ARQUIVO
- Para cada token válido:
  - Incrementa contador
  - Adiciona ao StringBuilder formatado
- Ignora tokens de FIM_DE_ARQUIVO se configurado
```

**Etapa 4: Tratamento de Caracteres Não Identificados**
```
- Obtém caracteres não reconhecidos do analisador
- Se houver caracteres não identificados:
  - Adiciona seção separada nos resultados
  - Lista todos os caracteres problemáticos
```

**Etapa 5: Formatação dos Resultados**
```
- Cabeçalho com total de tokens
- Lista numerada de tokens (Tipo: Lexema)
- Seção de caracteres não identificados (se houver)
- Rodapé com status da análise
```

**Etapa 6: Exibição e Salvamento**
```
- Exibe resultados na textAreaTokens
- Salva em arquivo se definido
- Limpa caracteres não identificados do analisador
```

### Método de Interface

**`mostrarAviso(String titulo, String mensagem)`** - void (privado)
- **Função**: Exibe caixas de diálogo informativas
- **Parâmetros**:
  - `titulo`: Título da janela de diálogo
  - `mensagem`: Conteúdo da mensagem
- **Tipo**: Alert.AlertType.INFORMATION
- **Uso**: Avisos, confirmações e mensagens de erro

## Estrutura da Interface Gráfica

### Layout Principal
- **Container**: VBox com espaçamento de 20px
- **Padding**: 20px em todas as direções
- **Cor de fundo**: Branco (#ffffff)
- **Dimensões da cena**: 800x700 pixels

### Seção de Cabeçalho
**Título**
- **Texto**: "Analisador Léxico"
- **Fonte**: Arial, negrito, 24px
- **Cor**: Azul escuro (#2c3e50)
- **Alinhamento**: Centralizado

**Botão Voltar**
- **Texto**: "← Voltar"
- **Posição**: Canto superior esquerdo
- **Ação**: Retorna à tela inicial
- **Estilo**: Botão padrão do sistema

### Seção de Seleção de Arquivos
**Layout**: GridPane 2x2 com espaçamento de 10px

**Linha 1: Arquivo de Entrada**
- **Label**: "Arquivo de Entrada:"
- **Botão**: "Selecionar Arquivo de Entrada"
- **Ação**: Abre FileChooser para seleção
- **Resultado**: Carrega arquivo automaticamente

**Linha 2: Arquivo de Saída (Opcional)**
- **Label**: "Arquivo de Saída (Opcional):"
- **Botão**: "Selecionar Arquivo de Saída"
- **Ação**: Define destino para salvamento
- **Resultado**: Configura salvamento automático

### Seção de Código Fonte
**Label**: "Código Fonte:"
- **Fonte**: Arial, negrito, 14px

**TextArea de Código**
- **Dimensões**: 600x300 pixels
- **Características**: 
  - Editável
  - Scroll automático
  - Fonte monoespaçada
  - Quebra de linha automática
- **Funcionalidade**: Permite entrada manual ou carregamento de arquivo

### Seção de Análise
**Botão Analisar**
- **Texto**: "ANALISAR"
- **Dimensões**: 200x40 pixels
- **Posição**: Centralizado
- **Estilo**: Botão destacado
- **Ação**: Executa análise léxica

### Seção de Resultados
**Label**: "Tokens Encontrados:"
- **Fonte**: Arial, negrito, 14px

**TextArea de Tokens**
- **Dimensões**: 600x200 pixels
- **Características**:
  - Somente leitura
  - Scroll automático
  - Fonte monoespaçada
  - Seleção de texto habilitada
- **Conteúdo**: Resultados formatados da análise

## Funcionalidades Implementadas

### Entrada de Dados
1. **Carregamento de arquivo**: Seleção e carregamento automático
2. **Entrada manual**: Digitação direta na área de texto
3. **Validação**: Verificação de conteúdo antes da análise

### Processamento
1. **Análise léxica**: Integração completa com AnalisadorLexico
2. **Contagem de tokens**: Estatísticas da análise
3. **Detecção de erros**: Identificação de caracteres não reconhecidos

### Saída de Dados
1. **Exibição formatada**: Resultados organizados e numerados
2. **Salvamento opcional**: Exportação para arquivo de texto
3. **Feedback visual**: Confirmações e avisos

### Navegação
1. **Retorno à tela inicial**: Botão de volta funcional
2. **Integração com sistema**: Comunicação com CompiladorGUI

## Formato dos Resultados

### Estrutura da Saída
```
=== ANÁLISE LÉXICA ===
Total de tokens encontrados: [número]

1. [TipoToken]: [lexema]
2. [TipoToken]: [lexema]
...

[Se houver caracteres não identificados:]
=== CARACTERES NÃO IDENTIFICADOS ===
[lista de caracteres problemáticos]

=== FIM DA ANÁLISE ===
```

### Exemplo de Saída
```
=== ANÁLISE LÉXICA ===
Total de tokens encontrados: 8

1. PROGRAM: program
2. IDENTIFICADOR: exemplo
3. PONTO_E_VIRGULA: ;
4. BEGIN: begin
5. IDENTIFICADOR: x
6. DOIS_PONTOS_IGUAL: :=
7. NUMERO_INTEIRO: 10
8. END: end

=== FIM DA ANÁLISE ===
```

## Tratamento de Erros

### Erros de Arquivo
- **Arquivo não encontrado**: Alerta informativo
- **Erro de leitura**: Mensagem de erro específica
- **Erro de escrita**: Notificação de falha no salvamento

### Erros de Análise
- **Código vazio**: Aviso para inserir código
- **Caracteres não identificados**: Listagem separada dos problemas
- **Falhas do analisador**: Tratamento gracioso de exceções

### Feedback ao Usuário
- **Alertas informativos**: Mensagens claras sobre problemas
- **Confirmações**: Notificação de operações bem-sucedidas
- **Instruções**: Orientações sobre como usar a interface

## Configurações de FileChooser

### Arquivo de Entrada
- **Título**: "Selecionar Arquivo de Entrada"
- **Diretório inicial**: Diretório do usuário
- **Filtros**:
  - "Arquivos de Texto (*.txt)" → "*.txt"
  - "Todos os Arquivos (*.*)" → "*.*"

### Arquivo de Saída
- **Título**: "Salvar Resultados Como"
- **Diretório inicial**: Diretório do usuário
- **Filtros**:
  - "Arquivos de Texto (*.txt)" → "*.txt"
  - "Todos os Arquivos (*.*)" → "*.*"
- **Modo**: Salvamento (permite criar novos arquivos)

## Integração com AnalisadorLexico

### Fluxo de Integração
1. **Criação**: Instancia AnalisadorLexico com código fonte
2. **Configuração**: Define exibição de fim de arquivo como false
3. **Processamento**: Loop chamando proximoToken()
4. **Coleta**: Armazena todos os tokens válidos
5. **Finalização**: Obtém caracteres não identificados
6. **Limpeza**: Limpa estado do analisador

### Tratamento de Tokens Especiais
- **FIM_DE_ARQUIVO**: Ignorado na exibição (configurável)
- **Tokens válidos**: Todos os outros tipos são exibidos
- **Formatação**: Tipo e lexema separados por dois pontos

## Vantagens da Implementação

### Usabilidade
- **Interface intuitiva**: Fluxo claro de trabalho
- **Flexibilidade**: Entrada por arquivo ou manual
- **Feedback imediato**: Resultados instantâneos
- **Salvamento opcional**: Exportação quando necessária

### Robustez
- **Tratamento de erros**: Graceful handling de falhas
- **Validação**: Verificações antes de operações críticas
- **Recuperação**: Sistema continua funcionando após erros

### Manutenibilidade
- **Métodos especializados**: Cada funcionalidade em método próprio
- **Separação de responsabilidades**: Interface e lógica separadas
- **Código limpo**: Estrutura clara e documentada

### Performance
- **Carregamento eficiente**: Leitura otimizada de arquivos
- **Processamento direto**: Integração direta com analisador
- **Memória controlada**: Limpeza adequada de recursos

Esta classe representa a interface principal do usuário com o analisador léxico, proporcionando uma experiência completa e profissional para análise de código fonte, com todas as funcionalidades necessárias para um ambiente de desenvolvimento de compiladores.