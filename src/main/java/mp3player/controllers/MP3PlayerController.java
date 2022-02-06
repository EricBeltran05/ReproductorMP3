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

/**
 *
 * @author Cristòfol-Lluís Thwaite
 */
public class MP3PlayerController {

    private MP3PlayerView v;
    private List<Song> songList;
    private List<PlayList> playlists;
    private Song currentTrack;
    private int trackPosInPlayList;
    private MediaPlayer player;
    private static String songsXML = "E:\\DAM 2\\Pack 3\\mp3-player-backbone\\src\\main\\resources\\configuration\\songs.xml";
    private static String partialJSON = "E:\\DAM 2\\Pack 3\\mp3-player-backbone\\src\\main\\resources\\playlists\\";
    private static File file = new File("E:\\DAM 2\\Pack 3\\mp3-player-backbone\\src\\main\\resources\\configuration\\songs.xml");

    //MODIFICACIÓ
    PlayList pl = new PlayList();

    public MP3PlayerController(List<Song> songList, MP3PlayerView v) {
        this.songList = songList;
        this.playlists = new ArrayList<>();
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
        playlists.add(p);
        AllJAXB todo = new AllJAXB();
        todo.setPlayLists(playlists);
        todo.setSongs(songList);
        saveSongs(todo);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        //Generació d'un String amb format JSON
        for (PlayList pl : playlists) {
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

        v.getPlayPause().setOnAction((e) -> playPausePlayer());

        sliderController();

        //v.getAddPlayList().setOnAction((eh) -> createPlayList());
        //Declaramos el TableView y recogemos la casilla seleccionada para darle una acción
        v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> {
            player.stop();
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

        //Botón newPlayList
        /*v.getAddPlayList().setOnAction((eh) -> {
            Stage ventanaPrincipal = (Stage) .getScene().getWindow();
            Stage newPlaylist = new Stage();
            newPlaylist.initModality(Modality.WINDOW_MODAL);
            newPlaylist.initOwner(ventanaPrincipal);

            var scene = new Scene(100, 50);
            scene.getStylesheets().add(
                    App.class.getClassLoader().getResource("css/MP3PlayerView.css")
                            .toExternalForm()
            );
            stage.setScene(scene);
            stage.setTitle("MP3 Player");
            stage.show();
        });*/
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
        trackPosInPlayList = trackPosInPlayList + 1;

        //If para volver a empezar al llegar al final
        if (trackPosInPlayList > songList.size() - 1) {
            trackPosInPlayList = 0;
        }
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        v.getAudioControls().getChildren().add(v.getMedia());
        songList.set(trackPosInPlayList, currentTrack);

        playPausePlayer();
    }

    private void playPrev() {
        //Cogemos la posición -1 de la canción seleccionada
        trackPosInPlayList = trackPosInPlayList - 1;

        //If para volver al final al llegar a la posición -1
        if (trackPosInPlayList < 0) {
            trackPosInPlayList = songList.size() - 1;
        }
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        v.getAudioControls().getChildren().add(v.getMedia());

        playPausePlayer();
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
    }

    private void createPlayList() {

        v.setPlayListTitle().setVisible(true);
        v.setPlayListTitleField().setVisible(true);

        PlayList p = new PlayList("Test PlayList", songList);
        v.getPlayListsList().getItems().add(p);
        for (Song so : songList) {
            v.getTrackTable().getItems().add(so);
        }
        trackPosInPlayList = 0;
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        v.getAudioControls().getChildren().add(v.getMedia());

    }

    private void deletePlayList() {
        v.getDeletePlayList();
    }

    /*
    public void saveSongs(Song s) {
        List<Song> songList = new ArrayList<>();

    }
     */

 /*public PlayList loadAllPlaylist(){
        //Creació del parser
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        //Generació d'un String amb format JSON
        String jsonString = gson.toJson(llista.getClients());

        try {
            FileWriter file = new FileWriter(ARCHIVO);
            BufferedWriter out = new BufferedWriter(file);
            out.write(jsonString);
            out.close();
            file.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return pl;
    }*/
    private void selectSong() {
        trackPosInPlayList = v.getTrackTable().getSelectionModel().getSelectedIndex();
        currentTrack = songList.get(trackPosInPlayList);
        v.getCurrentTitle().setText(currentTrack.getTitle());
        v.getCurrentArtist().setText(currentTrack.getArtist());
        v.getTotalTime().setText(currentTrack.getTimeFormat());
        loadCurrentTrack();
        v.getAudioControls().getChildren().add(v.getMedia());

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
    public static void saveSongs(AllJAXB s) {//Vale pues sí, probamos a ver si vale con un bucle aunque igual lo que pasa ahora es que se 
        try {
            JAXBContext context = JAXBContext.newInstance(AllJAXB.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(s, System.out);

            marshaller.marshal(s, new File(songsXML));
        } catch (JAXBException ex) {
            System.err.println("ERROR AMB EL SERIALITZADOR JAXB: " + ex.getMessage());
        }
    }

}
