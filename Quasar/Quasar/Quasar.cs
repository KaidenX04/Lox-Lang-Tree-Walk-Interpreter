namespace Quasar;

public static class Quasar {
    public static bool error = false;

    public static void Main(string[] args) 
    {
        string input;
        List<Token> tokens;
        List<Statement> statements;
        Lexer lexer = new();

        do
        {
            error = false;
            Console.Write("> ");
            input = Console.ReadLine();
            tokens = lexer.Lex(input);
            statements = Parser.Parse(tokens);
            if (error == false)
            {
                foreach (Statement statement in statements)
                {
                    statement.execute();
                }
            }
        } while (input != "");
    }

    public static void ThrowException(string message) 
    { 
        error = true;
        Console.WriteLine(message);
    }
}
