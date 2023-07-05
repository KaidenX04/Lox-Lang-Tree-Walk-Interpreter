import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;


public class Lang {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            List<Token> tokens = parse(scanner.nextLine());
            Parser parser = new Parser(tokens);
            Expr expr = parser.parse();
            System.out.println(expr);
        }
    }

    public static List<Token> parse(String input) {
        List<Token> tokens = new ArrayList<>();
        char[] charArr = input.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            switch (charArr[i]) {
                case '+':
                    tokens.add(new Token(TokenType.ADD, "+"));
                    break;
                case '-':
                    tokens.add(new Token(TokenType.SUB, "-"));
                    break;
                case '*':
                    if (charArr[i + 1] == '*') {
                        i++;
                        tokens.add(new Token(TokenType.POW, "**"));
                        break;
                    }
                    tokens.add(new Token(TokenType.MUL, "*"));
                    break;
                case '/':
                    tokens.add(new Token(TokenType.DIV, "/"));
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                default:
                    if (Character.isDigit(charArr[i])) {
                        String completeNumber = "";
                        while (Character.isDigit(charArr[i])) {
                            char addChar = charArr[i];
                            completeNumber += addChar;
                            if (i == charArr.length - 1) {
                                break;
                            }
                            else {
                                i++;
                            }
                        }
                        tokens.add(new Token(TokenType.NUM, completeNumber));
                    }
                    break;
            }
        }
        return tokens;
    }
}