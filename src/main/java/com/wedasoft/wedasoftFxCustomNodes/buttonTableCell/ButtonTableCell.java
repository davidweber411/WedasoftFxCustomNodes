package com.wedasoft.wedasoftFxCustomNodes.buttonTableCell;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

import java.util.function.Consumer;

public class ButtonTableCell<TableRowT> extends TableCell<TableRowT, Void> {

    private final Button button;

    public ButtonTableCell(String title, Consumer<TableRowT> onButtonClickCallback) {
        this.button = new Button(title);
        this.button.setOnAction(event -> onButtonClickCallback.accept(getTableRow().getItem()));
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(button);
        }
    }

}

