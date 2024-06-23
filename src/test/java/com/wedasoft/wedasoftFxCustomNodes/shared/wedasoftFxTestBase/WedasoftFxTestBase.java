package com.wedasoft.wedasoftFxCustomNodes.shared.wedasoftFxTestBase;

import javafx.scene.input.KeyCode;

/**
 * This is the only class, that shall be used by developers for interacting with this framework.<br>
 * All other classes of this framework are not meant to be used directly.
 *
 * @author davidweber411
 */
public abstract class WedasoftFxTestBase {

    /**
     * This method runs the passed code on the JavaFX thread.<br>
     * The code to run is passed with the CodeRunner Interface.<br>
     * The main thread is forced to wait until the code is executed.<br>
     * <b>Do not put assertions in the CodeRunner. This will not work properly.</b>
     *
     * @param codeRunner Passes code to the JavaFX thread.
     * @throws Exception If an error occurs.
     */
    public synchronized void runOnJavaFxThreadAndJoin(
            CodeRunner codeRunner)
            throws Exception {

        WedasoftFxTestBaseImpl.runAndWaitForPlatformStartup();
        WedasoftFxTestBaseImpl.runAndWaitForPlatformRunLater(codeRunner);
    }

    /**
     * This method presses a key after the given amount of milliseconds. The key is pressed in an other thread.
     *
     * @param millisToWaitBeforeKeyPress Milliseconds to wait before pressing the key.
     * @param keyToPress                 The key to press.
     */
    public static void pressKeyAsyncInOtherThread(
            int millisToWaitBeforeKeyPress,
            KeyCode keyToPress) {

        WedasoftFxTestBaseImpl.pressKeyAsyncInOtherThread(millisToWaitBeforeKeyPress, keyToPress);
    }

}
