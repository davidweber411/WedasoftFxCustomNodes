package com.wedasoft.wedasoftFxCustomNodes.shared.wedasoftFxTestBase;

/**
 * This class represnts an exception for the {@link WedasoftFxTestBaseException}.
 *
 * @author davidweber411
 */
public class WedasoftFxTestBaseException extends Exception {

    /**
     * Constructs an exception.
     *
     * @param message The message.
     */
    public WedasoftFxTestBaseException(
            String message) {

        super(message);
    }

    /**
     * Constructs an exception.
     *
     * @param message The message.
     * @param cause   The cause.
     */
    public WedasoftFxTestBaseException(
            String message,
            Throwable cause) {

        super(message, cause);
    }

}
