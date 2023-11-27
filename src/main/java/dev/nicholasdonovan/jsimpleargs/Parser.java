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
 * <p>Responsible for parsing the command line arguments passed to the application. After arguments are created,
 * arguments are passed to the {@code
 * Parser} class via its {@code parse} method. Then will validate the argument names and values, and populate the
 * {@code Argument} objects with any
 * values that were passed on the command line. Validation is summarized as so:</p>
 * <ol>
 *   <li>An argument is found and parsed in the user-provided args.</li>
 *   <li>Arguments are then validated using parameters provided by the programmer during creation.</li>
 *   <li>Once validated, if the argument has values, the values are parsed and then validated.</li>
 *   <li>After the values are parsed and added, the parse method will check to ensure all required args were
 *   provided</li>
 *   <li>If all validations are passed, the program will have access to the arguments and values</li>
 * </ol>
 * <p>If at any stage, program help or argument help is requested it will be printed (help can be disabled).</p>
 * <p>If an error is detected the methods will throw specific exceptions to the
 * {@link InvalidInputException JSimpleArgs} exception for easy handling.</p>
 */
class Parser {
  private final Map<String, Argument> shortNameToArgument = new HashMap<>();
  private final Map<String, Argument> nameToArgument = new HashMap<>();

  private boolean programHelpRequested = false;
  private boolean argumentHelpRequested = false;
  private Argument requestedHelpArgument;

  /** Error message constants used in argument parsing. */
  private static final String UNRECOGNIZED_ARGUMENT_MESSAGE = "Unrecognized argument provided: %s";
  private static final String TOO_MANY_VALUES_MESSAGE = "Argument %s allowed to only have one value.";
  private static final String MISSING_REQUIRED_ARGUMENT = "Argument %s is required but not specified.";
  private static final String ARGUMENT_DOES_NOT_HAVE_VALUE_MESSAGE = "Argument %s may not have a value but was " +
                                                                     "assigned: %s";
  private static final String ARGUMENT_MISSING_REQUIRED_VALUE_MESSAGE = "Argument %s requires a value.";


  /**
   * Parses the command line items array into a list of separate arguments and values, checks if all required arguments
   * are present, then adds and validates values (if needed) are present.
   *
   * @param commandLineItems the array of command-line arguments
   * @throws InvalidInputException if there is an error during parsing, such as illegal argument usage, unknown
   *                               argument, or illegal value
   */
  public void parse(String[] commandLineItems) throws InvalidInputException {
    List<String> items = parseItemsToList(commandLineItems);

    if (isHelpEnabled() && isHelpRequested()) return;

    markArgumentsAsPresent(items);
    checkForRequiredArgs();
    parseValues(items);
    checkForRequiredValues();
  }

  /** Add command line items to list */
  private List<String> parseItemsToList(String[] argsArray) {
    List<String> items = new ArrayList<>();
    for (int i = 0; i < argsArray.length; ++i) {
      String item = argsArray[i];
      parseItem(items, item, i);
    }
    return items;
  }

  /** Parse and add items(s) to the list */
  private void parseItem(List<String> items, String item, int i) {
    if (isArgument(item)) {
      items.add(item);
    } else if (isHelp(item)) {
      setHelpRequested(items, i);
    } else if (isArgumentAssigned(item)) {
      addAssignedArguments(item, items);
    } else if (isPotentiallyConcatenated(item)) {
      addConcatenatedArguments(item, items);
    } else {
      items.add(item);
    }
  }

  /** Returns true if the given name is an argument name */
  boolean isArgument(String name) {
    return isInPrimaryArgumentMap(name) || isInShortArgumentMap(name);
  }

  /** Returns true if the name is a key in the primary {@code nameToArgument} map */
  private boolean isInPrimaryArgumentMap(String name) {
    return this.nameToArgument.containsKey(name);
  }

  /** Returns true if the name is a key in the {@code shortNameToArgument} map */
  private boolean isInShortArgumentMap(String subArg) {
    return this.shortNameToArgument.containsKey(subArg);
  }

  /** Returns true if the passed string is a help request */
  private boolean isHelp(String item) {
    return item.equals("-h") || item.equals("--help");
  }

  /** Sets the appropriate help flags whenever help is requested by the user */
  private void setHelpRequested(List<String> args, int i) {
    if (isArgumentHelp(args, i)) {
      requestHelpForArgument(args, i);
    } else {
      requestHelpForProgram();
    }
  }

  /** Returns true if the last item in the list is an argument */
  private boolean isArgumentHelp(List<String> args, int i) {
    return args.size() > 1 && i > 0 && isArgument(args.get(i - 1));
  }

  /** Sets the proper help flag and argument pointer if help's requested for an argument */
  private void requestHelpForArgument(List<String> args, int i) {
    argumentHelpRequested = true;
    requestedHelpArgument = getArgument(args.get(i - 1));
    requestedHelpArgument.setHelpRequestedTrue();
  }

  /** Sets the proper help flag to signal program help was requested */
  private void requestHelpForProgram() {
    programHelpRequested = true;
  }

  /**
   * Returns an {@code Argument} object based on the provided name, if the name is not found a {@link NullArgument}
   * object is returned.
   *
   * @param name the name of the argument
   * @return an {@code Argument} object represented by the name
   */
  public Argument getArgument(String name) {
    Argument arg;
    if ((arg = this.nameToArgument.get(name)) == null) {
      arg = this.shortNameToArgument.get(name);
    }
    if (arg == null) {
      return new NullArgument();
    }
    return arg;
  }

  /** Checks if the argument has an assigned value (ex: --input=./file) */
  private boolean isArgumentAssigned(String arg) {
    String[] splitArg = arg.split("=");
    return splitArg.length == 2 && isArgument(splitArg[0]) && !splitArg[1].isEmpty();
  }

  /** Splits an assigned argument and adds the two values into the list (ex: --input=./file). */
  private void addAssignedArguments(String arg, List<String> args) {
    String[] splitArg = arg.split("=");
    args.add(splitArg[0]);
    args.add(splitArg[1]);
  }

  /** Returns true if the argument provided is potentially concatenated */
  private boolean isPotentiallyConcatenated(String arg) {
    return arg.startsWith("-") && !arg.startsWith("--") && arg.length() > 2;
  }

  /**
   * Determines if an argument is a concatenation of several short arguments and adds each short argument as a separate
   * string to the given {@code items} list.
   */
  private void addConcatenatedArguments(String item, List<String> items) {
    // Hold potential arguments until either it's not concat or `potentialConcat` is empty
    List<String> potentialArguments = new ArrayList<>();

    // Concatenated and assigned are allowed, split item by '='
    String[] itemArr = item.split("=");

    // Save item
    String potentialConcat = itemArr[0];

    // Remove leading hyphen
    potentialConcat = potentialConcat.substring(1);

    // Remove arguments from string
    potentialConcat = removeArgumentsFromString(potentialArguments, potentialConcat);

    // If concatenated, add all to list
    if (isConcatenated(potentialConcat)) {
      items.addAll(potentialArguments);
      if (isAssignedValueWithConcatenation(itemArr)) {
        items.add(itemArr[1]);
      }
    }
  }

  /** Remove all arguments from potential concatenation */
  private String removeArgumentsFromString(List<String> potentialArguments, String potentialConcat) {
    // Start with first char
    int index = 1;

    while (!isEndOfString(potentialConcat, index)) {

      // Run through combinations of characters
      String potentialArg = "-" + potentialConcat.substring(0, index);

      if (isInShortArgumentMap(potentialArg)) {
        // Remove potentialArg from string
        potentialConcat = potentialConcat.substring(index);

        // Add to potentials
        potentialArguments.add(potentialArg);

        // Reset index
        index = 0;
      }

      ++index;
    }
    return potentialConcat;
  }

  /** If all arguments are removed, it's concatenated */
  private boolean isConcatenated(String potentialConcat) {
    return potentialConcat.isEmpty();
  }

  /** Returns true if an assigned value is detected with the concatenated arguments */
  private boolean isAssignedValueWithConcatenation(String[] itemArr) {
    return itemArr.length > 1 && !itemArr[1].isEmpty();
  }

  /** Returns true if end of string */
  private boolean isEndOfString(String arg, int index) {
    return index > arg.length();
  }

  /** Returns true if help is enabled */
  private boolean isHelpEnabled() {
    return JSimpleArgs.isHelpEnabled();
  }

  /** Returns true if help is requested */
  private boolean isHelpRequested() {
    return isProgramHelpRequested() || isArgumentHelpRequested();
  }

  /** Returns true if program help is requested */
  boolean isProgramHelpRequested() {
    return this.programHelpRequested;
  }

  /** Returns true if argument help is requested */
  boolean isArgumentHelpRequested() {
    return this.argumentHelpRequested;
  }

  /** Marks parsed arguments as present */
  private void markArgumentsAsPresent(List<String> args) {
    for (String argument : args) {
      getArgument(argument).setPresentTrue();
    }
  }

  /** Checks for arguments marked as required, if they are not in the CLI an InvalidInputException is thrown */
  private void checkForRequiredArgs() throws InvalidInputException {
    for (Argument arg : getArguments()) {
      if (arg.isRequired() && !arg.isPresent()) {
        throw new InvalidInputException(
            new MissingRequiredArgumentException(String.format(MISSING_REQUIRED_ARGUMENT, arg.getName()))
        );
      }
    }
  }

  /** Parse the argument value from the list */
  private void parseValues(List<String> args) throws InvalidInputException {
    try {
      parseArgumentValues(args);
    } catch (UnknownArgumentException | IllegalValueException e) {
      throw new InvalidInputException(e);
    }
  }

  /** Parses and adds argument values from the list */
  private void parseArgumentValues(List<String> args) throws UnknownArgumentException, IllegalValueException {
    Argument argument;
    int i = 0;

    while (!isEndOfList(args, i)) {
      String current = args.get(i);
      argument = getArgument(current);

      if (argument.isNull()) {
        throw new UnknownArgumentException(String.format(UNRECOGNIZED_ARGUMENT_MESSAGE, current));
      }

      if (argument.isHasValue()) {
        if (tryToAddValue(argument, args, i)) ++i;
      } else if (isNextElementInList(args, i)) {
        verifyNextIsArgument(current, args.get(i + 1));
      }

      ++i;
    }
  }

  /**
   * Check for and add value (if one is assigned). If the argument requires a value, has a default value, and one is
   * not assigned in the command line, the default value is assigned to the argument
   */
  private boolean tryToAddValue(Argument argument, List<String> args, int i) throws IllegalValueException {
    if (isValueInCommandLine(args, i)) {
      addValueToArgument(argument, args, i);
      return true;
    } else if (isDefaultValuePresent(argument)) {
      argument.addValue(argument.getDefaultValue());
    }
    return false;
  }

  /** Adds value to argument if the next item in the list is not an argument. */
  private void addValueToArgument(Argument argument, List<String> args, int i) throws IllegalValueException {
    if (isTooManyValues(argument)) {
      throw new IllegalValueException(String.format(TOO_MANY_VALUES_MESSAGE, args.get(i)));
    }
    argument.addValue(args.get(i + 1));
  }

  /** Returns true if the argument has a default value */
  private boolean isDefaultValuePresent(Argument argument) {
    return !argument.getDefaultValue().isEmpty();
  }

  /** Returns true if there's a value in command line */
  private boolean isValueInCommandLine(List<String> args, int i) {
    return isNextElementInList(args, i) && !isArgument(args.get(i + 1));
  }

  /** Returns true if the index has reached the end of the list */
  private <T> boolean isEndOfList(List<T> args, int i) {
    return i >= args.size();
  }

  /** Returns true if there's a next element in the list */
  private boolean isNextElementInList(List<String> args, int i) {
    return i < args.size() - 1;
  }

  /** Returns true if the argument is being assigned too many values */
  private boolean isTooManyValues(Argument argument) {
    return argument.isSingleValue() && !argument.getValues().isEmpty();
  }

  /**
   * Verifies the next element is an argument, if it's not, the user is either trying to use an unrecognized commmand
   * or trying to assign a value to an argument that doesn't take one.
   */
  private void verifyNextIsArgument(String current, String next) throws IllegalValueException {
    if (!isArgument(next)) { // Argument has no value, but value assigned
      String message = String.format(ARGUMENT_DOES_NOT_HAVE_VALUE_MESSAGE, current, next);
      if (isPotentialKeywordArgument(next)) {
        message = String.format(UNRECOGNIZED_ARGUMENT_MESSAGE, next) + " or " + message;
      }
      throw new IllegalValueException(message);
    }
  }

  /** Returns true if the string starts with a hyphen, meaning it could be intended as a keyword argument */
  private boolean isPotentialKeywordArgument(String next) {
    return next.startsWith("-");
  }

  /** Verifies that all the required arguments are present */
  private void checkForRequiredValues() throws InvalidInputException {
    for (Argument argument : getArguments()) {
      if (isArgumentMissingRequiredValue(argument)) {
        throw new InvalidInputException(String.format(ARGUMENT_MISSING_REQUIRED_VALUE_MESSAGE, argument.getName()));
      }
    }
  }

  /** Returns true if an argument requires a value but is missing one. */
  private static boolean isArgumentMissingRequiredValue(Argument argument) {
    return argument.isPresent() && argument.getValues().isEmpty() && argument.isRequiresValue();
  }

  /** Returns a collection of arguments */
  Collection<Argument> getArguments() {
    return this.nameToArgument.values();
  }

  /** Returns the help string of an argument that the user requested help for */
  String getArgumentHelpString() {
    return this.requestedHelpArgument.getHelp();
  }

  /**
   * Returns a string representation of the parser's arguments
   *
   * @return a string representation of the arguments
   */
  @Override
  public String toString() {
    StringBuilder string = new StringBuilder();

    StringBuilder keywordArgs = buildKeywordArgumentsString();
    if (keywordArgs.length() != 0) {
      string.append("Keyword Arguments: \n").append(keywordArgs).append("\n");
    }

    StringBuilder positionalArgs = buildPositionalArgumentsString();
    if (positionalArgs.length() != 0) {
      string.append("Positional Arguments: \n").append(positionalArgs);
    }

    return string.toString();
  }

  /** Return a string builder of keyword arguments */
  private StringBuilder buildKeywordArgumentsString() {
    StringBuilder keywordArgs = new StringBuilder();
    for (Argument arg : getArguments()) {
      if (arg instanceof KeywordArgument) {
        keywordArgs.append(arg).append("\n");
      }
    }
    return keywordArgs;
  }

  /** Return a string builder of positional arguments */
  private StringBuilder buildPositionalArgumentsString() {
    StringBuilder positionalArgs = new StringBuilder();
    for (Argument arg : getArguments()) {
      if (arg instanceof PositionalArgument) {
        positionalArgs.append(arg).append("\n");
      }
    }
    return positionalArgs;
  }

  /** Adds a keyword argument to the maps */
  void addKeywordArgument(Argument argument) {
    this.nameToArgument.put(argument.getName(), argument);
    this.shortNameToArgument.put(((KeywordArgument) argument).getShortName(), argument);
  }

  /** Adds a positional argument to the map */
  void addPositionalArgument(Argument argument) {
    this.nameToArgument.put(argument.getName(), argument);
  }
}