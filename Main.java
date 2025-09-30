// Importações necessárias para o funcionamento do compilador
import gui.CompiladorGUI;           // Interface gráfica principal do compilador
import gui.TelaAnalisadorLexico;    // Tela específica para análise léxica
import gui.TelaInicial;             // Tela inicial com menu de opções
import lexico.AnalisadorLexico;     // Motor do analisador léxico
import lexico.Token;                // Classe que representa um token
import lexico.TipoToken;            // Enumeração dos tipos de tokens

/**
 * Classe principal do compilador
 * 
 * Esta é a classe de entrada do programa que inicializa a interface gráfica
 * do compilador. O compilador possui três fases principais:
 * 1. Análise Léxica (implementada)
 * 2. Análise Sintática (não implementada)
 * 3. Análise Semântica (não implementada)
 * 
 */
@SuppressWarnings("unused")
public class Main {

    public static void main(String[] args) {
        // Lança a aplicação JavaFX com interface gráfica
        System.out.println("Iniciando Compilador GUI...");
        CompiladorGUI.main(args);
    }
}