
package mp3player.models;

import java.io.Serializable;
import javafx.util.Duration;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "songs")
@XmlAccessorType(XmlAccessType.NONE)
/**
 *
 * @author Cristòfol-Lluís Thwaite
 */
public class Song implements Serializable{
    
    @XmlAttribute
    private String title;
    @XmlElement(name = "genere")
    private String genere;
    @XmlAttribute
    private String artist;
    @XmlElement(name = "album")
    private String album;
    @XmlElement(name = "time")
    private Duration time;
    private String timeFormat;
    @XmlElement(name = "songPath")
    private String songPath;
    
    public Song() {
    }

    public Song(
            String title, 
            String genre, 
            String artist, 
            String album, 
            Duration time, 
            String songPath
    ) {
        this.title = title;
        this.genere = genre;
        this.artist = artist;
        this.album = album;
        this.time = time;
        timeFormat = formatDuration(time);
        this.songPath = songPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genere;
    }

    public void setGenre(String genre) {
        this.genere = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Duration getTime() {
        return time;
    }

    public void setTime(Duration time) {
        this.time = time;
    }
    
    public String getTimeFormat() {
        return timeFormat;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    @Override
    public String toString() {
        return "Song{" + "title=" + title + ", genre=" + genere + ", artist=" 
                + artist + ", album=" + album + ", time=" + time.toString()
                + ", songPath=" + songPath + '}';
    }
    
    public static String formatDuration(Duration duration) {
        double seconds = duration.toSeconds();
        int mm = (int) (seconds % 3600) / 60;
        int ss = (int) seconds % 60;
        return String.format("%02d:%02d", mm, ss);
    }
    
}
