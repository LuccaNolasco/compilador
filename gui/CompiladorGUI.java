package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CompiladorGUI extends Application {
    
    private Stage primaryStage;
    private Stage lexicalStage; // Referência para a janela do analisador léxico
    
    private TelaInicial telaInicial;
    private TelaAnalisadorLexico telaAnalisadorLexico;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Compilador - Analisador Léxico, Sintático e Semântico");
        primaryStage.setResizable(false);
        
        // Inicializar as telas
        telaInicial = new TelaInicial(primaryStage, this);
        telaAnalisadorLexico = new TelaAnalisadorLexico(primaryStage, this);
        
        // Criar a tela principal
        Scene mainScene = telaInicial.criarCena();
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    
    public void abrirAnalisadorLexico() {
        Scene lexicalScene = telaAnalisadorLexico.criarCena();
        
        // Fechar a janela principal
        primaryStage.close();
        
        // Abrir nova janela do analisador léxico
        lexicalStage = new Stage();
        lexicalStage.setTitle("Analisador Léxico");
        lexicalStage.setScene(lexicalScene);
        lexicalStage.setResizable(false);
        
        // Configurar o que acontece quando a janela é fechada
        lexicalStage.setOnCloseRequest(e -> {
            // Quando fechar o analisador léxico, fechar a aplicação
            System.exit(0);
        });
        
        lexicalStage.show();
    }
    
    public void voltarTelaInicial() {
        // Fechar a janela do analisador léxico
        if (lexicalStage != null) {
            lexicalStage.close();
        }
        
        // Recriar e mostrar a tela principal
        Scene mainScene = telaInicial.criarCena();
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}