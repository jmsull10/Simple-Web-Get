import java.util.*;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class SWGSullivan {
	public static void main(String[] args) throws Exception {
		Calendar time = Calendar.getInstance();
		SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
		long start = System.currentTimeMillis();
		
		boolean valid = args[0].contains("http://") || args[0].contains("https://");

		String http = "http://";
			
		if(!valid) { // only not valid if http or https is not present.
			args[0] = http + args[0];
		}
		URL url = new URL(args[0]);

		try { // catches the UnknownHostException thrown by InetAddress.getByName().
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			String sURL = url.toString();
			int statusCode = connection.getResponseCode();
			
			System.out.println("--" + date.format(time.getTime()) + "-- " + sURL);
			String file = url.getFile();
			String fileName = file.substring(file.lastIndexOf('/')+1);
			System.out.printf("%14s%s%n", "=> ", fileName);
	
			String hostName = url.getHost();
			System.out.print("Resolving " + hostName + "... ");
			InetAddress ipAddress = InetAddress.getByName(url.getHost());
	
			String sIP = ipAddress.toString();
			String ip = sIP.substring(sIP.lastIndexOf('/')+1);
			System.out.print(ip + "\n");
				
			int port = url.getDefaultPort();
			System.out.print("Connecting to " + hostName + "[" + ip + "]:" + port + "... ");
			Socket clientSocket = new Socket(ip, port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				
			if(clientSocket.isConnected()) { // Checks to see if the client connected to the server.
				System.out.print("connected. \n");
			}
			System.out.print("HTTP request sent, awating response... ");
				
			if(statusCode > 299) {// Status codes over 299 will be error messages.
				if(statusCode == 404) { // Status code of 404 will return a different message.
					System.out.println("404 Not Found");
					System.out.println(date.format(time.getTime()) + " ERROR 404: Not Found.");
				} else { // default error code
					System.out.println(date.format(time.getTime()) + " Not Found");
				}
			} else { // the status code is 200 because there are no errors.
				System.out.println("200 OK");
				
				outToServer.writeBytes("GET " + port + "HTTP/1.1\r\n" +
										"Host: " + hostName + "\r\n" + 
										"Connection: close \r\n\r\n");
				DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
		
				long end = System.currentTimeMillis()-start;
				double seconds = end/1000.0;
					
				String conLength = connection.getHeaderField("Content-Length");
				String conType = connection.getHeaderField("Content-Type");
					
				if(conType.indexOf(";")>0) { // Checks for a semicolon before printing.
					System.out.println("Length: " + conLength + 
						" " + conType.substring(0, conType.indexOf(";")));
				} else {
					System.out.println("Length: " + conLength + " " + conType);
				}
					
				double kb = Integer.parseInt(conLength)/1000.0;
				double rate = kb/seconds;
					
			    System.out.println(date.format(time.getTime()) + " (" + String.format("%.2f", rate) 
			    + " KB/s) - '" + fileName + "' saved [" + conLength + "/" + conLength + "]");
			} 
		} catch (UnknownHostException e) {
			System.out.println("--" + date.format(time.getTime()) + "-- " + url.toString());
			String file = url.getFile();
			String fileName = file.substring(file.lastIndexOf('/')+1);
			System.out.printf("%14s%s%n", "=> ", fileName);
			System.out.print("Resolving " + url.getHost() + "... ");
			System.out.println("failed: Host not found. \n");
		}
	}	
}
