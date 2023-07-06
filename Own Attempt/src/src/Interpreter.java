public class Interpreter {
    public void interpret(Expr expr) {
        Expr result = expr.evaluate();
        Expr.Literal literal = (Expr.Literal) result;
        System.out.println(literal.value);
    }
}
