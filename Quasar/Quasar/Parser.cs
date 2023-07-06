namespace Quasar
{
    internal static class Parser
    {
        public static int CurrentToken;

        public static List<Token> Tokens;

        public static List<Statement> statements;

        public static Environment Environment;

        public static List<Statement> Parse(List<Token> tokens)
        {
            Environment = new Environment();
            statements = new List<Statement>();
            Tokens = tokens;
            CurrentToken = 0;
            while (CurrentToken < tokens.Count) 
            {
                statements.Add(Statement());
            }
            return statements;
        }

        public static Statement Statement()
        {
            if (Tokens[CurrentToken].TokenType == TokenType.PRINT)
            {
                return Print();
            }
            else
            {
                return Expression();
            }
        }

        public static Statement Print()
        {
            CurrentToken++;
            Statement.ExpressionStmt expr = Expression();
            CurrentToken--;
            if (CurrentToken < Tokens.Count && Tokens[CurrentToken].TokenType == TokenType.SEMI_COL)
            {
                CurrentToken++;
                return new Statement.Print(expr);
            }
            else
            {
                Quasar.ThrowException("Expected ';' after print statement.");
                CurrentToken++;
                return null;
            }
        }

        public static Statement.ExpressionStmt Expression()
        {
            Expression expr = Arithmetic();
            if (CurrentToken < Tokens.Count && Tokens[CurrentToken].TokenType == TokenType.SEMI_COL)
            {
                CurrentToken++;
                return new Statement.ExpressionStmt(expr);
            }
            else
            {
                Quasar.ThrowException("Expected ';' after expression statement.");
                return null;
            }
        }

        public static Expression Arithmetic()
        {
            Expression expr = Factor();

            while (CurrentToken < Tokens.Count && (Tokens[CurrentToken].TokenType == TokenType.ADD || Tokens[CurrentToken].TokenType == TokenType.SUB || Tokens[CurrentToken].TokenType == TokenType.DIV))
            {
                Token currentToken = Tokens[CurrentToken];

                if (currentToken.TokenType == TokenType.ADD || currentToken.TokenType == TokenType.SUB)
                {
                    Token op = currentToken;
                    CurrentToken++;
                    Expression right = Factor();
                    expr = new Expression.Binary(expr, op, right);
                }
                else if (currentToken.TokenType == TokenType.DIV)
                {
                    Token op = currentToken;
                    CurrentToken++;
                    Expression right = Factor();
                    expr = new Expression.Binary(expr, op, right);
                }
                else
                {
                    break;
                }
            }

            return expr;
        }

        public static Expression Factor()
        {
            Expression expr = Unary();
            while (CurrentToken < Tokens.Count && (Tokens[CurrentToken].TokenType == TokenType.MUL || Tokens[CurrentToken].TokenType == TokenType.POW))
            {
                expr = new Expression.Binary(expr, Tokens[CurrentToken++], Unary());
            }
            return expr;
        }

        public static Expression Unary()
        {
            if (CurrentToken < Tokens.Count && Tokens[CurrentToken].TokenType == TokenType.SUB)
            {
                return new Expression.Unary(Tokens[CurrentToken++], Unary());
            }
            return Primary();
        }

        public static Expression Primary()
        {
            if (CurrentToken < Tokens.Count)
            {
                Token current = Tokens[CurrentToken];

                if (current.TokenType == TokenType.NUM)
                {
                    CurrentToken++;
                    return new Expression.Literal(current.Value);
                }
                else if (current.TokenType == TokenType.LEFT_PAREN)
                {
                    CurrentToken++;
                    Expression expr = Arithmetic();

                    if (CurrentToken < Tokens.Count && Tokens[CurrentToken].TokenType == TokenType.RIGHT_PAREN)
                    {
                        CurrentToken++;
                        return expr;
                    }
                    else
                    {
                        Quasar.ThrowException("Missing closing parenthesis.");
                        return null;
                    }
                }
                else
                {
                    Quasar.ThrowException("Letter in arithmetic expression.");
                    CurrentToken++;
                    return null;
                }
            }

            Quasar.ThrowException($"Failed to parse token {Tokens[CurrentToken].Value}.");
            return null;
        }
    }
}
