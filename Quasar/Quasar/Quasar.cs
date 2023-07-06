namespace Quasar;

public class Quasar { 
    public static void Main(String[] args) 
    {
        String input;
        List<Token> tokens;
        Lexer lexer = new();

        do
        {
            Console.WriteLine("> ");
            input = Console.ReadLine();
            tokens = lexer.Lex(input);
            
        } while (input != null);
    }
}
