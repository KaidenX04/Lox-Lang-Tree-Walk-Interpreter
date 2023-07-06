namespace Quasar
{
    internal class Environment
    {
        public Dictionary<string, string> environment;

        public Environment() 
        { 
            environment = new Dictionary<string, string>();
        }

        public void Define(string name, string value)
        {
            if (environment.ContainsKey(name)) 
            {
                environment[name] = value;
            }
            else
            {
                environment.Add(name, value);
            }
        }

        public string Get(string name) 
        { 
            if (environment.ContainsKey(name)) 
            {
                return environment[name];
            }
            Quasar.ThrowException("Access nonexistant variable.");
            return null;
        }
    }
}
