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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p> Represents a command line argument with various properties. An instance of this class represents a command
 * line argument that can be passed to a program. The argument can have a short name and a long name, a description, and
 * various optional properties.</p>
 * <p> The properties include whether the argument accepts a single or multiple values, whether the argument has a
 * value, whether the argument is required, and whether the argument is present. Additionally, the argument can have
 * a default value and a help message. </p>
 * <p> To create an instance of this class, pass in the short name, long name, and description of the argument. Then,
 * set various properties of the argument using the setter methods.</p>
 */
public abstract class Argument {
  private final List<String> values;
  private final String description;

  private boolean presentInCommand = false;
  private boolean helpRequested = false;

  private boolean required = false;
  private boolean hasValue = false;
  private boolean requiresValue = false;
  private boolean singleValue = false;
  private String defaultValue = "";
  private String help = "No help available for argument.";

  /**
   * Construct a new {@code Argument} object
   *
   * @param description the description of the argument
   */
  Argument(String description) {
    this.description = description;
    this.values = new ArrayList<>();
  }

  /** Marked present when the parser detects the argument in the command line */
  void setPresentTrue() {
    this.presentInCommand = true;
  }

  /** Marked true when an argument is followed by 'help', '--help', or '-h', unless they are also defined arguments */
  void setHelpRequestedTrue() {
    this.helpRequested = true;
  }

  /**
   * Marks the argument as required, forcing the user to supply the argument in the command line.
   *
   * @return this {@code Argument} object instance
   */
  public Argument required() {
    this.required = true;
    return this;
  }

  /**
   * Marks the argument as single use, which means the argument can only be used once in the command line.
   *
   * @return this {@code Argument} object instance
   */
  public Argument singleValue() {
    this.singleValue = true;
    return this;
  }

  /**
   * Marks the argument as having a value, which means it will expect one in the command line.
   *
   * @return this {@code Argument} object instance
   * @see #defaultValue(String)
   */
  public Argument hasValue() {
    this.hasValue = true;
    return this;
  }

  /**
   * Marks the argument as requiring a value, which means it must have one in the command line. If {@code
   * defaultValue} is not set, the parser will throw an error to be handled by the programmer.
   *
   * @return this {@code Argument} object instance
   * @see #defaultValue(String)
   */
  public Argument requiresValue() {
    this.hasValue = true;
    this.requiresValue = true;
    return this;
  }

  /**
   * Sets the {@code defaultValue} of this argument to the given value and returns this object. Preferably use
   * {@link #requiresValue()} for clarity in combination with this.
   *
   * @param defaultValue the default value to set for this argument
   * @return this {@code Argument} object instance
   */
  public Argument defaultValue(String defaultValue) {
    // Ensure flag is enabled to have value
    this.hasValue = true;
    this.requiresValue = true;

    // Set default value string
    if (defaultValue != null) {
      this.defaultValue = defaultValue;
    }

    // Return this object instance
    return this;
  }

  /**
   * The help string will print for an argument when the strings `-h`, `--help`, or 'help
   * follows the argument name. Sets the {@code help} string of this argument to the given value and returns this
   * object.
   *
   * @param help the help text to set for this argument
   * @return this {@code Argument} object instance
   */
  public Argument help(String help) {
    if (help != null) {
      this.help = help;
    }
    return this;
  }

  /** Adds the provided value to the value list */
  void addValue(String value) {
    if (value == null) value = "";
    this.values.add(value);
  }

  /**
   * Returns true if this argument is a {@code NullArgument} object.
   *
   * @return {@code true} if this argument is a {@code NullArgument} object, {@code false} otherwise
   * @see NullArgument
   */
  public boolean isNull() {
    return this instanceof NullArgument;
  }

  /**
   * Returns whether the argument was set in the programs arguments.
   *
   * @return {@code true} if this argument was detected, {@code false} otherwise
   */
  public boolean isPresent() {
    return this.presentInCommand;
  }

  /**
   * Returns whether help should be shown for this argument.
   *
   * @return {@code true} if help should be shown for this argument, {@code false} otherwise
   */
  public boolean isHelpRequested() {
    return this.helpRequested;
  }

  /**
   * Returns whether this argument is required.
   *
   * @return {@code true} if this argument is required, {@code false} otherwise
   */
  public boolean isRequired() {
    return this.required;
  }

  /**
   * Returns whether this argument has a value.
   *
   * @return {@code true} if this argument has a value, {@code false} otherwise
   */
  public boolean isHasValue() {
    return this.hasValue;
  }

  /**
   * Returns whether a value is required
   *
   * @return {@code true} if a value is required, {@code false} otherwise
   */
  public boolean isRequiresValue() {
    return this.requiresValue;
  }

  /**
   * Returns the description of this argument.
   *
   * @return the description of this argument
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Returns whether this argument is a single-argument flag.
   *
   * @return {@code true} if this argument is a single-argument flag, {@code false} otherwise
   */
  public boolean isSingleValue() {
    return this.singleValue;
  }

  /**
   * Returns an unmodifiable list of the values associated with this argument.
   *
   * @return an unmodifiable list of the values associated with this argument
   */
  public List<String> getValues() {
    return Collections.unmodifiableList(this.values);
  }

  /**
   * Returns the first value in the values list as a String
   *
   * @return the first value in the values list
   */
  public String getValue() {
    return values.isEmpty() ? "" : this.values.get(0);
  }

  /**
   * Returns the default value of this argument.
   *
   * @return the default value of this argument
   */
  public String getDefaultValue() {
    return this.defaultValue;
  }

  /**
   * Returns the help string for this argument.
   *
   * @return the help string
   */
  public String getHelp() {
    return this.help;
  }

  /**
   * Return the name of the argument.
   *
   * @return the name of the argument
   */
  public abstract String getName();
}
