package com.wedasoft.wedasoftFxCustomNodes.htmlViewer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Getter;

import java.net.URL;

@Getter
public class HtmlViewer extends BorderPane {

    private final WebView webView;
    private final TextField searchTextField;
    private final JavascriptBridge javascriptBridge;
    private int selectedSearchResultIndex;

    public HtmlViewer(URL url) {
        webView = new WebView();
        webView.setMaxHeight(Integer.MAX_VALUE);
        webView.setMaxWidth(Integer.MAX_VALUE);
        webView.getEngine().setJavaScriptEnabled(true);
        setCenter(webView);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search...");
        searchTextField.setMinWidth(0);
        searchTextField.setMinHeight(0);

        javascriptBridge = new JavascriptBridge(webView);

        Button searchButton = new Button("Search");
        searchButton.setMinWidth(0);
        searchButton.setMinHeight(0);
        searchButton.setOnAction(e -> onSearchButtonClick());

        Button selectPreviousSearchResultButton = new Button("/\\");
        selectPreviousSearchResultButton.setOnAction(e -> onSelectPreviousSearchResultButtonClick());

        Button selectNextSearchResultButton = new Button("\\/");
        selectNextSearchResultButton.setOnAction(e -> onSelectNextSearchResultButtonClick());

        Button resetButton = new Button("Reset");
        resetButton.setMinWidth(0);
        resetButton.setMinHeight(0);
        resetButton.setOnAction(e -> onResetButtonClick());

        HBox searchBar = new HBox(
                searchTextField,
                searchButton,
                selectPreviousSearchResultButton,
                selectNextSearchResultButton,
                resetButton);
        HBox.setHgrow(searchTextField, Priority.ALWAYS);
        searchBar.setMinHeight(0);
        searchBar.setMaxWidth(Double.MAX_VALUE);
        searchBar.setSpacing(5);
        searchBar.setPadding(new Insets(5, 5, 5, 5));
        setTop(searchBar);

        loadHtmlByUrl(url);

        setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) onSearchButtonClick();
            if (e.getCode() == KeyCode.F && e.isControlDown()) onSearchShortcutClick();
        });
    }

    private void onSelectNextSearchResultButtonClick() {
        selectedSearchResultIndex = javascriptBridge.selectNextSearchResultAndReturnNewIndex(selectedSearchResultIndex);
    }

    private void onSelectPreviousSearchResultButtonClick() {
        selectedSearchResultIndex = javascriptBridge.selectPreviousSearchResultAndReturnNewIndex(selectedSearchResultIndex);
    }


    public void loadHtmlByUrl(URL url) {
        webView.getEngine().load(url.toExternalForm());
    }

    private void onSearchShortcutClick() {
        webView.requestFocus();
        searchTextField.requestFocus();
    }

    private void onSearchButtonClick() {
        javascriptBridge.removeHighlightings();
        if (searchTextField.getText().trim().isBlank()) {
            return;
        }
        javascriptBridge.highlightText(searchTextField.getText());
        selectedSearchResultIndex = -1;
        onSelectNextSearchResultButtonClick();
    }

    private void onResetButtonClick() {
        javascriptBridge.removeHighlightings();
        selectedSearchResultIndex = -1;
        searchTextField.setText("");
    }

    @SuppressWarnings("unused")
    private String getCurrentHtmlOfWebViewDocument() {
        return javascriptBridge.getDomAsHtmlString();
    }

    private static class JavascriptBridge {
        private final WebEngine webEngine;
        private final String nodeContainingHighlightings;
        private final boolean caseInSensitive;
        private final String cssClassUsedForHighlighting;

        public JavascriptBridge(final WebView webView) {
            this.webEngine = webView.getEngine();
            this.nodeContainingHighlightings = "document.body";
            this.caseInSensitive = true;
            this.cssClassUsedForHighlighting = "wedasoft-highlighted";
        }

        void highlightText(String textToHighlight) {
            final String javascript = String.format("""
                             /**
                             * @param {string} elem Element to search for keywords in
                             * @param {string[]} keywords Keywords to highlight
                             * @param {boolean} caseInSensitive Differenciate between capital and lowercase letters
                             * @param {string} cssClass Class to apply to the highlighted keyword
                             */
                            function highlightInsideElement(elem, keywords, caseInSensitive = true, cssClass) {
                                const flags = caseInSensitive ? 'gi' : 'g';
                                // Sort longer matches first to avoid
                                // highlighting keywords within keywords.
                                keywords.sort((a, b) => b.length - a.length);
                                Array.from(elem.childNodes).forEach(child => {
                                    const keywordRegex = RegExp(keywords.join('|'), flags);
                                    if (child.nodeType !== 3) { // not a text node
                                        highlightInsideElement(child, keywords, caseInSensitive, cssClass);
                                    } else if (keywordRegex.test(child.textContent)) {
                                        const frag = document.createDocumentFragment();
                                        let lastIdx = 0;
                                        child.textContent.replace(keywordRegex, (match, idx) => {
                                            const part = document.createTextNode(child.textContent.slice(lastIdx, idx));
                                            const highlighted = document.createElement('span');
                                            highlighted.textContent = match;
                                            highlighted.classList.add(cssClass);
                                            highlighted.style.background='yellow';
                                            frag.appendChild(part);
                                            frag.appendChild(highlighted);
                                            lastIdx = idx + match.length;
                                        });
                                        const end = document.createTextNode(child.textContent.slice(lastIdx));
                                        frag.appendChild(end);
                                        child.parentNode.replaceChild(frag, child);
                                    }
                                });
                            }
                                                   
                            highlightInsideElement(%s, ['%s'], %s, '%s');""",
                    nodeContainingHighlightings,
                    textToHighlight,
                    caseInSensitive,
                    cssClassUsedForHighlighting);

            webEngine.executeScript(javascript);
        }

        void removeHighlightings() {
            final String javascript = String.format("""
                            /**
                             * @param {string} nodeToRemoveHighlightingsIn Node to remove highlights from.
                             * @param {string} cssClass Class used for highlighting.
                             */
                            function removeHighlightings(nodeToRemoveHighlightingsIn, cssClass) {
                              Array.from(nodeToRemoveHighlightingsIn.querySelectorAll(`.${cssClass}`)).forEach(span => {
                                const parent = span.parentNode;
                                parent.replaceChild(document.createTextNode(span.textContent), span);
                                parent.normalize();
                              });
                            }
                                            
                            removeHighlightings(%s, '%s');""",
                    nodeContainingHighlightings,
                    cssClassUsedForHighlighting);

            webEngine.executeScript(javascript);
        }

        Integer selectNextSearchResultAndReturnNewIndex(int selectedSearchResultIndex) {
            return selectSearchResultAndReturnNewIndex(true, selectedSearchResultIndex);
        }

        Integer selectPreviousSearchResultAndReturnNewIndex(int selectedSearchResultIndex) {
            return selectSearchResultAndReturnNewIndex(false, selectedSearchResultIndex);
        }

        Integer selectSearchResultAndReturnNewIndex(boolean scrollToNextResult, int selectedSearchResultIndex) {
            final String javascript = String.format("""
                            /**
                             * @param {string} cssClass The css class used for highlighting and to scroll to.
                             * @param selectedSearchResultIndex {number} The index of the current 'selected' search result.
                             * @param scrollToNext {boolean} True for scrolling to the next search result, false for the previous one.
                             * @returns {number} The new current index of the selected search result.
                             */
                            function scrollToSearchResult(cssClass, selectedSearchResultIndex, scrollToNext = true) {
                                function scrollToResultAndMark(allResults, newResultIndex) {
                                    for (let result of allResults) {
                                        result.style.background = 'yellow';
                                    }
                                    allResults[newResultIndex].scrollIntoView(true);
                                    allResults[newResultIndex].style.background = '#FF9632';
                                    return newResultIndex;
                                }

                                let allResults = document.querySelectorAll('.' + cssClass);

                                if (allResults.length === 0) {
                                    return -1;
                                }
                                if (selectedSearchResultIndex === -1) {
                                    let firstIndex = 0;
                                    return scrollToResultAndMark(allResults, firstIndex);
                                }

                                if (scrollToNext) {
                                    if (selectedSearchResultIndex < allResults.length - 1) {
                                        let nextIndex = selectedSearchResultIndex + 1;
                                        return scrollToResultAndMark(allResults, nextIndex);
                                    } else {
                                        let firstIndex = 0;
                                        return scrollToResultAndMark(allResults, firstIndex);
                                    }
                                } else {
                                    if (selectedSearchResultIndex > 0) {
                                        let previousIndex = selectedSearchResultIndex - 1;
                                        return scrollToResultAndMark(allResults, previousIndex);
                                    } else {
                                        let lastIndex = allResults.length - 1;
                                        return scrollToResultAndMark(allResults, lastIndex);
                                    }
                                }
                            }
                                                                                                                                    
                            scrollToSearchResult('%s', %s, %s);""",
                    cssClassUsedForHighlighting,
                    selectedSearchResultIndex,
                    scrollToNextResult);

            return (int) webEngine.executeScript(javascript);
        }

        String getDomAsHtmlString() {
            final String javascript = "document.documentElement.outerHTML";
            return (String) webEngine.executeScript(javascript);
        }

    }

}
