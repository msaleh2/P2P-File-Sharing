package edu.ncsu.csc401.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client extends Thread {
	
	private static Socket serverSocket = null;
	private static PrintWriter pw = null;
	private static BufferedReader br = null;

	public static void main(String[] args) {
		//Try to open a connection to the server
        String hostName = "127.0.0.1";
        //String hostName = "10.139.83.66";
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
            	System.out.println("Client: Successfully connected to server");
            	int num;
				do {
            		num = listCommands();
            	} while(num != 4);
		    } catch (UnknownHostException e) {
		        System.err.println("Trying to connect to unknown host: " + e);
		    } catch (IOException e) {
		        System.err.println("IOException:  " + e);
		    }
        }
    }
	
	private static int listCommands() throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Client: Choose an option");
    	System.out.println("Client: 1. ADD RFC TO SERVER");
    	System.out.println("Client: 2. LOOKUP RFC");
    	System.out.println("Client: 3. LIST ALL RFC'S AVAILABLE");
    	System.out.println("Client: 4. TERMINATE CONNECTION");
    	int choice = sc.nextInt();
    	switch(choice) {
    		case 1:
    			addRFC(sc);
    			break;
    		case 2:
    			lookupRFC(sc);
    			break;
    		case 3:
    			listAllRFC(sc);
    			break;
    		case 4:
		        serverSocket.close();
		        pw.close();
		        br.close();
    		default:
    			System.out.println("Client: Invalid Command");  
    			listCommands();
    	}
    	return choice;
	}

	private void addAllRFCsOnJoin() {
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
				+ hostname + "\nPort: " + port + "\nTitle: " + title;
		pw.println(message);
		System.out.println("Client: You sent this message:\n" + message);
	}

	private static void lookupRFC(Scanner sc) throws IOException {
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
		String message = "LOOKUP RFC " + rfc + " P2P-CI/1.0\nHost: " + hostname
				+ "\nPort: " + port + "\nTitle: " + title;
		pw.println(message);
		System.out.println("Client: You sent this message:\n" + message);
        String serverResponse;
        while ((serverResponse = br.readLine()) != null) {
            System.out.println("Server: " + serverResponse);
        }
	}
	
	private static void listAllRFC(Scanner sc) throws IOException {
		System.out.println("Client: Enter hostname");
		String hostname = sc.nextLine();
		System.out.println("Client: Enter port");
		int port = sc.nextInt();
		sc.nextLine();
		String message = "LIST ALL P2P-CI/1.0\nHost: " + hostname + "\nPort: " + port;
		pw.println(message);
		System.out.println("Client: You sent this message:\n" + message);
        String serverResponse;
        while ((serverResponse = br.readLine()) != null) {
            System.out.println("Server: " + serverResponse);
        }
	}
}
