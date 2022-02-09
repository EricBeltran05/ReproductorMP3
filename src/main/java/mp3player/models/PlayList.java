
package mp3player.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Cristòfol-Lluís Thwaite Rivas
 */
@XmlRootElement(name = "playlist")
@XmlAccessorType (XmlAccessType.FIELD)
public class PlayList {
    
    @XmlAttribute
    private String title;
    
    private List<Song> songList;
    @XmlElement(name = "path")
    private String playlistPath; 
    
    public PlayList() {
        songList = new ArrayList<>();
    }

    public PlayList(String title, List<Song> songList) {
        this.title = title;
        this.songList = songList;
        this.playlistPath = "C:\\Users\\toniO\\OneDrive\\Documentos\\NetBeansProjects\\m06-uf1-p1-reproductormp3\\src\\main\\resources\\playlists\\"+title+".json";
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    @Override
    public String toString() {
        return title;
    }
    
}
