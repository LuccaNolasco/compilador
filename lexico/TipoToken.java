package lexico;

/**
 * Enumeração que define todos os tipos de tokens reconhecidos pelo analisador léxico
 * 
 * Esta enum categoriza os diferentes elementos que podem ser encontrados
 * no código fonte durante a análise léxica. Cada token identificado
 * será classificado em uma dessas categorias.
 * 
 * As categorias incluem:
 * - Palavras reservadas da linguagem Pascal
 * - Identificadores (nomes de variáveis, funções, etc.)
 * - Números (inteiros e reais)
 * - Literais (caracteres e strings)
 * - Operadores (aritméticos, relacionais, lógicos)
 * - Símbolos especiais (parênteses, vírgulas, etc.)
 * - Tokens especiais de controle
 * 
 */
public enum TipoToken {
    // === PALAVRAS RESERVADAS ESPECÍFICAS (não utilizadas diretamente) ===
    // Estas constantes existem para compatibilidade, mas o analisador
    // usa PALAVRA_RESERVADA como tipo genérico
    ABSOLUTE, ARRAY, BEGIN, CASE, CHAR, CONST, DIV, DO, DOWTO, ELSE, 
    EXTERNAL, FILE, FOR, FORWARD, FUNC, FUNCTION, GOTO, IF, IMPLEMENTATION, 
    INTEGER, INTERFACE, INTERRUPT, LABEL, MAIN, NIL, NIT, OF, PACKED, 
    PROC, PROGRAM, REAL, RECORD, REPEAT, SET, SHL, SHR, STRING, THEN, 
    TO, TYPE, UNIT, UNTIL, USES, VAR, WHILE, WITH, XOR,
    
    // === CATEGORIAS PRINCIPAIS DE TOKENS ===
    
    PALAVRA_RESERVADA,      // Palavras-chave da linguagem (begin, end, if, etc.)
    IDENTIFICADOR,          // Nomes de variáveis, funções, procedimentos
    
    // Números
    NUMERO_INTEIRO,         // Números inteiros (123, -456)
    NUMERO_REAL,            // Números reais (12.34, 1.23e-4)
    
    // Literais
    CHAR_LITERAL,           // Caracteres entre aspas simples ('a', 'Z')
    STRING_LITERAL,         // Strings entre aspas duplas ("hello world")
    
    // Operadores
    OPERADOR_ARITMETICO,    // +, -, *, /
    OPERADOR_RELACIONAL,    // =, <>, <, >, <=, >=
    OPERADOR_LOGICO,        // and, or, not (se implementados)
    
    // Atribuição
    ATRIBUICAO,             // :=
    
    // Símbolos especiais
    SIMBOLO_ESPECIAL,       // (, ), ;, ,, ., :
    
    // Tokens especiais de controle
    FIM,                    // Ponto final após "end" (end.)
    FIM_DE_ARQUIVO          // Indica fim do código fonte
}
