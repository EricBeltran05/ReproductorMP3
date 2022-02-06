/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3player.models;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eric_
 */
@XmlRootElement(name = "songs")
@XmlAccessorType (XmlAccessType.NONE)
public class SongsListJAXB 
{
    @XmlElement(name = "song")
    private List<Song> songs = null;
 
    public List<Song> getSongs() {
        return songs;
    }
 
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}