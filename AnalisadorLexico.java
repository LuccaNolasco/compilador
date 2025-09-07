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
    ignorarEspacosEmBranco();

    if (posicaoAtual >= codigoFonte.length()) {
        return new Token(TipoToken.FIM_DE_ARQUIVO, "\0");
    }

    char caractereAtual = codigoFonte.charAt(posicaoAtual);

    // Primeiro, trata os tokens que podem ter múltiplos caracteres
    if (Character.isLetter(caractereAtual)) {
        return reconhecerIdentificador();
    }

    if (Character.isDigit(caractereAtual)) {
        return reconhecerNumero();
    }

    // Agora, trata os símbolos. Note que o avanço da posição (posicaoAtual++)
    // agora é feito DENTRO de cada case.
    switch (caractereAtual) {
        case '(':
            posicaoAtual++;
            return new Token(TipoToken.SIMBOLO_ESPECIAL, "(");
        case ')':
            posicaoAtual++;
            return new Token(TipoToken.SIMBOLO_ESPECIAL, ")");
        case ';':
            posicaoAtual++;
            return new Token(TipoToken.SIMBOLO_ESPECIAL, ";");
        case ',':
            posicaoAtual++;
            return new Token(TipoToken.SIMBOLO_ESPECIAL, ",");
        case '.':
            posicaoAtual++;
            return new Token(TipoToken.SIMBOLO_ESPECIAL, ".");
        case '+':
            posicaoAtual++;
            return new Token(TipoToken.OPERADOR_ARITMETICO, "+");
        case '-':
            posicaoAtual++;
            return new Token(TipoToken.OPERADOR_ARITMETICO, "-");
        case '*':
            posicaoAtual++;
            return new Token(TipoToken.OPERADOR_ARITMETICO, "*");
        case '/':
            posicaoAtual++;
            // Na próxima etapa, vamos adicionar a lógica para comentários aqui (/*)
            return new Token(TipoToken.OPERADOR_ARITMETICO, "/");
        
        case ':':
            if (peek() == '=') {
                posicaoAtual += 2; // Avança 2 posições para consumir ':='
                return new Token(TipoToken.ATRIBUICAO, ":=");
            } else {
                posicaoAtual++; // Avança 1 posição para consumir ':'
                return new Token(TipoToken.SIMBOLO_ESPECIAL, ":");
            }

        case '<':
            if (peek() == '=') {
                posicaoAtual += 2;
                return new Token(TipoToken.OPERADOR_RELACIONAL, "<=");
            } else if (peek() == '>') {
                posicaoAtual += 2;
                return new Token(TipoToken.OPERADOR_RELACIONAL, "<>");
            } else {
                posicaoAtual++;
                return new Token(TipoToken.OPERADOR_RELACIONAL, "<");
            }
        
        case '>':
            if (peek() == '=') {
                posicaoAtual += 2;
                return new Token(TipoToken.OPERADOR_RELACIONAL, ">=");
            } else {
                posicaoAtual++;
                return new Token(TipoToken.OPERADOR_RELACIONAL, ">");
            }

        case '=':
            posicaoAtual++;
            return new Token(TipoToken.OPERADOR_RELACIONAL, "=");

        default:
            System.err.println("Caractere desconhecido: " + caractereAtual);
            posicaoAtual++;
            return proximoToken();
    }
}

       private Token reconhecerIdentificador() {
        int posicaoInicial = posicaoAtual;
        // Avança enquanto for letra ou dígito
        while (posicaoAtual < codigoFonte.length() && 
               Character.isLetterOrDigit(codigoFonte.charAt(posicaoAtual))) {
            posicaoAtual++;
        }
        
        // Extrai o lexema do código fonte
        String lexema = codigoFonte.substring(posicaoInicial, posicaoAtual);
        
        // Verifica se é uma palavra reservada ou um identificador comum
        TipoToken tipo = PALAVRAS_RESERVADAS.getOrDefault(lexema.toLowerCase(), TipoToken.IDENTIFICADOR);
        
        return new Token(tipo, lexema);
    }

     private Token reconhecerNumero() {
        int posicaoInicial = posicaoAtual;
        boolean ehReal = false;

        // Consome a parte inteira do número
        while (posicaoAtual < codigoFonte.length() && 
               Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
            posicaoAtual++;
        }

        // Verifica se há uma parte fracionária (tornando o número real)
        if (posicaoAtual < codigoFonte.length() && codigoFonte.charAt(posicaoAtual) == '.') {
            ehReal = true;
            posicaoAtual++; // Consome o '.'

            // Consome a parte fracionária
            while (posicaoAtual < codigoFonte.length() && 
                   Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
                posicaoAtual++;
            }
        }
        
        // Verifica a notação científica (ex: 1.23e-04), que também o torna real
        if (posicaoAtual < codigoFonte.length() && 
            (codigoFonte.charAt(posicaoAtual) == 'e' || codigoFonte.charAt(posicaoAtual) == 'E')) {
            ehReal = true;
            posicaoAtual++; // Consome o 'e' ou 'E'

            // Verifica se há um sinal de '+' ou '-' no expoente
            if (posicaoAtual < codigoFonte.length() && 
                (codigoFonte.charAt(posicaoAtual) == '+' || codigoFonte.charAt(posicaoAtual) == '-')) {
                posicaoAtual++; // Consome o sinal
            }

            // Consome os dígitos do expoente
            while (posicaoAtual < codigoFonte.length() && 
                   Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
                posicaoAtual++;
            }
        }

        String lexema = codigoFonte.substring(posicaoInicial, posicaoAtual);
        TipoToken tipo = ehReal ? TipoToken.NUMERO_REAL : TipoToken.NUMERO_INTEIRO;

        return new Token(tipo, lexema);
    }

      private char peek() {
        if (posicaoAtual + 1 >= codigoFonte.length()) {
            return '\0'; // Retorna nulo se estivermos no fim
        }
        return codigoFonte.charAt(posicaoAtual + 1);
    }
}
