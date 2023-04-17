/**
 * Copyright (C) 2023 Nick Donovan
 * <p>
 * This file is part of jSimpleArgs.
 * <p>
 * jSimpleArgs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, orParser
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

package dev.nicholasdonovan.jsimpleargs.exceptions;

/**
 * Indicates that an argument name is invalid in some way such as a null argument name was provided, an attempt at
 * retrieving the argument was not finished, or there are invalid characters in the argument name.
 */
public class InvalidArgumentNameException extends Exception {
  /**
   * Constructs a new InvalidArgumentNameException with a default message.
   */
  public InvalidArgumentNameException() {
    super();
  }

  /**
   * Constructs a new InvalidArgumentNameException with a specified message.
   *
   * @param message the error message
   */
  public InvalidArgumentNameException(String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidArgumentNameException with the specified error message and cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public InvalidArgumentNameException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new InvalidArgumentNameException with the specific cause.
   *
   * @param cause the cause of the exception
   */
  public InvalidArgumentNameException(Throwable cause) {
    super(cause);
  }
}
