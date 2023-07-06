using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Quasar
{
    internal class Lexer
    {
        public List<Token> Lex(String input)
        {
            List<Token> tokens = new();

            for (int i = 0; i < input.Length; i++) 
            { 
                switch (input[i]) 
                {
                    case '+':
                        tokens.Add(new Token(TokenType.ADD, "+"));
                        break;
                    case '-':
                        tokens.Add(new Token(TokenType.SUB, "-"));
                        break;
                    case '*':
                        if (input[i + 1] == '*')
                        {
                            i++;
                            tokens.Add(new Token(TokenType.POW, "**"));
                            break;
                        }
                        tokens.Add(new Token(TokenType.MUL, "*"));
                        break;
                    case '/':
                        tokens.Add(new Token(TokenType.DIV, "/"));
                        break;
                    case '(':
                        tokens.Add(new Token(TokenType.LEFT_PAREN, "("));
                        break;
                    case ')':
                        tokens.Add(new Token(TokenType.RIGHT_PAREN, ")"));
                        break;
                    default:
                        if (Char.IsDigit(input[i]))
                        {
                            String completeNumber = "";
                            while (Char.IsDigit(input[i]))
                            {
                                completeNumber += input[i];
                                if (i ==  input.Length - 1)
                                {
                                    break;
                                }
                                else
                                {
                                    i++;
                                }
                            }
                            tokens.Add(new Token(TokenType.NUM, completeNumber));
                        }
                        break;
                }
            }
            return tokens;
        }
    }
}
