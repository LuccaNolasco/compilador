package gui;

// Importações do JavaFX para interface gráfica
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Importações do analisador sintático
import sintatico.AnalisadorSintatico;

// Importações para manipulação de arquivos
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

/**
 * Classe responsável pela interface gráfica do analisador sintático
 * 
 * Esta tela permite ao usuário:
 * - Selecionar arquivos de código fonte para análise sintática
 * - Visualizar o conteúdo do arquivo
 * - Executar a análise sintática
 * - Ver se o programa foi aceito ou rejeitado
 * - Ver a linha do erro caso não seja aceito
 * - Salvar os resultados em arquivo
 * - Configurar entrada de texto em português brasileiro
 * 
 */
public class TelaAnalisadorSintatico {
    
    // Referências necessárias para funcionamento da tela
    private Stage primaryStage;    // Janela principal da aplicação
    private CompiladorGUI mainApp; // Referência para a aplicação principal
    
    /**
     * Construtor da tela do analisador sintático
     */
    public TelaAnalisadorSintatico(Stage primaryStage, CompiladorGUI mainApp) {
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
    }
    
    /**
     * Configura o input method para layout de teclado brasileiro em controles de texto
     */
    private void configurarInputMethodBrasileiro(Control controle) {
        // Configurar propriedades específicas para layout brasileiro
        controle.getProperties().put("javafx.scene.control.skin.behavior.TextInputControlBehavior.locale", new Locale("pt", "BR"));
        
        // Adicionar listener para garantir que o locale seja aplicado
        controle.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Quando o controle recebe foco, garantir que o locale brasileiro está ativo
                Locale.setDefault(new Locale("pt", "BR"));
                
                // Configurar propriedades específicas para input method
                System.setProperty("java.awt.im.style", "on-the-spot");
                System.setProperty("user.language", "pt");
                System.setProperty("user.country", "BR");
            }
        });
        
        // Configurar propriedades CSS para suporte a caracteres especiais brasileiros
        String estilo = controle.getStyle();
        if (estilo == null) estilo = "";
        
        // Adicionar configurações específicas para UTF-8 e caracteres brasileiros
        String novoEstilo = estilo + 
            "; -fx-font-encoding: 'UTF-8'" +
            "; -fx-text-encoding: 'UTF-8'" +
            "; -fx-font-smoothing-type: gray";
        
        controle.setStyle(novoEstilo);
        
        // Para TextArea e TextField, configurar propriedades adicionais
        if (controle instanceof TextInputControl) {
            TextInputControl textControl = (TextInputControl) controle;
            
            // Garantir que aceite caracteres especiais brasileiros
            textControl.textProperty().addListener((obs, oldText, newText) -> {
                // Não fazer nada especial, apenas garantir que o texto seja preservado
                // O JavaFX deve lidar com UTF-8 automaticamente
            });
        }
    }

    /**
     * Cria e configura a cena da tela do analisador sintático
     */
    public Scene criarCena() {
        // Layout principal usando BorderPane
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f8ff;");  // Fundo azul claro
        
        // === ÁREA SUPERIOR - Cabeçalho com título e botão voltar ===
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 10, 0));
        
        // Botão para voltar à tela inicial
        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-background-radius: 5;");
        btnVoltar.setPrefWidth(100);
        btnVoltar.setOnAction(e -> mainApp.voltarTelaInicial());
        
        // Título da tela
        Label titulo = new Label("ANALISADOR SINTÁTICO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titulo.setStyle("-fx-text-fill: #2c3e50;");
        
        // Spacers para centralizar o título entre o botão e a margem direita
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        // Montar o cabeçalho
        topBox.getChildren().addAll(btnVoltar, spacer1, titulo, spacer2);
        root.setTop(topBox);
        
        // === ÁREA CENTRAL - Controles principais ===
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));
        
        // --- Seção de seleção de arquivos ---
        GridPane fileGrid = new GridPane();
        fileGrid.setHgap(10);
        fileGrid.setVgap(10);
        
        // Campo para arquivo de entrada
        Label lblEntrada = new Label("Arquivo de Entrada:");
        lblEntrada.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextField txtCaminhoEntrada = new TextField();
        txtCaminhoEntrada.setPrefWidth(450);
        txtCaminhoEntrada.setPromptText("@arquivo.txt");
        
        // Configurar suporte a caracteres brasileiros
        configurarInputMethodBrasileiro(txtCaminhoEntrada);
        
        Button btnSelecionarEntrada = new Button("Selecionar Arquivo");
        btnSelecionarEntrada.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        
        // Campo para arquivo de saída (opcional)
        Label lblSaida = new Label("Arquivo de Saída (opcional):");
        lblSaida.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextField txtCaminhoSaida = new TextField();
        txtCaminhoSaida.setPrefWidth(450);
        txtCaminhoSaida.setPromptText("sintatico_saida.txt");
        
        // Configurar suporte a caracteres brasileiros
        configurarInputMethodBrasileiro(txtCaminhoSaida);
        
        Button btnSelecionarSaida = new Button("Selecionar Local");
        btnSelecionarSaida.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 5;");
        
        // Organizar campos de arquivo em grid
        fileGrid.add(lblEntrada, 0, 0);
        fileGrid.add(txtCaminhoEntrada, 1, 0);
        fileGrid.add(btnSelecionarEntrada, 2, 0);
        fileGrid.add(lblSaida, 0, 1);
        fileGrid.add(txtCaminhoSaida, 1, 1);
        fileGrid.add(btnSelecionarSaida, 2, 1);
        
        // --- Área de visualização do código fonte ---
        Label lblConteudo = new Label("Conteúdo do Arquivo:");
        lblConteudo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // Criar componente com numeração de linhas
        HBox conteudoComLinhas = criarAreaComNumeracaoLinhas();
        TextArea txtConteudo = (TextArea) conteudoComLinhas.getChildren().get(1);
        
        // Configurar suporte a caracteres brasileiros
        configurarInputMethodBrasileiro(txtConteudo);
        
        // --- Botão principal para executar análise ---
        Button btnAnalisar = new Button("ANALISAR");
        btnAnalisar.setPrefWidth(150);
        btnAnalisar.setPrefHeight(40);
        btnAnalisar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10;");
        
        // --- Área de exibição dos resultados ---
        Label lblResultados = new Label("Resultado da Análise Sintática:");
        lblResultados.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextArea txtResultados = new TextArea();
        txtResultados.setPrefRowCount(8);
        txtResultados.setPrefHeight(150);
        txtResultados.setEditable(false);  // Somente leitura
        txtResultados.setPromptText("O resultado da análise sintática aparecerá aqui...");
        txtResultados.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-background-color: #ecf0f1;");
        
        // Configurar suporte a caracteres brasileiros (mesmo sendo read-only)
        configurarInputMethodBrasileiro(txtResultados);
        
        // Label para exibir status (aceito/não aceito)
        Label lblStatus = new Label("");
        lblStatus.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblStatus.setStyle("-fx-text-fill: #2c3e50;");
        
        // Montar a estrutura central da interface
        centerBox.getChildren().addAll(
            fileGrid,                // Seção de arquivos
            new Separator(),         // Linha separadora
            lblConteudo,             // Label do código fonte
            conteudoComLinhas,       // Área do código fonte com numeração
            btnAnalisar,             // Botão de análise
            new Separator(),         // Linha separadora
            lblStatus,               // Status da análise
            lblResultados,           // Label dos resultados
            txtResultados            // Área dos resultados
        );
        
        // Adicionar área central ao layout principal
        root.setCenter(centerBox);
        
        // === CONFIGURAÇÃO DOS EVENTOS DOS BOTÕES ===
        
        // Evento do botão "Selecionar Arquivo" (entrada)
        btnSelecionarEntrada.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar arquivo de entrada");
            // Filtros para tipos de arquivo aceitos
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de texto", "*.txt", "*.pas", "*.pascal")
            );
            File arquivo = fileChooser.showOpenDialog(primaryStage);
            if (arquivo != null) {
                // Exibir nome do arquivo com @ (convenção do compilador)
                txtCaminhoEntrada.setText("@" + arquivo.getName());
                // Carregar conteúdo do arquivo na área de texto
                carregarArquivo(arquivo, txtConteudo);
                // Limpar campo de resultados ao selecionar novo arquivo
                txtResultados.clear();
                lblStatus.setText("");
                lblStatus.setStyle("-fx-text-fill: #2c3e50;");
            }
        });
        
        // Evento do botão "Selecionar Local" (saída)
        btnSelecionarSaida.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar local para salvar resultados");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de texto", "*.txt")
            );
            
            // Sugerir nome padrão para o arquivo de saída
            fileChooser.setInitialFileName("sintatico_saida.txt");
            
            File arquivo = fileChooser.showSaveDialog(primaryStage);
            if (arquivo != null) {
                // Exibir caminho completo do arquivo de saída
                txtCaminhoSaida.setText(arquivo.getAbsolutePath());
            }
        });
        
        // Evento do botão "ANALISAR" (principal)
        btnAnalisar.setOnAction(e -> {
            String conteudo = txtConteudo.getText();
            if (conteudo.isEmpty()) {
                mostrarAviso("Por favor, selecione um arquivo ou digite o código fonte.");
                return;
            }
            
            // Executar análise sintática do código
            String resultado = analisarCodigo(conteudo, lblStatus);
            txtResultados.setText(resultado);
            
            // Salvar resultados automaticamente se arquivo de saída foi especificado
            String caminhoSaida = txtCaminhoSaida.getText();
            if (!caminhoSaida.isEmpty()) {
                salvarResultados(resultado, caminhoSaida);
            }
        });
        
        // Retornar a cena configurada com dimensões fixas
        return new Scene(root, 1200, 800);
    }
    
    /**
     * Cria um componente com numeração de linhas à esquerda da área de texto
     * 
     * Retorna um HBox contendo uma TextArea para números (somente leitura)
     * e outra TextArea para o conteúdo do arquivo.
     * As duas áreas são sincronizadas para rolagem e atualização de números.
     */
    private HBox criarAreaComNumeracaoLinhas() {
        HBox container = new HBox(0);
        
        // TextArea para números das linhas (somente leitura)
        TextArea txtNumerosLinhas = new TextArea();
        txtNumerosLinhas.setPrefWidth(60);
        txtNumerosLinhas.setPrefRowCount(15);
        txtNumerosLinhas.setPrefHeight(200);
        txtNumerosLinhas.setEditable(false);
        txtNumerosLinhas.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; " +
                                  "-fx-background-color: #e8e8e8; -fx-text-fill: #666; " +
                                  "-fx-padding: 5px; -fx-border-color: #ccc; -fx-border-width: 0 1 0 0; " +
                                  "-fx-text-alignment: right;");
        txtNumerosLinhas.setFocusTraversable(false);
        
        // TextArea para conteúdo do arquivo
        TextArea txtConteudo = new TextArea();
        txtConteudo.setPrefRowCount(15);
        txtConteudo.setPrefHeight(200);
        txtConteudo.setPromptText("O conteúdo do arquivo selecionado aparecerá aqui...");
        txtConteudo.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        // Sincronizar rolagem vertical entre as duas áreas
        txtConteudo.textProperty().addListener((obs, oldText, newText) -> {
            atualizarNumeracaoLinhas(txtNumerosLinhas, newText);
        });
        
        txtConteudo.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            txtNumerosLinhas.setScrollTop(newVal.doubleValue());
        });
        
        txtNumerosLinhas.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            txtConteudo.setScrollTop(newVal.doubleValue());
        });
        
        // Adicionar as duas áreas ao container
        container.getChildren().addAll(txtNumerosLinhas, txtConteudo);
        
        // Atualizar números inicialmente
        atualizarNumeracaoLinhas(txtNumerosLinhas, txtConteudo.getText());
        
        return container;
    }
    
    /**
     * Atualiza a numeração das linhas na TextArea de números
     * 
     * Calcula o número de linhas no conteúdo e atualiza a área de números
     * com os números correspondentes, alinhados à direita.
     */
    private void atualizarNumeracaoLinhas(TextArea txtNumerosLinhas, String conteudo) {
        if (conteudo == null || conteudo.isEmpty()) {
            txtNumerosLinhas.setText("1");
            return;
        }
        
        // Contar linhas usando split com limite -1 para incluir linhas vazias no final
        String[] linhas = conteudo.split("\n", -1);
        int numLinhas = linhas.length;
        
        // Se não há quebra de linha no final, garantir pelo menos 1 linha
        if (numLinhas == 0) {
            numLinhas = 1;
        }
        
        // Criar string com números das linhas alinhados à direita
        StringBuilder numeros = new StringBuilder();
        int larguraNumero = String.valueOf(numLinhas).length();
        
        for (int i = 1; i <= numLinhas; i++) {
            String numero = String.valueOf(i);
            // Alinhar à direita preenchendo com espaços à esquerda
            while (numero.length() < larguraNumero) {
                numero = " " + numero;
            }
            numeros.append(numero);
            if (i < numLinhas) {
                numeros.append("\n");
            }
        }
        
        txtNumerosLinhas.setText(numeros.toString());
    }
    
    /**
     * Carrega o conteúdo de um arquivo para a área de texto
     * 
     * Lê todo o conteúdo do arquivo e exibe na TextArea fornecida.
     * Em caso de erro, exibe um aviso para o usuário.
     */
    private void carregarArquivo(File arquivo, TextArea txtConteudo) {
        try {
            // Ler todo o conteúdo do arquivo como String
            String conteudo = new String(Files.readAllBytes(arquivo.toPath()));
            txtConteudo.setText(conteudo);
        } catch (IOException e) {
            // Exibir erro caso não consiga ler o arquivo
            mostrarAviso("Erro ao carregar arquivo: " + e.getMessage());
        }
    }
    
    /**
     * Executa a análise sintática do código fonte fornecido
     * 
     * Este método é o coração da funcionalidade do analisador sintático.
     * Processa o código e retorna uma string formatada com:
     * 1. Resultado da análise (Aceito ou Não Aceito)
     * 2. Linha do erro (se não aceito)
     */
    private String analisarCodigo(String codigo, Label lblStatus) {
        StringBuilder resultado = new StringBuilder();
        
        try {
            // Criar instância do analisador sintático
            AnalisadorSintatico analisador = new AnalisadorSintatico(codigo);
            
            // Executar análise
            boolean aceito = analisador.analisar();
            
            if (aceito) {
                resultado.append("========================================\n");
                resultado.append("       PROGRAMA ACEITO\n");
                resultado.append("========================================\n");
                resultado.append("O programa foi analisado sintaticamente e está correto.\n");
                resultado.append("========================================\n");
                
                // Atualizar label de status com cor verde
                lblStatus.setText("✓ PROGRAMA ACEITO");
                lblStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 16px;");
            } else {
                int linhaErro = analisador.getLinhaErro();
                resultado.append("========================================\n");
                resultado.append("       PROGRAMA NÃO ACEITO\n");
                resultado.append("========================================\n");
                resultado.append("Erro sintático encontrado provavelmente próximo a linha: ").append(linhaErro).append("\n");
                resultado.append("O programa não está de acordo com a gramática da linguagem.\n");
                resultado.append("========================================\n");
                
                // Atualizar label de status com cor vermelha
                lblStatus.setText("✖ PROGRAMA NÃO ACEITO");
                lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 16px;");
            }
            
        } catch (Exception e) {
            // Capturar e reportar qualquer erro durante a análise
            resultado.append("========================================\n");
            resultado.append("       ERRO DURANTE A ANÁLISE\n");
            resultado.append("========================================\n");
            resultado.append("Erro: ").append(e.getMessage()).append("\n");
            resultado.append("========================================\n");
            
            // Atualizar label de status com cor vermelha
            lblStatus.setText("✖ ERRO");
            lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 16px;");
        }
        
        return resultado.toString();
    }
    
    /**
     * Salva os resultados da análise sintática em um arquivo
     * 
     * Grava o conteúdo dos resultados no arquivo especificado
     * e exibe uma confirmação para o usuário.
     */
    private void salvarResultados(String resultados, String caminho) {
        try {
            // Criar e escrever no arquivo
            FileWriter writer = new FileWriter(caminho);
            writer.write(resultados);
            writer.close();
            
            // Confirmar sucesso para o usuário
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Resultados salvos em: " + caminho);
            alert.showAndWait();
            
        } catch (IOException e) {
            // Exibir erro caso não consiga salvar
            mostrarAviso("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
    
    /**
     * Exibe um diálogo de aviso para o usuário
     * 
     * Método utilitário para mostrar mensagens informativas
     * ou de erro de forma padronizada.
     */
    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

