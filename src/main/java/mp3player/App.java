package mp3player;

import java.io.File;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
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
        lista.add(s);
        lista.add(s1);
        lista.add(s2);

        return lista;
    }
}
