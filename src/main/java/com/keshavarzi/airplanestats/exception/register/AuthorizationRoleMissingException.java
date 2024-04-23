package com.keshavarzi.airplanestats.exception.register;

/**
 * Custom exception if a role is missing from the db plane-stats.user_data.role.
 * Roles should be set before the application runs
 */
public class AuthorizationRoleMissingException extends Exception {
  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public AuthorizationRoleMissingException(final String message) {
    super("Security role was not found: " + message);
  }
}
