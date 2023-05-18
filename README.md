
# jSimpleArgs
jSimpleArgs is a flexible and powerful library for parsing command-line arguments in Java applications, while providing clear and helpful error messages and support for displaying help text to the user. It supports both short and long argument names, as well as assigning values to arguments and concatenating multiple short arguments together.

### Features
-   Support for short and long argument names
-   Support for optional and required arguments
-   Automatic help message generation
-   Lightweight and easy to use

### Installation
To use jSimpleArgs in your Java project, you can download the latest release from the GitHub repository [releases](https://github.com/nick-donovan/jsimpleargs/releases).

### Usage
To use JSimpleArgs in your project, simply create an instance of the `JSimpleArgs` class and create new arguments using the `newArgument()` method. You can then parse command-line arguments using the `parse()` method. Here's an example:

    import dev.nicholasdonovan.jsimpleargs.JSimpleArgs;
    import dev.nicholasdonovan.jsimpleargs.exceptions.InvalidInputException;
    import dev.nicholasdonovan.jsimpleargs.exceptions.InvalidParserUsageException;
    
    public class Main {
        public static void main(String[] args) throws InvalidParserUsageException, InvalidInputException {
            // args = {"-i", "/folder1/file1.txt"};
            JSimpleArgs parser = new JSimpleArgs();
            
            // Create a new keyword argument
            parser.newArgument("-i", "--input", "The path to the input file.")
                .required()
                .requiresValue();
        
            // Parse the 'args' array
            parser.parse(args);
        
            // Get the argument's value
            String value = parser.getArgument("-i").getValue();
        
            // Print the value
            System.out.println(value);
        
            // `/folder1/file1.txt` is printed.
        }
    }


In this example, we create an JSimpleArgs object and add an argument with the short name `-i`, long name `--input`, its description to `"The path to the input file."`, its requiredness with `required()`, and the fact that it requires a value with `requiresValue()`. 

We then parse the command-line arguments using the parse() method, which may throw either an `InvalidInputException` exception if the command line input is invalid, or an `InvalidParserUsageException` exception if the programmer used the parser incorrectly.

Finally, we retrieve the value of the argument using the `getValue()` method of the parser, and print it to the console.

JSimpleArgs will automatically generate a help message for your program based on the arguments you define. You can customize the help message using the `setHelp()` method.

For more information, see the [documentation](https://github.com/nick-donovan/jsimpleargs/wiki).

### License
jSimpleArgs is licensed under the GPL version 3 or later. See the [LICENSE](https://github.com/nick-donovan/jsimpleargs/blob/main/LICENSE) file for more information.



