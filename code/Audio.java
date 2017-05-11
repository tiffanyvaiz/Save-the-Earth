package gamedesign;
import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


public class Audio {
    
    AudioClip audio_clip;
    Media media;
    
    public Audio(String fn)
    {
        URL url = getClass().getResource(fn);//   
        audio_clip = new AudioClip(url.toString()); 
        
    }
   
    AudioClip getAudioClip()
    {
        return audio_clip;
    }
       
    
}
