import lexico.AnalisadorLexico;
import lexico.Token;

public class TesteCharString {
    public static void main(String[] args) {
        // Teste com chars e strings
        String codigo = "'a' 'Z' '1' ' ' \"oi, como vai?\" \"hello world\" \"123\" \"\"";
        
        AnalisadorLexico analisador = new AnalisadorLexico(codigo);
        
        System.out.println("Testando reconhecimento de chars e strings:");
        System.out.println("Código: " + codigo);
        System.out.println("\nTokens encontrados:");
        
        Token token;
        while ((token = analisador.proximoToken()) != null) {
            System.out.println(token);
        }
        
        System.out.println("\nTeste concluído!");
    }
}