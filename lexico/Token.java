package lexico;

/**
 * Classe que representa um token do analisador léxico
 * 
 * Um token é a menor unidade significativa do código fonte,
 * composto por:
 * - lexema: a sequência de caracteres que forma o token
 * - tipo: a categoria do token (identificador, número, operador, etc.)
 * 
 * Exemplos:
 * - Token("programa", PALAVRA_RESERVADA)
 * - Token("x", IDENTIFICADOR)
 * - Token("123", NUMERO_INTEIRO)
 * - Token("+", OPERADOR_ARITMETICO)
 * 
 * @author Equipe do Compilador
 */
public class Token {
    // Campos públicos e finais para acesso direto e imutabilidade
    public final TipoToken tipo;    // Categoria do token
    public final String lexema;     // Texto original do token
    
    /**
     * Construtor do token
     */
    public Token(TipoToken tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }
    
    /**
     * Representação textual do token no formato padrão
     */
    @Override
    public String toString() {
        return "<" + lexema + ", " + tipo + ">";
    }
}
