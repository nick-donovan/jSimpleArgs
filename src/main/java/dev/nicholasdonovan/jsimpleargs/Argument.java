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
 * <p>Represents a command line argument with various properties. An instance of this class represents a command line argument that can be passed
 * to a program. The argument can have a short name and a long name, a description, and various optional properties.
 * The properties include whether the argument accepts a single or multiple values, whether the argument has a value, whether the argument is
 * required, and whether the argument is present. Additionally, the argument can have a default value and a help message.</p>
 * <p> To create an instance of this class, pass in the short name, long name, and description of the argument. Then, set various properties of the
 * argument using the setter methods.</p>
 */
public class Argument {
  /**
   * Short name of the argument.
   */
  private final String shortName;
  /**
   * Long name of the argument.
   */
  private final String longName;
  /**
   * Description of the argument.
   */
  private final String description;
  /**
   * Whether the argument accepts only one value. This flag is {@code false} by default.
   */
  private boolean singleArg = false;
  /**
   * Whether the argument expects a value. This flag is {@code false} by default.
   */
  private boolean hasValue = false;
  /**
   * Whether the argument is required. This flag is {@code false} by default.
   */
  private boolean required = false;
  /**
   * Determines whether the argument was set in the programs arguments. This flag is {@code false} by default.
   */
  private boolean present = false;
  /**
   * Default value for the argument. This flag is empty by default.
   */
  private String defaultValue = "";
  /**
   * Help text for the argument.
   */
  private String help = "No help available for argument.";
  /**
   * Whether the help text for the argument should be displayed.
   */
  private boolean showHelp = false;
  /**
   * The list of values associated with this argument.
   * <p>When an argument has values, they are all stored in this list. This list is initialized as an empty ArrayList
   * by default and is populated with values from the {@code Parser} class.</p>
   *
   * <p>To retrieve the values, use the {@link #getValues()} method, which returns an unmodifiable view of the list.</p>
   */
  private final List<String> values = new ArrayList<>();

  /**
   * Constructs a new {@code Argument} object with the given short name, long name, and description.
   *
   * @param shortName   the short name of the argument (can be null)
   * @param longName    the long name of the argument (can be null)
   * @param description the description of the argument (can be null)
   */
  public Argument(String shortName, String longName, String description) {
    this.shortName = shortName;
    this.longName = longName;
    this.description = description;
  }

  /**
   * Sets the {@code singleArg} field of this argument to {@code true} and returns this object.
   *
   * @return this {@code Argument} object with the {@code singleArg} field set to {@code true}
   */
  public Argument singleArg() {
    this.singleArg = true;
    return this;
  }

  /**
   * Sets the {@code hasValue} field of this argument to {@code true} and returns this object.
   *
   * @return this {@code Argument} object with the {@code hasValue} field set to {@code true}
   */
  public Argument hasValue() {
    this.hasValue = true;
    return this;
  }

  /**
   * Sets the {@code required} field of this argument to {@code true} and returns this object.
   *
   * @return this {@code Argument} object with the {@code required} field set to {@code true}
   */
  public Argument required() {
    this.required = true;
    return this;
  }

  /**
   * Sets the {@code defaultValue} field of this argument to the given value and returns this object.
   *
   * @param defaultValue the default value to set for this argument (can be null)
   * @return this {@code Argument} object with the {@code defaultValue} field set to the given value
   */
  public Argument defaultValue(String defaultValue) {
    if (defaultValue == null) {
      defaultValue = "";
    }
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * Sets the {@code help} field of this argument to the given help text and returns this object.
   *
   * @param help the help text to set for this argument (can be null)
   * @return this {@code Argument} object with the {@code help} field set to the given help text
   */
  public Argument help(String help) {
    if (help == null) {
      help = "";
    }
    this.help = help;
    return this;
  }

  /**
   * Sets the showHelp flag to true.
   */
  public void showHelp() {
    this.showHelp = true;
  }

  /**
   * Sets the present flag to true once the argument has been parsed.
   */
  public void setPresentInArg() {
    this.present = true;
  }

  /**
   * Returns true if the given argument equals the short name of this argument.
   *
   * @param arg the argument to match
   * @return true if the argument matches the short name
   */
  public boolean equalsShortName(String arg) {
    return this.getShortName().equals(arg);
  }

  /**
   * Returns true if the given argument equals the long name of this argument.
   *
   * @param arg the argument to match
   * @return true if the argument matches the long name
   */
  public boolean equalsLongName(String arg) {
    return this.getLongName().equals(arg);
  }

  /**
   * Returns true if the given argument matches either the short or long name of this argument.
   *
   * @param arg the argument to match
   * @return true if the argument matches either the short or long name
   */
  public boolean equalsName(String arg) {
    return this.equalsLongName(arg) || this.equalsShortName(arg);
  }

  /**
   * Returns a formatted string representation of the {@code Argument} object, including its short name, long name,
   * whether it has a value, and its description.
   *
   * @return a formatted string representation of the {@code Argument} object
   */
  @Override
  public String toString() {
    return String.format("-%-4s --%-20s %20s %-40s", this.getShortName(), String.format("%s%s", this.getLongName(), (this.hasValue ? " <value>" :
        "")), this.getDefaultValue(), this.getDescription());
  }

  /**
   * Checks whether the given object is equal to the current {@code Argument} object by comparing their respective fields.
   *
   * @param rhs the object to compare with the current {@code Argument} object
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object rhs) {
    if (!(rhs instanceof Argument right)) {
      return false;
    }

    return this.shortName.equals(right.shortName) && this.longName.equals(right.longName) && this.description.equals(right.description) && this.singleArg == right.singleArg && this.hasValue == right.hasValue && this.required == right.required;
  }

  /**
   * Calculates the hash code of the {@code Argument} object based on its fields.
   *
   * @return the hash code of the {@code Argument} object
   */
  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + shortName.hashCode();
    result = 31 * result + longName.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + (singleArg ? 1 : 0);
    result = 31 * result + (hasValue ? 1 : 0);
    result = 31 * result + (required ? 1 : 0);
    result = 31 * result + (help.isEmpty() ? 1 : help.hashCode());
    return result;
  }


  /* ********** Public Accessors *************** */

  /**
   * Returns an unmodifiable list of the values associated with this argument.
   *
   * @return an unmodifiable list of the values associated with this argument
   */
  public List<String> getValues() { return Collections.unmodifiableList(this.values); }

  /**
   * Returns the first value in the values list associated with this argument as a string or {@code null} if there is none.
   *
   * @return the first value in the values list associated with this argument as a string or {@code null} if there is none
   */
  public String getValue() { return values.isEmpty() ? null : this.values.get(0); }

  /**
   * Returns the short name of this argument.
   *
   * @return the short name of this argument
   */
  public String getShortName() { return this.shortName; }

  /**
   * Returns the long name of this argument.
   *
   * @return the long name of this argument
   */
  public String getLongName() { return this.longName; }

  /**
   * Returns the description of this argument.
   *
   * @return the description of this argument
   */
  public String getDescription() { return this.description; }

  /**
   * Returns whether this argument is a single-argument flag.
   *
   * @return {@code true} if this argument is a single-argument flag, {@code false} otherwise
   */
  public boolean getSingleArg() { return this.singleArg; }

  /**
   * Returns whether this argument is required.
   *
   * @return {@code true} if this argument is required, {@code false} otherwise
   */
  public boolean getRequired() { return this.required; }

  /**
   * Returns whether this argument has a value.
   *
   * @return {@code true} if this argument has a value, {@code false} otherwise
   */
  public boolean getHasValue() { return this.hasValue; }

  /**
   * Returns whether help should be shown for this argument.
   *
   * @return {@code true} if help should be shown for this argument, {@code false} otherwise
   */
  public boolean getShowHelp() { return this.showHelp; }

  /**
   * Returns the default value of this argument.
   *
   * @return the default value of this argument
   */
  public String getDefaultValue() { return this.defaultValue; }

  /**
   * Returns the help string for this argument.
   *
   * @return the help string
   */
  public String getHelp() {
    return this.help;
  }

  /**
   * Returns whether the argument was set in the programs arguments.
   *
   * @return {@code true} if this argument was detected, {@code false} otherwise
   */
  public boolean isPresent() {
    return this.present;
  }

  /* ********** Protected Accessors *************** */

  /**
   * Returns the list of values associated with this argument. This method is unprotected and should only be used by
   * subclasses.
   *
   * @return the list of values associated with this argument
   */
  protected List<String> getValuesUnprotected() { return this.values; }
}
