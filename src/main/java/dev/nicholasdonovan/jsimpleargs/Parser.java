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

import dev.nicholasdonovan.jsimpleargs.exceptions.*;

import java.util.*;

/**
 * <p>Responsible for parsing the command line arguments passed to the application. After arguments are created, arguments are passed to the {@code
 * Parser} class via its {@code parse} method. Then will validate the argument names and values, and populate the {@code Argument} objects with any
 * values that were passed on the command line. Validation is summarized as so:</p>
 * <ol>
 *   <li>An argument is found and parsed in the user-provided args.</li>
 *   <li>Arguments are then validated using parameters provided by the programmer during creation.</li>
 *   <li>Once validated, if the argument has values, the values are parsed and then validated.</li>
 *   <li>After the values are parsed and added, the parse method will check to ensure all required args were provided</li>
 *   <li>If all validations are passed, the program will have access to the arguments and values</li>
 * </ol>
 * <p>If at any stage, program help or argument help is requested it will be printed (help can be disabled).</p>
 * <p>If an error is detected the methods will throw specific exceptions to the
 * {@link JSimpleArgsException JSimpleArgs} exception for easy handling.</p>
 */
class Parser {
  /**
   * Determines whether leading and trailing whitespace should be removed from the arguments when parsing.
   * By default, it is set to {@code false}, meaning that the input will not be trimmed.
   */
  private boolean trimArgs = false;

  /**
   * A map of argument short names to their respective {@code Argument} objects.
   * The {@code Argument} objects contain information about the argument such as its name, description, and values.
   *
   * @see Argument
   */
  private final Map<String, Argument> argumentShortNames = new HashMap<>();
  /**
   * A map of argument long names to their respective {@code Argument} objects.
   * The {@code Argument} objects contain information about the argument such as its name, description, and values.
   *
   * @see Argument
   */
  private final Map<String, Argument> argumentLongNames = new HashMap<>();

  /** Error message constants used in argument parsing. */
  private static final String UNRECOGNIZED_ARGUMENT_MESSAGE = "Unrecognized argument provided: ";
  private static final String TOO_MANY_VALUES_MESSAGE = "Argument allowed to only have one value: ";
  private static final String MISSING_REQUIRED_ARGUMENT_MESSAGE = "Argument is required but not specified: ";
  private static final String VALUE_NOT_ALLOWED_MESSAGE = "Argument may not have a value: ";
  private static final String SINGLE_ARG_SPECIFIED_TWICE_MESSAGE = "Argument only allowed to be specified once: ";
  private static final String MISSING_ARGUMENT_VALUE_MESSAGE = "Argument requires a value: ";
  private static final String DUPLICATE_ARGUMENT_MESSAGE = "This argument is a duplicate of another: ";
  private static final String INVALID_ARGUMENT_NAME_MESSAGE = "Argument names must contain letters, numbers, or " + "dashes: ";
  private static final String NULL_ARGS_MESSAGE = "Null args were passed to the Parser parse method.";


  /* ********** Protected Methods *************** */

  /**
   * Checks whether an argument name matches either a short or long name.
   *
   * @param name the argument name to check
   * @return {@code true} if the name matches either a short or long name; {@code false} otherwise
   */
  public boolean argumentNamesMatch(String name) {
    return argumentLongNames.containsKey(name) || argumentShortNames.containsKey(name);
  }

  /**
   * Validates an argument name to ensure it only contains alphanumeric characters or hyphens.
   *
   * @param name the argument name to validate
   * @throws InvalidArgumentNameException if the name contains characters other than alphanumeric characters or hyphens
   */
  public void validateName(String name) throws InvalidArgumentNameException {
    if (name == null || !name.matches("^[a-zA-Z0-9-]+$")) {
      throw new InvalidArgumentNameException(INVALID_ARGUMENT_NAME_MESSAGE + name);
    }
  }

  /**
   * Checks whether there are any duplicate arguments with the same short or long name.
   *
   * @param shortName the short name of the argument to check for duplicates
   * @param longName  the long name of the argument to check for duplicates
   * @throws IllegalArgumentUsageException if there is already an argument with the same short or long name
   */
  public void checkDuplicateArguments(String shortName, String longName) throws IllegalArgumentUsageException {
    if (this.argumentNamesMatch(shortName) || this.argumentNamesMatch(longName)) {
      throw new IllegalArgumentUsageException(DUPLICATE_ARGUMENT_MESSAGE + "-" + shortName + " --" + longName);
    }
  }

  /**
   * Sets the {@code trimInput} flag to {@code true}, indicating that leading and trailing whitespace should be trimmed
   * from arguments.
   */
  public void trimArgs() {
    this.trimArgs = true;
  }

  /**
   * Parses the command-line arguments and populates the fields of the given {@code JSimpleArgs} object accordingly.
   * <p> The method first creates a mutable list of strings called {@code args} using the contents of the {@code argsArray}
   * parameter. If the list is not empty, the first argument is a help flag, and the {@code isHelpEnabled} flag in the
   * {@code jSimpleArgs} parameter is set to true, the method calls the {@code showHelp} method of the {@code jSimpleArgs}
   * object and returns from the method to signal help was requested. </p>
   * <p> Next, the method calls the {@code parseArguments} method to parse the arguments in the {@code args} list and
   * the {@code checkRequiredArgs} method to check if all required arguments are present. If any of these methods throw
   * an exception of type {@code IllegalArgumentUsageException}, {@code UnknownArgumentException}, or {@code IllegalValueException},
   * the method catches the exception and throws a {@code JSimpleArgsException} with the caught exception as its cause.</p>
   * <p> Finally, the method calls the {@code isArgHelpRequested} method with the {@code jSimpleArgs} parameter to check
   * if the help flag was requested for an argument. </p>
   *
   * @param argsArray   the array of command-line arguments
   * @param jSimpleArgs the object of type {@code JSimpleArgs} used for signaling help functions.
   * @throws JSimpleArgsException if there is an error during parsing, such as illegal argument usage, unknown argument, or illegal value
   */
  public void parse(String[] argsArray, JSimpleArgs jSimpleArgs) throws JSimpleArgsException {
    // Check if argsArray is null, can cause NullPointerException if we don't.
    if (argsArray == null) {
      throw new JSimpleArgsException(NULL_ARGS_MESSAGE);
    }

    // Convert argsArray to List for easy manipulation.
    List<String> args = new ArrayList<>(Arrays.asList(argsArray));

    // If the first arg is help.
    if (!args.isEmpty() && isHelp(args.get(0)) && jSimpleArgs.isHelpEnabled()) {
      jSimpleArgs.showHelp();
      return;
    }

    // Parse arguments, their associated values, and validate both.
    try {
      parseArguments(args);

      // Check if argument help is requested.
      if (isArgHelpRequested(jSimpleArgs)) return;
      checkRequiredArgs();
    } catch (IllegalArgumentUsageException | UnknownArgumentException | IllegalValueException e) {
      throw new JSimpleArgsException(e);
    }

    // Check if argument help is requested.
    isArgHelpRequested(jSimpleArgs);
  }

  /**
   * Returns the {@code Argument} object associated with the given name.
   * <p> The method first checks if there is an {@code Argument} object associated with the given {@code name} as a long
   * name. If an {@code Argument} object is found, it is returned. Otherwise, the method checks if there is an {@code Argument}
   * object associated with the given {@code name} as a short name. If an {@code Argument} object is found, it is returned.
   * If there is no {@code Argument} object associated with the given {@code name}, the method returns null.</p>
   *
   * @param name the long or short name of the {@code Argument} object to retrieve
   * @return the {@code Argument} object associated with the given name
   */
  public Argument getArgument(String name) {
    if (name == null) {
      return null;
    }

    // Replace hyphens if present, the program doesn't expect them.
    name = name.replaceAll("^-{0,2}", "");

    Argument argument = this.getArgumentLongNames().get(name);

    if (argument == null) {
      argument = this.getArgumentShortNames().get(name);
    }

    return argument;
  }

  /* ********** Private Methods *************** */

  /**
   * Determines whether the given argument is a help option.
   * <p> The method checks whether the given {@code arg} string is equal to "-h" or "--help". </p>
   *
   * @param arg the argument to check
   * @return {@code true} if the argument is a help option, {@code false} otherwise
   */
  private boolean isHelp(String arg) {
    return "-h".equals(arg) || "--help".equals(arg);
  }

  /**
   * Parses the list of command line arguments and sets the corresponding {@code Argument} values.
   * <p>The method iterates through each argument in the given {@code args} list, and if an argument is a valid command
   * line argument, it sets the corresponding {@code Argument} object's value.</p>
   * <p> The method also performs validation on the arguments to ensure that they are valid and specified correctly. </p>
   *
   * @param args the list of command line arguments to parse
   * @throws UnknownArgumentException      if an unrecognized argument is encountered
   * @throws IllegalArgumentUsageException if an argument is used incorrectly
   * @throws IllegalValueException         if an argument value is invalid
   */
  private void parseArguments(List<String> args) throws UnknownArgumentException, IllegalArgumentUsageException, IllegalValueException {
    // Iterate through args.
    for (int i = 0; i < args.size(); ++i) {
      String arg = args.get(i);
      // If arg is an argument, clean, retrieve, then process.
      if (isArgument(arg)) {
        arg = prepArg(arg);
        Argument argument = getArgument(arg);

        // If the argument wasn't found, check if it's empty, assigned (-b=bacon), or concatenated (-blt), if the
        // latter two are true, they're seperated and added to the list
        if (argument == null && (arg.isEmpty() || argIsAssigned(arg, args, i) || argIsConcat(arg, args, i))) {
          continue;
        }

        // Performs validation and checks if help was requested. Returns if help requested.
        if (validateArgumentReturnHelp(argument, args, arg, i)) return;

        // Mark argument present, and parse and validate its values (if applicable).
        argument.setPresentInArg();
        parseValues(argument, args, i);
        validateValues(argument, arg);
      }
    }
  }

  /**
   * Checks whether all required arguments have been specified.
   * <p> The method iterates through each {@code Argument} object and checks whether it is a required argument. If an
   * argument is required and has not been specified, an {@code IllegalArgumentUsageException} is thrown with a message
   * indicating which argument is missing. </p>
   *
   * @throws IllegalArgumentUsageException if a required argument is missing
   */
  private void checkRequiredArgs() throws IllegalArgumentUsageException {
    for (Argument argument : this.getArguments()) {
      if (argument.getRequired() && !argument.isPresent()) {
        throw new IllegalArgumentUsageException(MISSING_REQUIRED_ARGUMENT_MESSAGE + argument);
      }
    }
  }


  /**
   * Validates the given {@code Argument} object and ensures that it is used correctly.
   * <p> The method checks whether the {@code Argument} object is null, whether the argument is being used to display
   * the help message, and whether a single use argument has been specified twice. If any of these conditions are true,
   * the method throws an exception with a corresponding message. </p>
   *
   * @param argument   the {@code Argument} object to validate
   * @param args       the list of command line arguments
   * @param arg        the current command line argument being processed
   * @param i          the current index of the argument being processed
   * @return {@code true} if help for the argument is requested, {@code false} otherwise
   * @throws UnknownArgumentException      if an unrecognized argument is encountered
   * @throws IllegalArgumentUsageException if an argument is used incorrectly
   */
  private boolean validateArgumentReturnHelp(Argument argument, List<String> args, String arg, int i) throws UnknownArgumentException,
      IllegalArgumentUsageException {
    if (argument == null) {
      throw new UnknownArgumentException(UNRECOGNIZED_ARGUMENT_MESSAGE + arg);
    } else if (isNotLastElement(i, args) && isHelp(args.get(i + 1))) {
      argument.showHelp();
      return true;
    } else if (argument.isPresent() && argument.getSingleArg()) {
      throw new IllegalArgumentUsageException(SINGLE_ARG_SPECIFIED_TWICE_MESSAGE + arg);
    }
    return false;
  }

  /**
   * Checks whether the given argument is a valid argument flag. A valid argument flag can either be a short flag
   * (a single dash) or a long flag (two dashes), each followed by alphanumeric characters and dashes.
   *
   * @param arg the argument to check.
   * @return {@code true} if the argument is a valid argument flag, {@code false} otherwise.
   */
  private boolean isArgument(String arg) {
    return arg != null && arg.matches("^-?-[a-zA-Z0-9-]*$");
  }

  /**
   * Prepares the {@code arg} string for processing by trimming if {@code trimInput} is {@code true}, and removing up
   * to two of the first hyphens.
   *
   * @param arg arg to be processed
   * @return processed arg
   */
  private String prepArg(String arg) {
    if (this.trimArgs) {
      arg = arg.trim();
    }
    arg = arg.replaceAll("^-{0,2}", "");
    return arg;
  }

  /**
   * Determines if an argument has an assigned value and separates it into two separate strings, adding them to the given
   * {@code args} list at positions i+1 and i+2 if true.
   *
   * @param arg  the argument to check
   * @param args the list of command-line arguments
   * @param i    the index of the argument in the list
   * @return true if the argument is an assigned value argument, false otherwise
   */
  private boolean argIsAssigned(String arg, List<String> args, int i) {
    String[] splitArg = arg.split("=");
    if (splitArg.length == 2 && !splitArg[0].isEmpty() && !splitArg[1].isEmpty()) {
      args.add(i + 1, "-" + splitArg[0]);
      args.add(i + 2, splitArg[1]);
      return true;
    }
    return false;
  }

  /**
   * Determines if an argument is a concatenation of several short arguments and adds each short argument as a separate
   * string to the given {@code args} list at positions i+1 to i+n if true.
   *
   * @param arg  the argument to check
   * @param args the list of command-line arguments
   * @param i    the index of the argument in the list
   * @return true if the argument is a concatenation of short arguments, false otherwise
   */
  private boolean argIsConcat(String arg, List<String> args, int i) {
    // Add potential arguments here, until either it's not concat or `arg` is empty
    List<String> potentialArguments = new ArrayList<>();

    // Iterate through `arg` to find possible arguments. Removing them as they're found.
    int last = 1;
    while (last <= arg.length()) {
      String sub = arg.substring(0, last);
      if (this.argumentShortNames.containsKey(sub)) {
        arg = arg.substring(last);
        potentialArguments.add("-" + sub);
        last = 0;
      }
      if (arg.length() == 0) {
        break;
      }
      ++last;
    }

    // Add all the arguments to the args list then return true, it was concatenated.
    if (arg.isEmpty()) {
      args.addAll(i + 1, potentialArguments);
      return true;
    }
    return false;
  }

  /**
   * Determines if the given index i is not the index of the last element in the list.
   *
   * @param i    the index to check
   * @param list the list to check
   * @param <T>  the type of elements in the list
   * @return true if the index is not the index of the last element in the list, false otherwise
   */
  private <T> boolean isNotLastElement(int i, List<T> list) {
    return i != list.size() - 1;
  }

  /**
   * Parses the input values for the given argument, adding them to the argument's list of values.
   *
   * @param argument the argument being parsed
   * @param args     the list of arguments being processed
   * @param i        the index of the argument in the list
   */
  private void parseValues(Argument argument, List<String> args, int i) {
    while (args.get(i) != null && isNotLastElement(i, args) && !isArgument(args.get(i + 1))) {
      String arg = args.get(++i).trim();
      if (!arg.isEmpty()) {
        argument.getValuesUnprotected().add(arg);
      }
    }
  }

  /**
   * Checks whether the help argument is present in the input JSimpleArgs object, and displays the help
   * information for any argument with the 'showHelp' flag set to true.
   *
   * @param jSimpleArgs the JSimpleArgs object being checked
   */
  private boolean isArgHelpRequested(JSimpleArgs jSimpleArgs) {
    for (Argument argument : this.getArguments())
      if (argument.getShowHelp() && jSimpleArgs.isHelpEnabled()) {
        jSimpleArgs.showHelp();
        System.out.println(argument.getHelp());
        return true;
      }
    return false;
  }

  /**
   * Validates the values of the given argument.
   * <p> If the argument is expected to have a value, the method checks if the argument has any values, and if not, it
   * checks if a default value is provided. If there are no values and no default value, the method throws an
   * {@code IllegalValueException} with an appropriate message. If the argument allows only a single value and multiple
   * values are provided, it throws an {@code IllegalValueException} with an appropriate message. </p>
   *
   * <p> If the argument is not expected to have a value, the method checks if the argument has any values, and if so,
   * it throws an {@code IllegalValueException} with an appropriate message. </p>
   *
   * @param argument the {@code Argument} object whose values are to be validated
   * @param arg      the argument string whose values are to be validated
   * @throws IllegalValueException if the argument's values are invalid, such as missing argument value, too many values, or values not allowed
   * @see Argument
   */
  private void validateValues(Argument argument, String arg) throws IllegalValueException {
    List<String> values = argument.getValuesUnprotected();
    if (argument.getHasValue()) {
      if (values.isEmpty()) {
        if (argument.getDefaultValue().isEmpty()) {
          throw new IllegalValueException(MISSING_ARGUMENT_VALUE_MESSAGE + arg);
        } else {
          values.add(argument.getDefaultValue());
        }
      }
      if (argument.getSingleArg() && values.size() > 1) {
        throw new IllegalValueException(TOO_MANY_VALUES_MESSAGE + arg + "=" + values);
      }
    } else {
      if (!values.isEmpty()) {
        throw new IllegalValueException(VALUE_NOT_ALLOWED_MESSAGE + arg + "=" + values);
      }
    }
  }

  /* ********** Public Accessors *************** */

  /**
   * Returns an unmodifiable view of the argumentShortNames map.
   *
   * @return an unmodifiable view of the argumentShortNames map
   */
  public Map<String, Argument> getArgumentShortNames() {
    return Collections.unmodifiableMap(this.argumentShortNames);
  }

  /**
   * Returns an unmodifiable view of the argumentLongNames map.
   *
   * @return an unmodifiable view of the argumentLongNames map
   */
  public Map<String, Argument> getArgumentLongNames() {
    return Collections.unmodifiableMap(this.argumentLongNames);
  }

  /**
   * Returns an unmodifiable view of the collection of arguments.
   *
   * @return an unmodifiable view of the collection of arguments
   */
  public Collection<Argument> getArguments() {
    return Collections.unmodifiableCollection(this.argumentShortNames.values());
  }

  /* ********** Protected Accessors *************** */

  /**
   * Returns the argumentShortNames map. This method is unprotected and should only be used by
   * subclasses.
   *
   * @return the argumentShortNames map
   */
  protected Map<String, Argument> getArgumentShortNamesUnprotected() {
    return this.argumentShortNames;
  }

  /**
   * Returns the argumentLongNames map. This method is unprotected and should only be used by
   * subclasses.
   *
   * @return the argumentLongNames map
   */
  protected Map<String, Argument> getArgumentLongNamesUnprotected() {
    return this.argumentLongNames;
  }
}
