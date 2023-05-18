package dev.nicholasdonovan.jsimpleargs;

/**
 * Represents a positional command line argument and is an extension of the Argument class, positional arguments may
 * hold values and are typically not preceded by hyphens, unlike keyword arguments.
 */
class PositionalArgument extends Argument {
  private final String name;

  /**
   * Construct a new positional {@code Argument} object (ex: input).
   *
   * @param name        the name of the argument
   * @param description the description of the argument
   */
  public PositionalArgument(String name, String description) {
    super(description);
    this.name = name;
  }

  /**
   * Return the string representation of the argument
   *
   * @return string representation of the argument
   */
  @Override
  public String toString() {
    return String.format("%-18s %-9s %-19s %-30s",
        this.name,
        this.isHasValue() ? " <value>" : "",
        this.getDefaultValue().isEmpty() ? "" : "Default: " + getDefaultValue(),
        this.getDescription());
  }

  /**
   * Return the name of the positional argument
   *
   * @return the name of the positional argument
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Returns true if this positional argument has the same name as the provided object.
   *
   * @param rhs the object to compare this object to
   * @return true if this positional argument has the same name as the provided object
   */
  @Override
  public boolean equals(Object rhs) {
    if (this == rhs) return true;
    if (!(rhs instanceof PositionalArgument right)) return false;

    return name.equals(right.name);
  }

  /**
   * Returns the representing hash code of this positional argument.
   *
   * @return the hash code of this argument as an integer
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
