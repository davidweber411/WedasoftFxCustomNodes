package com.wedasoft.wedasoftFxCustomNodes.shared.wedasoftFxTestBase;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;
import static java.util.Objects.nonNull;

/**
 * This class should not be used directly.<br>
 * There is no guarantee, that this class will not change in future.<br>
 * This class implements the framework functions.
 *
 * @author davidweber411
 */
public class WedasoftFxTestBaseImpl {

    /* General */
    private static final boolean DEBUG_MODE = false;

    /* PlatformStartup */
    private static final int PS_TIMEOUT_SECONDS_TO_WAIT = 15;
    private static volatile boolean ps_isTimedOut;
    private static boolean ps_runPlatformStartup = true;
    private static boolean ps_platformStartupIsLoading = true;

    /* PlatformRunLater */
    public static final int PRL_TIMEOUT_SECONDS_TO_WAIT = 15;
    private static volatile boolean prl_jfxThreadIsLoading;
    private static Exception prl_passedExceptionFromJfxThread;

    static synchronized void runAndWaitForPlatformStartup()
            throws Exception {

        if (ps_runPlatformStartup) {
            Platform.startup(() -> {
                Platform.setImplicitExit(false);
                ps_platformStartupIsLoading = false;
                ps_runPlatformStartup = false;
            });
            LocalDateTime timeOutTime = LocalDateTime.now().plusSeconds(PS_TIMEOUT_SECONDS_TO_WAIT);
            while (ps_platformStartupIsLoading) {
                if (DEBUG_MODE) System.out.println("ps looping...");
                if (LocalDateTime.now().isAfter(timeOutTime)) {
                    ps_isTimedOut = true;
                    Platform.exit();
                    throw new WedasoftFxTestBaseException("Timeout during the execution of runAndWaitForPlatformStartup()");
                }
                //noinspection BusyWait
                Thread.sleep(250);
            }
        }
    }

    static synchronized void runAndWaitForPlatformRunLater(
            CodeRunner codeRunner)
            throws Exception {

        prl_jfxThreadIsLoading = true;

        Platform.runLater(() -> {
            try {
                codeRunner.run();
            } catch (Exception e) {
                prl_passedExceptionFromJfxThread = e;
            } finally {
                prl_jfxThreadIsLoading = false;
            }
        });
        LocalDateTime timeOutTime = LocalDateTime.now().plusSeconds(PRL_TIMEOUT_SECONDS_TO_WAIT);
        while (prl_jfxThreadIsLoading) {
            if (DEBUG_MODE) System.out.println("prl looping...");
            if (ps_isTimedOut || LocalDateTime.now().isAfter(timeOutTime)) {
                ps_isTimedOut = false;
                throw new WedasoftFxTestBaseException("Timeout during the execution of runAndWaitForPlatformRunLater()");
            }
            //noinspection BusyWait
            Thread.sleep(250);
        }
        Thread.sleep(200); // quick and dirty fix for assuming a wrong test result
        if (nonNull(prl_passedExceptionFromJfxThread)) {
            WedasoftFxTestBaseException exception = new WedasoftFxTestBaseException("The passed JavaFX code could not be initialized without errors. CodeRunner.initialize() threw an Exception. ", prl_passedExceptionFromJfxThread);
            prl_passedExceptionFromJfxThread = null;
            throw exception;
        }
    }

    /**
     * Types a key asynchronously in another thread. The thread waits the given amount of millis before it types the key.
     *
     * @param millisToWait The millis the Thread waits before typing.
     * @param keyToPress   The key to type.
     */
    public static void pressKeyAsyncInOtherThread(
            int millisToWait,
            KeyCode keyToPress) {

        new Thread(() -> {
            try {
                sleep(millisToWait);
                new Robot().keyPress(keyToPress.getCode());
                new Robot().keyRelease(keyToPress.getCode());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

}
