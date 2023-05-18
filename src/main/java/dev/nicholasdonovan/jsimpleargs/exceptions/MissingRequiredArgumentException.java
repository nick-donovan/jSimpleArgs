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

package dev.nicholasdonovan.jsimpleargs.exceptions;

/**
 * Indicates that an argument is being called incorrectly.
 */
public class MissingRequiredArgumentException extends Exception {
  /**
   * Constructs a new MissingRequiredArgumentException with a default message.
   */
  public MissingRequiredArgumentException() {
    super();
  }

  /**
   * Constructs a new MissingRequiredArgumentException with a specified message.
   *
   * @param message the error message
   */
  public MissingRequiredArgumentException(String message) {
    super(message);
  }

  /**
   * Constructs a new MissingRequiredArgumentException with the specified error message and cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public MissingRequiredArgumentException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new MissingRequiredArgumentException with the specific cause.
   *
   * @param cause the cause of the exception
   */
  public MissingRequiredArgumentException(Throwable cause) {
    super(cause);
  }
}
