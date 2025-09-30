package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.nio.charset.Charset;

public class CompiladorGUI extends Application {
    
    private Stage primaryStage;
    private Stage lexicalStage; // Referência para a janela do analisador léxico
    
    private TelaInicial telaInicial;
    private TelaAnalisadorLexico telaAnalisadorLexico;
    
    @Override
    public void start(Stage primaryStage) {
        // Configurar localização brasileira
        configurarLocalizacaoBrasileira();
        
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
    
    /**
     * Configura a localização para o padrão brasileiro
     */
    private void configurarLocalizacaoBrasileira() {
        // Define o locale padrão para português brasileiro
        Locale localeBrasil = new Locale("pt", "BR");
        Locale.setDefault(localeBrasil);
        
        // Configura propriedades do sistema para layout de teclado brasileiro
        System.setProperty("user.language", "pt");
        System.setProperty("user.country", "BR");
        System.setProperty("user.region", "BR");
        System.setProperty("user.timezone", "America/Sao_Paulo");
        
        // Configurações específicas para JavaFX e encoding
        System.setProperty("javafx.platform.locale", "pt-BR");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("java.awt.im.style", "below-the-spot");
        
        // Configurações específicas para input method brasileiro
        System.setProperty("java.awt.im.style", "on-the-spot");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Garantir que o charset padrão seja UTF-8
        System.setProperty("native.encoding", "UTF-8");
        
        // Debug: mostra a configuração atual
        System.out.println("Localização configurada: " + Locale.getDefault());
        System.out.println("País: " + Locale.getDefault().getCountry());
        System.out.println("Idioma: " + Locale.getDefault().getLanguage());
        System.out.println("Charset padrão: " + Charset.defaultCharset());
        System.out.println("Encoding de arquivo: " + System.getProperty("file.encoding"));
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