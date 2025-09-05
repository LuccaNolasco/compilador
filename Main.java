import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        // O nome do arquivo de entrada
        String nomeArquivo = "arquivo.txt";

        try {
       
            String codigoFonte = new String(Files.readAllBytes(Paths.get(nomeArquivo)));
        
            AnalisadorLexico analisador = new AnalisadorLexico(codigoFonte);

            // Pede tokens ao analisador um por um, até encontrar o fim do arquivo
            Token token;
            do {
                token = analisador.proximoToken();
                // Imprime o token no formato <lexema, TIPO>
                System.out.println(token);
            } while (token.tipo != TipoToken.FIM_DE_ARQUIVO);

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}