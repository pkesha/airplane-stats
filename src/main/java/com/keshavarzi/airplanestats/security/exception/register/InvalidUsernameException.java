package com.keshavarzi.airplanestats.security.exception.register;

/** Custom Exception that username is improperly formatted. */
public final class InvalidUsernameException extends Exception {
  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   */
  public InvalidUsernameException(final String message) {
    super("Invalid Username: " + message);
  }
}
