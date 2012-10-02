package no.hag.android.garageuino;

import java.net.Socket;

import android.util.Log;

public class GarageUinoClient {

	public Socket client;
	private String ipAddress;
	private int port;
	
	public GarageUinoClient(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
		
		try {
			client = new Socket(ipAddress, port);
			
		} catch (Exception e) {
			
		}
	}
	
	public String getLCD() {
		sendData("password getLCD\r");
		String lcd = "";
		lcd += readData(20) + "\n";
		lcd += readData(20) + "\n";
		lcd += readData(20) + "\n";
		lcd += readData(20);
		
		return lcd;
	}
	
	public void pushButton() {
		sendData("password pushButton\r");
	}
	
	public boolean[] getLED() {
		sendData("password getLED\r");
		boolean led[] = new boolean[4];
		led[0] = readByte(1) == 1;
		led[1] = readByte(1) == 1;
		led[2] = readByte(1) == 1;
		led[3] = readByte(1) == 1;
		
		return led;
	}
	
	
	
	private void sendData(String data) {
		if (ensureConnection()) {
			try {
				client.getOutputStream().write(data.getBytes());
				
			} catch (Exception e) {}
		}
	}
	
	private String readData(int length) {
		if (ensureConnection()) {
			try {
				byte buffer[] = new byte[length];
				
				int read = 0;
				
				while ((read += client.getInputStream().read(buffer, read, buffer.length - read)) < 20) {
				}
				
				return new String(buffer);
				
			} catch (Exception e) {
				Log.e("HAG", "" + e.getMessage());
			}
		}
		
		return null;
	}
	
	private Byte readByte(int length) {
		if (ensureConnection()) {
			try {
				return (byte) client.getInputStream().read();
				
			} catch (Exception e) {
				Log.e("HAG", "" + e.getMessage());
			}
		}
		
		return null;
	}
	
	private boolean ensureConnection() {
		if (client != null && client.isConnected()) {
			return true;
			
		} else {
			Log.i("HAG", "Reconnecting...");
			client = null;
			
			try {
				client = new Socket(ipAddress, port);
				return client.isConnected();
				
			} catch (Exception e) {
				return false;
			}
		}
	}
	
}
