package com.wedasoft.wedasoftFxCustomNodes.htmlViewer;

import com.wedasoft.wedasoftFxCustomNodes.shared.wedasoftFxTestBase.WedasoftFxTestBase;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

class HtmlViewerTest extends WedasoftFxTestBase {

    @Test
    void test() throws Exception {
        runOnJavaFxThreadAndJoin(() -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new HtmlViewer(getClass().getResource(
                    "/com/wedasoft/wedasoftFxCustomNodes/testHtmlFile.html"))));
            stage.show();
            stage.close();
        });
    }

}