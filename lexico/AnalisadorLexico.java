package lexico;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class AnalisadorLexico {

  private String codigoFonte;
  private int posicaoAtual;
  private Token tokenAnterior; // Para rastrear o token anterior
  
  // Controle para exibir ou não o token FIM_DE_ARQUIVO
  private boolean exibirFimDeArquivo = false; // Desativado por padrão
  
  // Lista para armazenar caracteres não identificados
  private List<Character> caracteresNaoIdentificados = new ArrayList<>();

    private static final Map<String, TipoToken> PALAVRAS_RESERVADAS = new HashMap<>();

    private static final Map<Character, Token> SIMBOLOS_SIMPLES = new HashMap<>();


   static {

        PALAVRAS_RESERVADAS.put("absolute", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("array", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("begin", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("case", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("char", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("const", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("div", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("do", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("dowto", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("else", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("end", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("external", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("file", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("for", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("forward", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("func", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("function", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("goto", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("if", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("implementation", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("integer", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("interface", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("interrupt", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("label", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("main", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("nil", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("nit", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("of", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("packed", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("proc", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("program", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("real", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("record", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("repeat", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("set", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("shl", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("shr", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("string", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("then", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("to", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("type", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("unit", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("until", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("uses", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("var", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("while", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("with", TipoToken.PALAVRA_RESERVADA);
        PALAVRAS_RESERVADAS.put("xor", TipoToken.PALAVRA_RESERVADA);

        SIMBOLOS_SIMPLES.put('(', new Token(TipoToken.SIMBOLO_ESPECIAL, "("));
        SIMBOLOS_SIMPLES.put(')', new Token(TipoToken.SIMBOLO_ESPECIAL, ")"));
        SIMBOLOS_SIMPLES.put(';', new Token(TipoToken.SIMBOLO_ESPECIAL, ";"));
        SIMBOLOS_SIMPLES.put(',', new Token(TipoToken.SIMBOLO_ESPECIAL, ","));
        SIMBOLOS_SIMPLES.put('.', new Token(TipoToken.SIMBOLO_ESPECIAL, "."));
        SIMBOLOS_SIMPLES.put(':', new Token(TipoToken.SIMBOLO_ESPECIAL, ":"));
        SIMBOLOS_SIMPLES.put('+', new Token(TipoToken.OPERADOR_ARITMETICO, "+"));
        SIMBOLOS_SIMPLES.put('-', new Token(TipoToken.OPERADOR_ARITMETICO, "-"));
        SIMBOLOS_SIMPLES.put('*', new Token(TipoToken.OPERADOR_ARITMETICO, "*"));
        SIMBOLOS_SIMPLES.put('/', new Token(TipoToken.OPERADOR_ARITMETICO, "/"));
    }
    public AnalisadorLexico(String codigoFonte) {
        this.codigoFonte = codigoFonte;
        this.posicaoAtual = 0;
        this.tokenAnterior = null; // Inicializa como null
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
        // Não retorna mais token FIM_DE_ARQUIVO, apenas null para indicar fim
        return null;
    }

    char caractereAtual = codigoFonte.charAt(posicaoAtual);

    // Primeiro, trata os tokens que podem ter múltiplos caracteres
    if (Character.isLetter(caractereAtual)) {
        Token token = reconhecerIdentificador();
        tokenAnterior = token;
        return token;
    }

    if (Character.isDigit(caractereAtual)) {
        Token token = reconhecerNumero();
        tokenAnterior = token;
        return token;
    }

    // Agora, trata os símbolos. Note que o avanço da posição (posicaoAtual++)
    // agora é feito DENTRO de cada case.
    switch (caractereAtual) {
        case '(':
            posicaoAtual++;
            Token token1 = new Token(TipoToken.SIMBOLO_ESPECIAL, "(");
            tokenAnterior = token1;
            return token1;
        case ')':
            posicaoAtual++;
            Token token2 = new Token(TipoToken.SIMBOLO_ESPECIAL, ")");
            tokenAnterior = token2;
            return token2;
        case ';':
            posicaoAtual++;
            Token token3 = new Token(TipoToken.SIMBOLO_ESPECIAL, ";");
            tokenAnterior = token3;
            return token3;
        case ',':
            posicaoAtual++;
            Token token4 = new Token(TipoToken.SIMBOLO_ESPECIAL, ",");
            tokenAnterior = token4;
            return token4;
        case '.':
            // Verifica se o token anterior é 'end' (PALAVRA_RESERVADA)
            if (tokenAnterior != null && tokenAnterior.tipo == TipoToken.PALAVRA_RESERVADA && tokenAnterior.lexema.equals("end")) {
                posicaoAtual++;
                Token tokenFim = new Token(TipoToken.FIM, ".");
                tokenAnterior = tokenFim;
                return tokenFim;
            } else {
                posicaoAtual++;
                Token token5 = new Token(TipoToken.SIMBOLO_ESPECIAL, ".");
                tokenAnterior = token5;
                return token5;
            }
        case '+':
            posicaoAtual++;
            Token token6 = new Token(TipoToken.OPERADOR_ARITMETICO, "+");
            tokenAnterior = token6;
            return token6;
        case '-':
            // Regra: Se "-" for precedido de um número ou identificador, é operador aritmético
            if (tokenAnterior != null && 
                (tokenAnterior.tipo == TipoToken.NUMERO_INTEIRO || 
                 tokenAnterior.tipo == TipoToken.NUMERO_REAL ||
                 tokenAnterior.tipo == TipoToken.IDENTIFICADOR)) {
                // Precedido de número ou identificador -> operador aritmético
                posicaoAtual++;
                Token token = new Token(TipoToken.OPERADOR_ARITMETICO, "-");
                tokenAnterior = token;
                return token;
            } else {
                // Não precedido de número ou identificador -> verifica se o próximo é dígito
                if (posicaoAtual + 1 < codigoFonte.length() && 
                    Character.isDigit(codigoFonte.charAt(posicaoAtual + 1))) {
                    // Próximo é dígito -> número negativo
                    Token numeroNegativo = reconhecerNumero();
                    tokenAnterior = numeroNegativo;
                    return numeroNegativo;
                } else {
                    // Próximo não é dígito -> operador aritmético
                    posicaoAtual++;
                    Token token = new Token(TipoToken.OPERADOR_ARITMETICO, "-");
                    tokenAnterior = token;
                    return token;
                }
            }
        case '*':
            posicaoAtual++;
            Token token7 = new Token(TipoToken.OPERADOR_ARITMETICO, "*");
            tokenAnterior = token7;
            return token7;
        case '/':
            // Verifica se é início de comentário /*
            if (posicaoAtual + 1 < codigoFonte.length() && 
                codigoFonte.charAt(posicaoAtual + 1) == '*') {
                // É um comentário, ignora tudo até encontrar */
                ignorarComentario();
                // Após ignorar o comentário, continua analisando o próximo token
                return proximoToken();
            } else {
                // É apenas um operador de divisão
                posicaoAtual++;
                Token token8 = new Token(TipoToken.OPERADOR_ARITMETICO, "/");
                tokenAnterior = token8;
                return token8;
            }
        
        case ':':
            if (peek() == '=') {
                posicaoAtual += 2; // Avança 2 posições para consumir ':='
                Token token9 = new Token(TipoToken.ATRIBUICAO, ":=");
                tokenAnterior = token9;
                return token9;
            } else {
                posicaoAtual++; // Avança 1 posição para consumir ':'
                Token token10 = new Token(TipoToken.SIMBOLO_ESPECIAL, ":");
                tokenAnterior = token10;
                return token10;
            }

        case '<':
            if (peek() == '=') {
                posicaoAtual += 2;
                Token token11 = new Token(TipoToken.OPERADOR_RELACIONAL, "<=");
                tokenAnterior = token11;
                return token11;
            } else if (peek() == '>') {
                posicaoAtual += 2;
                Token token12 = new Token(TipoToken.OPERADOR_RELACIONAL, "<>");
                tokenAnterior = token12;
                return token12;
            } else {
                posicaoAtual++;
                Token token13 = new Token(TipoToken.OPERADOR_RELACIONAL, "<");
                tokenAnterior = token13;
                return token13;
            }
        
        case '>':
            if (peek() == '=') {
                posicaoAtual += 2;
                Token token14 = new Token(TipoToken.OPERADOR_RELACIONAL, ">=");
                tokenAnterior = token14;
                return token14;
            } else {
                posicaoAtual++;
                Token token15 = new Token(TipoToken.OPERADOR_RELACIONAL, ">");
                tokenAnterior = token15;
                return token15;
            }

        case '=':
            posicaoAtual++;
            Token token16 = new Token(TipoToken.OPERADOR_RELACIONAL, "=");
            tokenAnterior = token16;
            return token16;

        case '\'':
            // Reconhece caracteres delimitados por aspas simples
            Token tokenChar = reconhecerChar();
            tokenAnterior = tokenChar;
            return tokenChar;

        case '"':
            // Reconhece strings delimitadas por aspas duplas
            Token tokenString = reconhecerString();
            tokenAnterior = tokenString;
            return tokenString;

        default:
            // Adiciona o caractere não identificado à lista
            caracteresNaoIdentificados.add(caractereAtual);
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

        // Verifica se há um sinal negativo no início
        if (posicaoAtual < codigoFonte.length() && codigoFonte.charAt(posicaoAtual) == '-') {
            posicaoAtual++; // Consome o sinal negativo
        }

        // Consome a parte inteira do número
        while (posicaoAtual < codigoFonte.length() && 
               Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
            posicaoAtual++;
        }

        // Verifica se há uma parte fracionária (tornando o número real)
        if (posicaoAtual < codigoFonte.length() && codigoFonte.charAt(posicaoAtual) == '.') {
            // Verifica se há pelo menos um dígito após o ponto
            if (posicaoAtual + 1 < codigoFonte.length() && 
                Character.isDigit(codigoFonte.charAt(posicaoAtual + 1))) {
                ehReal = true;
                posicaoAtual++; // Consome o '.'

                // Consome a parte fracionária
                while (posicaoAtual < codigoFonte.length() && 
                       Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
                    posicaoAtual++;
                }
            }
            // Se não há dígito após o ponto, não consome o ponto (será tratado como símbolo separado)
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

    // Método para ativar/desativar a exibição do token FIM_DE_ARQUIVO
    public void setExibirFimDeArquivo(boolean exibir) {
        this.exibirFimDeArquivo = exibir;
    }
    
    // Método para verificar se está exibindo FIM_DE_ARQUIVO
    public boolean isExibindoFimDeArquivo() {
        return this.exibirFimDeArquivo;
    }

    // Método para verificar se o token anterior é um número ou identificador
    private boolean tokenAnteriorEhNumeroOuIdentificador() {
        return tokenAnterior != null && 
               (tokenAnterior.tipo == TipoToken.NUMERO_INTEIRO || 
                tokenAnterior.tipo == TipoToken.NUMERO_REAL || 
                tokenAnterior.tipo == TipoToken.IDENTIFICADOR);
    }
    
    // Método para ignorar comentários /* */
    private void ignorarComentario() {
        // Avança além do /*
        posicaoAtual += 2;
        
        // Procura pelo fim do comentário */
        while (posicaoAtual + 1 < codigoFonte.length()) {
            if (codigoFonte.charAt(posicaoAtual) == '*' && 
                codigoFonte.charAt(posicaoAtual + 1) == '/') {
                // Encontrou o fim do comentário, avança além do */
                posicaoAtual += 2;
                return;
            }
            posicaoAtual++;
        }
        
        // Se chegou aqui, o comentário não foi fechado (erro, mas vamos ignorar até o fim)
        posicaoAtual = codigoFonte.length();
    }
    
    // Método para obter os caracteres não identificados
    public List<Character> getCaracteresNaoIdentificados() {
        return new ArrayList<>(caracteresNaoIdentificados);
    }
    
    // Método para reconhecer caracteres delimitados por aspas simples
    private Token reconhecerChar() {
        int posicaoInicial = posicaoAtual;
        posicaoAtual++; // Consome a aspa simples inicial
        
        // Verifica se há pelo menos um caractere e uma aspa de fechamento
        if (posicaoAtual >= codigoFonte.length()) {
            System.err.println("Erro: Caractere não fechado");
            return new Token(TipoToken.CHAR_LITERAL, codigoFonte.substring(posicaoInicial));
        }
        
        // Consome o caractere (pode ser qualquer caractere, incluindo espaços)
        char caractere = codigoFonte.charAt(posicaoAtual);
        posicaoAtual++;
        
        // Verifica se há a aspa de fechamento
        if (posicaoAtual >= codigoFonte.length() || codigoFonte.charAt(posicaoAtual) != '\'') {
            System.err.println("Erro: Caractere não fechado");
            return new Token(TipoToken.CHAR_LITERAL, codigoFonte.substring(posicaoInicial, posicaoAtual));
        }
        
        posicaoAtual++; // Consome a aspa simples final
        
        // Retorna apenas o conteúdo do caractere, sem as aspas
        return new Token(TipoToken.CHAR_LITERAL, String.valueOf(caractere));
    }
    
    // Método para reconhecer strings delimitadas por aspas duplas
    private Token reconhecerString() {
        int posicaoInicial = posicaoAtual;
        posicaoAtual++; // Consome a aspa dupla inicial
        
        StringBuilder conteudoString = new StringBuilder();
        
        // Consome caracteres até encontrar a aspa dupla de fechamento
        while (posicaoAtual < codigoFonte.length() && codigoFonte.charAt(posicaoAtual) != '"') {
            conteudoString.append(codigoFonte.charAt(posicaoAtual));
            posicaoAtual++;
        }
        
        // Verifica se encontrou a aspa de fechamento
        if (posicaoAtual >= codigoFonte.length()) {
            System.err.println("Erro: String não fechada");
            return new Token(TipoToken.STRING_LITERAL, conteudoString.toString());
        }
        
        posicaoAtual++; // Consome a aspa dupla final
        
        // Retorna apenas o conteúdo da string, sem as aspas
        return new Token(TipoToken.STRING_LITERAL, conteudoString.toString());
    }
    
    // Método para limpar a lista de caracteres não identificados
    public void limparCaracteresNaoIdentificados() {
        caracteresNaoIdentificados.clear();
    }
}
