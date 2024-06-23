package com.wedasoft.wedasoftFxCustomNodes.shared;

import javafx.scene.input.KeyCode;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is used for simulating real user actions.
 */
public class UserRobotUtil {

    /**
     * Types the given key in the given order. Sleeps between every key the given amount of seconds.
     *
     * @param keys    The keys to type.
     * @param seconds The amount of seconds to wait between the keys.
     */
    public static void typeKeysAfterSeconds(List<KeyCode> keys, int seconds) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(seconds);
                for (KeyCode key : keys) {
                    new Robot().keyPress(key.getCode());
                    new Robot().keyRelease(key.getCode());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
