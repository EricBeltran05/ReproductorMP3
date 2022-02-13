package mp3player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mp3player.models.Song;
import mp3player.views.MP3PlayerView;
import mp3player.controllers.MP3PlayerController;
import mp3player.models.PlayList;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        MP3PlayerView view = new MP3PlayerView();
        MP3PlayerController c = new MP3PlayerController(startingSongList(), view);
        c.initController();
        var scene = new Scene(view.getRootPane(), 1280, 720);
        scene.getStylesheets().add(
                App.class.getClassLoader().getResource("css/MP3PlayerView.css")
                        .toExternalForm()
        );
        stage.setScene(scene);
        stage.setTitle("MP3 Player");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private static List<Song> startingSongList() {

        List<Song> lista = new ArrayList<>();
        Song s = new Song(
                "Hells Bells",
                "Rock",
                "AC/DC",
                "Back in Black",
                Duration.seconds(311),
                "audios/hells_bells.mp3"
        );
        Song s1 = new Song(
                "Closer",
                "Pop",
                "The Chainsmokers ft. Hailey",
                "Single",
                Duration.seconds(262),
                "audios/Closer.mp3"
        );
        Song s2 = new Song(
                "Like me better",
                "Pop",
                "Lauv",
                "How I'm feeling",
                Duration.seconds(197),
                "audios/Like_Me_Better.mp3"
        );
        Song s3 = new Song(
                "Broken Ties",
                "Hard",
                "The Whistlers",
                "Remix",
                Duration.seconds(197),
                "audios/Broken_Ties_The_Whistlers_Remix.mp3"
        );
        Song s4 = new Song(
                "Where No One Goes",
                "Folk",
                "John Powell",
                "How to Train Your Dragon 2",
                Duration.seconds(165),
                "audios/Where_No_One_Goes.mp3"
        );
        lista.add(s);
        lista.add(s1);
        lista.add(s2);
        lista.add(s3);
        //lista.add(s4);
        //Ver si hay alguna en el fichero no manual
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            PlayList p = gson.fromJson(
                    new FileReader("src\\main\\resources\\playlists\\AllSongs.json"),
                    PlayList.class
            );
            for(Song ext: p.getSongList()){
                int existe = 0;
                for(Song in: lista){
                    if(ext.getSongPath().equals(in.getSongPath())){
                        existe = 1;
                    }
                }
                if(existe == 0){
                    lista.add(ext);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return lista;
    }
}
