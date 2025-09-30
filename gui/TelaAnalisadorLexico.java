package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lexico.AnalisadorLexico;
import lexico.TipoToken;
import lexico.Token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

public class TelaAnalisadorLexico {
    
    private Stage primaryStage;
    private CompiladorGUI mainApp;
    
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

    public Scene criarCena() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        // Área superior - Título e botão voltar
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 10, 0));
        
        // Botão Voltar
        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-background-radius: 5;");
        btnVoltar.setPrefWidth(100);
        btnVoltar.setOnAction(e -> mainApp.voltarTelaInicial());
        
        // Título centralizado
        Label titulo = new Label("ANALISADOR LÉXICO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titulo.setStyle("-fx-text-fill: #2c3e50;");
        
        // Spacer para centralizar o título
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        topBox.getChildren().addAll(btnVoltar, spacer1, titulo, spacer2);
        root.setTop(topBox);
        
        // Área central - Controles principais
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));
        
        // Seção de seleção de arquivos
        GridPane fileGrid = new GridPane();
        fileGrid.setHgap(10);
        fileGrid.setVgap(10);
        
        // Arquivo de entrada
        Label lblEntrada = new Label("Arquivo de Entrada:");
        lblEntrada.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextField txtCaminhoEntrada = new TextField();
        txtCaminhoEntrada.setPrefWidth(450);
        txtCaminhoEntrada.setPromptText("@arquivo.txt");
        
        // Configurar input method para layout brasileiro
        configurarInputMethodBrasileiro(txtCaminhoEntrada);
        
        Button btnSelecionarEntrada = new Button("Selecionar Arquivo");
        btnSelecionarEntrada.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        
        // Arquivo de saída
        Label lblSaida = new Label("Arquivo de Saída (opcional):");
        lblSaida.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextField txtCaminhoSaida = new TextField();
        txtCaminhoSaida.setPrefWidth(450);
        txtCaminhoSaida.setPromptText("tokens_saida.txt");
        
        // Configurar input method para layout brasileiro
        configurarInputMethodBrasileiro(txtCaminhoSaida);
        
        Button btnSelecionarSaida = new Button("Selecionar Local");
        btnSelecionarSaida.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 5;");
        
        fileGrid.add(lblEntrada, 0, 0);
        fileGrid.add(txtCaminhoEntrada, 1, 0);
        fileGrid.add(btnSelecionarEntrada, 2, 0);
        fileGrid.add(lblSaida, 0, 1);
        fileGrid.add(txtCaminhoSaida, 1, 1);
        fileGrid.add(btnSelecionarSaida, 2, 1);
        
        // Área de conteúdo do arquivo
        Label lblConteudo = new Label("Conteúdo do Arquivo:");
        lblConteudo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextArea txtConteudo = new TextArea();
        txtConteudo.setPrefRowCount(15);
        txtConteudo.setPrefHeight(200);
        txtConteudo.setPromptText("O conteúdo do arquivo selecionado aparecerá aqui...");
        txtConteudo.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        // Configurar input method para layout brasileiro
        configurarInputMethodBrasileiro(txtConteudo);
        
        // Botão de análise
        Button btnAnalisar = new Button("ANALISAR");
        btnAnalisar.setPrefWidth(150);
        btnAnalisar.setPrefHeight(40);
        btnAnalisar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10;");
        
        // Área de resultados
        Label lblResultados = new Label("Tokens Encontrados:");
        lblResultados.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextArea txtResultados = new TextArea();
        txtResultados.setPrefRowCount(18);
        txtResultados.setPrefHeight(250);
        txtResultados.setEditable(false);
        txtResultados.setPromptText("Os tokens analisados aparecerão aqui...");
        txtResultados.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-background-color: #ecf0f1;");
        
        // Configurar input method para layout brasileiro (mesmo sendo read-only, pode ser útil)
        configurarInputMethodBrasileiro(txtResultados);

        centerBox.getChildren().addAll(
            fileGrid,
            new Separator(),
            lblConteudo,
            txtConteudo,
            btnAnalisar,
            new Separator(),
            lblResultados,
            txtResultados
        );
        
        root.setCenter(centerBox);
        
        // Eventos dos botões
        btnSelecionarEntrada.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar arquivo de entrada");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de texto", "*.txt", "*.pas", "*.pascal")
            );
            File arquivo = fileChooser.showOpenDialog(primaryStage);
            if (arquivo != null) {
                txtCaminhoEntrada.setText("@" + arquivo.getName());
                carregarArquivo(arquivo, txtConteudo);
            }
        });
        
        btnSelecionarSaida.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar local para salvar tokens");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de texto", "*.txt")
            );
            
            // Definir diretório inicial como @tabelas_de_tokens/
            File diretorioTokens = new File("lexico/tabelas_de_tokens");
            if (!diretorioTokens.exists()) {
                diretorioTokens.mkdirs(); // Criar diretório se não existir
            }
            if (diretorioTokens.exists() && diretorioTokens.isDirectory()) {
                fileChooser.setInitialDirectory(diretorioTokens);
            }
            
            // Sugerir nome padrão para o arquivo
            fileChooser.setInitialFileName("tokens_saida.txt");
            
            File arquivo = fileChooser.showSaveDialog(primaryStage);
            if (arquivo != null) {
                txtCaminhoSaida.setText(arquivo.getAbsolutePath());
            }
        });
        
        btnAnalisar.setOnAction(e -> {
            String conteudo = txtConteudo.getText();
            if (conteudo.isEmpty()) {
                mostrarAviso("Por favor, selecione um arquivo ou digite o código fonte.");
                return;
            }
            
            String resultados = analisarCodigo(conteudo);
            txtResultados.setText(resultados);
            
            // Salvar resultados se um arquivo de saída foi especificado
            String caminhoSaida = txtCaminhoSaida.getText();
            if (!caminhoSaida.isEmpty()) {
                salvarResultados(resultados, caminhoSaida);
            }
        });
        
        return new Scene(root, 1200, 800);
    }
    
    private void carregarArquivo(File arquivo, TextArea txtConteudo) {
        try {
            String conteudo = new String(Files.readAllBytes(arquivo.toPath()));
            txtConteudo.setText(conteudo);
        } catch (IOException e) {
            mostrarAviso("Erro ao carregar arquivo: " + e.getMessage());
        }
    }
    
    private String analisarCodigo(String codigo) {
        StringBuilder resultado = new StringBuilder();
        
        try {
            AnalisadorLexico analisador = new AnalisadorLexico(codigo);
            analisador.setExibirFimDeArquivo(true);
            
            // Primeira parte: tokens válidos
            Token token;
            do {
                token = analisador.proximoToken();
                if (token != null) {
                    resultado.append(token.toString()).append("\n");
                }
            } while (token != null && token.tipo != TipoToken.FIM_DE_ARQUIVO);
            
            // Adicionar separador
            resultado.append("\n");
            resultado.append("========================================\n");
            resultado.append("       CARACTERES NÃO IDENTIFICADOS\n");
            resultado.append("========================================\n");
            
            // Segunda parte: caracteres não identificados
            java.util.List<Character> caracteresNaoIdentificados = analisador.getCaracteresNaoIdentificados();
            if (caracteresNaoIdentificados.isEmpty()) {
                resultado.append("Nenhum caractere não identificado encontrado.\n");
            } else {
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
            resultado.append("Erro durante a análise: ").append(e.getMessage());
        }
        
        return resultado.toString();
    }
    
    private void salvarResultados(String resultados, String caminho) {
        try {
            FileWriter writer = new FileWriter(caminho);
            writer.write(resultados);
            writer.close();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Resultados salvos em: " + caminho);
            alert.showAndWait();
            
        } catch (IOException e) {
            mostrarAviso("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
    
    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
