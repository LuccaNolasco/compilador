package lexico;

public class Token {
   public final TipoToken tipo;
    public final String lexema;

    public Token(TipoToken tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return "<" + lexema + ", " + tipo + ">";
    }
}
