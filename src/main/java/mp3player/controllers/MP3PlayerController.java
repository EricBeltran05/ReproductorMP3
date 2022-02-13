package mp3player.controllers;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mp3player.App;
import mp3player.models.AllJAXB;
import mp3player.models.PlayList;
import mp3player.models.PlayListsJAXB;
import mp3player.models.Song;
import mp3player.models.SongsListJAXB;
import mp3player.views.MP3PlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 *
 * @author Cristòfol-Lluís Thwaite
 */
public class MP3PlayerController {

    private MP3PlayerView v;
    private List<Song> songList;
    private List<PlayList> playList;
    private Song currentTrack;
    private PlayList currentPlayList;
    private int trackPosInPlayList;
    private int trackPosPlayList;
    private MediaPlayer player;
    private static String songsXML = "src\\main\\resources\\configuration\\songs.xml";
    private static String partialJSON = "src\\main\\resources\\playlists\\";
    private static File file = new File("src\\main\\resources\\configuration\\songs.xml");

    //MODIFICACIÓ
    PlayList pl = new PlayList();

    public MP3PlayerController(List<Song> songList, MP3PlayerView v) {
        this.songList = songList;
        this.playList = new ArrayList<>();
        this.v = v;
        player = null;
        initView();

    }

    public void initView() {
        v.getTitle().setCellValueFactory(new PropertyValueFactory<>("title"));
        v.getGenre().setCellValueFactory(new PropertyValueFactory<>("genre"));
        v.getArtist().setCellValueFactory(new PropertyValueFactory<>("artist"));
        v.getAlbum().setCellValueFactory(new PropertyValueFactory<>("album"));
        v.getTime().setCellValueFactory(new PropertyValueFactory<>("timeFormat"));
        PlayList p = new PlayList("All Songs", songList);
        v.getPlayListsList().getItems().add(p);
        for (Song s : songList) {
            v.getTrackTable().getItems().add(s);
        }

        trackPosInPlayList = 0;
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        v.getAudioControls().getChildren().add(v.getMedia());

        //PARA EL XML
        playList.add(p);
        loadAllPlaylist();
        v.getPlayListsList().getItems().clear();
        v.getPlayListsList().getItems().addAll(playList);
        saveSongs();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        //Generació d'un String amb format JSON
        for (PlayList pl : playList) {
            String jsonString = gson.toJson(pl);

            try {
                FileWriter file = new FileWriter(partialJSON + pl.getTitle().replace(" ", "") + ".json");
                BufferedWriter out = new BufferedWriter(file);
                out.write(jsonString);
                out.close();
                file.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        //createPlayList();
    }

    public void initController() {

        v.getPlayPause().setOnAction((e) -> {
            playPausePlayer();
            sliderController();
        });

        //v.getAddPlayList().setOnAction((eh) -> createPlayList());
        //Declaramos el TableView y recogemos la casilla seleccionada para darle una acción
        v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> {
            player.stop();
            //player.getStatus().equals(Status.PLAYING);
            selectSong();
            sliderController();

        });

        //Le damos una acción al botón Forward
        v.getForward().setOnAction((e) -> {
            player.stop();
            playNext();
            sliderController();
        });

        //Le damos una acción al botón Back
        v.getBack().setOnAction((e) -> {
            player.stop();
            playPrev();
            //v.getSlider().adjustValue(0);
            sliderController();
        });

        //Acción para el botón shuffle y cambiamos el color del boton al hacer click
        v.getShuffle().setOnAction((e) -> {
            if (v.getShuffle().getStyle().equals("button")) {
                v.getShuffle().setStyle("-fx-background-color: gray");
            } else {
                v.getShuffle().setStyle("button");
            }
            System.out.println(v.getShuffle().getStyle());
        });

        v.getRepeat().setOnAction((e) -> {
            if (v.getRepeat().getStyle().equals("button")) {
                v.getRepeat().setStyle("-fx-background-color: gray");
            } else {
                v.getRepeat().setStyle("button");
            }
        });

        v.getAddPlayList().setOnAction((eh) -> {
            createPlayList();
        });

        v.getAddPlayList().setOnAction((eh) -> {
            createPlayList();
        });
        v.getPlayListsList().getSelectionModel().selectedItemProperty().addListener((eh) -> {
            switchPlayList();
        });

        v.getDeletePlayList().setOnAction((eh) -> {
            deletePlayList();
        });
    }

    private void sliderController() {
        v.getSlider().setOnMousePressed((e) -> {
            if (player.getStatus() == Status.PLAYING) {
                player.pause();
            }
        });

        v.getSlider().setOnMouseReleased((e) -> {
            player.seek(Duration.seconds(v.getSlider().getValue()));
            if (v.getPlayPause().getStyleClass().contains("pause")) {
                player.play();
            }
        });

        //Change label every time current time changes
        player.currentTimeProperty().addListener(
                (observableValue, oldDuration, newDuration) -> {
                    v.getSlider().setValue(newDuration.toSeconds());
                    v.getCurrentTime().setText(
                            Song.formatDuration(newDuration)
                    );
                }
        );
        /*
        v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> {

            System.out.println("CLICK");
            player.stop();
            //player.getStatus().equals(Status.PLAYING);
            selectSong();
            sliderController();

        });*/
        v.getSlider().maxProperty().bind(Bindings.createDoubleBinding(
                () -> player.getTotalDuration().toSeconds(),
                player.totalDurationProperty()
        )
        );
    }

    private void playPausePlayer() {
        if (v.getPlayPause().getStyleClass().contains("play")) {
            v.getPlayPause().getStyleClass().remove("play");
            v.getPlayPause().getStyleClass().add("pause");
            v.getPlayPause().setGraphic(
                    GlyphsDude.createIcon(FontAwesomeIcon.PAUSE, "9px")
            );
            player.play();
        } else {
            v.getPlayPause().getStyleClass().remove("pause");
            v.getPlayPause().getStyleClass().add("play");
            v.getPlayPause().setGraphic(
                    GlyphsDude.createIcon(FontAwesomeIcon.PLAY, "9px")
            );
            player.pause();
        }
    }

    private void playNext() {
        //Cogemos la posicion +1 de la canción seleccionada
        //trackPosInPlayList = trackPosInPlayList + 1;

        if (v.getShuffle().getStyle().equals("button")) {
            trackPosInPlayList = ThreadLocalRandom.current().nextInt(0, songList.size() - 1 + 1);
        } else {
            trackPosInPlayList = trackPosInPlayList + 1;
        }

        //If para volver a empezar al llegar al final
        if (trackPosInPlayList > songList.size() - 1) {
            trackPosInPlayList = 0;
        }
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        //v.getAudioControls().getChildren().add(v.getMedia());
        songList.set(trackPosInPlayList, currentTrack);

        if (v.getPlayPause().getStyleClass().contains("pause")) {
            player.play();
        }
        //playPausePlayer();
    }

    private void playPrev() {
        //Cogemos la posición -1 de la canción seleccionada
        //trackPosInPlayList = trackPosInPlayList - 1;

        if (v.getShuffle().getStyle().equals("button")) {
            trackPosInPlayList = ThreadLocalRandom.current().nextInt(0, songList.size() - 1 + 1);
        } else {
            trackPosInPlayList = trackPosInPlayList - 1;
        }

        //If para volver al final al llegar a la posición -1
        if (trackPosInPlayList < 0) {
            trackPosInPlayList = songList.size() - 1;
        }
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        //v.getAudioControls().getChildren().add(v.getMedia());

        if (v.getPlayPause().getStyleClass().contains("pause")) {
            player.play();
        }
        //playPausePlayer();
    }

    private void loadCurrentTrack() {
        try {
            Media media = new Media(
                    MP3PlayerController.class.getClassLoader()
                            .getResource(currentTrack.getSongPath())
                            .toURI().toString());
            player = new MediaPlayer(media);
            v.setMedia(new MediaView(player));
        } catch (URISyntaxException e) {
            System.err.println();
        }

        player.setOnEndOfMedia(() -> {
            if (v.getRepeat().getStyle().equals("button")) {
                player.stop();
                player.play();
                sliderController();
            } else {
                playNext();
                sliderController();
            }
        });
    }

    private void createPlayList() {

        Stage popupwindow = new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Add new PlayList!");

        //MODIFICACIÓ
        TextField newPlayListTitleField;
        ListView<String> songListView;

        Label help = new Label("Use Ctrl or Shift to select multiple songs");
        Label label1 = new Label("Display a title");

        Button button1 = new Button("Cancel");
        Button btnAccept = new Button("Accept");

        //NewPlayList
        newPlayListTitleField = new TextField("");

        songListView = new ListView<>();
        songListView.autosize();

        //End NewPlayList
        button1.setOnAction(e -> popupwindow.close());

        VBox layout = new VBox(10);

        List<Song> newsonglist = new ArrayList<>();
        List<String> strings = new ArrayList<>(playList.get(0).getSongList().size());
        for (Song s : playList.get(0).getSongList()) {
            strings.add(s.getTitle());
        }

        ObservableList<String> items = FXCollections.observableArrayList(strings);
        songListView.setItems(items);
        songListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        songListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            ObservableList<String> selectedItems = songListView.getSelectionModel().getSelectedItems();
            System.out.println(selectedItems);
            newsonglist.clear();
            for (Song s : playList.get(0).getSongList()) {
                for (String t : selectedItems) {
                    if (s.getTitle().equals(t)) {
                        newsonglist.add(s);
                    }
                }

            }
        });
        layout.getChildren().addAll(label1, newPlayListTitleField, help, songListView, btnAccept, button1);

        layout.setAlignment(Pos.CENTER);

        Scene scene1 = new Scene(layout, 300, 250);
        scene1.getStylesheets().add(
                App.class.getClassLoader().getResource("css/AddPlayList.css")
                        .toExternalForm()
        );
        newPlayListTitleField.setPromptText("Playlist name");
        newPlayListTitleField.getStyleClass().add("persistent-prompt");

        btnAccept.setOnAction(e -> {
            PlayList p = new PlayList(newPlayListTitleField.getText(), newsonglist);
            v.getPlayListsList().getItems().add(p);
            playList.add(p);

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            String jsonString = gson.toJson(p);

            try {
                FileWriter file = new FileWriter(partialJSON + p.getTitle().replace(" ", "") + ".json");
                BufferedWriter out = new BufferedWriter(file);
                out.write(jsonString);
                out.close();
                file.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
            saveSongs();
            popupwindow.close();
        });

        popupwindow.setScene(scene1);

        popupwindow.showAndWait();

    }

    private void switchPlayList() {
        trackPosPlayList = v.getPlayListsList().getSelectionModel().getSelectedIndex();
        currentPlayList = playList.get(trackPosPlayList);
        v.getTrackTable().getItems().clear();
        v.getTrackTable().getItems().addAll(currentPlayList.getSongList());
        songList = currentPlayList.getSongList();

        currentTrack = songList.get(0);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        player.play();
    }

    private void deletePlayList() {
        Stage popupwindow = new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Remove a PlayList!");

        ListView<String> playListView;

        Label help = new Label("Use Ctrl or Shift to select multiple playlist");

        Button button1 = new Button("Cancel");
        Button btnAccept = new Button("Accept");

        playListView = new ListView<>();
        playListView.autosize();

        button1.setOnAction(e -> popupwindow.close());

        VBox layout = new VBox(10);

        List<PlayList> toRemove = new ArrayList<>();
        List<String> strings = new ArrayList<>(playList.get(0).getSongList().size() - 1);
        for (PlayList p : playList) {
            if (!p.getTitle().equals("All Songs")) {
                strings.add(p.getTitle());
            }
        }

        ObservableList<String> items = FXCollections.observableArrayList(strings);
        playListView.setItems(items);
        playListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        playListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            ObservableList<String> selectedItems = playListView.getSelectionModel().getSelectedItems();
            System.out.println(selectedItems);
            toRemove.clear();
            for (PlayList p : playList) {
                for (String t : selectedItems) {
                    if (p.getTitle().equals(t)) {
                        toRemove.add(p);
                    }
                }

            }
        });
        layout.getChildren().addAll(help, playListView, btnAccept, button1);

        layout.setAlignment(Pos.CENTER);

        Scene scene1 = new Scene(layout, 300, 250);
        scene1.getStylesheets().add(
                App.class.getClassLoader().getResource("css/AddPlayList.css")
                        .toExternalForm()
        );

        btnAccept.setOnAction(e -> {
            for (PlayList p : toRemove) {
                v.getPlayListsList().getItems().remove(p);
                playList.remove(p);

                File remove = new File(partialJSON + p.getTitle().replaceAll(" ", "") + ".json");
                remove.delete();

            }

            saveSongs();
            popupwindow.close();
        });

        popupwindow.setScene(scene1);

        popupwindow.showAndWait();
    }

    public void loadAllPlaylist() {
        Gson gson = new Gson();
        File directorio = new File(partialJSON);

        try {
            //Des-serialització d'un array estàtic
            for (String f : directorio.list()) {
                PlayList p = gson.fromJson(
                        new FileReader(partialJSON + f),
                        PlayList.class
                );
                if (!p.getTitle().equals("All Songs")) {

                    playList.add(p);
                    System.out.println(playList);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    private void selectSong() {
        trackPosInPlayList = v.getTrackTable().getSelectionModel().getSelectedIndex();
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();

        player.play();
        v.getAudioControls().setVisible(true);
        playPausePlayer();

    }

    public static Song loadSongs() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Song.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        File f = new File(songsXML);
        if (f.length() != 0) {
            return (Song) unmarshaller.unmarshal(f);
        } else {
            return new Song();
        }
    }

    //
    public void saveSongs() {
        AllJAXB todo = new AllJAXB();
        todo.setPlayLists(playList);
        todo.setSongs(playList.get(0).getSongList());
        try {
            JAXBContext context = JAXBContext.newInstance(AllJAXB.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(todo, System.out);

            marshaller.marshal(todo, new File(songsXML));
        } catch (JAXBException ex) {
            System.err.println("ERROR AMB EL SERIALITZADOR JAXB: " + ex.getMessage());
        }
    }

}
