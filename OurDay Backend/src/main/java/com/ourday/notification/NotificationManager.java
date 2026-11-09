package com.ourday.notification;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NotificationManager {
	
	private TrayIcon trayIcon;
	
	public NotificationManager() {
		if(!SystemTray.isSupported()) {
			System.out.println("System Tray notifications are not supported!");
			return;
		}
		try {
			SystemTray tray = SystemTray.getSystemTray();
			BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			
			trayIcon= new TrayIcon(image, "OurDay");
			trayIcon.setImageAutoSize(true);
			tray.add(trayIcon);
		}
		catch(AWTException e) {
			System.err.println("Unable to initialize notifications." + e.getMessage());
		}
	}
	
	public boolean showNotification(String title, String message) {
		if(trayIcon == null) {
			return false;
		}
		
		trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
		return true;
	}

}
