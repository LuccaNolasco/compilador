package gui;

// Importações do JavaFX para criação da interface gráfica
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;

/**
 * Classe responsável pela tela inicial do compilador
 * 
 * Esta tela apresenta o menu principal com as opções disponíveis:
 * - Analisador Léxico (funcional)
 * - Analisador Sintático (não implementado)
 * - Analisador Semântico (não implementado)
 * 
 * A interface possui um design temático com imagens e cores personalizadas.
 * 
 */
public class TelaInicial {
    
    // Referências necessárias para funcionamento da tela
    private Stage primaryStage;    // Janela principal da aplicação
    private CompiladorGUI mainApp; // Referência para a aplicação principal
    
    /**
     * Construtor da tela inicial
     */
    public TelaInicial(Stage primaryStage, CompiladorGUI mainApp) {
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
    }
    
    /**
     * Cria e configura a cena da tela inicial
     */
    public Scene criarCena() {
        // Container principal com espaçamento vertical
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f0f8ff;");  // Cor de fundo azul claro
        
        // Título principal da aplicação
        Label titulo = new Label("Σ ★ - O real Sigma da Bahia");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setStyle("-fx-text-fill: #2c3e50;");  // Cor do texto
        
        // Container para a imagem principal
        VBox imagemProfessor = new VBox(10);
        imagemProfessor.setAlignment(Pos.CENTER);
        
        // Carrega e exibe a imagem principal
        ImageView imagemPrincipal = criarImageView("gui/images/realSigma.png", 300, 300);
        
        imagemProfessor.getChildren().add(imagemPrincipal);
        
        // Container para os botões do menu principal
        VBox botoesContainer = new VBox(20);
        botoesContainer.setAlignment(Pos.CENTER);
        
        // Botão Analisador Léxico (funcional - cor verde)
        Button btnLexico = new Button("✓ Analisador Léxico");
        configurarBotao(btnLexico, "#27ae60", true);  // Verde para indicar disponível
        btnLexico.setOnAction(e -> mainApp.abrirAnalisadorLexico());
        
        // Botão Analisador Sintático (não implementado - cor cinza)
        Button btnSintatico = new Button("✖ Analisador Sintático");
        configurarBotao(btnSintatico, "#95a5a6", false);  // Cinza para indicar bloqueado
        btnSintatico.setOnAction(e -> mostrarAviso("Analisador Sintático ainda não implementado"));
        
        // Botão Analisador Semântico (não implementado - cor cinza)
        Button btnSemantico = new Button("✖ Analisador Semântico");
        configurarBotao(btnSemantico, "#95a5a6", false);  // Cinza para indicar bloqueado
        btnSemantico.setOnAction(e -> mostrarAviso("Analisador Semântico ainda não implementado"));
        
        // Adicionar todos os botões ao container
        botoesContainer.getChildren().addAll(btnLexico, btnSintatico, btnSemantico);
        
        // Container para imagens adicionais (reservado para futuras expansões)
        HBox imagensAdicionais = new HBox(20);
        imagensAdicionais.setAlignment(Pos.CENTER);
        
        // Montar a estrutura completa da tela
        root.getChildren().addAll(titulo, imagemProfessor, botoesContainer, imagensAdicionais);
        
        // Retornar a cena configurada com dimensões fixas
        return new Scene(root, 600, 720);
    }
    
    /**
     * Configura a aparência e comportamento de um botão
     */
    private void configurarBotao(Button botao, String cor, boolean ativo) {
        // Definir tamanho padrão dos botões
        botao.setPrefWidth(400);
        botao.setPrefHeight(70);
        botao.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        if (ativo) {
            // Configuração para botões ativos (funcionais)
            botao.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; " +
                          "-fx-background-radius: 10; -fx-cursor: hand;");
            
            // Efeito hover - escurece quando mouse passa por cima
            botao.setOnMouseEntered(e -> botao.setStyle("-fx-background-color: #229954; -fx-text-fill: white; " +
                                                       "-fx-background-radius: 10; -fx-cursor: hand;"));
            // Volta cor original quando mouse sai
            botao.setOnMouseExited(e -> botao.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; " +
                                                      "-fx-background-radius: 10; -fx-cursor: hand;"));
        } else {
            // Configuração para botões inativos (não implementados)
            botao.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: #bdc3c7; " +
                          "-fx-background-radius: 10; -fx-cursor: not-allowed; " +
                          "-fx-opacity: 0.7; -fx-border-color: #7f8c8d; -fx-border-width: 1;");
        }
    }
    
    /**
     * Exibe um diálogo de aviso para o usuário
     */
    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Cria um ImageView a partir de um arquivo de imagem
     * Se a imagem não existir ou houver erro no carregamento,
     * retorna um placeholder cinza no lugar.
     */
    private ImageView criarImageView(String caminho, double largura, double altura) {
        try {
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                // Carregar e configurar a imagem
                Image imagem = new Image(arquivo.toURI().toString());
                ImageView imageView = new ImageView(imagem);
                imageView.setFitWidth(largura);
                imageView.setFitHeight(altura);
                imageView.setPreserveRatio(true);  // Manter proporção original
                imageView.setSmooth(true);         // Suavização da imagem
                // Aplicar efeito de sombra
                imageView.setStyle("-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                return imageView;
            } else {
                // Criar placeholder se imagem não existir
                return criarPlaceholder(largura, altura);
            }
        } catch (Exception e) {
            // Criar placeholder em caso de erro
            return criarPlaceholder(largura, altura);
        }
    }
    
    /**
     * Cria um placeholder visual quando a imagem não pode ser carregada
     */
    private ImageView criarPlaceholder(double largura, double altura) {
        Rectangle placeholder = new Rectangle(largura, altura);
        placeholder.setFill(Color.LIGHTGRAY);
        placeholder.setStroke(Color.DARKGRAY);
        placeholder.setStrokeWidth(2);
        ImageView placeholderView = new ImageView();
        placeholderView.setFitWidth(largura);
        placeholderView.setFitHeight(altura);
        return placeholderView;
    }
}
