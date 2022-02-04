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
import mp3player.models.Song;
import mp3player.views.MP3PlayerView;
import mp3player.controllers.MP3PlayerController;

/**
 * JavaFX App
 */
public class App extends Application {

    private static String songsXML = "E:\\DAM 2\\Pack 3\\mp3-player-backbone\\src\\main\\resources\\configuration\\songs.xml";
    private static File file = new File("E:\\DAM 2\\Pack 3\\mp3-player-backbone\\src\\main\\resources\\configuration\\songs.xml");

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
        List<Song> songList = new ArrayList<>();
        Song s = new Song(
                "Hells Bells",
                "Rock",
                "AC/DC",
                "Back in Black",
                Duration.seconds(311),
                "audios/hells_bells.mp3"
        );
        songList.add(s);

        Song s1 = new Song(
                "Closer",
                "Pop",
                "The Chainsmokers ft. Hailey",
                "Single",
                Duration.seconds(262),
                "audios/Closer.mp3"
        );
        songList.add(s1);

        Song s2 = new Song(
                "Like me better",
                "Pop",
                "Lauv",
                "How I'm feeling",
                Duration.seconds(197),
                "audios/Like_Me_Better.mp3"
        );
        songList.add(s2);

        /*try {
            JAXBContext context = JAXBContext.newInstance(Song.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(s, file);
            marshaller.marshal(s1, file);
            marshaller.marshal(s2, file);

        } catch (JAXBException ex) {
            System.err.println("ERROR AMB EL SERIALITZADOR JAXB: " + ex.getMessage());
        }*/

        return songList;
    }
}
