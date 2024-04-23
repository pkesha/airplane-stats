package com.keshavarzi.airplanestats.exception.register;

/**
 * Custom Exception for invalid password/login attempt.
 */
public class InvalidPasswordException extends Exception {

  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public InvalidPasswordException(final String message) {
    super("Invalid Password: " + message);
  }

  //    /**
  //     * Returns detail reason for invalid password
  //     * @return detail reason for invalid password
  //     */
  //    private String printMessage()
  //    {
  //        switch (this.passwordConditionViolated) {
  //            case 1:
  //                return ("Password length should be between 8 to 15 characters");
  //            case 2:
  //                return ("Password should contain at least one digit (0-9)");
  //            case 3:
  //                return ("Password should contain at least one special character");
  //            case 4:
  //                return ("Password should contain at least one uppercase letter (A-Z)");
  //            case 5:
  //                return ("Password should contain at least one lowercase letter (a-z)");
  //            default:
  //                return ("");
  //        }
  //    }

}
