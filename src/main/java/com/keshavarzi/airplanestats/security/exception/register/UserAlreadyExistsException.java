package com.keshavarzi.airplanestats.security.exception.register;

/** Custom Exception to indicate that Email has already been registered. */
public final class UserAlreadyExistsException extends Exception {
  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public UserAlreadyExistsException(final String message) {
    super("Username Exists: " + message);
  }
}
