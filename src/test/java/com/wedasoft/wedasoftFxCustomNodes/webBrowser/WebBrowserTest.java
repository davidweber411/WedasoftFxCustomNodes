package com.wedasoft.wedasoftFxCustomNodes.webBrowser;

import com.wedasoft.wedasoftFxCustomNodes.shared.wedasoftFxTestBase.WedasoftFxTestBase;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

class WebBrowserTest extends WedasoftFxTestBase {

    private Stage stage;
    private WebBrowser webBrowser;

    @Test
    void test() throws Exception {
        runOnJavaFxThreadAndJoin(() -> {
            webBrowser = new WebBrowser();
            webBrowser.loadUrl("https://www.github.com");
            stage = new Stage();
            stage.setScene(new Scene(webBrowser));
            stage.show();
            stage.close();
        });
    }

}