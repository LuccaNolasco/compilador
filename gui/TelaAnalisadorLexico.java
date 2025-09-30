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

// Importações do analisador léxico
import lexico.AnalisadorLexico;
import lexico.TipoToken;
import lexico.Token;

// Importações para manipulação de arquivos
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

/**
 * Classe responsável pela interface gráfica do analisador léxico
 * 
 * Esta tela permite ao usuário:
 * - Selecionar arquivos de código fonte para análise
 * - Visualizar o conteúdo do arquivo
 * - Executar a análise léxica
 * - Ver os tokens identificados
 * - Salvar os resultados em arquivo
 * - Configurar entrada de texto em português brasileiro
 * 
 */
 public class TelaAnalisadorLexico {
    
    // Referências necessárias para funcionamento da tela
    private Stage primaryStage;    // Janela principal da aplicação
    private CompiladorGUI mainApp; // Referência para a aplicação principal
    
    /**
     * Construtor da tela do analisador léxico
     */
    public TelaAnalisadorLexico(Stage primaryStage, CompiladorGUI mainApp) {
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
     * Cria e configura a cena da tela do analisador léxico
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
        Label titulo = new Label("ANALISADOR LÉXICO");
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
        txtCaminhoSaida.setPromptText("tokens_saida.txt");
        
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
        
        TextArea txtConteudo = new TextArea();
        txtConteudo.setPrefRowCount(15);
        txtConteudo.setPrefHeight(200);
        txtConteudo.setPromptText("O conteúdo do arquivo selecionado aparecerá aqui...");
        txtConteudo.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");  // Fonte monoespaçada
        
        // Configurar suporte a caracteres brasileiros
        configurarInputMethodBrasileiro(txtConteudo);
        
        // --- Botão principal para executar análise ---
        Button btnAnalisar = new Button("ANALISAR");
        btnAnalisar.setPrefWidth(150);
        btnAnalisar.setPrefHeight(40);
        btnAnalisar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10;");
        
        // --- Área de exibição dos resultados ---
        Label lblResultados = new Label("Tokens Encontrados:");
        lblResultados.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextArea txtResultados = new TextArea();
        txtResultados.setPrefRowCount(18);
        txtResultados.setPrefHeight(250);
        txtResultados.setEditable(false);  // Somente leitura
        txtResultados.setPromptText("Os tokens analisados aparecerão aqui...");
        txtResultados.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-background-color: #ecf0f1;");
        
        // Configurar suporte a caracteres brasileiros (mesmo sendo read-only)
        configurarInputMethodBrasileiro(txtResultados);

        // Montar a estrutura central da interface
        centerBox.getChildren().addAll(
            fileGrid,                // Seção de arquivos
            new Separator(),         // Linha separadora
            lblConteudo,             // Label do código fonte
            txtConteudo,             // Área do código fonte
            btnAnalisar,             // Botão de análise
            new Separator(),         // Linha separadora
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
            }
        });
        
        // Evento do botão "Selecionar Local" (saída)
        btnSelecionarSaida.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar local para salvar tokens");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de texto", "*.txt")
            );
            
            // Definir diretório padrão como lexico/tabelas_de_tokens/
            File diretorioTokens = new File("lexico/tabelas_de_tokens");
            if (!diretorioTokens.exists()) {
                diretorioTokens.mkdirs(); // Criar diretório se não existir
            }
            if (diretorioTokens.exists() && diretorioTokens.isDirectory()) {
                fileChooser.setInitialDirectory(diretorioTokens);
            }
            
            // Sugerir nome padrão para o arquivo de saída
            fileChooser.setInitialFileName("tokens_saida.txt");
            
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
            
            // Executar análise léxica do código
            String resultados = analisarCodigo(conteudo);
            txtResultados.setText(resultados);
            
            // Salvar resultados automaticamente se arquivo de saída foi especificado
            String caminhoSaida = txtCaminhoSaida.getText();
            if (!caminhoSaida.isEmpty()) {
                salvarResultados(resultados, caminhoSaida);
            }
        });
        
        // Retornar a cena configurada com dimensões fixas
        return new Scene(root, 1200, 800);
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
     * Executa a análise léxica do código fonte fornecido
     * 
     * Este método é o coração da funcionalidade do analisador léxico.
     * Processa o código e retorna uma string formatada com:
     * 1. Lista de todos os tokens identificados
     * 2. Lista de caracteres não reconhecidos (se houver)
     */
    private String analisarCodigo(String codigo) {
        StringBuilder resultado = new StringBuilder();
        
        try {
            // Criar instância do analisador léxico
            AnalisadorLexico analisador = new AnalisadorLexico(codigo);
            analisador.setExibirFimDeArquivo(true);
            
            // === PRIMEIRA PARTE: Extrair todos os tokens válidos ===
            Token token;
            do {
                token = analisador.proximoToken();
                if (token != null) {
                    // Adicionar token ao resultado no formato <lexema, tipo>
                    resultado.append(token.toString()).append("\n");
                }
            } while (token != null && token.tipo != TipoToken.FIM_DE_ARQUIVO);
            
            // === SEGUNDA PARTE: Relatório de caracteres não identificados ===
            resultado.append("\n");
            resultado.append("========================================\n");
            resultado.append("       CARACTERES NÃO IDENTIFICADOS\n");
            resultado.append("========================================\n");
            
            // Obter lista de caracteres que não foram reconhecidos
            java.util.List<Character> caracteresNaoIdentificados = analisador.getCaracteresNaoIdentificados();
            if (caracteresNaoIdentificados.isEmpty()) {
                resultado.append("Nenhum caractere não identificado encontrado.\n");
            } else {
                // Listar todos os caracteres não identificados
                resultado.append("Caracteres encontrados: ");
                for (int i = 0; i < caracteresNaoIdentificados.size(); i++) {
                    char c = caracteresNaoIdentificados.get(i);
                    resultado.append("'").append(c).append("'");
                    if (i < caracteresNaoIdentificados.size() - 1) {
                        resultado.append(", ");
                    }
                }
                resultado.append("\n");
                resultado.append("Total de caracteres não identificados: ").append(caracteresNaoIdentificados.size()).append("\n");
            }
            
            resultado.append("========================================\n");
            
        } catch (Exception e) {
            // Capturar e reportar qualquer erro durante a análise
            resultado.append("Erro durante a análise: ").append(e.getMessage());
        }
        
        return resultado.toString();
    }
    
    /**
     * Salva os resultados da análise léxica em um arquivo
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
