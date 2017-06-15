package edu.ncsu.csc401.client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	
	private static Socket serverSocket = null;
	private static PrintWriter pw = null;
	private static BufferedReader br = null;
	private static String hostName = "127.0.0.1";

	public static void main(String[] args) {
		//Try to open a connection to the server
 
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
            	addAllRFCsOnJoin();
            	int num;
				do {
            		num = listCommands();
            	} while(num != 5);
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
    	System.out.println("Client: 4. GET RFC FROM PEER");
    	System.out.println("Client: 5. TERMINATE CONNECTION");
    	int choice = sc.nextInt();
    	sc.nextLine();
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
    			downloadRFCFromPeer(sc);
    			break;
    		case 5:
    			pw.println("CLOSE " + hostName);
		        serverSocket.close();
		        pw.close();
		        br.close();
		        System.out.println("Client: Disconnected.");
		        break;
    		default:
    			System.out.println("Client: Invalid Command");  
    			listCommands();
    	}
    	return choice;
	}

	private static void addAllRFCsOnJoin() {
		//TODO
	}
	
	private static void addRFC(Scanner sc) {
		System.out.println("Client: Enter RFC");
		int rfc = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter port");
		int port = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter Title");
		String title = sc.nextLine();
		String message = "ADD RFC " + rfc + " P2P-CI/1.0\nHost: "
				+ hostName + "\nPort: " + port + "\nTitle: " + title;
		pw.println(message);
		//System.out.println("Client: You sent this message:\n" + message);
	}

	private static void lookupRFC(Scanner sc) throws IOException {
		System.out.println("Client: Enter RFC");
		int rfc = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter port");
		int port = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter Title");
		String title = sc.nextLine();
		String message = "LOOKUP RFC " + rfc + " P2P-CI/1.0\nHost: " + hostName
				+ "\nPort: " + port + "\nTitle: " + title;
		pw.println(message);
		//System.out.println("Client: You sent this message:\n" + message);
        String serverResponse;
        serverResponse = br.readLine();
        while (!serverResponse.equals("-1")) {
            System.out.println("Server: " + serverResponse);
            serverResponse = br.readLine();
        }
	}
	
	private static void listAllRFC(Scanner sc) throws IOException {
		System.out.println("Client: Enter port");
		int port = sc.nextInt();
		sc.nextLine();
		String message = "LIST ALL P2P-CI/1.0\nHost: " + hostName + "\nPort: " + port;
		pw.println(message);
		//System.out.println("Client: You sent this message:\n" + message);
        String serverResponse;
        serverResponse = br.readLine();
        while (!serverResponse.equals("-1")) {
            System.out.println("Server: " + serverResponse);
            serverResponse = br.readLine();
        }
	}
	
	//TODO: clients need to always be listening for peer connections.
	private static void downloadRFCFromPeer(Scanner sc) throws IOException {
		//1. open connection to peer at specified port
		System.out.println("Client: Enter RFC");
		int rfc = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter hostname");
		String hostname = sc.nextLine();
		System.out.println("Client: Enter port");
		int port = sc.nextInt();
		sc.nextLine();
		
		Socket clientSocket = null;
		PrintWriter pwClient = null;
		BufferedReader brClient = null;
        try {
            clientSocket = new Socket(hostname, port);
            pwClient = new PrintWriter(clientSocket.getOutputStream(), true);
            brClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            if (clientSocket != null && brClient != null) {
                System.out.println("Client: Successfully connected to peer");
            }
        } catch (UnknownHostException e) {
            System.err.println("cannot connect to hostname");
        } catch (IOException e) {
            System.err.println("problem with reader");
        }        

		//2. request rfc
		String os = System.getProperty("os.name");
		String message = "GET RFC " + rfc + " P2P-CI/1.0\nHost: " + hostname 
				+ "\nOS: " + os;
		pwClient.println(message);
		
		//3. receive file
		String serverResponse;
        //TODO: fix bug
        while ((serverResponse = br.readLine()) != null) {
            System.out.println("Server: " + serverResponse);
        	if(serverResponse.startsWith("Content-Type:")) {
        		break; //exit and start writing data to array
        	}
        }
        
        ArrayList<String> data = new ArrayList<String>();
        //TODO: fix bug
        while ((serverResponse = br.readLine()) != null) {
        	data.add(serverResponse);
        }
        
		//4. store file
        PrintWriter fileWriter = new PrintWriter(Integer.toString(rfc) + ".txt", "UTF-8");
		//TODO: we need directory structure
        for(int i = 0; i < data.size(); i++) {
			fileWriter.println(data.get(i));
		}
		
		//5. close connection
		fileWriter.close();
		clientSocket.close();
		pwClient.close();
		brClient.close();
	}
}
