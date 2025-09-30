package gui;

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

public class TelaInicial {
    
    private Stage primaryStage;
    private CompiladorGUI mainApp;
    
    public TelaInicial(Stage primaryStage, CompiladorGUI mainApp) {
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
    }
    
    public Scene criarCena() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        // Título
        Label titulo = new Label("Σ ★ - O real Sigma da Bahia");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setStyle("-fx-text-fill: #2c3e50;");
        
        // Área para imagem principal
        VBox imagemProfessor = new VBox(10);
        imagemProfessor.setAlignment(Pos.CENTER);
        
        // Imagem principal
        ImageView imagemPrincipal = criarImageView("gui/images/realSigma.png", 300, 300);
        
        imagemProfessor.getChildren().add(imagemPrincipal);
        
        // Container para os botões
        VBox botoesContainer = new VBox(20);
        botoesContainer.setAlignment(Pos.CENTER);
        
        // Botão Analisador Léxico (disponível)
        Button btnLexico = new Button("✓ Analisador Léxico");
        configurarBotao(btnLexico, "#27ae60", true);
        btnLexico.setOnAction(e -> mainApp.abrirAnalisadorLexico());
        
        // Botão Analisador Sintático (bloqueado)
        Button btnSintatico = new Button("✖ Analisador Sintático");
        configurarBotao(btnSintatico, "#95a5a6", false);
        btnSintatico.setOnAction(e -> mostrarAviso("Analisador Sintático ainda não implementado"));
        
        // Botão Analisador Semântico (bloqueado)
        Button btnSemantico = new Button("✖ Analisador Semântico");
        configurarBotao(btnSemantico, "#95a5a6", false);
        btnSemantico.setOnAction(e -> mostrarAviso("Analisador Semântico ainda não implementado"));
        
        botoesContainer.getChildren().addAll(btnLexico, btnSintatico, btnSemantico);
        
        // Área adicional para mais imagens do professor
        HBox imagensAdicionais = new HBox(20);
        imagensAdicionais.setAlignment(Pos.CENTER);
    
        
        
        root.getChildren().addAll(titulo, imagemProfessor, botoesContainer, imagensAdicionais);
        
        return new Scene(root, 600, 720);
    }
    
    private void configurarBotao(Button botao, String cor, boolean ativo) {
        botao.setPrefWidth(400);
        botao.setPrefHeight(70);
        botao.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        if (ativo) {
            botao.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; " +
                          "-fx-background-radius: 10; -fx-cursor: hand;");
            botao.setOnMouseEntered(e -> botao.setStyle("-fx-background-color: #229954; -fx-text-fill: white; " +
                                                       "-fx-background-radius: 10; -fx-cursor: hand;"));
            botao.setOnMouseExited(e -> botao.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; " +
                                                      "-fx-background-radius: 10; -fx-cursor: hand;"));
        } else {
            botao.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: #bdc3c7; " +
                          "-fx-background-radius: 10; -fx-cursor: not-allowed; " +
                          "-fx-opacity: 0.7; -fx-border-color: #7f8c8d; -fx-border-width: 1;");
        }
    }
    
    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private ImageView criarImageView(String caminho, double largura, double altura) {
        try {
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                Image imagem = new Image(arquivo.toURI().toString());
                ImageView imageView = new ImageView(imagem);
                imageView.setFitWidth(largura);
                imageView.setFitHeight(altura);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                return imageView;
            } else {
                // Se a imagem não existir, criar um placeholder
                Rectangle placeholder = new Rectangle(largura, altura);
                placeholder.setFill(Color.LIGHTGRAY);
                placeholder.setStroke(Color.DARKGRAY);
                placeholder.setStrokeWidth(2);
                ImageView placeholderView = new ImageView();
                placeholderView.setFitWidth(largura);
                placeholderView.setFitHeight(altura);
                return placeholderView;
            }
        } catch (Exception e) {
            // Em caso de erro, criar um placeholder
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
}
