package com.wedasoft.wedasoftFxCustomNodes.htmlViewer;

import com.wedasoft.wedasoftFxCustomNodes.shared.wedasoftFxTestBase.WedasoftFxTestBase;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.wedasoft.wedasoftFxCustomNodes.shared.UserRobotUtil.typeKeysAfterSeconds;
import static javafx.scene.input.KeyCode.*;
import static org.assertj.core.api.Assertions.assertThat;

class HtmlViewerTest extends WedasoftFxTestBase {

    private Stage stage;
    private HtmlViewer htmlViewer;

    @Test
    void test() throws Exception {
        runOnJavaFxThreadAndJoin(() -> {
            htmlViewer = new HtmlViewer(getClass().getResource("/com/wedasoft/wedasoftFxCustomNodes/testHtmlFile.html"));
            stage = new Stage();
            stage.setScene(new Scene(htmlViewer));
            stage.show();
//            stage.showAndWait();
//            stage.close();
        });
        TimeUnit.SECONDS.sleep(1);
        assertThat(htmlViewer).isNotNull();

        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSearchTextField().requestFocus());
        typeKeysAfterSeconds(List.of(H, A, N, D, L, I), 0);
        TimeUnit.SECONDS.sleep(1);
        assertThat(htmlViewer.getSearchTextField().getText()).isEqualTo("handli");

        /* search button */
        assertThat(htmlViewer.getSelectedSearchResultIndex()).isEqualTo(-1);
        typeKeysAfterSeconds(List.of(ENTER), 0);
        TimeUnit.SECONDS.sleep(1);
        assertThat(htmlViewer.getSelectedSearchResultIndex()).isEqualTo(0);

        /* next search result button */
        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSelectNextSearchResultButton().fire());
        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSelectNextSearchResultButton().fire());
        assertThat(htmlViewer.getSelectedSearchResultIndex()).isEqualTo(2);

        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSelectNextSearchResultButton().fire());
        assertThat(htmlViewer.getSelectedSearchResultIndex()).isEqualTo(0);

        /* previous search result button */
        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSelectPreviousSearchResultButton().fire());
        assertThat(htmlViewer.getSelectedSearchResultIndex()).isEqualTo(2);

        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSelectPreviousSearchResultButton().fire());
        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSelectPreviousSearchResultButton().fire());
        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSelectPreviousSearchResultButton().fire());
        assertThat(htmlViewer.getSelectedSearchResultIndex()).isEqualTo(2);

        /* reset button */
        runOnJavaFxThreadAndJoin(() -> htmlViewer.getResetButton().fire());
        assertThat(htmlViewer.getSearchTextField().getText()).isEqualTo("");
        assertThat(htmlViewer.getSelectedSearchResultIndex()).isEqualTo(-1);

        /* shortcut ctrl+f */
        runOnJavaFxThreadAndJoin(() -> htmlViewer.getSearchTextField().requestFocus());
        typeKeysAfterSeconds(List.of(A, B, C), 0);
        TimeUnit.SECONDS.sleep(1);
        assertThat(htmlViewer.getSearchTextField().getText()).isEqualTo("abc");

        typeCtrlWithKey(F);
        TimeUnit.SECONDS.sleep(1);
        typeKeysAfterSeconds(List.of(D, E, F), 0);
        TimeUnit.SECONDS.sleep(1);
        assertThat(htmlViewer.getSearchTextField().getText()).isEqualTo("def");
    }


    private void typeCtrlWithKey(KeyCode key) {
        new Thread(() -> {
            try {
                new Robot().keyPress(CONTROL.getCode());
                new Robot().keyPress(key.getCode());
                new Robot().keyRelease(key.getCode());
                Thread.sleep(100);
                new Robot().keyRelease(CONTROL.getCode());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}