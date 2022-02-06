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
@XmlRootElement(name = "playlists")
@XmlAccessorType (XmlAccessType.NONE)
public class PlayListsJAXB 
{
    @XmlElement(name = "playlist")
    private List<PlayList> playlists = null;
 
    public List<PlayList> getPlayLists() {
        return playlists;
    }
 
    public void setPlayLists(List<PlayList> playlists) {
        this.playlists = playlists;
    }
}