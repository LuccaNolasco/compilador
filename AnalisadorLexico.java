import java.util.HashMap;
import java.util.Map;

public class AnalisadorLexico {

  private String codigoFonte;
  private int posicaoAtual;


    private static final Map<String, TipoToken> PALAVRAS_RESERVADAS = new HashMap<>();

    private static final Map<Character, Token> SIMBOLOS_SIMPLES = new HashMap<>();


   static {

        PALAVRAS_RESERVADAS.put("absolute", TipoToken.ABSOLUTE);
        PALAVRAS_RESERVADAS.put("array", TipoToken.ARRAY);
        PALAVRAS_RESERVADAS.put("begin", TipoToken.BEGIN);
        PALAVRAS_RESERVADAS.put("case", TipoToken.CASE);
        PALAVRAS_RESERVADAS.put("char", TipoToken.CHAR);
        PALAVRAS_RESERVADAS.put("const", TipoToken.CONST);
        PALAVRAS_RESERVADAS.put("div", TipoToken.DIV);
        PALAVRAS_RESERVADAS.put("do", TipoToken.DO);
        PALAVRAS_RESERVADAS.put("dowto", TipoToken.DOWTO);
        PALAVRAS_RESERVADAS.put("else", TipoToken.ELSE);
        PALAVRAS_RESERVADAS.put("end", TipoToken.END);
        PALAVRAS_RESERVADAS.put("external", TipoToken.EXTERNAL);
        PALAVRAS_RESERVADAS.put("file", TipoToken.FILE);
        PALAVRAS_RESERVADAS.put("for", TipoToken.FOR);
        PALAVRAS_RESERVADAS.put("forward", TipoToken.FORWARD);
        PALAVRAS_RESERVADAS.put("func", TipoToken.FUNC);
        PALAVRAS_RESERVADAS.put("function", TipoToken.FUNCTION);
        PALAVRAS_RESERVADAS.put("goto", TipoToken.GOTO);
        PALAVRAS_RESERVADAS.put("if", TipoToken.IF);
        PALAVRAS_RESERVADAS.put("implementation", TipoToken.IMPLEMENTATION);
        PALAVRAS_RESERVADAS.put("integer", TipoToken.INTEGER);
        PALAVRAS_RESERVADAS.put("interface", TipoToken.INTERFACE);
        PALAVRAS_RESERVADAS.put("interrupt", TipoToken.INTERRUPT);
        PALAVRAS_RESERVADAS.put("label", TipoToken.LABEL);
        PALAVRAS_RESERVADAS.put("main", TipoToken.MAIN);
        PALAVRAS_RESERVADAS.put("nil", TipoToken.NIL);
        PALAVRAS_RESERVADAS.put("nit", TipoToken.NIT);
        PALAVRAS_RESERVADAS.put("of", TipoToken.OF);
        PALAVRAS_RESERVADAS.put("packed", TipoToken.PACKED);
        PALAVRAS_RESERVADAS.put("proc", TipoToken.PROC);
        PALAVRAS_RESERVADAS.put("program", TipoToken.PROGRAM);
        PALAVRAS_RESERVADAS.put("real", TipoToken.REAL);
        PALAVRAS_RESERVADAS.put("record", TipoToken.RECORD);
        PALAVRAS_RESERVADAS.put("repeat", TipoToken.REPEAT);
        PALAVRAS_RESERVADAS.put("set", TipoToken.SET);
        PALAVRAS_RESERVADAS.put("shl", TipoToken.SHL);
        PALAVRAS_RESERVADAS.put("shr", TipoToken.SHR);
        PALAVRAS_RESERVADAS.put("string", TipoToken.STRING);
        PALAVRAS_RESERVADAS.put("then", TipoToken.THEN);
        PALAVRAS_RESERVADAS.put("to", TipoToken.TO);
        PALAVRAS_RESERVADAS.put("type", TipoToken.TYPE);
        PALAVRAS_RESERVADAS.put("unit", TipoToken.UNIT);
        PALAVRAS_RESERVADAS.put("until", TipoToken.UNTIL);
        PALAVRAS_RESERVADAS.put("uses", TipoToken.USES);
        PALAVRAS_RESERVADAS.put("var", TipoToken.VAR);
        PALAVRAS_RESERVADAS.put("while", TipoToken.WHILE);
        PALAVRAS_RESERVADAS.put("with", TipoToken.WITH);
        PALAVRAS_RESERVADAS.put("xor", TipoToken.XOR);

        SIMBOLOS_SIMPLES.put('(', new Token(TipoToken.SIMBOLO_ESPECIAL, "("));
        SIMBOLOS_SIMPLES.put(')', new Token(TipoToken.SIMBOLO_ESPECIAL, ")"));
        SIMBOLOS_SIMPLES.put(';', new Token(TipoToken.SIMBOLO_ESPECIAL, ";"));
        SIMBOLOS_SIMPLES.put(',', new Token(TipoToken.SIMBOLO_ESPECIAL, ","));
        SIMBOLOS_SIMPLES.put('.', new Token(TipoToken.SIMBOLO_ESPECIAL, "."));
        SIMBOLOS_SIMPLES.put('+', new Token(TipoToken.OPERADOR_ARITMETICO, "+"));
        SIMBOLOS_SIMPLES.put('-', new Token(TipoToken.OPERADOR_ARITMETICO, "-"));
        SIMBOLOS_SIMPLES.put('*', new Token(TipoToken.OPERADOR_ARITMETICO, "*"));
        SIMBOLOS_SIMPLES.put('/', new Token(TipoToken.OPERADOR_ARITMETICO, "/"));
    }
    public AnalisadorLexico(String codigoFonte) {
        this.codigoFonte = codigoFonte;
        this.posicaoAtual = 0;
    }



  private void ignorarEspacosEmBranco() {
        while (posicaoAtual < codigoFonte.length() && Character.isWhitespace(codigoFonte.charAt(posicaoAtual))) {
            posicaoAtual++;
        }
    }

    // MÉTODO proximoToken ATUALIZADO
    public Token proximoToken() {
        // 1. Ignora qualquer espaço em branco antes do próximo token
        ignorarEspacosEmBranco();

        // 2. Verifica se chegamos ao fim do arquivo depois de ignorar os espaços
        if (posicaoAtual >= codigoFonte.length()) {
            return new Token(TipoToken.FIM_DE_ARQUIVO, "\0");
        }

        // 3. O resto da lógica para reconhecer o token
        char caractereAtual = codigoFonte.charAt(posicaoAtual);

        // Verifica se é um símbolo simples que já mapeamos
        if (SIMBOLOS_SIMPLES.containsKey(caractereAtual)) {
            posicaoAtual++;
            return SIMBOLOS_SIMPLES.get(caractereAtual);
        }
        
        // Se não for um símbolo conhecido, por enquanto, vamos apenas avançar
        // Nas próximas etapas, trataremos identificadores, números, etc. aqui.
        posicaoAtual++;
        return proximoToken();
    }
}
