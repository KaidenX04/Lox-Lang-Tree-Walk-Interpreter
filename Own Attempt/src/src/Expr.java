abstract class Expr {
    public abstract Expr evaluate();
    public static class Binary extends Expr {
        public Expr left;
        public Token operator;
        public Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public Expr evaluate() {
            Expr leftValue = left.evaluate();
            Expr rightValue = right.evaluate();

            switch (operator.type) {
                case ADD:
                    return new Expr.Literal(String.valueOf(getLiteralNumber(leftValue) + Expr.getLiteralNumber(rightValue)));
                case SUB:
                    return new Expr.Literal(String.valueOf(getLiteralNumber(leftValue) - Expr.getLiteralNumber(rightValue)));
                case MUL:
                    return new Expr.Literal(String.valueOf(getLiteralNumber(leftValue) * Expr.getLiteralNumber(rightValue)));
                case DIV:
                    return new Expr.Literal(String.valueOf(getLiteralNumber(leftValue) / Expr.getLiteralNumber(rightValue)));
                case POW:
                    return new Expr.Literal(String.valueOf(Math.pow(getLiteralNumber(leftValue), Expr.getLiteralNumber(rightValue))));
            }

            throw new RuntimeException("Failed to evaluate.");
        }
    }

    public static class Unary extends Expr {
        public Token operator;
        public Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        public Expr evaluate() {
            Expr rightValue = right.evaluate();

            switch (operator.type) {
                case SUB:
                    return new Expr.Literal(String.valueOf(Expr.getLiteralNumber(rightValue) * -1));
            }

            throw new RuntimeException("Failed to evaluate.");
        }
    }

    public static class Literal extends Expr {
        public String value;

        Literal(String value) {
            this.value = value;
        }

        public Expr evaluate() {
            return this;
        }
    }

    private static String getLiteralValue(Expr expr) {
        Literal literal = (Literal) expr;
        return literal.value;
    }

    private static double getLiteralNumber(Expr expr) {
        String value = getLiteralValue(expr);
        return Double.parseDouble(value);
    }
}
