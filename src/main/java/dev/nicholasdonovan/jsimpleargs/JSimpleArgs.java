/**
 * Copyright (C) 2023 Nick Donovan
 * <p>
 * This file is part of jSimpleArgs.
 * <p>
 * jSimpleArgs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * jSimpleArgs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with jSimpleArgs. If not, see <http://www.gnu.org/licenses/>.
 */

package dev.nicholasdonovan.jsimpleargs;

import dev.nicholasdonovan.jsimpleargs.exceptions.IllegalArgumentUsageException;
import dev.nicholasdonovan.jsimpleargs.exceptions.InvalidArgumentNameException;
import dev.nicholasdonovan.jsimpleargs.exceptions.JSimpleArgsException;

/**
 * The interface for the {@code Parser} class and is responsible for creating new command-line arguments with specified options. It uses the {@code
 * Parser} class to perform argument parsing, the results of which are stored Argument objects.
 * The class provides various methods to enable/disable help functions, show help, and set usage messages. It also includes methods for creating
 * new Argument objects with short and long names and a description. Additionally, the JSimpleArgs class provides methods for accessing and
 * manipulating the Argument objects and returning a String representation of the parsed arguments.
 *
 * @see Parser
 * @see Argument
 */
public class JSimpleArgs {
  /**
   * The {@code Parser} object used to parse command-line arguments and update {@code Argument} objects.
   */
  private final Parser argParser;
  /**
   * The usage string that appears in the help message. This string is empty by default.
   */
  private String usage;

  /**
   * The usage string that appears in the help message. This string is empty by default.
   */
  private String help;
  /**
   * A boolean flag indicating whether the help option was requested. This flag is {@code false} by default.
   */
  private boolean showHelp = false;

  /**
   * A boolean flag indicating whether the help option should be disabled. This flag is false by default.
   */
  private boolean disableHelp = false;

  /** Error message constants. */
  private static final String ARGUMENT_NOT_FOUND_MESSAGE = "Argument requested was not found: ";

  /* ********** Public Methods *************** */

  /**
   * Creates a new instance of JSimpleArgs with an empty usage information and a new instance of the Parser class.
   */
  public JSimpleArgs() {
    this.argParser = new Parser();
    this.usage = "";
    this.help = "";
  }

  /**
   * Creates a new instance of JSimpleArgs with the specified usage information and a new instance of the Parser class.
   *
   * @param usage the usage information for the command line interface
   */
  public JSimpleArgs(String usage) {
    this.argParser = new Parser();
    if (usage == null) {
      usage = "";
    }
    this.usage = usage;
    this.help = "";
  }

  /**
   * Creates a new argument with the given short name, long name, and description. The short and long names should not
   * start with any hyphens, as these will be added automatically by the parser. The method returns an object of type
   * {@code Argument}, which can be further configured with various options.
   *
   * @param shortName   the short name of the argument
   * @param longName    the long name of the argument
   * @param description a description of the argument
   * @return an object of type {@code Argument} representing the newly created argument
   * @throws JSimpleArgsException if there is an error during argument creation
   */
  public Argument newArgument(String shortName, String longName, String description) throws JSimpleArgsException {
    // Check if any of the parameters are null, can cause NullPointerException if this isn't done.
    if (shortName == null || longName == null || description == null) {
      throw new JSimpleArgsException(new InvalidArgumentNameException("New argument fields may not be null."));
    }

    // Replace hyphens if present, the program doesn't expect them.
    shortName = shortName.replaceAll("^-", "");
    longName = longName.replaceAll("^-{0,2}", "");

    // Validate names and check duplicates
    try {
      this.argParser.validateName(shortName);
      this.argParser.validateName(longName);
      this.argParser.checkDuplicateArguments(shortName, longName);
    } catch (IllegalArgumentUsageException | InvalidArgumentNameException e) {
      throw new JSimpleArgsException(e);
    }

    // Create new argument and reference it in both parser maps.
    Argument argument = new Argument(shortName, longName, description);
    this.argParser.getArgumentShortNamesUnprotected().put(shortName, argument);
    this.argParser.getArgumentLongNamesUnprotected().put(longName, argument);
    return argument;
  }

  /**
   * Returns the {@code Argument} object with the specified name by calling the {@code Parser} class's
   * {@link Parser#getArgument(String) getArgument}.
   *
   * @param name the name of the argument to retrieve
   * @return the {@code Argument} object with the specified name
   * @throws JSimpleArgsException if the argument is not found
   */
  public Argument getArgument(String name) throws JSimpleArgsException {
    Argument argument = this.argParser.getArgument(name);
    if (argument == null) {
      throw new JSimpleArgsException(new InvalidArgumentNameException(ARGUMENT_NOT_FOUND_MESSAGE + name));
    }
    return argument;
  }

  /**
   * Sets the help flag to true, indicating that help has been requested.
   */
  public void showHelp() { this.showHelp = true; }

  /**
   * Sets the {@code Parser} class {@code trimInput} flag to {@code true}, indicating that leading and trailing
   * whitespace should be trimmed from arguments.
   *
   * @return the {@code JSimpleArgs} object
   */
  public JSimpleArgs trimArgs() {
    this.argParser.trimArgs();
    return this;
  }

  /**
   * Disables the help functionality.
   *
   * @return the {@code JSimpleArgs} object
   */
  public JSimpleArgs disableHelp() {
    this.disableHelp = true;
    return this;
  }

  /**
   * Passes the command line arguments passed to the application to the {@code Parser} class's {@code parse} function.
   *
   * @param args the array of command-line arguments
   * @return the {@code JSimpleArgs} object
   * @throws JSimpleArgsException if there is an error during parsing, such as illegal argument usage, unknown argument, or illegal value
   * @see Parser#parse
   */
  public JSimpleArgs parse(String[] args) throws JSimpleArgsException {
    this.argParser.parse(args, this);
    return this;
  }

  /* ********** Overridden Methods *************** */

  /**
   * Returns a string representation of the {@code JSimpleArgs} object. The string contains a description of each argument
   * and its corresponding value(s).
   *
   * @return a string representation of the {@code JSimpleArgs} object.
   */
  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();

    for (Argument argument : this.argParser.getArguments()) {
      stringBuilder.append(argument);
      stringBuilder.append(argument.getValues());
      stringBuilder.append('\n');
    }
    return stringBuilder.toString();
  }

  /* ********** Public Mutators *************** */

  /**
   * Sets the usage string for the {@code JSimpleArgs} object.
   *
   * @param usage the usage string to be set
   * @return the {@code JSimpleArgs} object with the updated usage string
   */
  public JSimpleArgs setUsage(String usage) {
    if (usage == null) {
      usage = "";
    }
    this.usage = usage;
    return this;
  }

  /**
   * Sets the help string for the {@code JSimpleArgs} object.
   *
   * @param help the help string to be set
   * @return the {@code JSimpleArgs} object with the updated help string
   */
  public JSimpleArgs setHelp(String help) {
    if (help == null) {
      help = "";
    }
    this.help = help;
    return this;
  }
  /* ********** Public Accessors *************** */

  /**
   * Returns the usage string for the {@code JSimpleArgs} object.
   *
   * @return the usage string for the {@code JSimpleArgs} object.
   */
  public String getUsage() { return this.usage; }

  /**
   * Returns the help string for the {@code JSimpleArgs} object.
   *
   * @return the help string for the {@code JSimpleArgs} object.
   */
  public String getHelp() {
    return help;
  }

  /**
   * Returns the usage and help string for the {@code JSimpleArgs} object.
   *
   * @return the usage and help string for the {@code JSimpleArgs} object.
   */
  public String getUsageHelp() {
    return String.format("%s%n%s", this.usage, this.help);
  }

  /**
   * Returns whether the help flag is enabled or disabled for the {@code JSimpleArgs} object.
   *
   * @return {@code true} if the help flag is enabled, {@code false} otherwise.
   */
  public boolean isHelpEnabled() { return !this.disableHelp; }

  /**
   * Returns whether the help flag was requested during parsing of command-line arguments for the {@code JSimpleArgs} object.
   *
   * @return {@code true} if the help flag was requested, {@code false} otherwise.
   */
  public boolean getShowHelp() {
    return showHelp;
  }
}
