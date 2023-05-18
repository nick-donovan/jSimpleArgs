package dev.nicholasdonovan.jsimpleargs;

import java.util.Collections;
import java.util.List;

/**
 * The NullArgument is used instead of {@code null} to represent a nonexistent argument. This allows the programmer
 * to avoid null pointer exceptions as well as keeping consistent code, without excessive error checking.
 */
class NullArgument extends Argument {
  /** Construct a new NullArgument object */
  NullArgument() {
    super("");
  }

  /**
   * Return an empty string representing the first value in the null arguments value list.
   *
   * @return an empty string representing the first value in the null arguments value list
   */
  @Override
  public String getValue() {
    return "";
  }

  /**
   * Return an empty list representing the null arguments value list.
   *
   * @return an empty list representing the null arguments value list
   */
  @Override
  public List<String> getValues() {
    return Collections.emptyList();
  }

  /**
   * An overridden, empty add value method for the null argument
   *
   * @param value the value that is intended to be inserted to the value list.
   */
  @Override
  void addValue(String value) {
    // Don't do anything.
  }

  /**
   * Return an empty string for the null argument name.
   *
   * @return an empty string for the null argument name
   */
  @Override
  public String getName() {
    return "";
  }

}
