package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.craftinginterpreters.lox.TokenType.*;

//lexer. scans through the source code splitting it up into lexical tokens, in the end, returns the list of tokens.
//aka. lexical analysis, lexing or tokenization
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    //connects keyword types to a string which can be searched for in the source code.
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("null", NULL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    //goes through the source code string scanning tokens one by one.
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    //uses switch to check for lexemes in the source code and creates a token for them if found.
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.':
                if (isDigit(peek())) {
                    number(true);
                }
                else {
                    addToken(DOT);
                }
                break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('+') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number(false);
                }
                else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    //keeps checking the next character to build an identifier literal, or to detect and add keywords to token list.
    private void identifier() {
        while (isAlphaNumberic(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    //keeps checking the next character to build a number literal. if allDecimals is true (number starts with a dot),
    //then no dots are allowed in the number.
    private void number(boolean allDecimals) {
        while(isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            if (allDecimals == true) {
                Lox.error(line, "Multiple dots in number.");
            }
            else {
                advance();
                while (isDigit(peek())) advance();
            }
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    //grabs all the characters for a string, excluding the quotation marks,
    //then adds a string token to the token list.
    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    //checks if the next character is as expected for dual character lexemes.
    //for example, >= will find >, check if the next character is =,
    //and use that to determine if the lexeme is > or >=
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    //checks the next character without consuming.
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    //checks two characters ahead without consuming.
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    //checks if a character is in the alphabet or an underscore.
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    //checks if a character is a digit.
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    //checks if a character is a digit, in the alphabet, or an underscore.
    private boolean isAlphaNumberic(char c) {
        return isAlpha(c) || isDigit(c);
    }

    //checks if end of file is reached.
    private boolean isAtEnd() {
        return current >= source.length();
    }

    //returns the next character in the source code.
    private char advance() {
        return source.charAt(current++);
    }

    //adds literal to token then calls add token.
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    //grabs the text of the current lexeme, then adds the token to the list of tokens with a line number.
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
