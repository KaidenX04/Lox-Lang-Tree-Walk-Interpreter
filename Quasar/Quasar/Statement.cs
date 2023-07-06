namespace Quasar
{
    abstract class Statement
    {
        public abstract Expression execute();

        public class Assignment : Statement 
        {
            public string Name;

            public Statement Value;

            public Assignment(string name, Statement value)
            {
                Name = name;
                Value = value;
            }

            public override Expression execute()
            {
                return new Expression.Literal(null);
            }
        }

        public class ExpressionStmt : Statement 
        {
            public Expression expression;

            public ExpressionStmt(Expression expression) 
            { 
                this.expression = expression;
            }

            public override Expression execute()
            {
                return expression.evaluate();
            }
        }

        public class Print : Statement
        {
            public ExpressionStmt expression;

            public Print(ExpressionStmt expression)
            {
                this.expression = expression;
            }

            public override Expression execute()
            {
                Expression.Literal literal = expression.execute() as Expression.Literal;
                Console.WriteLine(literal.Value);
                return literal;
            }
        }
    }
}
