package gui;

// Importações do JavaFX para interface gráfica
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
// Importações para configuração de localização
import java.util.Locale;
import java.nio.charset.Charset;

/**
 * Classe principal da interface gráfica do compilador
 * 
 * Esta classe gerencia a aplicação JavaFX e controla a navegação entre
 * as diferentes telas do compilador. É responsável por:
 * - Configurar a localização brasileira
 * - Gerenciar as transições entre telas
 * - Manter referências das janelas ativas
 * 
 */
public class CompiladorGUI extends Application {
    
    // Referências para as janelas da aplicação
    private Stage primaryStage;        // Janela principal
    private Stage lexicalStage;        // Janela do analisador léxico
    private Stage sintaticoStage;     // Janela do analisador sintático
    
    // Instâncias das diferentes telas do compilador
    private TelaInicial telaInicial;                // Tela inicial com menu
    private TelaAnalisadorLexico telaAnalisadorLexico;  // Tela do analisador léxico
    private TelaAnalisadorSintatico telaAnalisadorSintatico;  // Tela do analisador sintático
    
    /**
     * Método principal do JavaFX que inicializa a aplicação
     */
    @Override
    public void start(Stage primaryStage) {
        // Configurar localização brasileira para suporte a acentos
        configurarLocalizacaoBrasileira();
        
        // Configurar a janela principal
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Compilador - Analisador Léxico, Sintático e Semântico");
        primaryStage.setResizable(false);  // Janela com tamanho fixo
        
        // Inicializar as diferentes telas do compilador
        telaInicial = new TelaInicial(primaryStage, this);
        telaAnalisadorLexico = new TelaAnalisadorLexico(primaryStage, this);
        telaAnalisadorSintatico = new TelaAnalisadorSintatico(primaryStage, this);
        
        // Criar e exibir a tela principal (menu inicial)
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
    
    /**
     * Abre a tela do analisador léxico
     * 
     * Este método é chamado quando o usuário clica no botão "Analisador Léxico"
     * na tela inicial. Fecha a janela principal e abre uma nova janela específica
     * para análise léxica.
     */
    public void abrirAnalisadorLexico() {
        // Criar a cena do analisador léxico
        Scene lexicalScene = telaAnalisadorLexico.criarCena();
        
        // Fechar a janela principal (menu inicial)
        primaryStage.close();
        
        // Criar e configurar nova janela para o analisador léxico
        lexicalStage = new Stage();
        lexicalStage.setTitle("Analisador Léxico");
        lexicalStage.setScene(lexicalScene);
        lexicalStage.setResizable(false);  // Janela com tamanho fixo
        
        // Configurar comportamento ao fechar a janela
        lexicalStage.setOnCloseRequest(e -> {
            // Quando fechar o analisador léxico, encerrar a aplicação
            System.exit(0);
        });
        
        // Exibir a janela do analisador léxico
        lexicalStage.show();
    }
    
    /**
     * Abre a tela do analisador sintático
     * 
     * Este método é chamado quando o usuário clica no botão "Analisador Sintático"
     * na tela inicial. Fecha a janela principal e abre uma nova janela específica
     * para análise sintática.
     */
    public void abrirAnalisadorSintatico() {
        // Criar a cena do analisador sintático
        Scene sintaticoScene = telaAnalisadorSintatico.criarCena();
        
        // Fechar a janela principal (menu inicial)
        primaryStage.close();
        
        // Criar e configurar nova janela para o analisador sintático
        sintaticoStage = new Stage();
        sintaticoStage.setTitle("Analisador Sintático");
        sintaticoStage.setScene(sintaticoScene);
        sintaticoStage.setResizable(false);  // Janela com tamanho fixo
        
        // Configurar comportamento ao fechar a janela
        sintaticoStage.setOnCloseRequest(e -> {
            // Quando fechar o analisador sintático, encerrar a aplicação
            System.exit(0);
        });
        
        // Exibir a janela do analisador sintático
        sintaticoStage.show();
    }
    
    /**
     * Retorna para a tela inicial (menu principal)
     * 
     * Este método é chamado quando o usuário clica no botão "Voltar"
     * nas telas dos analisadores. Fecha a janela atual e reabre
     * a janela principal com o menu inicial.
     */
    public void voltarTelaInicial() {
        // Fechar a janela do analisador léxico se estiver aberta
        if (lexicalStage != null) {
            lexicalStage.close();
        }
        
        // Fechar a janela do analisador sintático se estiver aberta
        if (sintaticoStage != null) {
            sintaticoStage.close();
        }
        
        // Recriar e exibir a tela principal (menu inicial)
        Scene mainScene = telaInicial.criarCena();
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    
    /**
     * Método main que inicia a aplicação JavaFX
     * 
     */
    public static void main(String[] args) {
        launch(args);  // Inicia a aplicação JavaFX
    }
}