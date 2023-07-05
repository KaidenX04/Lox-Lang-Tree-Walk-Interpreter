import java.util.List;

public class Parser {
    public List<Token> tokens;
    public int currentToken;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = 0;
    }

    public Expr parse() {
        return arithmetic();
    }

    public Expr arithmetic() {
        Expr expr = factor();
        if (currentToken < tokens.size() && tokens.get(currentToken).type == TokenType.ADD || tokens.get(currentToken).type == TokenType.SUB) {
            System.out.println(tokens.get(currentToken).value);
            System.out.println(tokens.get(currentToken).type);
            expr = new Expr.Binary(expr, tokens.get(currentToken++), factor());
        }
        return expr;
    }

    public Expr factor() {
        Expr expr = unary();
        if (currentToken < tokens.size() && tokens.get(currentToken).type == TokenType.MUL || tokens.get(currentToken).type == TokenType.DIV || tokens.get(currentToken).type == TokenType.POW) {
            expr = new Expr.Binary(expr, tokens.get(currentToken++), unary());
        }
        return expr;
    }

    public Expr unary() {
        if (currentToken < tokens.size() && tokens.get(currentToken).type == TokenType.SUB) {
            System.out.println(tokens.get(currentToken).value);
            return new Expr.Unary(tokens.get(currentToken++), unary());
        }

        return primary();
    }

    public Expr primary() {
        if (currentToken < tokens.size() && tokens.get(currentToken).type == TokenType.NUM) {
            System.out.println(tokens.get(currentToken).value);
            System.out.println(tokens.get(currentToken).type);
            System.out.println(currentToken);
            return new Expr.Literal(tokens.get(currentToken++).value);
        }
        throw new RuntimeException("empty expression");
    }
}
