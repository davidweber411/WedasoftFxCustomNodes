package com.wedasoft.wedasoftFxCustomNodes.webBrowser;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

public class WebBrowser extends BorderPane {

    private final WebView webView;
    private final TextField urlTextField;

    public WebBrowser() {
        webView = new WebView();
        urlTextField = new TextField();
        setTop(createMenuBar());
        setCenter(createBrowser());
    }

    public String getCurrentUrl() {
        return webView.getEngine().getLocation();
    }

    public void loadUrl(String fullQualifiedUrl) {
        if (fullQualifiedUrl == null) {
            urlTextField.setText("");
            return;
        }
        if (!fullQualifiedUrl.startsWith("http://") && !fullQualifiedUrl.startsWith("https://")) {
            fullQualifiedUrl = "http://" + fullQualifiedUrl;
        }
        urlTextField.setText(fullQualifiedUrl);
        webView.getEngine().load(fullQualifiedUrl);
    }

    private Node createBrowser() {
        webView.setMaxHeight(Integer.MAX_VALUE);
        webView.setMaxWidth(Integer.MAX_VALUE);
        webView.getEngine().setJavaScriptEnabled(true);
        return webView;
    }

    private HBox createMenuBar() {
        WebHistory history = webView.getEngine().getHistory();

        Button backButton = new Button("<");
        backButton.setMinWidth(0);
        backButton.setMinHeight(0);
        backButton.setOnAction(e -> {
            int currentIndex = history.getCurrentIndex();
            if (currentIndex > 0) {
                history.go(-1);
                urlTextField.setText(webView.getEngine().getLocation());
            }
        });
        Button forwardButton = new Button(">");
        forwardButton.setMinWidth(0);
        forwardButton.setMinHeight(0);
        forwardButton.setOnAction(e -> {
            int currentIndex = history.getCurrentIndex();
            if (currentIndex < history.getEntries().size() - 1) {
                history.go(1);
                urlTextField.setText(webView.getEngine().getLocation());
            }
        });

        urlTextField.setMinWidth(0);
        urlTextField.setMinHeight(0);
        urlTextField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loadUrl(urlTextField.getText());
            }
        });

        Button reloadButton = new Button("Reload");
        reloadButton.setMinWidth(0);
        reloadButton.setMinHeight(0);
        reloadButton.setOnAction(e -> loadUrl(urlTextField.getText()));

        HBox hBox = new HBox(backButton, forwardButton, urlTextField, reloadButton);
        HBox.setHgrow(urlTextField, Priority.ALWAYS);
        hBox.setMinHeight(0);
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        return hBox;
    }

}
