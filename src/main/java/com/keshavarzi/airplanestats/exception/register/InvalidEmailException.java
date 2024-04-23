package com.keshavarzi.airplanestats.exception.register;

public class InvalidEmailException extends Exception {

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   */
  public InvalidEmailException(final String message) {
    super("Invalid Email: " + message);
  }
}
