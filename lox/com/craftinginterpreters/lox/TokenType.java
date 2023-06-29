package com.craftinginterpreters.lox;

//an enum that contains all the types of tokens that are supported by the language.
enum TokenType {
    //single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    //one or Two character tokens.
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    //literals.
    IDENTIFIER, STRING, NUMBER,

    //keywords.
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NULL, OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}
