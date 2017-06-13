package edu.ncsu.csc401.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client extends Thread {
	
	private static PrintWriter pw = null;
	private static BufferedReader br = null;

	public static void main(String[] args) {
		//Try to open a connection to the server
		Socket serverSocket = null;
        //String hostName = "127.0.0.1";
        String hostName = "10.139.83.66";
        try {
            serverSocket = new Socket(hostName, 7733);
            pw = new PrintWriter(serverSocket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("cannot connect to hostname");
        } catch (IOException e) {
            System.err.println("problem with writer/reader");
        }
        
        
	    if (serverSocket != null && pw != null && br != null) {
            try {
            	pw.write("Client: Client has connected to you");
            	System.out.println("Client: Successfully connected to server");
            	listCommands();
		        String serverResponse;
		        while ((serverResponse = br.readLine()) != null) {
		            System.out.println("Server: " + serverResponse);
		        }
		        serverSocket.close();
		        pw.close();
		        br.close();
		    } catch (UnknownHostException e) {
		        System.err.println("Trying to connect to unknown host: " + e);
		    } catch (IOException e) {
		        System.err.println("IOException:  " + e);
		    }
        }
    }
	
	private static void listCommands() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Client: Choose an option");
    	System.out.println("Client: 1. ADD RFC TO SERVER");
    	System.out.println("Client: 2. LOOKUP RFC");
    	System.out.println("Client: 3. LIST ALL RFC'S AVAILABLE");
    	int choice = sc.nextInt();
    	switch(choice) {
    		case 1:
    			addRFC(sc);
    			break;
    		case 2:
    			//lookupRFC();
    			break;
    		case 3:
    			//listAllRFC();
    			break;
    		default:
    			
    	}
	}

	private void addAllFilesOnJoin() {
		//TODO
	}
	
	private static void addRFC(Scanner sc) {
		System.out.println("Client: Enter RFC");
		int rfc = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter hostname");
		String hostname = sc.nextLine();
		System.out.println("Client: Enter port");
		int port = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter Title");
		String title = sc.nextLine();
		String message = "ADD RFC " + rfc + " P2P-CI/1.0\nHost: "
				+ hostname + "\nPort: " + port + "\nTitle: " + title + "\n";
		pw.write(message);
		System.out.println("Client: You sent this message:\n" + message);
	}

	private void lookupRFC() {
		// TODO Auto-generated method stub
		
	}
	
	private void listAllRFC() {
		// TODO Auto-generated method stub
		
	}
}
