package com.jemchicomac.backfire.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jemchicomac.backfire.Backfire;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.fullscreen = true;
		
		//config.width = 640;
		//config.height = 360;
		
		new LwjglApplication(new Backfire(), config);
	}
}
