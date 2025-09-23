import gui.CompiladorGUI;
import gui.TelaAnalisadorLexico;
import gui.TelaInicial;
import lexico.AnalisadorLexico;
import lexico.Token;
import lexico.TipoToken;

@SuppressWarnings("unused")
public class Main {

    public static void main(String[] args) {
        // Lança a aplicação JavaFX
        System.out.println("Iniciando Compilador GUI...");
        CompiladorGUI.main(args);
    }
}