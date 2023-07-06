namespace Quasar
{
    internal abstract class Expression
    {
        public abstract Expression evaluate();

        public class Binary : Expression
        {   
            public Expression Left;

            public Token Operator;

            public Expression Right;

            public Binary(Expression left, Token _operator, Expression right) 
            { 
                Left = left;
                Right = right;
                Operator = _operator;
            }

            public override Expression evaluate()
            {
                Expression leftValue = Left.evaluate();
                Expression rightValue = Right.evaluate();

                switch (Operator.TokenType) {
                    case TokenType.ADD:
                        return new Literal((GetLiteralDoubleValue(leftValue) + GetLiteralDoubleValue(rightValue)).ToString());
                    case TokenType.SUB:
                        return new Literal((GetLiteralDoubleValue(leftValue) - GetLiteralDoubleValue(rightValue)).ToString());
                    case TokenType.MUL:
                        return new Literal((GetLiteralDoubleValue(leftValue) * GetLiteralDoubleValue(rightValue)).ToString());
                    case TokenType.DIV:
                        return new Literal((GetLiteralDoubleValue(leftValue) / GetLiteralDoubleValue(rightValue)).ToString());
                    case TokenType.POW:
                        return new Literal(Math.Pow(GetLiteralDoubleValue(leftValue), GetLiteralDoubleValue(rightValue)).ToString());
                }

                Quasar.ThrowException($"Evaluation failed at binary expression {GetLiteralValue(leftValue)} {Operator.Value} {GetLiteralValue(rightValue)}");
                return null;
            }
        }

        public class Grouping : Expression 
        {
            public Expression Expression;

            public Grouping(Expression expression)
            {
                Expression = expression;
            }

            public override Expression evaluate()
            {
                return Expression.evaluate();
            }
        }


        public class Unary : Expression 
        {
            public Token Operator;

            public Expression Right;

            public Unary(Token _operator, Expression right)
            {
                Operator = _operator;
                this.Right = right;
            }

            public override Expression evaluate() 
            { 
                Expression rightValue = Right.evaluate();

                switch (Operator.TokenType) 
                { 
                    case TokenType.SUB:
                        return new Literal((GetLiteralDoubleValue(rightValue) * -1).ToString());
                }

                Quasar.ThrowException($"Evaluation failed at unary expression {Operator.Value} {GetLiteralValue(rightValue)}.");
                return null;
            }
        }

        public class Literal : Expression
        {
            public string Value;

            public Literal(string value) 
            { 
                Value = value;
            }

            public override Expression evaluate()
            {
                return this;
            }
        }

        private string GetLiteralValue(Expression expression)
        {
            Literal literal = expression as Literal;
            return literal.Value;
        }

        private double GetLiteralDoubleValue(Expression expression) 
        {
            Literal literal = expression as Literal;
            return double.Parse(literal.Value);
        }
    }
}
