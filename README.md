
# jSimpleArgs
jSimpleArgs is a flexible and powerful library for parsing command-line arguments in Java applications, while providing clear and helpful error messages and support for displaying help text to the user. It supports both short and long argument names, as well as assigning values to arguments and concatenating multiple short arguments together.

### Features
-   Support for short and long option names
-   Support for optional and required arguments
-   Automatic help message generation
-   Lightweight and easy to use

### Installation
To use jSimpleArgs in your Java project, you can download the latest release from the GitHub repository [releases](https://github.com/nick-donovan/jsimpleargs/releases).

### Usage
To use JSimpleArgs in your project, simply create an instance of the `JSimpleArgs` class and create new arguments using the `newArgument()` method. You can then parse command-line arguments using the `parse()` method. Here's an example:

    import dev.nicholasdonovan.jsimpleargs.JSimpleArgs;
    import dev.nicholasdonovan.jsimpleargs.exceptions.JSimpleArgsException;

    public class MyApplication {
      public static void main(String[] args) throws JSimpleArgsException {
        // args = {"-i", "/folder1/file1.txt"};
        String parserHelpString = """  
            This is an example of a parser help string.
              You can add usage instructions here or other details you like.
            """;
        JSimpleArgs parser = new JSimpleArgs()
            .setUsage("Usage: java -jar MyApplication -i <file>")
            .setHelp(parserHelpString);

        try {
          parser.newArgument("-i", "--input", "The path to the input file.")
              .hasValue()
              .required()
              .help("""  
                  Input flag help:
                    This string tells the program which file(s) to scan.
                    Multiple can be specified, but will be scanned sequentially
                      Usage examples:
                        -i /home/user/path1/file1.txt
                        -i /home/user/path1/file1.txt /home/user/path1/file2.txt
                        --input /home/user/path1/file1.txt
                        --input /home/user/path1/file1.txt /home/user/path1/file2.txt
                  """)
              .defaultValue("/default/dir/");
          parser.parse(args);
          if (parser.getShowHelp()) {
            System.out.println(parser.getHelp());
          }
        } catch (JSimpleArgsException e) {
          System.err.println(e.getMessage());
        }

        String value = parser.getArgument("-i").getValue();

        System.out.println(value);
        // `/folder1/file1.txt` is printed.
      }
    }


In this example, we create an JSimpleArgs object and add an argument with the short name `-i`, long name `--input`, its description to `"The path to the input file."`, its need for a value with `hasValue()`, its requiredness with `required()`, a help string with `help()`, and a default value with `defaultValue()`. 

We then parse the command-line arguments using the parse() method, which may throw a JSimpleArgsException if there are any errors. We catch and handle this exception. Finally, we retrieve the value of the argument using the `getValue()` method of the parser, and print it to the console.

JSimpleArgs will automatically generate a help message for your program based on the arguments you define. You can customize the help message using the `setHelp()` method.

For more information, see the [documentation](https://github.com/nick-donovan/jsimpleargs/wiki).

### License
jSimpleArgs is licensed under the GPL version 3 or later. See the [LICENSE](https://github.com/nick-donovan/jsimpleargs/blob/main/LICENSE) file for more information.



