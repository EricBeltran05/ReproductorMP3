/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3player.views;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;

/**
 *
 * @author eric_
 */
public class MP3PlayListView {

    private final SplitPane rootPane;
    private final Label newPlayListTitle;
    private final TextField newPlayListTitleField;

    public MP3PlayListView() {

        rootPane = new SplitPane();
        rootPane.setOrientation(Orientation.VERTICAL);
        rootPane.setDividerPositions(0.85);
        rootPane.setMinHeight(50.0);
        rootPane.setMinWidth(100.0);

        newPlayListTitle = new Label("New PlayList");
        newPlayListTitle.setId("newPlayList");
        //newPlayListTitle.setVisible(false);
        newPlayListTitle.getStyleClass().add("padding-10");
        newPlayListTitle.setMaxHeight(61.0);

        newPlayListTitleField = new TextField("");
        //newPlayListTitleField.setVisible(false);
        newPlayListTitleField.getStyleClass().add("padding-10");
        newPlayListTitleField.setMaxHeight(61.0);
    }

    public TextField setPlayListTitleField() {
        return newPlayListTitleField;
    }

    public Label setPlayListTitle() {
        return newPlayListTitle;

    }

}
