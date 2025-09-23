import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import lexico.AnalisadorLexico;
import lexico.TipoToken;
import lexico.Token;

public class TesteComFimDeArquivo {

    public static void main(String[] args) {
        String nomeArquivo = "arquivo.txt";

        try {
            String codigoFonte = new String(Files.readAllBytes(Paths.get(nomeArquivo)));
            
            AnalisadorLexico analisador = new AnalisadorLexico(codigoFonte);
            
            // Ativando a exibição do token FIM_DE_ARQUIVO
            analisador.setExibirFimDeArquivo(true);
            
            System.out.println("=== Teste com FIM_DE_ARQUIVO ativado ===");
            
            Token token;
            do {
                token = analisador.proximoToken();
                if (token != null) {
                    System.out.println(token);
                }
            } while (token != null && token.tipo != TipoToken.FIM_DE_ARQUIVO);

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}