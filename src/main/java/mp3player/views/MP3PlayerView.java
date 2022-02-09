package mp3player.views;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import mp3player.models.PlayList;
import mp3player.models.Song;

/**
 *
 * @author Cristòfol-Lluís Thwaite
 */
public class MP3PlayerView {

    private final SplitPane rootPane;
    private final SplitPane playListPane;
    private final VBox playLists;
    private final Label playListsTitle;
    private final ListView<PlayList> playListsList;
    private final HBox playListsControls;
    private final Button addPlayList;
    private final Button deletePlayList;
    private final TableView<Song> trackTable;
    private final TableColumn<Song, String> title;
    private final TableColumn<Song, String> genre;
    private final TableColumn<Song, String> artist;
    private final TableColumn<Song, String> album;
    private final TableColumn<Song, String> time;
    private final HBox audioControls;
    private final HBox trackControls;
    private final Button back;
    private final Button playPause;
    private final Button forward;
    private final VBox currentTrack;
    private final Label currentTitle;
    private final Label currentArtist;
    private final Slider slider;
    private final HBox trackTimes;
    private final Label currentTime;
    private final Label separator;
    private final Label totalTime;
    private final HBox playListControls;
    private final Button shuffle;
    private final Button repeat;
    private MediaView media;



    public MP3PlayerView() {
        rootPane = new SplitPane();
        rootPane.setOrientation(Orientation.VERTICAL);
        rootPane.setDividerPositions(0.85);
        rootPane.setMinHeight(720.0);
        rootPane.setMinWidth(1280.0);

        playListPane = new SplitPane();
        playListPane.setOrientation(Orientation.HORIZONTAL);
        playListPane.setDividerPositions(0.2);

        playLists = new VBox(3);
        playListsTitle = new Label("Playlists");
        playListsTitle.setId("playListsTitle");
        playListsTitle.getStyleClass().add("padding-10");
        playListsTitle.setMaxHeight(61.0);
        playListsList = new ListView<>();
        playListsList.setMinHeight(490.0);
        playListsList.setId("playListsList");
        VBox.setVgrow(playListsList, Priority.ALWAYS);
        playListsControls = new HBox(2);
        playListsControls.setMaxHeight(61.0);
        playListsControls.getStyleClass().add("padding-10");
        addPlayList = new Button();
        addPlayList.setAlignment(Pos.CENTER);
        addPlayList.setGraphic(
                GlyphsDude.createIcon(FontAwesomeIcon.PLUS, "9px")
        );
        addPlayList.setPrefSize(25.0, 25.0);


        deletePlayList = new Button();
        deletePlayList.setGraphic(
                GlyphsDude.createIcon(FontAwesomeIcon.MINUS, "9px")
        );
        deletePlayList.setPrefSize(25.0, 25.0);
        playListsControls.getChildren().addAll(addPlayList, deletePlayList);
        playLists.getChildren().addAll(
                playListsTitle, playListsList, playListsControls
        );

        trackTable = new TableView<>();
        trackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        title = new TableColumn<>("Title");
        genre = new TableColumn<>("Genre");
        artist = new TableColumn<>("Artist");
        album = new TableColumn<>("Album");
        time = new TableColumn<>("Time");
        trackTable.getColumns().addAll(title, genre, artist, album, time);

        playListPane.getItems().addAll(playLists, trackTable);
        //Constrain max width of left component binding it to parent pane
        playLists.maxWidthProperty().bind(
                playListPane.widthProperty().multiply(0.25)
        );

        audioControls = new HBox(6);
        audioControls.setMinHeight(108.0);
        audioControls.setAlignment(Pos.CENTER);

        trackControls = new HBox(3);
        trackControls.getStyleClass().add("padding-10");
        trackControls.setAlignment(Pos.CENTER);
        back = new Button();
        back.setPrefSize(30.0, 25.0);
        back.setId("back");
        back.setGraphic(GlyphsDude.createIcon(
                FontAwesomeIcon.FAST_BACKWARD, "10px")
        );
        playPause = new Button();
        playPause.setPrefSize(25.0, 25.0);
        playPause.setId("playPause");
        playPause.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.PLAY, "9px"));
        playPause.getStyleClass().add("play");
        forward = new Button();
        forward.setPrefSize(30.0, 25.0);
        forward.setId("forward");
        forward.setGraphic(GlyphsDude.createIcon(
                FontAwesomeIcon.FAST_FORWARD, "10px")
        );
        trackControls.getChildren().addAll(back, playPause, forward);

        currentTrack = new VBox(2);
        currentTrack.setAlignment(Pos.CENTER);
        currentTitle = new Label("Current Song Title");
        currentTitle.setId("currentTitle");
        currentArtist = new Label("Current Artist");
        currentArtist.setId("currentArtist");
        currentTrack.getChildren().addAll(currentTitle, currentArtist);

        slider = new Slider();
        HBox.setHgrow(slider, Priority.ALWAYS);
        slider.setId("slider");
        slider.autosize();

        trackTimes = new HBox(3);
        trackTimes.setAlignment(Pos.CENTER);
        currentTime = new Label("00:00");
        separator = new Label("/");
        totalTime = new Label("00:00");
        trackTimes.getChildren().addAll(currentTime, separator, totalTime);

        playListControls = new HBox(2);
        playListControls.setAlignment(Pos.CENTER);
        playListControls.getStyleClass().add("padding-10");
        shuffle = new Button();
        shuffle.setPrefSize(25.0, 30.0);
        shuffle.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.RANDOM, "14px"));
        repeat = new Button();
        repeat.setPrefSize(25.0, 30.0);
        repeat.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REPEAT, "14px"));
        playListControls.getChildren().addAll(shuffle, repeat);

        audioControls.getChildren().addAll(
                trackControls,
                currentTrack,
                slider,
                trackTimes,
                playListControls
        );

        rootPane.getItems().addAll(playListPane, audioControls);
        //Constrain min height of bottom component binding it to paren pane
        audioControls.maxHeightProperty().bind(
                rootPane.heightProperty().multiply(0.15)
        );

        //MODIFICACIÓ
        //PlayListNew
    }

    public SplitPane getRootPane() {
        return rootPane;
    }

    public SplitPane getPlayListPane() {
        return playListPane;
    }

    public VBox getPlayLists() {
        return playLists;
    }

    public Label getPlayListsTitle() {
        return playListsTitle;
    }

    public ListView<PlayList> getPlayListsList() {
        return playListsList;
    }

    public HBox getPlayListsControls() {
        return playListsControls;
    }

    public Button getAddPlayList() {
        return addPlayList;
    }

    public Button getDeletePlayList() {
        return deletePlayList;
    }

    public TableView<Song> getTrackTable() {
        return trackTable;
    }

    public TableColumn<Song, String> getTitle() {
        return title;
    }

    public TableColumn<Song, String> getGenre() {
        return genre;
    }

    public TableColumn<Song, String> getArtist() {
        return artist;
    }

    public TableColumn<Song, String> getAlbum() {
        return album;
    }

    public TableColumn<Song, String> getTime() {
        return time;
    }

    public HBox getAudioControls() {
        return audioControls;
    }

    public HBox getTrackControls() {
        return trackControls;
    }

    public Button getBack() {
        return back;
    }

    public Button getPlayPause() {
        return playPause;
    }

    public Button getForward() {
        return forward;
    }

    public VBox getCurrentTrack() {
        return currentTrack;
    }

    public Label getCurrentTitle() {
        return currentTitle;
    }

    public Label getCurrentArtist() {
        return currentArtist;
    }

    public Slider getSlider() {
        return slider;
    }

    public HBox getTrackTimes() {
        return trackTimes;
    }

    public Label getCurrentTime() {
        return currentTime;
    }

    public Label getSeparator() {
        return separator;
    }

    public Label getTotalTime() {
        return totalTime;
    }

    public HBox getPlayListControls() {
        return playListControls;
    }

    public Button getShuffle() {
        return shuffle;
    }

    public Button getRepeat() {
        return repeat;
    }

    public MediaView getMedia() {
        return media;
    }

    public void setMedia(MediaView media) {
        this.media = media;
    }
}
