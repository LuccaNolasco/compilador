package lexico;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Léxico para linguagem Pascal
 * 
 * Esta classe implementa a primeira fase de um compilador: a análise léxica.
 * Sua função é transformar uma sequência de caracteres (código fonte) em
 * uma sequência de tokens (unidades léxicas significativas).
 * 
 * Funcionalidades principais:
 * - Reconhecimento de palavras reservadas
 * - Identificação de identificadores
 * - Processamento de números (inteiros e reais)
 * - Reconhecimento de operadores e símbolos especiais
 * - Tratamento de comentários
 * - Processamento de literais (caracteres e strings)
 * - Detecção de caracteres não reconhecidos
 * 
 */
public class AnalisadorLexico {

    // === ATRIBUTOS DE CONTROLE ===
    private String codigoFonte;                 // Código fonte a ser analisado
    private int posicaoAtual;                   // Posição atual no código fonte
    private Token tokenAnterior;                // Token anterior (para contexto)
    
    // Controle para exibir ou não o token FIM_DE_ARQUIVO
    private boolean exibirFimDeArquivo = false; // Desativado por padrão
    
    // Lista para armazenar caracteres não identificados
    private List<Character> caracteresNaoIdentificados = new ArrayList<>();

    // === TABELAS DE RECONHECIMENTO ===
    
    // Mapa das palavras reservadas da linguagem Pascal
    private static final Map<String, TipoToken> PALAVRAS_RESERVADAS = new HashMap<>();

    // Mapa dos símbolos simples (um caractere)
    private static final Map<Character, Token> SIMBOLOS_SIMPLES = new HashMap<>();


    // === INICIALIZAÇÃO ESTÁTICA DAS TABELAS ===
    static {
        // Inicializar mapa de palavras reservadas
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

        // Inicializar mapa de símbolos simples
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
    
    // === CONSTRUTOR ===
    
    /**
     * Construtor do analisador léxico
     */
    public AnalisadorLexico(String codigoFonte) {
        this.codigoFonte = codigoFonte;
        this.posicaoAtual = 0;
        this.tokenAnterior = null; // Inicializa sem token anterior
    }

    // === MÉTODOS PRINCIPAIS ===
    
    /**
     * Ignora espaços em branco, tabs e quebras de linha
     * 
     * Avança a posição atual até encontrar um caractere não-branco
     */
    private void ignorarEspacosEmBranco() {
        while (posicaoAtual < codigoFonte.length() && Character.isWhitespace(codigoFonte.charAt(posicaoAtual))) {
            posicaoAtual++;
        }
    }

    /**
     * Método principal para obter o próximo token do código fonte
     * 
     * Este é o método mais importante do analisador léxico. Ele:
     * 1. Ignora espaços em branco
     * 2. Verifica se chegou ao fim do código
     * 3. Identifica o tipo de token baseado no primeiro caractere
     * 4. Chama o método apropriado para processar cada tipo
     * 5. Atualiza o token anterior para contexto
     * 
     */
    public Token proximoToken() {
        // Pular espaços em branco
        ignorarEspacosEmBranco();

        // Verificar se chegou ao fim do código fonte
        if (posicaoAtual >= codigoFonte.length()) {
            return null; // Fim da análise
        }

        char caractereAtual = codigoFonte.charAt(posicaoAtual);

        // === RECONHECIMENTO POR TIPO DE CARACTERE ===
        
        // Identificadores e palavras reservadas (começam com letra)
        if (Character.isLetter(caractereAtual)) {
            Token token = reconhecerIdentificador();
            tokenAnterior = token;
            return token;
        }

        // Números (começam com dígito)
        if (Character.isDigit(caractereAtual)) {
            Token token = reconhecerNumero();
            tokenAnterior = token;
            return token;
        }

        // === SÍMBOLOS E OPERADORES ===
        // Cada case avança a posição e retorna o token apropriado
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
            // REGRA ESPECIAL: Se o token anterior é 'end', então '.' é fim de programa
            if (tokenAnterior != null && tokenAnterior.tipo == TipoToken.PALAVRA_RESERVADA && tokenAnterior.lexema.equals("end")) {
                posicaoAtual++;
                Token tokenFim = new Token(TipoToken.FIM, ".");
                tokenAnterior = tokenFim;
                return tokenFim;
            } else {
                // Caso contrário, é apenas um ponto normal
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
            // REGRA COMPLEXA: O '-' pode ser operador ou parte de número negativo
            if (tokenAnterior != null && 
                (tokenAnterior.tipo == TipoToken.NUMERO_INTEIRO || 
                 tokenAnterior.tipo == TipoToken.NUMERO_REAL ||
                 tokenAnterior.tipo == TipoToken.IDENTIFICADOR)) {
                // Precedido de número ou identificador -> é operador de subtração
                posicaoAtual++;
                Token token = new Token(TipoToken.OPERADOR_ARITMETICO, "-");
                tokenAnterior = token;
                return token;
            } else {
                // Não precedido de número/identificador -> verificar se próximo é dígito
                if (posicaoAtual + 1 < codigoFonte.length() && 
                    Character.isDigit(codigoFonte.charAt(posicaoAtual + 1))) {
                    // Próximo é dígito -> é número negativo
                    Token numeroNegativo = reconhecerNumero();
                    tokenAnterior = numeroNegativo;
                    return numeroNegativo;
                } else {
                    // Próximo não é dígito -> é operador de subtração
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
            // REGRA ESPECIAL: Verificar se é comentário /* ou operador de divisão
            if (posicaoAtual + 1 < codigoFonte.length() && 
                codigoFonte.charAt(posicaoAtual + 1) == '*') {
                // É início de comentário /* ... */
                ignorarComentario();
                // Após ignorar o comentário, continuar análise
                return proximoToken();
            } else {
                // É apenas operador de divisão
                posicaoAtual++;
                Token token8 = new Token(TipoToken.OPERADOR_ARITMETICO, "/");
                tokenAnterior = token8;
                return token8;
            }
        
        case ':':
            // Verificar se é atribuição ':=' ou apenas dois pontos ':'
            if (peek() == '=') {
                posicaoAtual += 2; // Consumir ':='
                Token token9 = new Token(TipoToken.ATRIBUICAO, ":=");
                tokenAnterior = token9;
                return token9;
            } else {
                posicaoAtual++; // Consumir apenas ':'
                Token token10 = new Token(TipoToken.SIMBOLO_ESPECIAL, ":");
                tokenAnterior = token10;
                return token10;
            }

        case '<':
            // Operadores relacionais: '<', '<=', '<>'
            if (peek() == '=') {
                posicaoAtual += 2; // Consumir '<='
                Token token11 = new Token(TipoToken.OPERADOR_RELACIONAL, "<=");
                tokenAnterior = token11;
                return token11;
            } else if (peek() == '>') {
                posicaoAtual += 2; // Consumir '<>' (diferente)
                Token token12 = new Token(TipoToken.OPERADOR_RELACIONAL, "<>");
                tokenAnterior = token12;
                return token12;
            } else {
                posicaoAtual++; // Consumir '<' (menor que)
                Token token13 = new Token(TipoToken.OPERADOR_RELACIONAL, "<");
                tokenAnterior = token13;
                return token13;
            }
        
        case '>':
            // Operadores relacionais: '>', '>='
            if (peek() == '=') {
                posicaoAtual += 2; // Consumir '>='
                Token token14 = new Token(TipoToken.OPERADOR_RELACIONAL, ">=");
                tokenAnterior = token14;
                return token14;
            } else {
                posicaoAtual++; // Consumir '>' (maior que)
                Token token15 = new Token(TipoToken.OPERADOR_RELACIONAL, ">");
                tokenAnterior = token15;
                return token15;
            }

        case '=':
            // Operador de igualdade
            posicaoAtual++;
            Token token16 = new Token(TipoToken.OPERADOR_RELACIONAL, "=");
            tokenAnterior = token16;
            return token16;

        case '\'':
            // Literais de caractere (aspas simples)
            Token tokenChar = reconhecerChar();
            tokenAnterior = tokenChar;
            return tokenChar;

        case '"':
            // Literais de string (aspas duplas)
            Token tokenString = reconhecerString();
            tokenAnterior = tokenString;
            return tokenString;

        default:
            // Caractere não reconhecido - adicionar à lista de erros
            caracteresNaoIdentificados.add(caractereAtual);
            System.err.println("Caractere desconhecido: " + caractereAtual);
            posicaoAtual++; // Pular o caractere problemático
            return proximoToken(); // Continuar análise
    }
}

    // === MÉTODOS DE RECONHECIMENTO ESPECÍFICOS ===
    
    /**
     * Reconhece identificadores e palavras reservadas
     * 
     * Um identificador começa com letra e pode conter letras e dígitos.
     * Após extrair o lexema, verifica se é palavra reservada.
     * 
     */
    private Token reconhecerIdentificador() {
        int posicaoInicial = posicaoAtual;
        
        // Consumir todos os caracteres alfanuméricos
        while (posicaoAtual < codigoFonte.length() && 
               Character.isLetterOrDigit(codigoFonte.charAt(posicaoAtual))) {
            posicaoAtual++;
        }
        
        // Extrair o lexema completo
        String lexema = codigoFonte.substring(posicaoInicial, posicaoAtual);
        
        // Verificar se é palavra reservada (case-insensitive)
        TipoToken tipo = PALAVRAS_RESERVADAS.getOrDefault(lexema.toLowerCase(), TipoToken.IDENTIFICADOR);
        
        return new Token(tipo, lexema);
    }

    /**
     * Reconhece números inteiros e reais
     * 
     * Suporta:
     * - Números inteiros: 123, -456
     * - Números reais: 12.34, -5.67
     * - Notação científica: 1.23e-4, 2E+5
     * - Números negativos quando apropriado
     * 
     */
    private Token reconhecerNumero() {
        int posicaoInicial = posicaoAtual;
        boolean ehReal = false;

        // Consumir sinal negativo se presente
        if (posicaoAtual < codigoFonte.length() && codigoFonte.charAt(posicaoAtual) == '-') {
            posicaoAtual++; // Consumir o '-'
        }

        // Consumir parte inteira (dígitos obrigatórios)
        while (posicaoAtual < codigoFonte.length() && 
               Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
            posicaoAtual++;
        }

        // Verificar parte fracionária (ponto decimal)
        if (posicaoAtual < codigoFonte.length() && codigoFonte.charAt(posicaoAtual) == '.') {
            // Só consumir o ponto se houver dígito após ele
            if (posicaoAtual + 1 < codigoFonte.length() && 
                Character.isDigit(codigoFonte.charAt(posicaoAtual + 1))) {
                ehReal = true;
                posicaoAtual++; // Consumir o '.'

                // Consumir dígitos da parte fracionária
                while (posicaoAtual < codigoFonte.length() && 
                       Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
                    posicaoAtual++;
                }
            }
            // Se não há dígito após '.', deixar o ponto para próximo token
        }
        
        // Verificar notação científica (e/E)
        if (posicaoAtual < codigoFonte.length() && 
            (codigoFonte.charAt(posicaoAtual) == 'e' || codigoFonte.charAt(posicaoAtual) == 'E')) {
            ehReal = true;
            posicaoAtual++; // Consumir 'e' ou 'E'

            // Consumir sinal do expoente (opcional)
            if (posicaoAtual < codigoFonte.length() && 
                (codigoFonte.charAt(posicaoAtual) == '+' || codigoFonte.charAt(posicaoAtual) == '-')) {
                posicaoAtual++; // Consumir sinal
            }

            // Consumir dígitos do expoente
            while (posicaoAtual < codigoFonte.length() && 
                   Character.isDigit(codigoFonte.charAt(posicaoAtual))) {
                posicaoAtual++;
            }
        }

        // Extrair lexema e determinar tipo
        String lexema = codigoFonte.substring(posicaoInicial, posicaoAtual);
        TipoToken tipo = ehReal ? TipoToken.NUMERO_REAL : TipoToken.NUMERO_INTEIRO;

        return new Token(tipo, lexema);
    }

    // === MÉTODOS UTILITÁRIOS ===
    
    /**
     * Espia o próximo caractere sem consumi-lo
     */
    private char peek() {
        if (posicaoAtual + 1 >= codigoFonte.length()) {
            return '\0'; // Caractere nulo indica fim
        }
        return codigoFonte.charAt(posicaoAtual + 1);
    }

    /**
     * Configura se deve exibir token FIM_DE_ARQUIVO
     */
    public void setExibirFimDeArquivo(boolean exibir) {
        this.exibirFimDeArquivo = exibir;
    }
    
    /**
     * Verifica se está configurado para exibir FIM_DE_ARQUIVO
     */
    public boolean isExibindoFimDeArquivo() {
        return this.exibirFimDeArquivo;
    }

    /**
     * Verifica se o token anterior é um número ou identificador
     * 
     * Usado para determinar contexto em casos ambíguos (como o operador '-')
     */
    private boolean tokenAnteriorEhNumeroOuIdentificador() {
        return tokenAnterior != null && 
               (tokenAnterior.tipo == TipoToken.NUMERO_INTEIRO || 
                tokenAnterior.tipo == TipoToken.NUMERO_REAL || 
                tokenAnterior.tipo == TipoToken.IDENTIFICADOR);
    }
    
    /**
     * Ignora comentarios
     * 
     * Avanca a posicao ate encontrar o fechamento do comentario.
     * Se o comentario nao for fechado, ignora ate o fim do codigo.
     */
    private void ignorarComentario() {
        // Pular o "/*" inicial
        posicaoAtual += 2;
        
        // Procurar pelo fechamento "*/"
        while (posicaoAtual + 1 < codigoFonte.length()) {
            if (codigoFonte.charAt(posicaoAtual) == '*' && 
                codigoFonte.charAt(posicaoAtual + 1) == '/') {
                // Encontrou fechamento - pular o "*/"
                posicaoAtual += 2;
                return;
            }
            posicaoAtual++;
        }
        
        // Comentario nao foi fechado - ir ate o fim (erro tolerado)
        posicaoAtual = codigoFonte.length();
    }
    
    /**
     * Retorna lista dos caracteres nao identificados durante a analise
     */
    public List<Character> getCaracteresNaoIdentificados() {
        return new ArrayList<>(caracteresNaoIdentificados);
    }
    
    /**
     * Reconhece literais de caractere delimitados por aspas simples
     * 
     * Formato: 'c' onde c é qualquer caractere
     * Retorna apenas o caractere, sem as aspas.
     * 
     * @return Token do tipo CHAR_LITERAL
     */
    private Token reconhecerChar() {
        int posicaoInicial = posicaoAtual;
        posicaoAtual++; // Consumir aspa simples inicial '
        
        // Verificar se há caractere suficiente
        if (posicaoAtual >= codigoFonte.length()) {
            System.err.println("Erro: Caractere não fechado");
            return new Token(TipoToken.CHAR_LITERAL, codigoFonte.substring(posicaoInicial));
        }
        
        // Consumir o caractere (qualquer um, incluindo espaços)
        char caractere = codigoFonte.charAt(posicaoAtual);
        posicaoAtual++;
        
        // Verificar aspa de fechamento
        if (posicaoAtual >= codigoFonte.length() || codigoFonte.charAt(posicaoAtual) != '\'') {
            System.err.println("Erro: Caractere não fechado");
            return new Token(TipoToken.CHAR_LITERAL, codigoFonte.substring(posicaoInicial, posicaoAtual));
        }
        
        posicaoAtual++; // Consumir aspa simples final '
        
        // Retornar apenas o conteudo, sem as aspas
        return new Token(TipoToken.CHAR_LITERAL, String.valueOf(caractere));
    }
    
    /**
     * Reconhece literais de string delimitados por aspas duplas
     * 
     * Formato: "texto" onde texto pode conter qualquer caractere
     * Retorna apenas o conteúdo, sem as aspas.
     * 
     */
    private Token reconhecerString() {
        int posicaoInicial = posicaoAtual;
        posicaoAtual++; // Consumir aspa dupla inicial "
        
        StringBuilder conteudoString = new StringBuilder();
        
        // Consumir todos os caracteres até a aspa de fechamento
        while (posicaoAtual < codigoFonte.length() && codigoFonte.charAt(posicaoAtual) != '"') {
            conteudoString.append(codigoFonte.charAt(posicaoAtual));
            posicaoAtual++;
        }
        
        // Verificar se encontrou aspa de fechamento
        if (posicaoAtual >= codigoFonte.length()) {
            System.err.println("Erro: String não fechada");
            return new Token(TipoToken.STRING_LITERAL, conteudoString.toString());
        }
        
        posicaoAtual++; // Consumir aspa dupla final "
        
        // Retornar apenas o conteudo, sem as aspas
        return new Token(TipoToken.STRING_LITERAL, conteudoString.toString());
    }
    
    /**
     * Limpa a lista de caracteres nao identificados
     * Util para reiniciar a analise ou limpar erros anteriores.
     */
    public void limparCaracteresNaoIdentificados() {
        caracteresNaoIdentificados.clear();
    }
}
