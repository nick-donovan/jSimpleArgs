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
import dev.nicholasdonovan.jsimpleargs.exceptions.InvalidInputException;
import dev.nicholasdonovan.jsimpleargs.exceptions.InvalidParserUsageException;

/**
 * The interface for the {@code Parser} class and is responsible for creating new command-line arguments with
 * specified options. It uses the {@code Parser} class to perform argument parsing, the results of which are stored
 * Argument objects. The class provides various methods to enable/disable help functions, show help, and set usage
 * messages. It also includes methods for creating new Argument objects with short and long names and a description.
 * Additionally, the JSimpleArgs class provides methods for accessing and manipulating the Argument objects and
 * returning a String representation of the parsed arguments.
 *
 * @see Parser
 * @see Argument
 */
public class JSimpleArgs {
  private final Parser argumentParser;
  private String programUsage;
  private String programHelp;


  /** A boolean flag indicating whether the help option should be disabled */
  private static boolean disableHelp = false;


  /** Error message constants. */
  private static final String INVALID_ARGUMENT_NAME = "Argument names must contain letters, numbers, or hyphens: ";
  private static final String DUPLICATE_ARGUMENT = "This argument is a duplicate of another: ";
  private static final String NULL_ARGUMENT_FIELD = "New argument fields may not be null";

  /** Creates a new instance of JSimpleArgs with an empty usage information and a new instance of the Parser class. */
  public JSimpleArgs() {
    this.argumentParser = new Parser();
    this.programUsage = "";
    this.programHelp = "";
  }

  /**
   * Create a new keyword argument with the given short name, long name, and description, and returns the newly
   * created argument object for further customization.
   * <p>Example: ("-i", "--input", "description")</p>
   *
   * @param shortName   the short name of the argument
   * @param longName    the long name of the argument
   * @param description a description of the argument
   * @return an object of type {@code Argument} representing the newly created argument
   * @throws InvalidParserUsageException if there is an error during argument creation
   */
  public Argument newArgument(String shortName, String longName, String description) throws InvalidParserUsageException {
    // Validate names and check duplicates
    validateArgumentName(shortName);
    validateArgumentName(longName);

    // Create new argument and reference it in both parser maps.
    Argument argument = new KeywordArgument(shortName, longName, description);
    argumentParser.addKeywordArgument(argument);

    // Return the new argument
    return argument;
  }

  /**
   * Create a new positional argument with the given name and description, and returns the newly created argument
   * object for further customization.
   * <p>Example: ("output", "description")</p>
   *
   * @param name        the positional name of the argument
   * @param description a description of the argument
   * @return an object of type {@code Argument} representing the newly created argument
   * @throws InvalidParserUsageException if there is an error during argument creation
   */
  public Argument newArgument(String name, String description) throws InvalidParserUsageException {
    // Validate names and check duplicates
    validateArgumentName(name);

    // Create new argument and reference it in the primary map.
    Argument argument = new PositionalArgument(name, description);
    argumentParser.addPositionalArgument(argument);

    // Return the new argument
    return argument;
  }

  /** Validates the provided argument name and checks if it's a duplicate. */
  void validateArgumentName(String name) throws InvalidParserUsageException {
    try {
      checkValidName(name);
      checkIfDuplicateArgument(name);
    } catch (IllegalArgumentUsageException | InvalidArgumentNameException e) {
      throw new InvalidParserUsageException(e.getMessage(), e);
    }
  }

  /**
   * Validates an argument name to ensure it is not null and only contains alphanumeric characters or hyphens.
   *
   * @param name the argument name to validate
   * @throws InvalidArgumentNameException if the name is null or contains non-alphanumeric characters or hyphens
   */
  private void checkValidName(String name) throws InvalidArgumentNameException {
    if (name == null) {
      throw new InvalidArgumentNameException(NULL_ARGUMENT_FIELD);
    }
    if (!isAlphaNumOrHyphen(name)) {
      throw new InvalidArgumentNameException(INVALID_ARGUMENT_NAME + name);
    }
  }

  /** Returns true if the string consists of alphanumeric or hyphen characters */
  private boolean isAlphaNumOrHyphen(String s) {
    if (s == null) return false;
    return s.matches("^[a-zA-Z0-9-]+$");
  }

  /** Throws an IllegalArgumentUsageException exception on duplicate arguments */
  private void checkIfDuplicateArgument(String name) throws IllegalArgumentUsageException {
    if (argumentParser.isArgument(name)) {
      throw new IllegalArgumentUsageException(DUPLICATE_ARGUMENT + name);
    }
  }

  /**
   * Parses the command line arguments passed to the application using the {@code Parser} class's {@code parse}
   * function.
   *
   * @param args the array of command-line arguments
   * @return the {@code JSimpleArgs} object
   * @throws InvalidInputException if there is an error during parsing, such as illegal argument usage, unknown
   *                               argument, or illegal value
   * @see Parser#parse
   */
  public JSimpleArgs parse(String[] args) throws InvalidInputException {
    if (args == null) args = new String[0];

    this.argumentParser.parse(args);
    checkForAndHandleHelp();

    return this;
  }

  /** Checks and performs the appropriate action if help was requested */
  private void checkForAndHandleHelp() {
    if (isHelpEnabled() && argumentParser.isProgramHelpRequested()) {
      printProgramHelp();
    } else if (isHelpEnabled() && argumentParser.isArgumentHelpRequested()) {
      System.out.println(argumentParser.getArgumentHelpString());
      printProgramHelp();
    }
  }

  /** Print program usage and help */
  private void printProgramHelp() {
    System.out.println(programUsage);
    System.out.println(programHelp);
  }

  /**
   * Disables the automatic help functionality. A help request can still be verified by using the two methods
   * {@link #isProgramHelpRequested()} and {@link #isArgumentHelpRequested()}
   *
   * @return the {@code JSimpleArgs} object
   */
  public JSimpleArgs disableHelp() {
    disableHelp = true;
    return this;
  }

  /**
   * Returns true if program help has been requested.
   *
   * @return true if requested, else false
   */
  public boolean isProgramHelpRequested() {
    return argumentParser.isProgramHelpRequested();
  }

  /**
   * Returns true if an argument help has been requested.
   *
   * @return true if requested, else false
   */
  public boolean isArgumentHelpRequested() {
    return argumentParser.isArgumentHelpRequested();
  }

  /**
   * Returns the argument associated with the provided name.
   *
   * @param name the name of the argument (short, long, or positional)
   * @return the argument object associated with the name
   */
  public Argument getArgument(String name) {
    return this.argumentParser.getArgument(name);
  }

  /**
   * Returns a string representation of the {@code JSimpleArgs} object.
   *
   * @return a string representation of the {@code JSimpleArgs} object
   */
  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();

    for (Argument argument : this.argumentParser.getArguments()) {
      stringBuilder.append(argument);
      stringBuilder.append(argument.getValues());
      stringBuilder.append('\n');
    }
    return stringBuilder.toString();
  }

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
    this.programUsage = usage;
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
    this.programHelp = help;
    return this;
  }

  /**
   * Returns the usage string for the {@code JSimpleArgs} object.
   *
   * @return the usage string for the {@code JSimpleArgs} object.
   */
  public String getUsage() {
    return this.programUsage;
  }

  /**
   * Returns the help string for the {@code JSimpleArgs} object.
   *
   * @return the help string for the {@code JSimpleArgs} object.
   */
  public String getHelp() {
    return this.programHelp;
  }

  /**
   * Returns whether the help flag is enabled or disabled.
   *
   * @return true if help is enabled, false otherwise
   */
  public static boolean isHelpEnabled() {
    return !disableHelp;
  }
}
