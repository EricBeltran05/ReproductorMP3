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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import mp3player.models.PlayList;
import mp3player.models.Song;
import mp3player.views.MP3PlayerView;

/**
 *
 * @author Cristòfol-Lluís Thwaite
 */
public class MP3PlayerController {

    private MP3PlayerView v;
    private List<Song> songList;
    private Song currentTrack;
    private int trackPosInPlayList;
    private MediaPlayer player;

    //MODIFICACIÓ
    PlayList pl = new PlayList();

    public MP3PlayerController(List<Song> songList, MP3PlayerView v) {
        this.songList = songList;
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

        //createPlayList();
    }

    public void initController() {
        
        v.getPlayPause().setOnAction((e) -> playPausePlayer());
        

        v.getSlider().maxProperty().bind(Bindings.createDoubleBinding(
                () -> player.getTotalDuration().toSeconds(),
                player.totalDurationProperty()
        )
        );

        //Change label every time current time changes
        player.currentTimeProperty().addListener(
                (observableValue, oldDuration, newDuration) -> {
                    v.getSlider().setValue(newDuration.toSeconds());
                    v.getCurrentTime().setText(
                            Song.formatDuration(newDuration)
                    );
                }
        );
        
        sliderController();

        //v.getAddPlayList().setOnAction((eh) -> createPlayList());
        //Declaramos el TableView y recogemos la casilla seleccionada para darle una acción
        v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> player.stop());
        v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> selectSong());
        v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> sliderController());
        
        //Le damos una acción al botón Forward
        v.getForward().setOnAction((e) -> {
                v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> player.stop());
                playNext();
                sliderController();
        });
        
        //Le damos una acción al botón Back
        v.getBack().setOnAction((e) -> {
                v.getTrackTable().getSelectionModel().selectedItemProperty().addListener((eh) -> player.stop());
                playPrev();
                sliderController();
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
    
    private void playNext(){
        //Cogemos la posicion +1 de la canción seleccionada
        trackPosInPlayList = trackPosInPlayList + 1;
        
        //If para volver a empezar al llegar al final
        if(trackPosInPlayList > songList.size() - 1){
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
    
    private void playPrev(){
        //Cogemos la posición -1 de la canción seleccionada
        trackPosInPlayList = trackPosInPlayList - 1;
        
        //If para volver al final al llegar a la posición -1
        if(trackPosInPlayList < 0){
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

    public void saveSongs(Song s) {
        List<Song> songList = new ArrayList<>();

    }

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
}
