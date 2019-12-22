package SoundStuff;
import sun.audio.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class Sound 
{
public static void main(String[] args)
{
	
	//creates button on a frame to press for music
    JFrame frame = new JFrame();
    frame.setSize(500,500);
    frame.setLocationRelativeTo(null);
    JButton button = new JButton("Menu Music");
    //JButton button1 = new JButton("HitBlock");
    //JButton button2 = new JButton("BreakBlock");
    frame.add(button);
    //frame.add(button1);
    //frame.add(button2);
    button.addActionListener(new AL());
    //button1.addActionListener(new AL());
    //button2.addActionListener(new AL());
    frame.setVisible(true);
}
    public static class AL implements ActionListener{
    	//watches for button press
        public final void actionPerformed(ActionEvent e){
            music();
    }
}

    public static void music() 
    {       
    	//Initializes all sound players/streamers/and data managers
        AudioPlayer AP = AudioPlayer.player;
        AudioStream AS;
        AudioData AD;
        // loop for continuous audio
        ContinuousAudioDataStream loop = null;

        try
        {
        	//tries to play selected music
            InputStream test = new FileInputStream("menu.wav");
            AS = new AudioStream(test);
            AudioPlayer.player.start(AS);
            AD = AS.getData();
            loop = new ContinuousAudioDataStream(AD);

        }
        catch(FileNotFoundException e){
        	//if music file isnt found
            System.out.print(e.toString());
        }
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
        //activates loop
        AP.start(loop);
    }


}