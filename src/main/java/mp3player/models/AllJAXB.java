/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3player.models;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eric_
 */
@XmlRootElement(name = "MP3")
@XmlAccessorType (XmlAccessType.NONE)
public class AllJAXB {
    
    @XmlElementWrapper(name="songs")
    @XmlElement(name="song")
    List<Song> songs;
    
    @XmlElementWrapper(name="playlists")
    @XmlElement(name="playlist")
    List<PlayList> playlists;
    
    
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
    
    public void setPlayLists(List<PlayList> playlists) {
        this.playlists = playlists;
    }
}
