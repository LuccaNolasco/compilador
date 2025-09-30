import lexico.AnalisadorLexico;
import lexico.Token;

public class TesteNumeroReal {
    public static void main(String[] args) {
        // Teste com números inteiros seguidos de ponto e números reais
        String codigo = "12. 12.5 123. 123.456 42.0 7. 3.14159";
        
        AnalisadorLexico analisador = new AnalisadorLexico(codigo);
        
        System.out.println("Testando reconhecimento de números reais vs inteiros com ponto:");
        System.out.println("Código: " + codigo);
        System.out.println("\nTokens encontrados:");
        
        Token token;
        while ((token = analisador.proximoToken()) != null) {
            System.out.println(token);
        }
        
        System.out.println("\nTeste concluído!");
        System.out.println("Esperado:");
        System.out.println("- '12.' deve ser: <12, NUMERO_INTEIRO> seguido de <., SIMBOLO_ESPECIAL>");
        System.out.println("- '12.5' deve ser: <12.5, NUMERO_REAL>");
    }
}