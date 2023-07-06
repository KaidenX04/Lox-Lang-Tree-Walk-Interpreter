namespace Quasar
{
    internal class Lexer
    {
        public Dictionary<string, TokenType> keywords = new Dictionary<string, TokenType>();

        public Lexer()
        {
            keywords.Add("var", TokenType.VAR);
            keywords.Add("print", TokenType.PRINT);
        }

        public List<Token> Lex(string input)
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
                    case '=':
                        tokens.Add(new Token(TokenType.ASSIGNMENT, "="));
                        break;
                    case '(':
                        tokens.Add(new Token(TokenType.LEFT_PAREN, "("));
                        break;
                    case ')':
                        tokens.Add(new Token(TokenType.RIGHT_PAREN, ")"));
                        break;
                    case ';':
                        tokens.Add(new Token(TokenType.SEMI_COL, ";"));
                        break;
                    default:
                        if (Char.IsDigit(input[i]))
                        {
                            String completeNumber = "";
                            while (i < input.Length && Char.IsDigit(input[i]))
                            {
                                completeNumber += input[i];
                                i++;
                            }
                            i--;
                            tokens.Add(new Token(TokenType.NUM, completeNumber));
                        }
                        if (Char.IsLetter(input[i]))
                        {
                            String completeIdentifier = "";
                            while (i < input.Length && input[i] != ' ' && input[i] != '=')
                            {
                                completeIdentifier += input[i];
                                i++;
                            }
                            i--;
                            if (keywords.ContainsKey(completeIdentifier))
                            {
                                tokens.Add(new Token(keywords.GetValueOrDefault(completeIdentifier), completeIdentifier));
                            }
                            else
                            {
                                tokens.Add(new Token(TokenType.IDENTIFIER, completeIdentifier));
                            }
                        }
                        break;
                }
            }
            return tokens;
        }
    }
}
