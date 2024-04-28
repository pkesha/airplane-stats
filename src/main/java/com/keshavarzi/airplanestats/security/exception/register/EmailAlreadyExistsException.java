package com.keshavarzi.airplanestats.security.exception.register;

/** Custom Exception to indicate that email has already been registered. */
public class EmailAlreadyExistsException extends Exception {

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public EmailAlreadyExistsException(final String message) {
    super("Email Exists: " + message);
  }
}
