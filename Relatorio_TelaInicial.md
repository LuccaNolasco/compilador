# Relatório da Classe TelaInicial

## Descrição Geral
A classe `TelaInicial` é responsável por criar e gerenciar a interface gráfica da tela principal do compilador. Esta classe implementa a tela de boas-vindas que apresenta as funcionalidades disponíveis do compilador, permitindo ao usuário navegar para o analisador léxico e informando sobre funcionalidades futuras (analisador sintático e semântico). A interface possui um design temático com elementos visuais personalizados.

## Estrutura da Classe

### Atributos
**`primaryStage`** (Stage)
- **Função**: Referência para a janela principal da aplicação
- **Uso**: Necessário para operações relacionadas à janela (como diálogos)
- **Escopo**: Privado

**`mainApp`** (CompiladorGUI)
- **Função**: Referência para a classe principal da aplicação
- **Uso**: Permite comunicação com o controlador principal para navegação
- **Escopo**: Privado

### Construtor
**`TelaInicial(Stage primaryStage, CompiladorGUI mainApp)`**
- **Função**: Inicializa a tela inicial com as referências necessárias
- **Parâmetros**:
  - `primaryStage`: Janela principal da aplicação
  - `mainApp`: Instância da classe controladora principal
- **Ação**: Armazena as referências para uso posterior na criação da interface

## Métodos da Classe

### Método Principal de Criação

**`criarCena()`** - Scene
- **Função**: Cria e configura toda a interface gráfica da tela inicial
- **Retorno**: Objeto Scene configurado e pronto para exibição
- **Estrutura criada**:
  - Layout principal (VBox)
  - Título da aplicação
  - Área de imagem principal
  - Container de botões de funcionalidades
  - Área para imagens adicionais

### Métodos de Configuração de Interface

**`configurarBotao(Button botao, String cor, boolean ativo)`** - void (privado)
- **Função**: Configura a aparência e comportamento de um botão
- **Parâmetros**:
  - `botao`: O botão a ser configurado
  - `cor`: Cor de fundo do botão (formato hexadecimal)
  - `ativo`: Define se o botão está ativo ou desabilitado
- **Configurações aplicadas**:
  - Dimensões (400x70 pixels)
  - Fonte (Arial, negrito, 16px)
  - Cores e efeitos visuais
  - Comportamento de hover (para botões ativos)
  - Cursor apropriado

**`criarImageView(String caminho, double largura, double altura)`** - ImageView (privado)
- **Função**: Cria um componente de imagem com tratamento de erros
- **Parâmetros**:
  - `caminho`: Caminho para o arquivo de imagem
  - `largura`: Largura desejada da imagem
  - `altura`: Altura desejada da imagem
- **Características**:
  - Verificação de existência do arquivo
  - Criação de placeholder em caso de erro
  - Preservação de proporção
  - Aplicação de efeitos visuais (sombra)
- **Tratamento de erros**: Cria retângulo cinza como placeholder se a imagem não for encontrada

### Método de Interação

**`mostrarAviso(String mensagem)`** - void (privado)
- **Função**: Exibe uma caixa de diálogo informativa para o usuário
- **Parâmetros**: `mensagem` - texto a ser exibido no diálogo
- **Uso**: Informar sobre funcionalidades não implementadas
- **Tipo de diálogo**: Alert.AlertType.INFORMATION

## Estrutura da Interface

### Layout Principal
- **Container**: VBox com espaçamento de 30px
- **Alinhamento**: Centralizado
- **Padding**: 50px em todas as direções
- **Cor de fundo**: Azul claro (#f0f8ff)

### Elementos da Interface

**Título**
- **Texto**: "Σ ★ - O real Sigma da Bahia"
- **Fonte**: Arial, negrito, 28px
- **Cor**: Azul escuro (#2c3e50)
- **Posicionamento**: Centralizado no topo

**Área de Imagem Principal**
- **Imagem**: "gui/images/realSigma.png"
- **Dimensões**: 300x300 pixels
- **Efeitos**: Sombra e bordas arredondadas
- **Fallback**: Placeholder cinza se imagem não encontrada

**Botões de Funcionalidades**
1. **Analisador Léxico** (Ativo)
   - Cor: Verde (#27ae60)
   - Ícone: ✓
   - Ação: Navega para o analisador léxico
   - Efeito hover: Verde mais escuro (#229954)

2. **Analisador Sintático** (Inativo)
   - Cor: Cinza (#95a5a6)
   - Ícone: ✖
   - Ação: Exibe aviso de não implementado
   - Aparência: Opacidade reduzida, cursor "not-allowed"

3. **Analisador Semântico** (Inativo)
   - Cor: Cinza (#95a5a6)
   - Ícone: ✖
   - Ação: Exibe aviso de não implementado
   - Aparência: Opacidade reduzida, cursor "not-allowed"

## Características de Design

### Design Responsivo
- **Layout flexível**: Usa VBox e HBox para organização automática
- **Espaçamento consistente**: 20-30px entre elementos
- **Alinhamento centralizado**: Todos os elementos centralizados

### Feedback Visual
- **Estados de botão**: Diferenciação clara entre ativo e inativo
- **Efeitos hover**: Mudança de cor ao passar o mouse
- **Cursores apropriados**: "hand" para ativos, "not-allowed" para inativos
- **Ícones informativos**: ✓ para disponível, ✖ para indisponível

### Tratamento de Erros
- **Imagens**: Placeholder automático se arquivo não encontrado
- **Exceções**: Try-catch para operações de arquivo
- **Feedback**: Mensagens informativas para o usuário

## Funcionalidades Implementadas

### Navegação
- **Para analisador léxico**: Funcional e operacional
- **Informações sobre futuras funcionalidades**: Avisos informativos

### Gestão de Recursos
- **Carregamento de imagens**: Com fallback para casos de erro
- **Otimização de layout**: Uso eficiente do espaço da tela

### Experiência do Usuário
- **Interface intuitiva**: Botões claramente identificados
- **Feedback imediato**: Resposta visual a interações
- **Informações claras**: Avisos sobre funcionalidades não implementadas

## Configurações Visuais

### Cores Utilizadas
- **Fundo principal**: #f0f8ff (Alice Blue)
- **Título**: #2c3e50 (Midnight Blue)
- **Botão ativo**: #27ae60 (Nephritis Green)
- **Botão hover**: #229954 (Green Sea)
- **Botão inativo**: #95a5a6 (Concrete Gray)
- **Texto inativo**: #bdc3c7 (Silver)
- **Borda inativa**: #7f8c8d (Asbestos)

### Tipografia
- **Fonte principal**: Arial
- **Título**: 28px, negrito
- **Botões**: 16px, negrito
- **Estilo**: Sans-serif para legibilidade

### Dimensões
- **Janela**: 600x720 pixels
- **Botões**: 400x70 pixels
- **Imagem principal**: 300x300 pixels
- **Espaçamentos**: 20-50 pixels

## Integração com o Sistema

### Comunicação com CompiladorGUI
- **Navegação**: Chama `mainApp.abrirAnalisadorLexico()` quando necessário
- **Dependência**: Requer instância de CompiladorGUI para funcionar

### Gerenciamento de Estado
- **Stateless**: Não mantém estado próprio
- **Recriação**: Interface é recriada a cada chamada de `criarCena()`

### Extensibilidade
- **Novos botões**: Fácil adição de novas funcionalidades
- **Modificação visual**: Cores e estilos centralizados
- **Novas imagens**: Sistema de fallback permite mudanças seguras

## Vantagens da Implementação

### Manutenibilidade
- **Métodos especializados**: Cada aspecto da interface tem seu método
- **Configuração centralizada**: Estilos e comportamentos em métodos dedicados
- **Código limpo**: Separação clara de responsabilidades

### Robustez
- **Tratamento de erros**: Graceful degradation para recursos não encontrados
- **Fallbacks**: Alternativas para casos de falha
- **Validação**: Verificações antes de operações críticas

### Usabilidade
- **Interface clara**: Funcionalidades claramente identificadas
- **Feedback visual**: Resposta imediata a ações do usuário
- **Informações úteis**: Avisos sobre funcionalidades futuras

Esta classe serve como porta de entrada amigável para o sistema do compilador, proporcionando uma experiência de usuário agradável e informativa, enquanto mantém a flexibilidade para futuras expansões do sistema.