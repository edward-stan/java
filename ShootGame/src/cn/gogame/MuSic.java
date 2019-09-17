package cn.gogame;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.swing.JFrame;

class MuSic extends JFrame{
	File file;
	URI uri;
	URL url;
	MuSic() {
		try {
			file=new File("E:/projects/java/课程资料/eclipse/ShootGame/src/music/SWIN - 只因你太美.wav");
			uri=file.toURI();
			url=file.toURL();
			AudioClip aau;
			aau=Applet.newAudioClip(url);
			aau.play();
			aau.loop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
