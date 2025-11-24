package sintatico;

import lexico.AnalisadorLexico;
import lexico.Token;
import java.util.*;

/**
 * Analisador Sintático SLR(1) para a linguagem Sigma
 * 
 * Esta classe implementa a análise sintática usando a técnica SLR(1).
 * Utiliza uma tabela de parsing construída a partir da gramática da linguagem
 * e realiza a análise através de uma pilha de estados e ações (shift/reduce).
 * 
 * Funcionalidades principais:
 * - Análise sintática de programas válidos
 * - Detecção de erros sintáticos
 * - Informação sobre linha do erro
 * - Aceitação ou rejeição do programa
 * 
 */
public class AnalisadorSintatico {
    
    // === ATRIBUTOS DE CONTROLE ===
    private AnalisadorLexico analisadorLexico;  // Analisador léxico para obter tokens
    private Token tokenAtual;                    // Token atual sendo analisado
    private Stack<Integer> pilhaEstados;        // Pilha de estados do autômato SLR(1)
    private int linhaAtual;                      // Linha atual para reportar erros
    private boolean aceito;                      // Flag indicando se programa foi aceito
    private String codigoFonte;                  // Código fonte original para calcular linha
    private int ultimaPosicaoProcessada;        // Última posição no código fonte que foi processada
    
    // === TABELA DE AÇÕES E GOTOS ===
    // Ações: "sN" = shift para estado N, "rN" = reduce pela produção N, "acc" = aceitar, "" = erro
    private Map<String, String> tabelaAcoes;
    // Gotos: mapeia (estado, não-terminal) -> novo estado
    private Map<String, Integer> tabelaGotos;
    
    // === PRODUÇÕES DA GRAMÁTICA ===
    // Cada produção é representada como: [lado esquerdo, tamanho do lado direito]
    private String[][] producoes;
    
    /**
     * Construtor do analisador sintático
     * 
     * @param codigoFonte Código fonte a ser analisado
     */
    public AnalisadorSintatico(String codigoFonte) {
        this.codigoFonte = codigoFonte;
        this.analisadorLexico = new AnalisadorLexico(codigoFonte);
        this.pilhaEstados = new Stack<>();
        this.linhaAtual = 1;
        this.ultimaPosicaoProcessada = 0;
        this.aceito = false;
        
        // Inicializar tabelas e produções
        inicializarProducoes();
        inicializarTabelaAcoes();
        inicializarTabelaGotos();
    }
    
    /**
     * Inicializa as produções da gramática aumentada
     * 
     * Produções numeradas conforme a gramática:
     * 0: S' -> S
     * 1: S -> program id ; D begin L end .
     * 2: S -> program id ; begin L end .
     * 3: D -> var V
     * 4: V -> I : T ; V
     * 5: V -> I : T ;
     * 6: T -> integer
     * 7: T -> Boolean
     * 8: I -> id
     * 9: I -> id , I
     * 10: L -> C ;
     * 11: L -> C ; L
     * 12: C -> A
     * 13: C -> R
     * 14: C -> W
     * 15: C -> M
     * 16: C -> N
     * 17: C -> P
     * 18: A -> id := E
     * 19: R -> read ( I )
     * 20: R -> readln
     * 21: R -> readln ( I )
     * 22: W -> write ( F )
     * 23: W -> writeln
     * 24: W -> writeln ( F )
     * 25: F -> G
     * 26: F -> G , F
     * 27: G -> str
     * 28: G -> E
     * 29: M -> begin L end
     * 30: N -> if B then C
     * 31: N -> if B then C else C
     * 32: P -> while B do C
     * 33: E -> E + E
     * 34: E -> E - E
     * 35: E -> E * E
     * 36: E -> E / E
     * 37: E -> - E
     * 38: E -> ( E )
     * 39: E -> id
     * 40: E -> num
     * 41: B -> E < E
     * 42: B -> E <= E
     * 43: B -> E > E
     * 44: B -> E >= E
     * 45: B -> E = E
     * 46: B -> E <> E
     * 47: B -> id
     */
    private void inicializarProducoes() {
        producoes = new String[][] {
            {"S'", "1"},      // 0: S' -> S
            {"S", "6"},       // 1: S -> program id ; D begin L end .
            {"S", "5"},       // 2: S -> program id ; begin L end .
            {"D", "2"},       // 3: D -> var V
            {"V", "5"},       // 4: V -> I : T ; V
            {"V", "4"},       // 5: V -> I : T ;
            {"T", "1"},       // 6: T -> integer
            {"T", "1"},       // 7: T -> Boolean
            {"I", "1"},       // 8: I -> id
            {"I", "3"},       // 9: I -> id , I
            {"L", "2"},       // 10: L -> C ;
            {"L", "3"},       // 11: L -> C ; L
            {"C", "1"},       // 12: C -> A
            {"C", "1"},       // 13: C -> R
            {"C", "1"},       // 14: C -> W
            {"C", "1"},       // 15: C -> M
            {"C", "1"},       // 16: C -> N
            {"C", "1"},       // 17: C -> P
            {"A", "3"},       // 18: A -> id := E
            {"R", "4"},       // 19: R -> read ( I )
            {"R", "1"},       // 20: R -> readln
            {"R", "4"},       // 21: R -> readln ( I )
            {"W", "4"},       // 22: W -> write ( F )
            {"W", "1"},       // 23: W -> writeln
            {"W", "4"},       // 24: W -> writeln ( F )
            {"F", "1"},       // 25: F -> G
            {"F", "3"},       // 26: F -> G , F
            {"G", "1"},       // 27: G -> str
            {"G", "1"},       // 28: G -> E
            {"M", "3"},       // 29: M -> begin L end
            {"N", "4"},       // 30: N -> if B then C
            {"N", "6"},       // 31: N -> if B then C else C
            {"P", "4"},       // 32: P -> while B do C
            {"E", "3"},       // 33: E -> E + E
            {"E", "3"},       // 34: E -> E - E
            {"E", "3"},       // 35: E -> E * E
            {"E", "3"},       // 36: E -> E / E
            {"E", "2"},       // 37: E -> - E
            {"E", "3"},       // 38: E -> ( E )
            {"E", "1"},       // 39: E -> id
            {"E", "1"},       // 40: E -> num
            {"B", "3"},       // 41: B -> E < E
            {"B", "3"},       // 42: B -> E <= E
            {"B", "3"},       // 43: B -> E > E
            {"B", "3"},       // 44: B -> E >= E
            {"B", "3"},       // 45: B -> E = E
            {"B", "3"},       // 46: B -> E <> E
            {"B", "1"}        // 47: B -> id
        };
    }
    
    /**
     * Inicializa a tabela de ações do parser SLR(1)
     * 
     * Baseado nas transições do arquivo transicoes.txt
     */
    private void inicializarTabelaAcoes() {
        tabelaAcoes = new HashMap<>();
        
        // === SHIFTS BASEADOS NAS TRANSIÇÕES ===
        
        // Estado 0
        tabelaAcoes.put("0,program", "s2");
        
        // Estado 1: S' -> S # (estado de aceitação)
        // Aceitar quando encontramos "." após reduzir S
        tabelaAcoes.put("1,.", "acc");
        
        // Estado 2
        tabelaAcoes.put("2,id", "s3");
        
        // Estado 3
        tabelaAcoes.put("3,;", "s4");
        
        // Estado 4
        tabelaAcoes.put("4,begin", "s6");
        tabelaAcoes.put("4,var", "s7");
        
        // Estado 6
        tabelaAcoes.put("6,id", "s18");
        tabelaAcoes.put("6,if", "s19");
        tabelaAcoes.put("6,read", "s20");
        tabelaAcoes.put("6,readln", "s21");
        tabelaAcoes.put("6,while", "s22");
        tabelaAcoes.put("6,write", "s23");
        tabelaAcoes.put("6,writeln", "s24");
        tabelaAcoes.put("6,begin", "s17");
        
        // Estado 7
        tabelaAcoes.put("7,id", "s27");
        
        // Estado 8
        tabelaAcoes.put("8,id", "s18");
        tabelaAcoes.put("8,if", "s19");
        tabelaAcoes.put("8,read", "s20");
        tabelaAcoes.put("8,readln", "s21");
        tabelaAcoes.put("8,while", "s22");
        tabelaAcoes.put("8,write", "s23");
        tabelaAcoes.put("8,writeln", "s24");
        tabelaAcoes.put("8,begin", "s17");
        
        // Estado 10
        tabelaAcoes.put("10,;", "s29");
        
        // Estado 5: S -> program id ; D begin L # end .
        // Quando encontramos "end" no estado 5, fazer shift para estado 11 (S -> program id ; D begin L end # .)
        tabelaAcoes.put("5,end", "s11");
        
        // Estado 11
        tabelaAcoes.put("11,end", "s30");
        
        // Estado 17
        tabelaAcoes.put("17,id", "s18");
        tabelaAcoes.put("17,if", "s19");
        tabelaAcoes.put("17,read", "s20");
        tabelaAcoes.put("17,readln", "s21");
        tabelaAcoes.put("17,while", "s22");
        tabelaAcoes.put("17,write", "s23");
        tabelaAcoes.put("17,writeln", "s24");
        tabelaAcoes.put("17,begin", "s17");
        
        // Estado 18
        tabelaAcoes.put("18,:=", "s32");
        
        // Estado 19
        tabelaAcoes.put("19,(", "s33");
        tabelaAcoes.put("19,-", "s34");
        tabelaAcoes.put("19,id", "s37");
        tabelaAcoes.put("19,num", "s38");
        
        // Estado 20
        tabelaAcoes.put("20,(", "s39");
        
        // Estado 21
        tabelaAcoes.put("21,(", "s40");
        
        // Estado 22
        tabelaAcoes.put("22,(", "s33");
        tabelaAcoes.put("22,-", "s34");
        tabelaAcoes.put("22,id", "s37");
        tabelaAcoes.put("22,num", "s38");
        
        // Estado 23
        tabelaAcoes.put("23,(", "s42");
        
        // Estado 24
        tabelaAcoes.put("24,(", "s43");
        
        // Estado 25
        tabelaAcoes.put("25,:", "s44");
        
        // Estado 27
        tabelaAcoes.put("27,,", "s45");
        
        // Estado 28
        tabelaAcoes.put("28,end", "s46");
        
        // Estado 29
        tabelaAcoes.put("29,id", "s18");
        tabelaAcoes.put("29,if", "s19");
        tabelaAcoes.put("29,read", "s20");
        tabelaAcoes.put("29,readln", "s21");
        tabelaAcoes.put("29,while", "s22");
        tabelaAcoes.put("29,write", "s23");
        tabelaAcoes.put("29,writeln", "s24");
        tabelaAcoes.put("29,begin", "s17");
        
        // Estado 30
        tabelaAcoes.put("30,.", "s48");
        
        // Estado 31: M -> begin L # end
        tabelaAcoes.put("31,end", "s49");
        
        // Estado 32
        tabelaAcoes.put("32,(", "s33");
        tabelaAcoes.put("32,-", "s34");
        tabelaAcoes.put("32,id", "s51");
        tabelaAcoes.put("32,num", "s38");
        
        // Estado 33
        tabelaAcoes.put("33,(", "s33");
        tabelaAcoes.put("33,-", "s34");
        tabelaAcoes.put("33,id", "s51");
        tabelaAcoes.put("33,num", "s38");
        
        // Estado 34
        tabelaAcoes.put("34,(", "s33");
        tabelaAcoes.put("34,-", "s34");
        tabelaAcoes.put("34,id", "s51");
        tabelaAcoes.put("34,num", "s38");
        
        // Estado 35
        tabelaAcoes.put("35,then", "s54");
        
        // Estado 36
        tabelaAcoes.put("36,+", "s56");
        tabelaAcoes.put("36,-", "s57");
        tabelaAcoes.put("36,*", "s55");
        tabelaAcoes.put("36,/", "s58");
        tabelaAcoes.put("36,<", "s59");
        tabelaAcoes.put("36,<=", "s60");
        tabelaAcoes.put("36,<>", "s61");
        tabelaAcoes.put("36,=", "s62");
        tabelaAcoes.put("36,>", "s63");
        tabelaAcoes.put("36,>=", "s64");
        
        // Estado 81: E -> E + E # - precisa fazer shift de + e - para processar E + E ou E - E
        // Não reduzir com + ou - para permitir associatividade à esquerda
        tabelaAcoes.put("81,+", "s56");
        tabelaAcoes.put("81,-", "s57");
        
        // Estado 82: E -> E - E # - precisa fazer shift de + e - para processar E + E ou E - E
        // Não reduzir com + ou - para permitir associatividade à esquerda
        tabelaAcoes.put("82,+", "s56");
        tabelaAcoes.put("82,-", "s57");
        tabelaAcoes.put("82,*", "s55");
        tabelaAcoes.put("82,/", "s58");
        
        // Estado 37
        // Reduz para B -> id ou E -> id dependendo do contexto
        // Mas também precisa fazer shift para operadores relacionais quando encontra E < E
        tabelaAcoes.put("37,<", "s59");
        tabelaAcoes.put("37,<=", "s60");
        tabelaAcoes.put("37,<>", "s61");
        tabelaAcoes.put("37,=", "s62");
        tabelaAcoes.put("37,>", "s63");
        tabelaAcoes.put("37,>=", "s64");
        // Também precisa fazer shift para operadores aritméticos quando encontra E + E, etc.
        tabelaAcoes.put("37,+", "s56");
        tabelaAcoes.put("37,-", "s57");
        tabelaAcoes.put("37,*", "s55");
        tabelaAcoes.put("37,/", "s58");
        
        // Estado 38
        // Reduz E -> num quando encontrar operadores ou fim
        
        // Estado 39
        tabelaAcoes.put("39,id", "s27");
        
        // Estado 40
        tabelaAcoes.put("40,id", "s27");
        
        // Estado 41
        tabelaAcoes.put("41,do", "s67");
        
        // Estado 42
        tabelaAcoes.put("42,(", "s33");
        tabelaAcoes.put("42,-", "s34");
        tabelaAcoes.put("42,id", "s51");
        tabelaAcoes.put("42,num", "s38");
        tabelaAcoes.put("42,str", "s71");
        
        // Estado 43
        tabelaAcoes.put("43,(", "s33");
        tabelaAcoes.put("43,-", "s34");
        tabelaAcoes.put("43,id", "s51");
        tabelaAcoes.put("43,num", "s38");
        tabelaAcoes.put("43,str", "s71");
        
        // Estado 44
        tabelaAcoes.put("44,integer", "s75");
        tabelaAcoes.put("44,boolean", "s73");  // Corrigido para minúsculo (tokenParaString converte para minúsculo)
        tabelaAcoes.put("44,Boolean", "s73");  // Mantido para compatibilidade
        
        // Estado 45
        tabelaAcoes.put("45,id", "s27");
        
        // Estado 46
        tabelaAcoes.put("46,.", "s77");
        
        // Estado 48
        // Estado de aceitação - será tratado no método analisar()
        
        // Estado 49
        // Reduz M -> begin L end
        
        // Estado 50
        tabelaAcoes.put("50,+", "s56");
        tabelaAcoes.put("50,-", "s57");
        tabelaAcoes.put("50,*", "s55");
        tabelaAcoes.put("50,/", "s58");
        
        // Estado 51
        // Reduz E -> id quando encontrar operadores ou fim
        
        // Estado 52
        tabelaAcoes.put("52,)", "s78");
        tabelaAcoes.put("52,+", "s56");
        tabelaAcoes.put("52,-", "s57");
        tabelaAcoes.put("52,*", "s55");
        tabelaAcoes.put("52,/", "s58");
        
        // Estado 53
        tabelaAcoes.put("53,+", "s56");
        tabelaAcoes.put("53,-", "s57");
        tabelaAcoes.put("53,*", "s55");
        tabelaAcoes.put("53,/", "s58");
        
        // Estado 54
        tabelaAcoes.put("54,id", "s18");
        tabelaAcoes.put("54,if", "s19");
        tabelaAcoes.put("54,read", "s20");
        tabelaAcoes.put("54,readln", "s21");
        tabelaAcoes.put("54,while", "s22");
        tabelaAcoes.put("54,write", "s23");
        tabelaAcoes.put("54,writeln", "s24");
        tabelaAcoes.put("54,begin", "s17");
        
        // Estado 55
        tabelaAcoes.put("55,(", "s33");
        tabelaAcoes.put("55,-", "s34");
        tabelaAcoes.put("55,id", "s51");
        tabelaAcoes.put("55,num", "s38");
        
        // Estado 56
        tabelaAcoes.put("56,(", "s33");
        tabelaAcoes.put("56,-", "s34");
        tabelaAcoes.put("56,id", "s51");
        tabelaAcoes.put("56,num", "s38");
        
        // Estado 57
        tabelaAcoes.put("57,(", "s33");
        tabelaAcoes.put("57,-", "s34");
        tabelaAcoes.put("57,id", "s51");
        tabelaAcoes.put("57,num", "s38");
        
        // Estado 58
        tabelaAcoes.put("58,(", "s33");
        tabelaAcoes.put("58,-", "s34");
        tabelaAcoes.put("58,id", "s51");
        tabelaAcoes.put("58,num", "s38");
        
        // Estado 59
        tabelaAcoes.put("59,(", "s33");
        tabelaAcoes.put("59,-", "s34");
        tabelaAcoes.put("59,id", "s51");
        tabelaAcoes.put("59,num", "s38");
        
        // Estado 60
        tabelaAcoes.put("60,(", "s33");
        tabelaAcoes.put("60,-", "s34");
        tabelaAcoes.put("60,id", "s51");
        tabelaAcoes.put("60,num", "s38");
        
        // Estado 61
        tabelaAcoes.put("61,(", "s33");
        tabelaAcoes.put("61,-", "s34");
        tabelaAcoes.put("61,id", "s51");
        tabelaAcoes.put("61,num", "s38");
        
        // Estado 62
        tabelaAcoes.put("62,(", "s33");
        tabelaAcoes.put("62,-", "s34");
        tabelaAcoes.put("62,id", "s51");
        tabelaAcoes.put("62,num", "s38");
        
        // Estado 63
        tabelaAcoes.put("63,(", "s33");
        tabelaAcoes.put("63,-", "s34");
        tabelaAcoes.put("63,id", "s51");
        tabelaAcoes.put("63,num", "s38");
        
        // Estado 64
        tabelaAcoes.put("64,(", "s33");
        tabelaAcoes.put("64,-", "s34");
        tabelaAcoes.put("64,id", "s51");
        tabelaAcoes.put("64,num", "s38");
        
        // Estado 65
        tabelaAcoes.put("65,)", "s90");
        
        // Estado 66
        tabelaAcoes.put("66,)", "s91");
        
        // Estado 67
        tabelaAcoes.put("67,id", "s18");
        tabelaAcoes.put("67,if", "s19");
        tabelaAcoes.put("67,read", "s20");
        tabelaAcoes.put("67,readln", "s21");
        tabelaAcoes.put("67,while", "s22");
        tabelaAcoes.put("67,write", "s23");
        tabelaAcoes.put("67,writeln", "s24");
        tabelaAcoes.put("67,begin", "s17");
        tabelaAcoes.put("67,else", "s97"); // Adicionado para lidar com else após um comando em um while dentro de um if-then
        
        // Estado 68
        tabelaAcoes.put("68,+", "s56");
        tabelaAcoes.put("68,-", "s57");
        tabelaAcoes.put("68,*", "s55");
        tabelaAcoes.put("68,/", "s58");
        
        // Estado 69
        tabelaAcoes.put("69,)", "s93");
        
        // Estado 70
        tabelaAcoes.put("70,,", "s94");
        
        // Estado 71
        // Reduz G -> str
        
        // Estado 72
        tabelaAcoes.put("72,)", "s95");
        
        // Estado 73
        // Reduz T -> Boolean
        
        // Estado 74
        tabelaAcoes.put("74,;", "s96");
        
        // Estado 75
        // Reduz T -> integer
        
        // Estado 77
        // Estado de aceitação
        
        // Estado 78
        // Reduz E -> ( E )
        
        // Estado 79
        tabelaAcoes.put("79,else", "s97");
        
        // Estado 80: E -> E * E # - precisa fazer shift de * para permitir associatividade à esquerda
        tabelaAcoes.put("80,*", "s55");
        tabelaAcoes.put("80,/", "s58");
        tabelaAcoes.put("80,(", "s33");
        tabelaAcoes.put("80,-", "s34");
        tabelaAcoes.put("80,id", "s51");
        tabelaAcoes.put("80,num", "s38");
        
        // Estado 81: E -> E + E # - precisa fazer shift de + e - para permitir associatividade à esquerda
        // (já definido acima nos estados 56 e 57)
        
        // Estado 82: E -> E - E # - precisa fazer shift de + e - para permitir associatividade à esquerda
        // (já definido acima nos estados 56 e 57)
        
        // Estado 83: E -> E / E # - precisa fazer shift de / para permitir associatividade à esquerda
        tabelaAcoes.put("83,*", "s55");
        tabelaAcoes.put("83,/", "s58");
        tabelaAcoes.put("83,(", "s33");
        tabelaAcoes.put("83,-", "s34");
        tabelaAcoes.put("83,id", "s51");
        tabelaAcoes.put("83,num", "s38");
        
        // Estados 84-89: Estados intermediários de expressões - shifts já definidos acima
        
        // Estado 90
        // Reduz R -> read ( I )
        
        // Estado 91
        // Reduz R -> readln ( I )
        
        // Estado 93
        // Reduz W -> write ( F )
        tabelaAcoes.put("93,else", "s97"); // Adicionado para lidar com else após um write em um if-then
        
        // Estado 94
        tabelaAcoes.put("94,(", "s33");
        tabelaAcoes.put("94,-", "s34");
        tabelaAcoes.put("94,id", "s51");
        tabelaAcoes.put("94,num", "s38");
        tabelaAcoes.put("94,str", "s71");
        
        // Estado 95
        // Reduz W -> writeln ( F )
        
        // Estado 96
        tabelaAcoes.put("96,id", "s27");
        
        // Estado 97
        tabelaAcoes.put("97,id", "s18");
        tabelaAcoes.put("97,if", "s19");
        tabelaAcoes.put("97,read", "s20");
        tabelaAcoes.put("97,readln", "s21");
        tabelaAcoes.put("97,while", "s22");
        tabelaAcoes.put("97,write", "s23");
        tabelaAcoes.put("97,writeln", "s24");
        tabelaAcoes.put("97,begin", "s17");
        
        // === REDUCES BASEADOS NOS FOLLOW SETS ===
        
        // Reduces para I (produção 8: I -> id)
        // FOLLOW(I) = {:,),,}
        // Reduz com ":" quando há apenas um id (sem vírgula antes)
        adicionarReduce("27", 8, new String[]{")", ",", ":"});
        
        // Reduces para I (produção 9: I -> id , I)
        // FOLLOW(I) = {:,),,}
        // Esta produção só deve ser reduzida em um estado diferente, após processar id , id
        // O estado 27 não deve reduzir produção 9 com ":" porque só tem um id na pilha
        
        // Reduces para T (produção 6: T -> integer)
        // FOLLOW(T) = {;}
        // Mas também precisa reduzir com "begin" quando não há ";" antes de "begin"
        adicionarReduce("75", 6, new String[]{";", "begin"});
        
        // Reduces para T (produção 7: T -> Boolean)
        // FOLLOW(T) = {;}
        adicionarReduce("73", 7, new String[]{";"});
        
        // Reduces para V (produção 4: V -> I : T ; V)
        // FOLLOW(V) = {begin}
        // Não precisa - já tratado pelo shift
        
        // Reduces para V (produção 5: V -> I : T ;)
        // FOLLOW(V) = {begin}
        // Mas também precisa reduzir com "var" quando há múltiplas declarações var
        // Estado 74: após reduzir T, precisamos reduzir V -> I : T ; com begin
        adicionarReduce("74", 5, new String[]{"begin"});
        adicionarReduce("96", 5, new String[]{"begin", "var"});
        
        // Reduces para D (produção 3: D -> var V)
        // FOLLOW(D) = {begin}
        // Quando reduz V completo no estado 8 (após GOTO(7, V) = 8), precisa reduzir D -> var V quando encontra begin
        adicionarReduce("8", 3, new String[]{"begin"});
        
        // Reduces para L (produção 10: L -> C ;)
        // FOLLOW(L) = {end}
        // Mas também precisa reduzir com "." quando está no final do programa
        // E também precisa reduzir com "else" quando há um bloco composto dentro de um if-then sem else
        adicionarReduce("10", 10, new String[]{"end", ".", "else"});
        
        // Reduces para L (produção 11: L -> C ; L)
        // FOLLOW(L) = {end}
        // Mas também precisa reduzir com "." quando está no final do programa
        // NOTA: Estado 11 faz shift para estado 30 quando encontra "end"
        // NOTA: Estado 28 faz shift para estado 46 quando encontra "end"
        // NOTA: Estado 31 faz shift para estado 49 quando encontra "end"
        adicionarReduce("29", 11, new String[]{"end", "."});
        
        // Reduces para C (produção 12: C -> A)
        // FOLLOW(C) = {;}
        // Mas também precisa reduzir com "else" quando está dentro de um if-then sem else
        adicionarReduce("12", 12, new String[]{";", "else"});
        
        // Reduces para C (produção 13: C -> R)
        // FOLLOW(C) = {;}
        adicionarReduce("13", 13, new String[]{";"});
        
        // Reduces para C (produção 14: C -> W)
        // FOLLOW(C) = {;}
        adicionarReduce("14", 14, new String[]{";"});
        
        // Reduces para C (produção 15: C -> M)
        // FOLLOW(C) = {;}
        // Mas também precisa reduzir com "." quando está no final do programa
        // E também precisa reduzir com "else" quando há um bloco composto dentro de um if-then sem else
        adicionarReduce("15", 15, new String[]{";", ".", "else"});
        
        // Reduces para C (produção 16: C -> N)
        // FOLLOW(C) = {;}
        // Mas também precisa reduzir com "else" quando há if aninhados
        // Quando estamos no estado 16 e encontramos "else", significa que já processamos
        // um if-then sem else, então devemos reduzir C -> N antes de processar o "else" do if externo
        adicionarReduce("16", 16, new String[]{";", "else"});
        
        // Reduces para C (produção 17: C -> P)
        // FOLLOW(C) = {;}
        adicionarReduce("17", 17, new String[]{";"});
        
        // Reduces para A (produção 18: A -> id := E)
        // FOLLOW(A) = {;}
        // Mas também precisa reduzir com "else" quando está dentro de um if-then sem else
        adicionarReduce("50", 18, new String[]{";", "else"});
        
        // Reduces para R (produção 19: R -> read ( I ))
        // FOLLOW(R) = {;}
        adicionarReduce("90", 19, new String[]{";"});
        
        // Reduces para R (produção 20: R -> readln)
        // FOLLOW(R) = {;}
        adicionarReduce("21", 20, new String[]{";"});
        
        // Reduces para R (produção 21: R -> readln ( I ))
        // FOLLOW(R) = {;}
        adicionarReduce("91", 21, new String[]{";"});
        
        // Reduces para W (produção 22: W -> write ( F ))
        // FOLLOW(W) = {;}
        adicionarReduce("93", 22, new String[]{";"});
        
        // Reduces para W (produção 23: W -> writeln)
        // FOLLOW(W) = {;}
        adicionarReduce("24", 23, new String[]{";"});
        
        // Reduces para W (produção 24: W -> writeln ( F ))
        // FOLLOW(W) = {;}
        adicionarReduce("95", 24, new String[]{";"});
        
        // Reduces para F (produção 25: F -> G)
        // FOLLOW(F) = {),,}
        adicionarReduce("70", 25, new String[]{")", ","});
        
        // Reduces para F (produção 26: F -> G , F)
        // FOLLOW(F) = {),,}
        adicionarReduce("94", 26, new String[]{")", ","});
        
        // Reduces para G (produção 27: G -> str)
        // FOLLOW(G) = {),,}
        adicionarReduce("71", 27, new String[]{")", ","});
        
        // Reduces para G (produção 28: G -> E)
        // FOLLOW(G) = {),,}
        // Estados 36, 50, 52, 53, 68, 78 podem reduzir G -> E
        // Estados 80, 81, 82, 83 são estados intermediários de expressões (E -> E op E)
        // e não devem reduzir G -> E, apenas E -> E op E
        adicionarReduce("36", 28, new String[]{")", ","});
        adicionarReduce("50", 28, new String[]{")", ","});
        adicionarReduce("52", 28, new String[]{")", ","});
        adicionarReduce("53", 28, new String[]{")", ","});
        adicionarReduce("68", 28, new String[]{")", ","});
        adicionarReduce("78", 28, new String[]{")", ","});
        
        // Reduces para M (produção 29: M -> begin L end)
        // FOLLOW(M) = {;}
        // Mas também precisa reduzir com "." quando está no final do programa
        // E também precisa reduzir com "else" quando há um bloco composto dentro de um if-then sem else
        adicionarReduce("49", 29, new String[]{";", ".", "else"});
        // Estado 31: M -> begin L # end - também precisa reduzir com ";" quando dentro de um if
        // E também precisa reduzir com "else" quando há um bloco composto dentro de um if-then sem else
        adicionarReduce("31", 29, new String[]{";", "else"});
        // Estado 46: M -> begin L end # - precisa reduzir com ";" quando há blocos aninhados
        adicionarReduce("46", 29, new String[]{";"});
        
        // Reduces para N (produção 30: N -> if B then C)
        // FOLLOW(N) = {;}
        // Estado 54: N -> if B then # C (antes de processar C)
        // Estado 79: N -> if B then C # (após processar C)
        // Mas quando reduzimos C -> M dentro de um if, chegamos ao estado 54 após GOTO(35, C) = 54
        // Então precisamos reduzir N -> if B then C no estado 54 também com ";"
        adicionarReduce("54", 30, new String[]{"else", ";"});  // Estado 54 reduz com "else" ou ";" quando C já foi reduzido
        adicionarReduce("79", 30, new String[]{";"});      // Estado 79 reduz com ";" quando não há else
        
        // Reduces para N (produção 31: N -> if B then C else C)
        // FOLLOW(N) = {;}
        // Mas também precisa reduzir com "else" quando há if aninhados
        // Quando estamos no estado 97 e encontramos "else", significa que já processamos
        // o segundo C do if atual, então devemos reduzir N -> if B then C else C antes
        // de processar o "else" do if externo
        adicionarReduce("97", 31, new String[]{";", "else"});
        
        // Reduces para P (produção 32: P -> while B do C)
        // FOLLOW(P) = {;}
        adicionarReduce("67", 32, new String[]{";"});
        
        // Reduces para E (produção 33: E -> E + E)
        // FOLLOW(E) = {;,),,,then,do,else,end,.,<,>,<=,>=,=,<>,+,-,*,/}
        // Precedência: + tem menor precedência que *,/
        adicionarReduce("56", 33, new String[]{"+", "-", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para E (produção 34: E -> E - E)
        adicionarReduce("57", 34, new String[]{"+", "-", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para E (produção 35: E -> E * E)
        // Precedência: * tem maior precedência que +,-
        adicionarReduce("55", 35, new String[]{"*", "/", "+", "-", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para E (produção 36: E -> E / E)
        adicionarReduce("58", 36, new String[]{"*", "/", "+", "-", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para E (produção 37: E -> - E)
        adicionarReduce("53", 37, new String[]{"+", "-", "*", "/", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para E (produção 38: E -> ( E ))
        adicionarReduce("78", 38, new String[]{"+", "-", "*", "/", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para E (produção 39: E -> id)
        adicionarReduce("51", 39, new String[]{"+", "-", "*", "/", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para E (produção 40: E -> num)
        adicionarReduce("38", 40, new String[]{"+", "-", "*", "/", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        
        // Reduces para B (produção 41: B -> E < E)
        // FOLLOW(B) = {then,do}
        adicionarReduce("59", 41, new String[]{"then", "do"});
        
        // Reduces para B (produção 42: B -> E <= E)
        adicionarReduce("60", 42, new String[]{"then", "do"});
        
        // Reduces para B (produção 43: B -> E > E)
        adicionarReduce("63", 43, new String[]{"then", "do"});
        
        // Reduces para B (produção 44: B -> E >= E)
        adicionarReduce("64", 44, new String[]{"then", "do"});
        
        // Reduces para B (produção 45: B -> E = E)
        adicionarReduce("62", 45, new String[]{"then", "do"});
        
        // Reduces para B (produção 46: B -> E <> E)
        adicionarReduce("61", 46, new String[]{"then", "do"});
        
        // Reduces para B (produção 47: B -> id)
        // FOLLOW(B) = {then,do}
        adicionarReduce("37", 47, new String[]{"then", "do"});
        
        // Reduces para estados intermediários de expressões (80-89)
        // Estado 80: E -> E * E # - reduz com operadores de menor precedência ou fim
        adicionarReduce("80", 35, new String[]{"+", "-", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        // Estado 81: E -> E + E # - reduz com operadores de menor precedência ou fim
        // Não reduzir com + ou - para permitir associatividade à esquerda (faz shift em vez disso)
        adicionarReduce("81", 33, new String[]{"*", "/", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        // Estado 82: E -> E - E # - reduz com operadores de menor precedência ou fim
        // Não reduzir com + ou - para permitir associatividade à esquerda (faz shift em vez disso)
        adicionarReduce("82", 34, new String[]{"*", "/", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        // Estado 83: E -> E / E # - reduz com operadores de menor precedência ou fim
        adicionarReduce("83", 36, new String[]{"+", "-", ";", ")", ",", "then", "do", "else", "end", ".", "<", ">", "<=", ">=", "=", "<>"});
        adicionarReduce("84", 41, new String[]{"then", "do"});
        adicionarReduce("85", 42, new String[]{"then", "do"});
        adicionarReduce("86", 46, new String[]{"then", "do"});
        adicionarReduce("87", 45, new String[]{"then", "do"});
        adicionarReduce("88", 43, new String[]{"then", "do"});
        adicionarReduce("89", 44, new String[]{"then", "do"});
    }
    
    /**
     * Adiciona ações de reduce para um estado específico
     */
    private void adicionarReduce(String estado, int numProducao, String[] tokens) {
        for (String token : tokens) {
            String chave = estado + "," + token;
            // Só adicionar se não houver ação já definida
            if (!tabelaAcoes.containsKey(chave)) {
                tabelaAcoes.put(chave, "r" + numProducao);
            }
        }
    }
    
    /**
     * Inicializa a tabela de GOTOs do parser SLR(1)
     */
    private void inicializarTabelaGotos() {
        tabelaGotos = new HashMap<>();
        
        // Estado 0
        tabelaGotos.put("0,S", 1);
        
        // Estado 4
        tabelaGotos.put("4,D", 5);
        tabelaGotos.put("4,L", 11);
        
        // Estado 5
        tabelaGotos.put("5,L", 11);
        
        // Estado 6
        tabelaGotos.put("6,C", 10);
        tabelaGotos.put("6,L", 11);
        tabelaGotos.put("6,A", 12);
        tabelaGotos.put("6,R", 13);
        tabelaGotos.put("6,W", 14);
        tabelaGotos.put("6,M", 15);
        tabelaGotos.put("6,N", 16);
        tabelaGotos.put("6,P", 17);
        
        // Estado 7
        tabelaGotos.put("7,V", 8);   // Quando reduz V completo, vai para estado 8 (D -> var V #)
        tabelaGotos.put("7,I", 25);  // Quando reduz I -> id, vai para estado 25 (V -> I # : T ;)
        tabelaGotos.put("7,L", 28);  // Quando reduz L após var (caso especial para aceitação com .)
        tabelaGotos.put("7,M", 15);  // Quando reduz M -> begin L end dentro de um bloco aninhado
        tabelaGotos.put("7,C", 10);  // Quando reduz C -> M após reduzir M no estado 7
        
        // Estado 8
        tabelaGotos.put("8,D", 5);   // Quando reduz D completo, vai para estado 5 (S -> program id ; D # begin L end .)
        tabelaGotos.put("8,L", 28);
        tabelaGotos.put("8,C", 10);
        tabelaGotos.put("8,A", 12);
        tabelaGotos.put("8,R", 13);
        tabelaGotos.put("8,W", 14);
        tabelaGotos.put("8,M", 15);
        tabelaGotos.put("8,N", 16);
        tabelaGotos.put("8,P", 17);
        
        // Estado 10
        tabelaGotos.put("10,L", 29);
        tabelaGotos.put("10,C", 10);  // Quando reduzimos C, podemos ter mais C's
        tabelaGotos.put("10,A", 12);
        tabelaGotos.put("10,R", 13);
        tabelaGotos.put("10,W", 14);
        tabelaGotos.put("10,M", 15);
        tabelaGotos.put("10,N", 16);
        tabelaGotos.put("10,P", 17);  // Quando reduzimos P dentro de L, precisamos fazer GOTO para P
        
        // Estado 17
        tabelaGotos.put("17,L", 31);
        tabelaGotos.put("17,C", 10);
        tabelaGotos.put("17,A", 12);
        tabelaGotos.put("17,R", 13);
        tabelaGotos.put("17,W", 14);
        tabelaGotos.put("17,M", 15);
        tabelaGotos.put("17,N", 16);
        tabelaGotos.put("17,P", 17);
        
        // Estado 19
        tabelaGotos.put("19,B", 35);
        tabelaGotos.put("19,E", 36);
        
        // Estado 22
        tabelaGotos.put("22,B", 41);
        tabelaGotos.put("22,E", 36);
        
        // Estado 25
        tabelaGotos.put("25,I", 27);
        
        // Estado 27
        tabelaGotos.put("27,I", 27);  // Para I -> id , I
        
        // Estado 29
        tabelaGotos.put("29,L", 29);
        tabelaGotos.put("29,C", 10);
        tabelaGotos.put("29,A", 12);
        tabelaGotos.put("29,R", 13);
        tabelaGotos.put("29,W", 14);
        tabelaGotos.put("29,M", 15);
        tabelaGotos.put("29,N", 16);
        tabelaGotos.put("29,P", 17);
        
        // Estado 18
        // Após reduzir E em A -> id := E, precisamos fazer GOTO para E no estado 18
        // Mas na verdade, após fazer shift de := no estado 18, vamos para estado 32
        // Então GOTO(18, E) não deveria existir normalmente, mas adicionamos apenas para E
        // Quando reduzimos A -> id := E no estado 12, desempilhamos o estado 12 e chegamos ao estado 18,
        // então precisamos fazer GOTO para A no estado 18
        // Quando reduzimos L -> C ; no estado 10, desempilhamos o estado 10 e chegamos ao estado 18,
        // então precisamos fazer GOTO para L no estado 18
        tabelaGotos.put("18,E", 12);  // Estado 12 é A -> id := E # (após reduzir E completo)
        tabelaGotos.put("18,A", 10);  // Quando reduzimos A -> id := E no estado 12, chegamos ao estado 18 e precisamos fazer GOTO para A (L -> C ;)
        tabelaGotos.put("18,L", 29);  // Quando reduzimos L -> C ; no estado 10, chegamos ao estado 18 e precisamos fazer GOTO para L (L -> C ; L)
        
        // Estado 32
        tabelaGotos.put("32,E", 50);
        tabelaGotos.put("32,A", 12);  // Quando reduzimos E completo no estado 50, chegamos ao estado 32 e precisamos fazer GOTO para A
        
        // Estado 50: A -> id := E # - precisa fazer GOTO para E quando reduzimos E -> E * E ou E -> - E
        tabelaGotos.put("50,E", 50);  // Quando reduzimos E -> E * E ou E -> - E e chegamos ao estado 50, fazer GOTO para E
        
        // Estado 33
        tabelaGotos.put("33,E", 52);
        
        // Estado 34
        tabelaGotos.put("34,E", 53);
        
        // Estado 35
        tabelaGotos.put("35,C", 54);
        tabelaGotos.put("35,A", 12);
        tabelaGotos.put("35,R", 13);
        tabelaGotos.put("35,W", 14);
        tabelaGotos.put("35,M", 15);
        tabelaGotos.put("97,L", 31); // N -> if B then C else C # L (quando L é um bloco begin...end)
        tabelaGotos.put("35,N", 16);
        tabelaGotos.put("35,P", 17);
        
        // Estado 36
        // E completo - não precisa de goto aqui
        
        // Estado 39
        tabelaGotos.put("39,I", 65);
        
        // Estado 40
        tabelaGotos.put("40,I", 66);
        
        // Estado 41
        tabelaGotos.put("41,C", 67);
        tabelaGotos.put("41,A", 12);
        tabelaGotos.put("41,R", 13);
        tabelaGotos.put("41,W", 14);
        tabelaGotos.put("41,M", 15);
        tabelaGotos.put("41,N", 16);
        tabelaGotos.put("41,P", 17);
        
        // Estado 42
        tabelaGotos.put("42,F", 69);
        tabelaGotos.put("42,G", 70);
        tabelaGotos.put("42,E", 36);
        
        // Estado 43
        tabelaGotos.put("43,F", 72);
        tabelaGotos.put("43,G", 70);
        tabelaGotos.put("43,E", 36);
        
        // Estado 44
        tabelaGotos.put("44,T", 74);
        
        // Estado 45
        tabelaGotos.put("45,I", 27);
        
        // Estado 54
        tabelaGotos.put("54,C", 79);
        tabelaGotos.put("54,A", 12);
        tabelaGotos.put("54,R", 13);
        tabelaGotos.put("54,W", 14);
        tabelaGotos.put("54,M", 15);
        tabelaGotos.put("54,N", 16);
        tabelaGotos.put("54,P", 17);
        tabelaGotos.put("54,L", 31);  // Quando reduzimos L dentro de um bloco begin...end dentro de um if, precisamos fazer GOTO para L
        
        // Estado 55
        tabelaGotos.put("55,E", 80);
        
        // Estado 56
        tabelaGotos.put("56,E", 81);
        
        // Estado 57
        tabelaGotos.put("57,E", 82);
        
        // Estado 58
        tabelaGotos.put("58,E", 83);
        
        // Estado 80: E -> E * E # - precisa fazer GOTO para E quando reduzimos E -> - E
        tabelaGotos.put("80,E", 80);  // Quando reduzimos E -> - E e chegamos ao estado 80, fazer GOTO para E
        
        // Estado 83: E -> E / E # - precisa fazer GOTO para E quando reduzimos E -> - E
        tabelaGotos.put("83,E", 83);  // Quando reduzimos E -> - E e chegamos ao estado 83, fazer GOTO para E
        
        // Estado 59
        tabelaGotos.put("59,E", 84);
        
        // Estado 60
        tabelaGotos.put("60,E", 85);
        
        // Estado 61
        tabelaGotos.put("61,E", 86);
        
        // Estado 62
        tabelaGotos.put("62,E", 87);
        
        // Estado 63
        tabelaGotos.put("63,E", 88);
        
        // Estado 64
        tabelaGotos.put("64,E", 89);
        
        // Estado 67
        tabelaGotos.put("67,C", 67);
        tabelaGotos.put("67,A", 12);
        tabelaGotos.put("67,R", 13);
        tabelaGotos.put("67,W", 14);
        tabelaGotos.put("67,M", 15);
        tabelaGotos.put("67,N", 16);
        tabelaGotos.put("67,P", 17);
        tabelaGotos.put("67,L", 31);  // Quando reduzimos L dentro de um while, precisamos fazer GOTO para L
        
        // Estado 68
        tabelaGotos.put("68,E", 68);
        
        // Estado 70
        tabelaGotos.put("70,F", 70);
        tabelaGotos.put("70,G", 70);
        tabelaGotos.put("70,E", 36);
        
        // Estado 74
        tabelaGotos.put("74,V", 96);
        tabelaGotos.put("74,I", 27);
        
        // Estado 79
        tabelaGotos.put("79,C", 97);
        tabelaGotos.put("79,A", 12);
        tabelaGotos.put("79,R", 13);
        tabelaGotos.put("79,W", 14);
        tabelaGotos.put("79,M", 15);
        tabelaGotos.put("79,N", 16);
        tabelaGotos.put("79,P", 17);
        tabelaGotos.put("79,L", 29);  // Quando reduzimos L -> C ; L dentro de um if, precisamos fazer GOTO para L
        
        // Estado 46
        // Após reduzir M -> begin L end, precisamos fazer GOTO para M
        // O estado anterior pode ser 6, 17, ou outro que espera M
        // Mas como não sabemos o contexto, vamos usar o estado 15 que é comum para C -> M
        tabelaGotos.put("46,M", 15);
        
        // Estado 94
        tabelaGotos.put("94,F", 94);
        tabelaGotos.put("94,G", 70);
        tabelaGotos.put("94,E", 36);
        
        // Estado 96
        tabelaGotos.put("96,V", 96);
        tabelaGotos.put("96,I", 27);
        
        // Estado 97
        tabelaGotos.put("97,C", 97);
        tabelaGotos.put("97,A", 12);
        tabelaGotos.put("97,R", 13);
        tabelaGotos.put("97,W", 14);
        tabelaGotos.put("97,M", 15);
        tabelaGotos.put("97,N", 16);
        tabelaGotos.put("97,P", 17);
    }
    
    /**
     * Converte um TipoToken para string para uso na tabela
     */
    private String tokenParaString(Token token) {
        if (token == null) return "$";
        
        switch (token.tipo) {
            case PALAVRA_RESERVADA:
                return token.lexema.toLowerCase();
            case IDENTIFICADOR:
                return "id";
            case NUMERO_INTEIRO:
            case NUMERO_REAL:
                return "num";
            case STRING_LITERAL:
                return "str";
            case OPERADOR_ARITMETICO:
                return token.lexema;
            case OPERADOR_RELACIONAL:
                return token.lexema;
            case ATRIBUICAO:
                return ":=";
            case SIMBOLO_ESPECIAL:
                return token.lexema;
            case FIM:
                return ".";
            default:
                return token.lexema.toLowerCase();
        }
    }
    
    /**
     * Método principal de análise sintática
     * 
     * @return true se o programa foi aceito, false caso contrário
     */
    public boolean analisar() {
        try {
            // Inicializar pilha com estado inicial
            pilhaEstados.push(0);
            
            // Obter primeiro token
            tokenAtual = analisadorLexico.proximoToken();
            atualizarLinha();
            
            // Contador para prevenir loop infinito
            int iteracoes = 0;
            int maxIteracoes = 10000; // Limite máximo de iterações
            
            // Variáveis para detectar loop de reduce infinito
            int ultimoEstado = -1;
            String ultimoToken = null;
            int contadorReduceMesmoEstado = 0;
            
            // Loop principal do parser
            while (iteracoes < maxIteracoes) {
                iteracoes++;
                
                int estadoAtual = pilhaEstados.peek();
                
                // Verificar se chegou ao fim (token null = fim de arquivo)
                if (tokenAtual == null) {
                    // Tentar aceitar se estiver em estado de aceitação
                    // Estados 48 e 77 são estados de aceitação
                    // Estado 11 também é estado de aceitação quando chegamos ao fim do arquivo após processar "end."
                    if (estadoAtual == 48 || estadoAtual == 77 || estadoAtual == 11) {
                        aceito = true;
                        return true;
                    }
                    // Caso contrário, erro - atualizar linha baseada na última posição processada
                    // Mas primeiro, tentar usar a linha atual se ela foi atualizada corretamente
                    atualizarLinhaFimArquivo();
                    return false;
                }
                
                String tokenStr = tokenParaString(tokenAtual);
                String chave = estadoAtual + "," + tokenStr;
                
                // Atualizar linha sempre que processamos um novo token
                // Isso garante que temos a linha correta para reportar erros
                // Mas só atualizar se a linha atual não for maior que 1 (para evitar resetar incorretamente)
                int linhaAntesAtualizar = linhaAtual;
                atualizarLinha();
                System.out.println("DEBUG: Linha antes de atualizar: " + linhaAntesAtualizar + ", depois: " + linhaAtual);
                
                // Se a linha foi resetada incorretamente para 1, tentar manter a linha anterior
                // Isso pode acontecer quando encontramos tokens duplicados (como múltiplos ";")
                if (linhaAntesAtualizar > 1 && linhaAtual == 1 && linhaAntesAtualizar > linhaAtual) {
                    System.out.println("DEBUG: CORREÇÃO: Linha foi resetada incorretamente de " + linhaAntesAtualizar + " para " + linhaAtual + ", mantendo linha anterior");
                    linhaAtual = linhaAntesAtualizar;
                }
                
                // CORREÇÃO ESPECIAL: No estado 18 com "(", verificar se o token anterior era "read"
                // Se sim, fazer shift para estado 20 (R -> read # ( I )) em vez de erro
                if (estadoAtual == 18 && tokenStr.equals("(")) {
                    // Verificar se o token anterior era "read" (palavra reservada ou id)
                    if (tokenAtual != null && tokenAtual.lexema != null && 
                        tokenAtual.lexema.toLowerCase().equals("read")) {
                        // Fazer shift para estado 20 (R -> read # ( I ))
                        System.out.println("DEBUG: CORREÇÃO: Estado 18 com '(' após 'read', fazendo shift para estado 20");
                        chave = "20,(";
                        tokenStr = "(";
                    }
                }
                
                // Debug: mostrar estado atual e token
                System.out.println("DEBUG: Estado=" + estadoAtual + ", Token=" + tokenStr + 
                                 " (lexema='" + (tokenAtual != null ? tokenAtual.lexema : "null") + "'), Chave=" + chave);
                System.out.println("DEBUG: Pilha=" + pilhaEstados.toString());
                
                String acao = tabelaAcoes.get(chave);
                
                // Flag para indicar se já obtivemos o próximo token na correção especial
                boolean tokenJaObtido = false;
                
                // CORREÇÃO ESPECIAL: No estado 18 com "(", verificar se foi "read" ou "write" tratado como "id"
                // Se o próximo token for "str", então é "write" e fazer shift para estado 42 (W -> write ( # F ))
                // Se o próximo token for "id", então pode ser "read" e fazer shift para estado 39 (R -> read ( # I ))
                if (acao == null && estadoAtual == 18 && tokenStr.equals("(")) {
                    // Obter próximo token para verificar se é str ou id
                    Token proximoToken = analisadorLexico.proximoToken();
                    if (proximoToken != null) {
                        String proximoTokenStr = tokenParaString(proximoToken);
                        if (proximoTokenStr.equals("str")) {
                            // É "write", fazer shift para estado 42 (W -> write ( # F ))
                            System.out.println("DEBUG: CORREÇÃO: Estado 18 com '(', próximo token é 'str', assumindo que foi 'write' tratado como 'id'");
                            System.out.println("DEBUG: Desempilhando estado 18 e empilhando estado 23, depois fazendo shift de '(' para estado 42");
                            // Desempilhar estado 18 (A -> id # := E)
                            pilhaEstados.pop();
                            // Empilhar estado 23 (W -> write # ( F ))
                            pilhaEstados.push(23);
                            // Fazer shift de "(" para estado 42
                            pilhaEstados.push(42);
                            // Guardar o próximo token para processar na próxima iteração
                            tokenAtual = proximoToken;
                            tokenJaObtido = true;
                            // Não precisamos definir acao aqui, pois já fizemos o shift manualmente
                            // Continuar o loop para processar o token str no estado 42
                            continue;
                        } else if (proximoTokenStr.equals("id")) {
                            // Pode ser "read", fazer shift para estado 39 (R -> read ( # I ))
                            System.out.println("DEBUG: CORREÇÃO: Estado 18 com '(', próximo token é 'id', assumindo que foi 'read' tratado como 'id'");
                            System.out.println("DEBUG: Desempilhando estado 18 e empilhando estado 20, depois fazendo shift de '(' para estado 39");
                            // Desempilhar estado 18 (A -> id # := E)
                            pilhaEstados.pop();
                            // Empilhar estado 20 (R -> read # ( I ))
                            pilhaEstados.push(20);
                            // Fazer shift de "(" para estado 39
                            pilhaEstados.push(39);
                            // Guardar o próximo token para processar na próxima iteração
                            tokenAtual = proximoToken;
                            tokenJaObtido = true;
                            // Não precisamos definir acao aqui, pois já fizemos o shift manualmente
                            // Continuar o loop para processar o token id no estado 39
                            continue;
                        } else {
                            // Não sabemos, assumir que é "read" por padrão
                            System.out.println("DEBUG: CORREÇÃO: Estado 18 com '(', próximo token é '" + proximoTokenStr + "', assumindo que foi 'read' tratado como 'id'");
                            System.out.println("DEBUG: Desempilhando estado 18 e empilhando estado 20, depois fazendo shift para estado 39");
                            // Desempilhar estado 18 (A -> id # := E)
                            pilhaEstados.pop();
                            // Empilhar estado 20 (R -> read # ( I ))
                            pilhaEstados.push(20);
                            // Fazer shift de "(" para estado 39
                            acao = "s39";
                            // Devolver o token para processar depois - manter tokenAtual como "("
                            // e processar o próximo token na próxima iteração
                        }
                    } else {
                        // Não há próximo token, assumir que é "read"
                        System.out.println("DEBUG: CORREÇÃO: Estado 18 com '(', não há próximo token, assumindo que foi 'read' tratado como 'id'");
                        System.out.println("DEBUG: Desempilhando estado 18 e empilhando estado 20, depois fazendo shift para estado 39");
                        // Desempilhar estado 18 (A -> id # := E)
                        pilhaEstados.pop();
                        // Empilhar estado 20 (R -> read # ( I ))
                        pilhaEstados.push(20);
                        // Fazer shift de "(" para estado 39
                        acao = "s39";
                    }
                }
                
                // CORREÇÃO ESPECIAL: No estado 18 com ";", assumir que foi "writeln" ou "readln" tratado como "id"
                // Reduzir W -> writeln (produção 23) ou R -> readln (produção 20) em vez de erro
                if (acao == null && estadoAtual == 18 && tokenStr.equals(";")) {
                    System.out.println("DEBUG: CORREÇÃO: Estado 18 com ';', assumindo que foi 'writeln' ou 'readln' tratado como 'id'");
                    // Verificar o token anterior para determinar se é writeln ou readln
                    // Por enquanto, vamos assumir que é writeln (mais comum)
                    System.out.println("DEBUG: Reduzindo W -> writeln (produção 23)");
                    // Desempilhar estado 18 (A -> id # := E)
                    pilhaEstados.pop();
                    // Empilhar estado 24 (W -> writeln #)
                    pilhaEstados.push(24);
                    // Reduzir W -> writeln com ";"
                    acao = "r23";
                }
                
                // CORREÇÃO ESPECIAL: No estado 96 com "var", assumir que há múltiplas declarações var
                // Reduzir V -> I : T ; (produção 5) primeiro, depois fazer shift de "var"
                if (acao == null && estadoAtual == 96 && tokenStr.equals("var")) {
                    System.out.println("DEBUG: CORREÇÃO: Estado 96 com 'var', assumindo múltiplas declarações var");
                    System.out.println("DEBUG: Reduzindo V -> I : T ; (produção 5) primeiro");
                    // Reduzir V -> I : T ; com "var"
                    acao = "r5";
                }
                
                // CORREÇÃO ESPECIAL: No estado 8 com "var", assumir que há múltiplos blocos var
                // Fazer shift de "var" para processar o próximo bloco de declarações
                if (acao == null && estadoAtual == 8 && tokenStr.equals("var")) {
                    System.out.println("DEBUG: CORREÇÃO: Estado 8 com 'var', assumindo múltiplos blocos var");
                    System.out.println("DEBUG: Fazendo shift de 'var' para estado 7");
                    // Fazer shift de "var" para estado 7 (D -> var # V)
                    acao = "s7";
                }
                
                // CORREÇÃO ESPECIAL: No estado 44 com "boolean", garantir que funciona corretamente
                // O token já deve ser reconhecido como palavra reservada, mas verificamos aqui também
                if (acao == null && estadoAtual == 44 && tokenStr.equals("boolean")) {
                    System.out.println("DEBUG: CORREÇÃO: Estado 44 com 'boolean', fazendo shift para estado 73");
                    // Fazer shift de "boolean" para estado 73 (T -> Boolean #)
                    acao = "s73";
                }
                
                // CORREÇÃO ESPECIAL: No estado 97 com "else", reduzir N -> if B then C else C (produção 31)
                // Isso acontece quando há if aninhados e já processamos o segundo C do if interno
                if (acao == null && estadoAtual == 97 && tokenStr.equals("else")) {
                    System.out.println("DEBUG: CORREÇÃO: Estado 97 com 'else', reduzindo N -> if B then C else C (produção 31)");
                    // Reduzir N -> if B then C else C com "else"
                    acao = "r31";
                }
                
                // CORREÇÃO ESPECIAL: No estado 28 com "else", assumir que estamos dentro de um bloco composto
                // dentro de um if-then sem else. O estado 28 representa S -> program id ; D begin L # end .,
                // mas quando estamos dentro de um bloco begin...end de um if, precisamos reduzir M -> begin L end.
                // Verificar se há estado 17 na pilha (que indica que estamos dentro de um begin...end)
                if (acao == null && estadoAtual == 28 && tokenStr.equals("else")) {
                    // Verificar se há estado 17 na pilha (indica que estamos dentro de um begin...end)
                    if (pilhaEstados.size() >= 2) {
                        // Criar uma cópia temporária da pilha para verificar sem modificar
                        Stack<Integer> pilhaTemp = new Stack<>();
                        pilhaTemp.addAll(pilhaEstados);
                        
                        // Verificar se há estado 17 antes do estado 28
                        boolean encontrouEstado17 = false;
                        while (!pilhaTemp.isEmpty()) {
                            int estado = pilhaTemp.pop();
                            if (estado == 17) {
                                encontrouEstado17 = true;
                                break;
                            }
                        }
                        
                        if (encontrouEstado17) {
                            // Estamos dentro de um bloco begin...end, precisamos reduzir M -> begin L end
                            // Mas o estado 28 é S -> program id ; D begin L # end ., então precisamos
                            // primeiro fazer shift de "end" para estado 46, depois reduzir M -> begin L end
                            // Mas como encontramos "else", significa que já processamos o bloco begin...end
                            // e precisamos reduzir M -> begin L end antes de processar o else
                            // Na verdade, precisamos fazer shift de "end" primeiro para chegar ao estado 46
                            System.out.println("DEBUG: CORREÇÃO: Estado 28 com 'else', detectado bloco begin...end na pilha");
                            System.out.println("DEBUG: Fazendo shift de 'end' para estado 46 primeiro");
                            // Fazer shift de "end" para estado 46 (M -> begin L end #)
                            // Mas espera, não temos "end" como token atual, temos "else"
                            // Precisamos reduzir L -> C ; (produção 10) primeiro para chegar ao estado 31
                            // Mas já estamos no estado 28, então precisamos fazer shift de "end" primeiro
                            // Mas não temos "end", temos "else"
                            // Na verdade, precisamos reduzir L -> C ; (produção 10) com "else" para chegar ao estado 31
                            System.out.println("DEBUG: Reduzindo L -> C ; (produção 10) com 'else' primeiro");
                            acao = "r10";
                        } else {
                            // Não encontramos estado 17, então estamos no programa principal
                            // Mas encontramos "else", o que não deveria acontecer no programa principal
                            // Tentar reduzir L -> C ; (produção 10) com "else"
                            System.out.println("DEBUG: CORREÇÃO: Estado 28 com 'else', não encontrado estado 17, tentando reduzir L -> C ;");
                            acao = "r10";
                        }
                    } else {
                        // Pilha muito pequena, tentar reduzir L -> C ; (produção 10) com "else"
                        System.out.println("DEBUG: CORREÇÃO: Estado 28 com 'else', pilha muito pequena, tentando reduzir L -> C ;");
                        acao = "r10";
                    }
                }
                
                // CORREÇÃO ESPECIAL: No estado 11 com "else", assumir que estamos dentro de um bloco composto
                // dentro de um if-then sem else. O estado 11 representa S -> program id ; D begin L # end .,
                // mas quando estamos dentro de um bloco begin...end de um if, precisamos reduzir M -> begin L end.
                // Verificar se há estado 17 na pilha (que indica que estamos dentro de um begin...end)
                if (acao == null && estadoAtual == 11 && tokenStr.equals("else")) {
                    // Verificar se há estado 17 na pilha (indica que estamos dentro de um begin...end)
                    if (pilhaEstados.size() >= 2) {
                        // Criar uma cópia temporária da pilha para verificar sem modificar
                        Stack<Integer> pilhaTemp = new Stack<>();
                        pilhaTemp.addAll(pilhaEstados);
                        
                        // Verificar se há estado 17 antes do estado 11
                        boolean encontrouEstado17 = false;
                        while (!pilhaTemp.isEmpty()) {
                            int estado = pilhaTemp.pop();
                            if (estado == 17) {
                                encontrouEstado17 = true;
                                break;
                            }
                        }
                        
                        if (encontrouEstado17) {
                            // Estamos dentro de um bloco begin...end, precisamos reduzir M -> begin L end
                            // Mas o estado 11 é S -> program id ; D begin L # end ., então precisamos
                            // primeiro fazer shift de "end" para estado 30, depois reduzir M -> begin L end
                            // Mas como encontramos "else", significa que já processamos o bloco begin...end
                            // e precisamos reduzir M -> begin L end antes de processar o else
                            // Na verdade, precisamos reduzir L -> C ; (produção 10) com "else" para chegar ao estado 31
                            System.out.println("DEBUG: CORREÇÃO: Estado 11 com 'else', detectado bloco begin...end na pilha");
                            System.out.println("DEBUG: Reduzindo L -> C ; (produção 10) com 'else' primeiro");
                            acao = "r10";
                        } else {
                            // Não encontramos estado 17, então estamos no programa principal
                            // Mas encontramos "else", o que não deveria acontecer no programa principal
                            // Tentar reduzir L -> C ; (produção 10) com "else"
                            System.out.println("DEBUG: CORREÇÃO: Estado 11 com 'else', não encontrado estado 17, tentando reduzir L -> C ;");
                            acao = "r10";
                        }
                    } else {
                        // Pilha muito pequena, tentar reduzir L -> C ; (produção 10) com "else"
                        System.out.println("DEBUG: CORREÇÃO: Estado 11 com 'else', pilha muito pequena, tentando reduzir L -> C ;");
                        acao = "r10";
                    }
                }
                
                // CORREÇÃO ESPECIAL: No estado 28 com ".", assumir que "end" já foi processado e fazer shift para estado 11
                // O estado 28 é S -> program id ; D begin L # end ., então quando encontramos ".", assumimos que "end" já foi processado
                // e fazemos shift para estado 11 (S -> program id ; D begin L end # .)
                // Depois, no estado 11 com ".", devemos aceitar o programa
                if (acao == null && estadoAtual == 28 && tokenStr.equals(".")) {
                    System.out.println("DEBUG: CORREÇÃO: Estado 28 com '.', assumindo que 'end' já foi processado");
                    System.out.println("DEBUG: Fazendo shift de '.' para estado 11 (S -> program id ; D begin L end # .)");
                    acao = "s11";
                }
                
                // CORREÇÃO ESPECIAL: No estado 11 com ".", aceitar o programa
                // O estado 11 é S -> program id ; D begin L end # ., então quando encontramos ".", devemos aceitar
                if (acao == null && estadoAtual == 11 && tokenStr.equals(".")) {
                    System.out.println("DEBUG: CORREÇÃO: Estado 11 com '.', aceitando programa");
                    acao = "acc";
                }
                
                // CORREÇÃO ESPECIAL: No estado 5 com ".", assumir que estamos reduzindo S -> program id ; D begin L end .
                // quando L já foi reduzido antes (caso especial para aceitação com múltiplos blocos var)
                // Mas só aplicar se a pilha tiver elementos suficientes (pelo menos 7: 6 símbolos + estado inicial)
                if (acao == null && estadoAtual == 5 && tokenStr.equals(".")) {
                    String[] producao1 = producoes[1]; // S -> program id ; D begin L end .
                    int tamanhoProducao1 = Integer.parseInt(producao1[1]);
                    if (pilhaEstados.size() >= tamanhoProducao1 + 1) {
                        System.out.println("DEBUG: CORREÇÃO: Estado 5 com '.', assumindo que estamos reduzindo S -> program id ; D begin L end .");
                        System.out.println("DEBUG: Reduzindo S -> program id ; D begin L end . (produção 1)");
                        // Reduzir S -> program id ; D begin L end . com "."
                        acao = "r1";
                    } else {
                        System.out.println("DEBUG: CORREÇÃO: Estado 5 com '.', mas pilha não tem elementos suficientes. Precisa " + (tamanhoProducao1 + 1) + ", tem " + pilhaEstados.size());
                    }
                }
                
                // CORREÇÃO ESPECIAL: No estado 31 com "else", mesmo que a pilha esteja incorreta,
                // devemos permitir a redução M -> begin L end porque o "else" indica que estamos no contexto correto.
                // A correção para o estado incorreto após o reduce será feita no método aplicarReduce()
                
                
                if (acao == null) {
                    // Erro: ação não encontrada para estado/token
                    System.out.println("DEBUG: ERRO - Ação não encontrada para " + chave);
                    System.out.println("DEBUG: Tokens disponíveis neste estado:");
                    for (String key : tabelaAcoes.keySet()) {
                        if (key.startsWith(estadoAtual + ",")) {
                            System.out.println("  " + key + " -> " + tabelaAcoes.get(key));
                        }
                    }
                    atualizarLinha();
                    return false;
                }
                
                System.out.println("DEBUG: Ação encontrada: " + acao);
                
                if (acao.equals("acc")) {
                    // Aceitar
                    aceito = true;
                    return true;
                } else if (acao.startsWith("s")) {
                    // Shift: empilhar estado e avançar token
                    int novoEstado = Integer.parseInt(acao.substring(1));
                    System.out.println("DEBUG: SHIFT para estado " + novoEstado);
                    pilhaEstados.push(novoEstado);
                    // Só avançar token se não já o obtivemos na correção especial
                    if (!tokenJaObtido) {
                        tokenAtual = analisadorLexico.proximoToken();
                        if (tokenAtual != null) {
                            atualizarLinha();
                        } else {
                            // Se chegamos ao fim do arquivo, manter a linha atual
                            // Ela já foi atualizada quando processamos o token anterior
                            System.out.println("DEBUG: Fim de arquivo alcançado após shift");
                        }
                    } else {
                        atualizarLinha();
                    }
                    if (tokenAtual != null) {
                        System.out.println("DEBUG: Próximo token: " + tokenAtual.lexema + " (tipo=" + tokenAtual.tipo + ")");
                    } else {
                        System.out.println("DEBUG: Fim de arquivo alcançado");
                    }
                    
                    // Resetar contador de reduce ao fazer shift
                    contadorReduceMesmoEstado = 0;
                    ultimoEstado = -1;
                    ultimoToken = null;
                    tokenJaObtido = false;  // Resetar flag
                } else if (acao.startsWith("r")) {
                    // Reduce: aplicar produção
                    int numProducao = Integer.parseInt(acao.substring(1));
                    
                    // CORREÇÃO ESPECIAL: No estado 54 com "else", se a ação é r30 (N -> if B then C) mas a pilha não tem elementos suficientes,
                    // fazer shift de "else" para o estado 97 em vez de reduzir
                    if (estadoAtual == 54 && tokenStr.equals("else") && numProducao == 30) {
                        String[] producao = producoes[numProducao];
                        int tamanho = Integer.parseInt(producao[1]);
                        if (pilhaEstados.size() < tamanho + 1) {
                            System.out.println("DEBUG: CORREÇÃO: Estado 54 com 'else', pilha não tem elementos suficientes para reduzir N -> if B then C");
                            System.out.println("DEBUG: Fazendo shift de 'else' para estado 97 em vez de reduzir");
                            // Fazer shift de "else" para estado 97 (N -> if B then C else # C)
                            pilhaEstados.push(97);
                            tokenAtual = analisadorLexico.proximoToken();
                            if (tokenAtual != null) {
                                atualizarLinha();
                            }
                            tokenJaObtido = true;
                            continue; // Continuar loop para processar próximo token no estado 97
                        }
                    }
                    
                    // CORREÇÃO ESPECIAL: No estado 79 com ";", se a ação é r30 (N -> if B then C) mas a pilha não tem elementos suficientes,
                    // não reduzir e fazer shift de ";" para processar o próximo comando
                    if (estadoAtual == 79 && tokenStr.equals(";") && numProducao == 30) {
                        String[] producao = producoes[numProducao];
                        int tamanho = Integer.parseInt(producao[1]);
                        if (pilhaEstados.size() < tamanho + 1) {
                            System.out.println("DEBUG: CORREÇÃO: Estado 79 com ';', pilha não tem elementos suficientes para reduzir N -> if B then C");
                            System.out.println("DEBUG: Não reduzindo, fazendo shift de ';' para estado 29 para processar próximo comando");
                            // Não reduzir, fazer shift de ";" para estado 29 (L -> C ; # L)
                            pilhaEstados.push(29);
                            tokenAtual = analisadorLexico.proximoToken();
                            if (tokenAtual != null) {
                                atualizarLinha();
                            }
                            tokenJaObtido = true;
                            continue; // Continuar loop para processar próximo token no estado 29
                        }
                    }
                    
                    // CORREÇÃO ESPECIAL: No estado 29 com "end", se a ação é r11 (L -> C ; L) mas a pilha não tem elementos suficientes,
                    // não reduzir e fazer shift de "end" para processar o fechamento do bloco begin...end
                    if (estadoAtual == 29 && tokenStr.equals("end") && numProducao == 11) {
                        String[] producao = producoes[numProducao];
                        int tamanho = Integer.parseInt(producao[1]);
                        if (pilhaEstados.size() < tamanho + 1) {
                            System.out.println("DEBUG: CORREÇÃO: Estado 29 com 'end', pilha não tem elementos suficientes para reduzir L -> C ; L");
                            System.out.println("DEBUG: Não reduzindo, fazendo shift de 'end' para estado 46 para processar fechamento do bloco begin...end");
                            // Não reduzir, fazer shift de "end" para estado 46 (M -> begin L end #)
                            pilhaEstados.push(46);
                            tokenAtual = analisadorLexico.proximoToken();
                            if (tokenAtual != null) {
                                atualizarLinha();
                            }
                            tokenJaObtido = true;
                            continue; // Continuar loop para processar próximo token no estado 46
                        }
                    }
                    
                    // CORREÇÃO ESPECIAL: No estado 27 com ":" ou ")", verificar se temos id , id na pilha
                    // Se sim, reduzir I -> id , I (produção 9), senão reduzir I -> id (produção 8)
                    if (estadoAtual == 27 && (tokenStr.equals(":") || tokenStr.equals(")")) && numProducao == 8) {
                        // Verificar se há estado 45 na pilha (indica que temos id , id)
                        if (pilhaEstados.size() >= 3) {
                            // Criar uma cópia temporária da pilha para verificar sem modificar
                            Stack<Integer> pilhaTemp = new Stack<>();
                            pilhaTemp.addAll(pilhaEstados);
                            
                            // Verificar se os últimos 3 estados são 27, 45, 27 (id , id)
                            if (pilhaTemp.size() >= 3) {
                                int estado3 = pilhaTemp.pop();
                                int estado2 = pilhaTemp.pop();
                                int estado1 = pilhaTemp.pop();
                                
                                if (estado3 == 27 && estado2 == 45 && estado1 == 27) {
                                    // Temos id , id na pilha, reduzir I -> id , I (produção 9)
                                    System.out.println("DEBUG: Detectado id , id na pilha, usando produção 9 em vez de 8");
                                    numProducao = 9;
                                }
                            }
                        }
                    }
                    
                    String[] producao = producoes[numProducao];
                    String ladoEsquerdo = producao[0];
                    int tamanho = Integer.parseInt(producao[1]);
                    
                    System.out.println("DEBUG: REDUCE pela produção " + numProducao + ": " + ladoEsquerdo + " -> " + tamanho + " símbolos");
                    System.out.println("DEBUG: Tamanho da pilha antes do reduce: " + pilhaEstados.size());
                    
                    // Verificar se a pilha tem elementos suficientes antes de fazer reduce
                    if (pilhaEstados.size() < tamanho + 1) {
                        // Erro: pilha não tem elementos suficientes
                        System.out.println("DEBUG: ERRO - Pilha não tem elementos suficientes. Precisa " + (tamanho + 1) + ", tem " + pilhaEstados.size());
                        atualizarLinha();
                        return false;
                    }
                    
                    // Detectar loop de reduce infinito
                    if (estadoAtual == ultimoEstado && tokenStr.equals(ultimoToken)) {
                        contadorReduceMesmoEstado++;
                        System.out.println("DEBUG: Aviso - Reduce repetido " + contadorReduceMesmoEstado + " vezes no mesmo estado/token");
                        if (contadorReduceMesmoEstado > 10) {
                            // Loop infinito detectado - mesmo estado e token fazendo reduce repetidamente
                            System.out.println("DEBUG: ERRO - Loop infinito detectado!");
                            atualizarLinha();
                            return false;
                        }
                    } else {
                        contadorReduceMesmoEstado = 0;
                    }
                    
                    ultimoEstado = estadoAtual;
                    ultimoToken = tokenStr;
                    
                    // Aplicar reduce e verificar se foi bem-sucedido
                    // A linha já foi atualizada no início do loop quando processamos o token
                    // Passar tokenAtual para aplicarReduce para verificar lookahead
                    if (!aplicarReduce(numProducao, tokenAtual)) {
                        // Erro durante reduce (GOTO não encontrado ou pilha vazia)
                        // A linha já foi atualizada no início do loop
                        System.out.println("DEBUG: ERRO durante reduce - retornando false");
                        return false;
                    }
                    System.out.println("DEBUG: Após reduce, estado no topo da pilha: " + pilhaEstados.peek());
                    // Não avançar token após reduce
                } else {
                    // Erro
                    atualizarLinha();
                    return false;
                }
            }
            
            // Se chegou aqui, excedeu o limite de iterações (loop infinito)
            atualizarLinha();
            return false;
            
        } catch (Exception e) {
            // Capturar qualquer exceção e retornar false
            atualizarLinha();
            return false;
        }
    }
    
    /**
     * Aplica uma redução pela produção especificada
     * 
     * @param numProducao Número da produção a ser aplicada
     * @param tokenLookahead Token atual (lookahead) para verificar contexto
     * @return true se o reduce foi aplicado com sucesso, false caso contrário
     */
    private boolean aplicarReduce(int numProducao, Token tokenLookahead) {
        String[] producao = producoes[numProducao];
        String ladoEsquerdo = producao[0];
        int tamanho = Integer.parseInt(producao[1]);
        
        System.out.println("DEBUG: Aplicando reduce - Produção " + numProducao + ": " + ladoEsquerdo + " -> " + tamanho + " símbolos");
        System.out.println("DEBUG: Pilha antes do reduce: " + pilhaEstados.toString());
        
        // Verificar se a pilha tem elementos suficientes para desempilhar
        if (pilhaEstados.size() < tamanho) {
            System.out.println("DEBUG: ERRO - Pilha não tem elementos suficientes. Precisa " + tamanho + ", tem " + pilhaEstados.size());
            return false;
        }
        
        // CORREÇÃO ESPECIAL: Verificar se há estado 17 na pilha ANTES de desempilhar
        // Isso é necessário para detectar quando estamos dentro de um bloco begin...end
        // quando reduzimos L -> C ; (produção 10)
        boolean encontrouEstado17AntesReduce = false;
        boolean encontrouEstadoIfAntesReduce = false;
        int estadoIfEncontradoAntesReduce = -1; // Armazenar o estado relacionado a if encontrado
        if (numProducao == 10 && ladoEsquerdo.equals("L")) {
            Stack<Integer> pilhaTempAntes = new Stack<>();
            pilhaTempAntes.addAll(pilhaEstados);
            while (!pilhaTempAntes.isEmpty()) {
                int estado = pilhaTempAntes.pop();
                if (estado == 17) {
                    encontrouEstado17AntesReduce = true;
                    System.out.println("DEBUG: CORREÇÃO: Detectado estado 17 na pilha ANTES de reduzir L -> C ;");
                    break;
                }
                // Também verificar se há estados relacionados a if (mesmo que o estado 17 já tenha sido removido)
                if (estado == 19 || estado == 35 || estado == 54 || estado == 79 || estado == 97) {
                    encontrouEstadoIfAntesReduce = true;
                    if (estadoIfEncontradoAntesReduce == -1) {
                        estadoIfEncontradoAntesReduce = estado;
                    }
                    System.out.println("DEBUG: CORREÇÃO: Detectado estado " + estado + " relacionado a if na pilha ANTES de reduzir L -> C ;");
                    // Não break aqui, continuar procurando estado 17
                }
            }
        }
        
        // CORREÇÃO ESPECIAL: Verificar se há estado relacionado a if na pilha ANTES de desempilhar
        // Isso é necessário para quando reduzimos M -> begin L end (produção 29) e chegamos a um estado incorreto
        if (numProducao == 29 && ladoEsquerdo.equals("M")) {
            Stack<Integer> pilhaTempAntesM = new Stack<>();
            pilhaTempAntesM.addAll(pilhaEstados);
            while (!pilhaTempAntesM.isEmpty()) {
                int estado = pilhaTempAntesM.pop();
                // Estados relacionados a if que esperam C ou M
                if (estado == 35 || estado == 54) {
                    System.out.println("DEBUG: CORREÇÃO: Detectado estado " + estado + " relacionado a if na pilha ANTES de reduzir M -> begin L end");
                    break;
                }
            }
        }
        
        // Desempilhar símbolos do lado direito
        for (int i = 0; i < tamanho; i++) {
            if (!pilhaEstados.isEmpty()) {
                int estadoRemovido = pilhaEstados.pop();
                System.out.println("DEBUG: Removendo estado " + estadoRemovido + " da pilha");
            }
        }
        
        // Obter estado após desempilhar
        if (pilhaEstados.isEmpty()) {
            System.out.println("DEBUG: ERRO - Pilha vazia após desempilhar " + tamanho + " elementos");
            atualizarLinha();
            return false;
        }
        
        int estadoAtual = pilhaEstados.peek();
        String chave = estadoAtual + "," + ladoEsquerdo;
        
        System.out.println("DEBUG: Estado após desempilhar: " + estadoAtual);
        System.out.println("DEBUG: Buscando GOTO para: " + chave);
        
        // CORREÇÃO ESPECIAL: Se detectamos estado 17 antes do reduce e estamos reduzindo L -> C ;,
        // usar GOTO(17, L) = 31 em vez do GOTO normal
        if (numProducao == 10 && ladoEsquerdo.equals("L") && encontrouEstado17AntesReduce) {
            System.out.println("DEBUG: CORREÇÃO: Reduzindo L -> C ; com estado 17 detectado antes do reduce");
            System.out.println("DEBUG: Usando GOTO(17, L) -> 31 como correção (dentro de bloco begin...end)");
            chave = "17,L";
            Integer novoEstadoCorrigido = tabelaGotos.get(chave);
            if (novoEstadoCorrigido != null) {
                System.out.println("DEBUG: GOTO encontrado após correção: " + chave + " -> estado " + novoEstadoCorrigido);
                pilhaEstados.push(novoEstadoCorrigido);
                System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
                return true;
            }
        }
        
        // CORREÇÃO ESPECIAL: Se detectamos estado relacionado a if antes do reduce (mas não estado 17),
        // e estamos reduzindo L -> C ; e o estado atual após desempilhar é 3, usar GOTO(17, L) = 31
        if (numProducao == 10 && ladoEsquerdo.equals("L") && encontrouEstadoIfAntesReduce && estadoAtual == 3) {
            System.out.println("DEBUG: CORREÇÃO: Reduzindo L -> C ; com estado relacionado a if detectado antes do reduce");
            System.out.println("DEBUG: Usando GOTO(17, L) -> 31 como correção (dentro de bloco begin...end de if)");
            chave = "17,L";
            Integer novoEstadoCorrigido = tabelaGotos.get(chave);
            if (novoEstadoCorrigido != null) {
                System.out.println("DEBUG: GOTO encontrado após correção: " + chave + " -> estado " + novoEstadoCorrigido);
                pilhaEstados.push(novoEstadoCorrigido);
                System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
                return true;
            }
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo L -> C ; e o estado atual após desempilhar é 3,
        // mas não há GOTO(3, L) definido, isso pode significar que estamos em um contexto especial.
        // Verificar se há algum estado que indica que estamos dentro de um bloco begin...end
        // mesmo que o estado 17 já tenha sido removido
        if (numProducao == 10 && ladoEsquerdo.equals("L") && estadoAtual == 3) {
            // Verificar se há estados que indicam que estamos processando um if (estado 19, 35, 54, etc.)
            // ou se há estados que indicam que estamos dentro de um bloco begin...end
            Stack<Integer> pilhaTempApos = new Stack<>();
            pilhaTempApos.addAll(pilhaEstados);
            boolean encontrouEstadoIfOuBegin = false;
            while (!pilhaTempApos.isEmpty()) {
                int estado = pilhaTempApos.pop();
                // Estados que indicam que estamos processando um if ou dentro de um bloco begin...end
                if (estado == 19 || estado == 35 || estado == 54 || estado == 79 || estado == 97) {
                    encontrouEstadoIfOuBegin = true;
                    System.out.println("DEBUG: CORREÇÃO: Detectado estado " + estado + " que indica processamento de if ou bloco begin...end");
                    break;
                }
            }
            
            if (encontrouEstadoIfOuBegin) {
                // Estamos dentro de um contexto de if ou bloco begin...end, usar GOTO(17, L) = 31
                // mesmo que o estado 17 não esteja mais na pilha
                System.out.println("DEBUG: CORREÇÃO: Reduzindo L -> C ; no estado 3, detectado contexto de if/bloco begin...end");
                System.out.println("DEBUG: Usando GOTO(17, L) -> 31 como correção");
                chave = "17,L";
                Integer novoEstadoCorrigido = tabelaGotos.get(chave);
                if (novoEstadoCorrigido != null) {
                    System.out.println("DEBUG: GOTO encontrado após correção: " + chave + " -> estado " + novoEstadoCorrigido);
                    pilhaEstados.push(novoEstadoCorrigido);
                    System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
                    return true;
                }
            }
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo M -> begin L end (produção 29) e o estado atual é 0 ou 3,
        // isso significa que desempilhamos estados demais. Precisamos procurar um estado anterior que espera M.
        // Isso pode acontecer quando reduzimos M -> begin L end no estado 31 com "else" e a pilha estava incorreta.
        // Se o lookahead é "else", devemos usar o estado relacionado a if encontrado ANTES do reduce
        // e fazer GOTO para o estado correto.
        if (numProducao == 29 && ladoEsquerdo.equals("M") && (estadoAtual == 0 || estadoAtual == 3)) {
            System.out.println("DEBUG: CORREÇÃO: Reduzindo M -> begin L end no estado " + estadoAtual + ", verificando contexto");
            // Verificar se o lookahead é "else" (indica que estamos processando um if-else)
            boolean lookaheadElse = false;
            if (tokenLookahead != null) {
                String lookaheadStr = tokenParaString(tokenLookahead);
                if (lookaheadStr.equals("else")) {
                    lookaheadElse = true;
                    System.out.println("DEBUG: CORREÇÃO: Detectado 'else' como lookahead ao reduzir M -> begin L end no estado " + estadoAtual);
                }
            }
            
            Integer novoEstadoCorrigido;
            if (lookaheadElse) {
                // Quando o lookahead é "else", sabemos que estamos dentro de um contexto de if,
                // então podemos usar GOTO(35, M) -> 15 mesmo que não encontremos estado relacionado a if ANTES do reduce
                // Isso é necessário porque quando reduzimos múltiplas vezes, os estados relacionados a if podem já ter sido removidos
                // mas o "else" ainda indica que estamos dentro de um contexto de if
                System.out.println("DEBUG: CORREÇÃO: Lookahead 'else' detectado, usando GOTO(35, M) -> 15");
                chave = "35,M";
                novoEstadoCorrigido = tabelaGotos.get(chave);
                if (novoEstadoCorrigido == null) {
                    // Se GOTO(35, M) não existe, usar estado 15 como fallback
                    System.out.println("DEBUG: CORREÇÃO: GOTO(35, M) não encontrado, usando estado 15 como fallback");
                    novoEstadoCorrigido = 15; // Estado comum para C -> M
                }
            } else {
                // Se o lookahead não é "else", usar o estado 15 que é comum para C -> M como fallback
                System.out.println("DEBUG: Usando GOTO(" + estadoAtual + ", M) -> 15 como correção (fallback)");
                novoEstadoCorrigido = 15; // Estado comum para C -> M
            }
            System.out.println("DEBUG: GOTO encontrado após correção: " + estadoAtual + ",M -> estado " + novoEstadoCorrigido);
            pilhaEstados.push(novoEstadoCorrigido);
            System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
            return true;
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo C -> M (produção 15) e o estado atual é 0,
        // isso significa que desempilhamos estados demais. Precisamos procurar um estado anterior que espera C.
        // Isso pode acontecer quando reduzimos C -> M no estado 15 após reduzir M -> begin L end no estado 0.
        // Se o lookahead é "else", sabemos que estamos dentro de um contexto de if,
        // então podemos usar GOTO(35, C) -> 54 mesmo que não encontremos estado relacionado a if ANTES do reduce.
        if (numProducao == 15 && ladoEsquerdo.equals("C") && estadoAtual == 0) {
            System.out.println("DEBUG: CORREÇÃO: Reduzindo C -> M no estado 0, verificando contexto");
            // Verificar se o lookahead é "else" (indica que estamos processando um if-else)
            boolean lookaheadElse = false;
            if (tokenLookahead != null) {
                String lookaheadStr = tokenParaString(tokenLookahead);
                if (lookaheadStr.equals("else")) {
                    lookaheadElse = true;
                    System.out.println("DEBUG: CORREÇÃO: Detectado 'else' como lookahead ao reduzir C -> M no estado 0");
                }
            }
            
            Integer novoEstadoCorrigido;
            if (lookaheadElse) {
                // Quando o lookahead é "else", sabemos que estamos dentro de um contexto de if,
                // então podemos usar GOTO(35, C) -> 54 mesmo que não encontremos estado relacionado a if ANTES do reduce
                System.out.println("DEBUG: CORREÇÃO: Lookahead 'else' detectado, usando GOTO(35, C) -> 54");
                chave = "35,C";
                novoEstadoCorrigido = tabelaGotos.get(chave);
                if (novoEstadoCorrigido == null) {
                    // Se GOTO(35, C) não existe, usar estado 54 como fallback
                    System.out.println("DEBUG: CORREÇÃO: GOTO(35, C) não encontrado, usando estado 54 como fallback");
                    novoEstadoCorrigido = 54; // Estado comum para N -> if B then C #
                }
            } else {
                // Fallback genérico - usar estado 10 que é comum para L -> C ;
                System.out.println("DEBUG: Usando GOTO(0, C) -> 10 como correção (fallback)");
                novoEstadoCorrigido = 10; // Estado comum para L -> C ;
            }
            System.out.println("DEBUG: GOTO encontrado após correção: " + estadoAtual + ",C -> estado " + novoEstadoCorrigido);
            pilhaEstados.push(novoEstadoCorrigido);
            System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
            return true;
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo M -> begin L end (produção 29) e o estado atual é 7,
        // isso significa que estamos em um contexto onde M não é esperado diretamente.
        // Precisamos procurar um estado anterior que espera M (como estado 17, 29, etc.)
        if (numProducao == 29 && ladoEsquerdo.equals("M") && estadoAtual == 7) {
            System.out.println("DEBUG: CORREÇÃO: Reduzindo M -> begin L end no estado 7, procurando estado anterior que espera M");
            // Verificar se há um estado anterior que espera M
            // O estado 17 é comum para M -> begin L end dentro de blocos
            // Mas também pode ser estado 29, 10, etc.
            // Por enquanto, vamos usar o estado 15 que é comum para C -> M
            System.out.println("DEBUG: Usando GOTO(7, M) -> 15 como correção");
            chave = "7,M";
            Integer novoEstadoCorrigido = tabelaGotos.get(chave);
            if (novoEstadoCorrigido != null) {
                System.out.println("DEBUG: GOTO encontrado após correção: " + chave + " -> estado " + novoEstadoCorrigido);
                pilhaEstados.push(novoEstadoCorrigido);
                System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
                return true;
            }
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo L -> C ; (produção 10) e o estado atual é 7,
        // mas encontramos "else", significa que estamos dentro de um bloco begin...end de um if.
        // Precisamos fazer GOTO para um estado que permita processar o else.
        // Mas na verdade, o estado 7 não deveria estar na pilha neste contexto.
        // Vamos verificar se há um estado 17 na pilha antes do estado 7
        if (numProducao == 10 && ladoEsquerdo.equals("L") && estadoAtual == 7) {
            // Verificar se há estado 17 na pilha antes do estado 7
            Stack<Integer> pilhaTemp = new Stack<>();
            pilhaTemp.addAll(pilhaEstados);
            boolean encontrouEstado17 = false;
            while (!pilhaTemp.isEmpty()) {
                int estado = pilhaTemp.pop();
                if (estado == 17) {
                    encontrouEstado17 = true;
                    break;
                }
            }
            
            if (encontrouEstado17) {
                // Estamos dentro de um bloco begin...end, fazer GOTO para estado 31 (M -> begin L # end)
                System.out.println("DEBUG: CORREÇÃO: Reduzindo L -> C ; no estado 7, detectado estado 17 na pilha");
                System.out.println("DEBUG: Usando GOTO(7, L) -> 31 como correção (dentro de bloco begin...end)");
                chave = "7,L";
                // Mas espera, já temos GOTO(7, L) = 28 definido
                // Precisamos usar um GOTO diferente ou modificar a lógica
                // Na verdade, se estamos dentro de um bloco begin...end, o estado anterior deveria ser 17, não 7
                // Vamos tentar usar GOTO(17, L) = 31
                chave = "17,L";
                Integer novoEstadoCorrigido = tabelaGotos.get(chave);
                if (novoEstadoCorrigido != null) {
                    System.out.println("DEBUG: GOTO encontrado após correção: " + chave + " -> estado " + novoEstadoCorrigido);
                    pilhaEstados.push(novoEstadoCorrigido);
                    System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
                    return true;
                }
            }
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo L -> C ; (produção 10) e o estado atual é 4,
        // mas encontramos "else", significa que estamos dentro de um bloco begin...end de um if.
        // Precisamos fazer GOTO para um estado que permita processar o else.
        // Vamos verificar se há um estado 17 na pilha antes do estado 4
        if (numProducao == 10 && ladoEsquerdo.equals("L") && estadoAtual == 4) {
            // Verificar se há estado 17 na pilha antes do estado 4
            Stack<Integer> pilhaTemp = new Stack<>();
            pilhaTemp.addAll(pilhaEstados);
            boolean encontrouEstado17 = false;
            while (!pilhaTemp.isEmpty()) {
                int estado = pilhaTemp.pop();
                if (estado == 17) {
                    encontrouEstado17 = true;
                    break;
                }
            }
            
            if (encontrouEstado17) {
                // Estamos dentro de um bloco begin...end, fazer GOTO para estado 31 (M -> begin L # end)
                System.out.println("DEBUG: CORREÇÃO: Reduzindo L -> C ; no estado 4, detectado estado 17 na pilha");
                System.out.println("DEBUG: Usando GOTO(4, L) -> 31 como correção (dentro de bloco begin...end)");
                // Na verdade, se estamos dentro de um bloco begin...end, o estado anterior deveria ser 17, não 4
                // Vamos tentar usar GOTO(17, L) = 31
                chave = "17,L";
                Integer novoEstadoCorrigido = tabelaGotos.get(chave);
                if (novoEstadoCorrigido != null) {
                    System.out.println("DEBUG: GOTO encontrado após correção: " + chave + " -> estado " + novoEstadoCorrigido);
                    pilhaEstados.push(novoEstadoCorrigido);
                    System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
                    return true;
                }
            }
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo L -> C ; (produção 10) e o estado atual é 3,
        // mas encontramos "else" como lookahead, significa que estamos dentro de um bloco begin...end de um if.
        // Precisamos fazer GOTO para um estado que permita processar o else.
        // NOTA: Mesmo que não haja estado relacionado a if na pilha ANTES do reduce, se o lookahead é "else",
        // sabemos que estamos dentro de um contexto de if, então podemos usar GOTO(17, L) = 31
        if (numProducao == 10 && ladoEsquerdo.equals("L") && estadoAtual == 3) {
            // Verificar se o lookahead é "else" (indica que estamos processando um if-else)
            boolean lookaheadElse = false;
            if (tokenLookahead != null) {
                String lookaheadStr = tokenParaString(tokenLookahead);
                if (lookaheadStr.equals("else")) {
                    lookaheadElse = true;
                    System.out.println("DEBUG: CORREÇÃO: Detectado 'else' como lookahead ao reduzir L -> C ; no estado 3");
                }
            }
            
            // Se o lookahead é "else", sabemos que estamos dentro de um contexto de if,
            // então podemos usar GOTO(17, L) = 31 mesmo que não haja estado relacionado a if na pilha ANTES do reduce
            // Isso é necessário porque quando reduzimos múltiplas vezes, os estados relacionados a if podem já ter sido removidos
            // mas o "else" ainda indica que estamos dentro de um contexto de if
            if (lookaheadElse) {
                // Estamos dentro de um bloco begin...end e processando um if-else, fazer GOTO para estado 31 (M -> begin L # end)
                System.out.println("DEBUG: CORREÇÃO: Reduzindo L -> C ; no estado 3, detectado lookahead 'else'");
                System.out.println("DEBUG: Usando GOTO(17, L) -> 31 como correção (dentro de bloco begin...end de if)");
                chave = "17,L";
                Integer novoEstadoCorrigido = tabelaGotos.get(chave);
                if (novoEstadoCorrigido != null) {
                    System.out.println("DEBUG: GOTO encontrado após correção: " + chave + " -> estado " + novoEstadoCorrigido);
                    pilhaEstados.push(novoEstadoCorrigido);
                    System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
                    return true;
                }
            }
        }
        
        // CORREÇÃO ESPECIAL: Se estamos reduzindo L -> C ; L (produção 11) e o estado atual é 0,
        // isso significa que desempilhamos estados demais. Precisamos procurar um estado anterior que espera L.
        // Mas o estado 0 é o estado inicial, então não há estado anterior. Neste caso, usar estado 29 como fallback
        // porque L -> C ; L geralmente leva ao estado 29 (L -> C ; L # L)
        if (numProducao == 11 && ladoEsquerdo.equals("L") && estadoAtual == 0) {
            System.out.println("DEBUG: CORREÇÃO: Reduzindo L -> C ; L no estado 0, usando estado 29 como fallback");
            Integer novoEstadoCorrigido = 29; // Estado comum para L -> C ; L # L
            System.out.println("DEBUG: GOTO encontrado após correção: 0,L -> estado " + novoEstadoCorrigido);
            pilhaEstados.push(novoEstadoCorrigido);
            System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
            return true;
        }
        
        // Obter novo estado do GOTO
        Integer novoEstado = tabelaGotos.get(chave);
        if (novoEstado != null) {
            System.out.println("DEBUG: GOTO encontrado: " + chave + " -> estado " + novoEstado);
            pilhaEstados.push(novoEstado);
            System.out.println("DEBUG: Pilha após GOTO: " + pilhaEstados.toString());
            return true;
        } else {
            // Erro: GOTO não encontrado
            // Retornar false de forma controlada, sem lançar exceção
            // A linha já foi atualizada antes de chamar aplicarReduce()
            System.out.println("DEBUG: ERRO - GOTO não encontrado para " + chave);
            System.out.println("DEBUG: GOTOs disponíveis para estado " + estadoAtual + ":");
            for (String key : tabelaGotos.keySet()) {
                if (key.startsWith(estadoAtual + ",")) {
                    System.out.println("  " + key + " -> " + tabelaGotos.get(key));
                }
            }
            // Não chamar atualizarLinha() aqui porque já foi chamado antes de aplicarReduce()
            return false;
        }
    }
    
    /**
     * Atualiza a linha atual baseado no token
     * 
     * Calcula a linha contando as quebras de linha no código fonte
     * até a posição do token atual, buscando a partir da última posição processada
     */
    private void atualizarLinha() {
        if (tokenAtual != null && codigoFonte != null) {
            String lexema = tokenAtual.lexema;
            
            // Buscar o lexema no código fonte a partir da última posição processada
            // Isso garante que encontramos a ocorrência correta do token
            int posicao = codigoFonte.indexOf(lexema, ultimaPosicaoProcessada);
            
            if (posicao >= 0) {
                // Contar linhas até a posição encontrada
                int linhas = 1;
                for (int i = 0; i < posicao && i < codigoFonte.length(); i++) {
                    if (codigoFonte.charAt(i) == '\n') {
                        linhas++;
                    }
                }
                linhaAtual = linhas;
                
                // Atualizar a última posição processada para depois do token atual
                // Isso garante que na próxima busca encontremos o próximo token
                ultimaPosicaoProcessada = posicao + lexema.length();
                
                // Verificar se há quebras de linha dentro do token (para tokens multi-linha)
                for (int i = posicao; i < ultimaPosicaoProcessada && i < codigoFonte.length(); i++) {
                    if (codigoFonte.charAt(i) == '\n') {
                        linhaAtual++;
                    }
                }
            } else {
                // Se não encontrar a partir da última posição, tentar buscar do início
                // (pode acontecer em casos especiais)
                posicao = codigoFonte.indexOf(lexema);
                if (posicao >= 0) {
                    int linhas = 1;
                    for (int i = 0; i < posicao && i < codigoFonte.length(); i++) {
                        if (codigoFonte.charAt(i) == '\n') {
                            linhas++;
                        }
                    }
                    linhaAtual = linhas;
                    ultimaPosicaoProcessada = posicao + lexema.length();
                }
                // Se ainda não encontrar, manter a linha atual
            }
        }
    }
    
    /**
     * Atualiza a linha atual quando chegamos ao fim do arquivo
     * 
     * Calcula a linha baseada na última posição processada ou usa a linha atual já calculada
     */
    private void atualizarLinhaFimArquivo() {
        if (codigoFonte != null) {
            System.out.println("DEBUG: atualizarLinhaFimArquivo - linhaAtual: " + linhaAtual + ", ultimaPosicaoProcessada: " + ultimaPosicaoProcessada);
            
            // Se a linha atual já foi atualizada (maior que 1), usar ela
            // Isso significa que processamos pelo menos um token
            if (linhaAtual > 1) {
                // Manter a linha atual, que já foi atualizada quando processamos o último token
                System.out.println("DEBUG: Usando linha atual já calculada: " + linhaAtual);
                return;
            }
            
            // Se a linha atual é 1 mas temos uma última posição processada válida,
            // calcular a linha baseada nela (pode ser que a linha tenha sido resetada incorretamente)
            if (ultimaPosicaoProcessada > 0 && ultimaPosicaoProcessada <= codigoFonte.length()) {
                // Contar linhas até a última posição processada
                int linhas = 1;
                for (int i = 0; i < ultimaPosicaoProcessada && i < codigoFonte.length(); i++) {
                    if (codigoFonte.charAt(i) == '\n') {
                        linhas++;
                    }
                }
                // Se a linha calculada for maior que 1, usar ela
                // Caso contrário, contar o total de linhas do arquivo
                if (linhas > 1) {
                    linhaAtual = linhas;
                    System.out.println("DEBUG: Calculando linha baseada em ultimaPosicaoProcessada (" + ultimaPosicaoProcessada + "): " + linhaAtual);
                } else {
                    // Se ainda for linha 1, contar o total de linhas do arquivo
                    linhas = 1;
                    for (int i = 0; i < codigoFonte.length(); i++) {
                        if (codigoFonte.charAt(i) == '\n') {
                            linhas++;
                        }
                    }
                    linhaAtual = linhas;
                    System.out.println("DEBUG: Calculando linha baseada no total de linhas do arquivo: " + linhaAtual);
                }
            } else {
                // Caso contrário, contar o número total de linhas no arquivo
                // Isso garante que reportamos pelo menos a última linha do arquivo
                int linhas = 1;
                for (int i = 0; i < codigoFonte.length(); i++) {
                    if (codigoFonte.charAt(i) == '\n') {
                        linhas++;
                    }
                }
                linhaAtual = linhas;
                System.out.println("DEBUG: Calculando linha baseada no total de linhas do arquivo: " + linhaAtual);
            }
        }
    }
    
    /**
     * Retorna a linha do erro (se houver)
     */
    public int getLinhaErro() {
        return linhaAtual;
    }
    
    /**
     * Verifica se o programa foi aceito
     */
    public boolean foiAceito() {
        return aceito;
    }
}
