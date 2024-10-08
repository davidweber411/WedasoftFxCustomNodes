package com.wedasoft.wedasoftFxCustomNodes.zoomableScrollPane;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class ZoomableScrollPane extends ScrollPane {

    @Getter
    private static final double initialScaleValue = 1;
    @Getter
    private double scaleValue = initialScaleValue;

    @Getter
    private final double zoomIntensity = 0.1;
    @Getter
    private final Node contentNode;
    private final Node zoomNode;

    public ZoomableScrollPane(Node contentNode) {
        super();
        this.contentNode = contentNode;
        this.zoomNode = new Group(contentNode);
        setContent(outerNode(zoomNode));

        // setPannable(true);
        setHbarPolicy(ScrollBarPolicy.ALWAYS);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
        setFitToHeight(true); // centering the content
        setFitToWidth(true); // centering the content

        updateScale();
    }

    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.setOnScroll(e -> {
            if (e.isControlDown()) {
                e.consume();
                onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
            }
        });
        return outerNode;
    }

    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private void updateScale() {
        contentNode.setScaleX(scaleValue);
        contentNode.setScaleY(scaleValue);
    }

    public void resetZoom() {
        scaleValue = initialScaleValue;
        contentNode.setScaleX(initialScaleValue);
        contentNode.setScaleY(initialScaleValue);
    }

    public void zoomIn() {
        onScroll(3, new Point2D(0, 0));
    }

    public void zoomOut() {
        onScroll(-3, new Point2D(0, 0));
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        updateScale();
        this.layout(); // refresh ScrollPane scroll positions & target bounds

        // convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = contentNode.parentToLocal(zoomNode.parentToLocal(mousePoint));

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = contentNode.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }

}