package com.craftinginterpreters.lox;

import java.util.Arrays;
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
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    //creates a for statement by building a while statement.
    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        }
        else if (match(VAR)) {
            initializer = varDeclaration();
        }
        else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        }

        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    //creates an if statement with a thenBranch and an optional elseBranch.
    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
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

    //creates a while statement with a condition and a body.
    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    //creates a block of code by creating a list of statements while looking for a closing brace.
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
           statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = or();

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

    //calls and to assign a value to expr.
    //if OR is found, creates a logical expression with expr as left,
    //previous (the token that was found with match) as operator, and calls and() to create right.
    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    //calls equality to assign a value to expr.
    //if AND is found, creates a logical expression with expr as left,
    //previous (the token that was found with match) as operator, and calls equality() to create right.
    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
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
