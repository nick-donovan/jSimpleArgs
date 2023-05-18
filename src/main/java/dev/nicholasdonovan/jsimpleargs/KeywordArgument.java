package dev.nicholasdonovan.jsimpleargs;

/**
 * Represents a keyword command line argument. Keyword arguments can hold values and are preceded by a hyphen for
 * the short name, and two hyphens for the long name. (Ex: -i or --input)
 */
class KeywordArgument extends Argument {
  private final String shortName;
  private final String longName;

  /**
   * Construct a new keyword {@code Argument} object (ex: -i/--input).
   *
   * @param shortName   the short name of the argument
   * @param longName    the long name of the argument
   * @param description the description of the argument
   */
  public KeywordArgument(String shortName, String longName, String description) {
    super(description);
    this.shortName = shortName;
    this.longName = longName;
  }

  /**
   * Returns a formatted string representation of the {@code Argument} object.
   *
   * @return a formatted string representation of the {@code Argument} object
   */
  @Override
  public String toString() {
    return String.format("%-4s %-14s %-8s %-19s %-30s",
        this.shortName,
        this.longName,
        this.isHasValue() ? "<value>" : "",
        this.getDefaultValue().isEmpty() ? "" : "Default: " + getDefaultValue(),
        this.getDescription());
  }

  /**
   * Return the short name of the keyword argument.
   *
   * @return the short name of the keyword argument
   */
  public String getShortName() {
    return this.shortName;
  }

  /**
   * Return the long name of the keyword argument.
   *
   * @return the long name of the keyword argument
   */
  @Override
  public String getName() {
    return this.longName;
  }

  /**
   * Returns true if this keyword argument has the same short and long name as the provided object.
   *
   * @param rhs the object to compare this object to
   * @return true if this positional argument has the same short and long name as the provided object
   */
  @Override
  public boolean equals(Object rhs) {
    if (this == rhs) return true;
    if (!(rhs instanceof KeywordArgument right)) return false;

    if (!shortName.equals(right.shortName)) return false;
    return longName.equals(right.longName);
  }

  /**
   * Returns the representing hash code of this positional argument.
   *
   * @return the hash code of this argument as an integer
   */
  @Override
  public int hashCode() {
    int result = shortName.hashCode();
    result = 31 * result + longName.hashCode();
    return result;
  }
}
