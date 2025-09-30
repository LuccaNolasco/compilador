# Relatório da Classe CompiladorGUI

## Descrição Geral
A classe `CompiladorGUI` é a classe principal da interface gráfica do compilador, responsável por gerenciar a aplicação JavaFX e coordenar a navegação entre as diferentes telas do sistema. Esta classe estende `Application` do JavaFX e serve como ponto de entrada da aplicação, controlando o ciclo de vida das janelas e configurando o ambiente de execução para suporte ao idioma português brasileiro.

## Herança e Estrutura
- **Classe pai**: `javafx.application.Application`
- **Padrão de design**: Controller/Coordinator
- **Responsabilidade**: Gerenciamento de janelas e navegação entre telas

## Atributos da Classe

### Atributos de Controle de Janelas
**`primaryStage`** (Stage)
- **Função**: Referência para a janela principal da aplicação
- **Uso**: Controle da janela inicial e navegação de volta à tela principal
- **Escopo**: Privado

**`lexicalStage`** (Stage)
- **Função**: Referência para a janela do analisador léxico
- **Uso**: Controle da janela secundária quando o analisador léxico está ativo
- **Escopo**: Privado

### Atributos de Telas
**`telaInicial`** (TelaInicial)
- **Função**: Instância da tela inicial do compilador
- **Responsabilidade**: Gerencia a interface de seleção de funcionalidades
- **Escopo**: Privado

**`telaAnalisadorLexico`** (TelaAnalisadorLexico)
- **Função**: Instância da tela do analisador léxico
- **Responsabilidade**: Gerencia a interface de análise léxica
- **Escopo**: Privado

## Métodos da Classe

### Método Principal de Inicialização

**`start(Stage primaryStage)`** - void
- **Função**: Método principal do JavaFX que inicializa a aplicação
- **Parâmetros**: `primaryStage` - janela principal fornecida pelo JavaFX
- **Funcionamento**:
  1. Configura a localização brasileira
  2. Define o título da janela principal
  3. Desabilita o redimensionamento da janela
  4. Inicializa as instâncias das telas
  5. Cria e exibe a cena inicial
- **Características**: Método obrigatório da classe Application

### Método de Configuração Regional

**`configurarLocalizacaoBrasileira()`** - void (privado)
- **Função**: Configura todo o ambiente da aplicação para o padrão brasileiro
- **Configurações realizadas**:
  - **Locale**: Define português brasileiro como padrão
  - **Propriedades do sistema**: Configura idioma, país e região
  - **Timezone**: Define fuso horário de São Paulo
  - **Encoding**: Configura UTF-8 como padrão
  - **JavaFX**: Configura locale específico para JavaFX
  - **Fontes**: Configura anti-aliasing e suavização de texto
- **Saída de debug**: Exibe informações de configuração no console
- **Importância**: Garante suporte adequado a caracteres especiais brasileiros

### Métodos de Navegação

**`abrirAnalisadorLexico()`** - void
- **Função**: Navega da tela inicial para o analisador léxico
- **Funcionamento**:
  1. Cria a cena do analisador léxico
  2. Fecha a janela principal
  3. Abre nova janela para o analisador léxico
  4. Configura título e propriedades da nova janela
  5. Define comportamento de fechamento da janela
- **Características**: Substitui a janela principal por uma nova janela

**`voltarTelaInicial()`** - void
- **Função**: Retorna do analisador léxico para a tela inicial
- **Funcionamento**:
  1. Fecha a janela do analisador léxico (se existir)
  2. Recria a cena da tela inicial
  3. Restaura a janela principal
- **Uso**: Navegação de volta à tela principal

### Método de Entrada da Aplicação

**`main(String[] args)`** - void (estático)
- **Função**: Ponto de entrada da aplicação Java
- **Parâmetros**: `args` - argumentos da linha de comando
- **Ação**: Chama `launch(args)` para iniciar a aplicação JavaFX

## Características Especiais da Implementação

### Gerenciamento de Janelas
- **Estratégia de janelas**: Usa janelas separadas em vez de troca de cenas
- **Controle de ciclo de vida**: Gerencia abertura e fechamento de janelas
- **Prevenção de redimensionamento**: Mantém layout fixo para consistência visual

### Configuração de Localização Avançada
A classe implementa uma configuração abrangente para suporte ao português brasileiro:

**Configurações de Locale:**
- Locale padrão: pt_BR
- Propriedades de sistema para idioma e país
- Configuração de timezone específica

**Configurações de Encoding:**
- UTF-8 como encoding padrão
- Configuração de encoding nativo
- Suporte a caracteres especiais brasileiros

**Configurações de Interface:**
- Input method brasileiro
- Anti-aliasing de fontes
- Suavização de texto
- Locale específico para JavaFX

### Padrão de Inicialização
- **Inicialização lazy**: Telas são criadas apenas quando necessárias
- **Referências mantidas**: Instâncias são reutilizadas para performance
- **Configuração centralizada**: Todas as configurações em um local

## Fluxo de Navegação

### Inicialização da Aplicação
1. `main()` é chamado
2. JavaFX chama `start()`
3. Configuração brasileira é aplicada
4. Tela inicial é criada e exibida

### Navegação para Analisador Léxico
1. Usuário clica no botão do analisador léxico
2. `abrirAnalisadorLexico()` é chamado
3. Janela principal é fechada
4. Nova janela do analisador léxico é aberta

### Retorno à Tela Inicial
1. Usuário clica em "Voltar" no analisador léxico
2. `voltarTelaInicial()` é chamado
3. Janela do analisador léxico é fechada
4. Janela principal é restaurada

## Vantagens do Design

### Separação de Responsabilidades
- **Coordenação**: Foca apenas na coordenação entre telas
- **Delegação**: Delega funcionalidades específicas para classes especializadas
- **Manutenibilidade**: Facilita manutenção e extensão

### Flexibilidade de Navegação
- **Janelas independentes**: Cada funcionalidade tem sua própria janela
- **Estado preservado**: Cada tela mantém seu próprio estado
- **Experiência de usuário**: Navegação clara e intuitiva

### Configuração Robusta
- **Suporte internacional**: Configuração completa para português brasileiro
- **Compatibilidade**: Funciona em diferentes sistemas operacionais
- **Debugging**: Informações de configuração para diagnóstico

## Integração com Outras Classes

### Relacionamento com TelaInicial
- **Composição**: CompiladorGUI contém uma instância de TelaInicial
- **Comunicação**: TelaInicial chama métodos de CompiladorGUI para navegação
- **Dependência**: TelaInicial depende de CompiladorGUI para funcionar

### Relacionamento com TelaAnalisadorLexico
- **Composição**: CompiladorGUI contém uma instância de TelaAnalisadorLexico
- **Comunicação**: TelaAnalisadorLexico pode chamar métodos de CompiladorGUI
- **Ciclo de vida**: CompiladorGUI gerencia o ciclo de vida da tela

### Padrão Observer Implícito
- As telas "observam" eventos de usuário e notificam CompiladorGUI
- CompiladorGUI responde mudando o estado da aplicação
- Comunicação bidirecional entre controlador e views

## Considerações de Performance

### Gerenciamento de Memória
- **Reutilização de instâncias**: Telas são criadas uma vez e reutilizadas
- **Liberação de recursos**: Janelas são adequadamente fechadas
- **Lazy loading**: Recursos são carregados apenas quando necessários

### Responsividade da Interface
- **Configuração única**: Configurações são aplicadas uma vez na inicialização
- **Navegação rápida**: Troca de janelas é eficiente
- **Prevenção de bloqueios**: Operações não bloqueiam a interface

Esta classe serve como o núcleo organizacional da aplicação, garantindo uma experiência de usuário consistente e suporte adequado ao idioma português brasileiro, enquanto mantém uma arquitetura limpa e extensível.