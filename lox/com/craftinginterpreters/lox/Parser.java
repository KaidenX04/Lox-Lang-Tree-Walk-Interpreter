package com.craftinginterpreters.lox;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import static com.craftinginterpreters.lox.TokenType.*;
public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    //begins parsing an expression.
    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    //begins recursive descent.
    private Expr expression() {
        return assignment();
    }

    //checks if there is a variable declaration, if so, begins evaluating it, if not, falls through to statement().
    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            return statement();
        }
        catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ':' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    //calls the function that will begin the recursive descent process and decide which side effects will happen
    //depending on the type of statement.
    private Stmt statement() {
        if (match(PRINT)) return printStatement();

        return expressionStatement();
    }

    //evaluates the expression then prints it.
    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    //evaluates an expression.
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Expression(expr);
    }

    private Expr assignment() {
        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    //calls comparison to assign a value to expr.
    //if != or == is found, creates a binary expression with expr as left,
    //previous (the token that was found with match) as operator, and calls comparison to create right.
    private Expr equality() {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //calls term to assign a value to expr.
    //if >, >=, < or <= is found, creates a binary expression with expr as left,
    //previous (the token that was found with match) as operator, and calls term to create right.
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //calls factor to assign a value to expr.
    //if - or + is found, creates a binary expression with expr as left,
    //previous (the token that was found with match) as operator, and calls factor to create right.
    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //calls unary to assign a value to expr.
    //if - or + is found, creates a binary expression with expr as left,
    //previous (the token that was found with match) as operator, and calls unary to create right.
    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //if ! or - is found, creates a unary expression with previous (the token that was found with match) as operator
    //and calls unary to create right.
    private Expr unary() {
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    //checks for different primary token types to create a primary expression.
    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NULL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    //goes through a list of token types checking if the next token is that type.
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    //advances and returns the previous token.
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    //peeks at the next token.
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    //moves to the next token and returns the previous.
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    //checks if end of file has been reached.
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    //checks the next token.
    private Token peek() {
        return tokens.get(current);
    }

    //returns the previous token.
    private Token previous() {
        return tokens.get(current - 1);
    }

    //creates an error.
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    //synchronizes the parser when an error is found to avoid reporting ghost errors and getting stuck in infinite loops.
    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch(peek().type) {
                case CLASS: case FOR: case FUN: case IF: case PRINT: case RETURN: case VAR: case WHILE:
                    return;
            }

            advance();
        }
    }
}
