abstract class Expr {
    public static class Binary extends Expr {
        Expr left;
        Token operator;
        Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

    }

    public static class Unary extends Expr {
        Token operator;
        Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
    }

    public static class Literal extends Expr {
        String value;

        Literal(String value) {
            this.value = value;
        }
    }
}
