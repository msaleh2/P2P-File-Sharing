package edu.ncsu.csc401.client;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Client extends Thread {
	
	private static Socket serverSocket = null;
	private static PrintWriter pw = null;
	private static BufferedReader br = null;
	//private static String serverHostName = "10.139.243.180";
	private static String serverHostName;
	private static String hostName = null;
	private static int lPort;
	private final String VERSION = "P2P-CI/1.0";
	private final String OS = System.getProperty("os.name");

	public static void main(String[] args) {
		//Try to open a connection to the server
 
        //String hostName = "10.139.83.66";
		serverHostName = args[0];
        try {
            serverSocket = new Socket(serverHostName, 7733);
            pw = new PrintWriter(serverSocket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("cannot connect to hostname");
        } catch (IOException e) {
            System.err.println("problem with writer/reader");
        }
        hostName = serverSocket.getLocalAddress().getHostAddress();
        
        Random rand = new Random();
        int listenPort = rand.nextInt(58000) + 2000;
        new Client(listenPort).start(); //start listening for connections
        
	    if (serverSocket != null && pw != null && br != null) {
            try {
            	System.out.println("Client: Successfully connected to server");
            	pw.println(hostName + " " + listenPort);
            	addAllRFCsOnJoin();
            	int num;
				do {
            		num = listCommands(listenPort);
            	} while(num != 5);
		    } catch (UnknownHostException e) {
		        System.err.println("Trying to connect to unknown host: " + e);
		    } catch (IOException e) {
		        System.err.println("IOException:  " + e);
		    }
        }
    }
	
	private static int listCommands(int listenPort) throws IOException {
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
    			addRFC(sc, listenPort);
    			break;
    		case 2:
    			lookupRFC(sc, listenPort);
    			break;
    		case 3:
    			listAllRFC(sc, listenPort);
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
    			listCommands(listenPort);
    	}
    	return choice;
	}

	private static void addAllRFCsOnJoin() {
		File folder = new File("rfcs");
		File[] rfcFiles = folder.listFiles();
		
	    for (int i = 0; i < rfcFiles.length; i++) {
	      if (rfcFiles[i].isFile()) {
	        String rfc = rfcFiles[i].getName();
	        
	        //TODO: where to get title?
	        String title = "";
	        
	        String message = "ADD RFC " + rfc.substring(0, rfc.length()-4) + " P2P-CI/1.0\nHost: "
					+ hostName + "\nPort: " + lPort + "\nTitle: " + title;
			System.out.println("Client: " + message);
	        pw.println(message);
	      }
	    }
	}
	
	private static void addRFC(Scanner sc, int listenPort) {
		System.out.println("Client: Enter RFC");
		int rfc = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter Title");
		String title = sc.nextLine();
		String message = "ADD RFC " + rfc + " P2P-CI/1.0\nHost: "
				+ hostName + "\nPort: " + listenPort + "\nTitle: " + title;
		pw.println(message);
		//System.out.println("Client: You sent this message:\n" + message);
	}

	private static void lookupRFC(Scanner sc, int listenPort) throws IOException {
		System.out.println("Client: Enter RFC");
		int rfc = sc.nextInt();
		sc.nextLine();
		System.out.println("Client: Enter Title");
		String title = sc.nextLine();
		String message = "LOOKUP RFC " + rfc + " P2P-CI/1.0\nHost: " + hostName
				+ "\nPort: " + listenPort + "\nTitle: " + title;
		pw.println(message);
		//System.out.println("Client: You sent this message:\n" + message);
        String serverResponse;
        serverResponse = br.readLine();
        while (!serverResponse.equals("-1")) {
            System.out.println("Server: " + serverResponse);
            serverResponse = br.readLine();
        }
	}
	
	private static void listAllRFC(Scanner sc, int listenPort) throws IOException {
		String message = "LIST ALL P2P-CI/1.0\nHost: " + hostName + "\nPort: " + listenPort;
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
		serverResponse = brClient.readLine();
		Scanner scan = new Scanner(serverResponse);
		String version = scan.next();
		PrintWriter toFile = null;
		if(!version.equals("Error:")){
			scan.nextLine();
			scan = new Scanner(brClient.readLine());
			String date = scan.nextLine();
			scan = new Scanner(brClient.readLine());
			String serverOs = scan.nextLine();
			scan = new Scanner(brClient.readLine());
			String lastMod = scan.nextLine();
			scan = new Scanner(brClient.readLine());
			String contentLength = scan.nextLine();
			scan = new Scanner(brClient.readLine());
			String contentType = scan.nextLine();
			
			toFile = new PrintWriter("rfcs/" + rfc + ".txt", "UTF-8");
			while(!(serverResponse = brClient.readLine()).equals("-1")){
				System.out.println(serverResponse);
				toFile.println(serverResponse);
			}
			
			String addNewRfc = "ADD RFC " + rfc + " P2P-CI/1.0\nHost: "
					+ hostName + "\nPort: " + lPort + "\nTitle: " + "";
			pw.println(message);
			
		} else {
			System.err.println(serverResponse);
		}
		
		
		//5. close connection
		toFile.close();
		clientSocket.close();
		pwClient.close();
		brClient.close();
	}
	
	public Client(int portNumber) {
		lPort = portNumber;
	}
	
	//listens for connections from peers
	public void run() {
		boolean listening = true;
		try {
			ServerSocket listener = new ServerSocket(lPort);
			Socket connection = null;
			
			while(listening){
				connection = listener.accept();
				PrintWriter output = new PrintWriter(connection.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				System.out.println("Peer connected: " + listener.getInetAddress().getHostAddress());
				String lineIn = input.readLine();
				Scanner scan = new Scanner(lineIn);
				if(scan.next().equals("GET")){
					scan.next();
					int rfc = scan.nextInt();
					String cVersion = scan.nextLine();
					lineIn = input.readLine();
					scan = new Scanner(lineIn);
					scan.next();
					String destHost = scan.nextLine();
					lineIn = input.readLine();
					scan = new Scanner(lineIn);
					scan.next();
					String os = scan.nextLine();
					
					boolean fileFound = false;
					File fileOut = null;
					File folder = new File("rfcs");
					File[] rfcFiles = folder.listFiles();
					System.out.println(rfcFiles.length + " files found.");
				    for (int i = 0; i < rfcFiles.length; i++) {
				      if (rfcFiles[i].isFile()) {
				        String fileName = rfcFiles[i].getName();
				        if(fileName.equals(rfc + ".txt")) {
				        	fileOut = rfcFiles[i];
				        	fileFound = true;
				        	break;
				        }
				      }
				    }
				    
				    if(!fileFound) {
				    	System.err.println("Error 404: Requested file not found.");
				    }
					
					String message;
					/**
					if(cVersion.equals(VERSION)) {
						
					} else {
						System.err.println("Error 505: P2P-CI Version Not Supported ");
						break;
					}
					*/
					
					
					message = VERSION + " 200 OK\n";
					message += "Date: " + new Date().toString() + "\n";
					message += "OS: " + OS + "\n";
					message += "Last Modified: " + new Date(fileOut.lastModified()).toString() + "\n";
					message += "Content Length: " + fileOut.length() + "\n";
					message += "Content Type: text/text\n";
					scan = new Scanner(fileOut);
					StringBuilder sb = new StringBuilder();
					sb.append(message);
					
					while(scan.hasNextLine()){
						sb.append(scan.nextLine() + "\n");
					}
					
					output.println(sb.toString());
					System.out.println(sb.toString());
					output.println("-1");
					scan.close();
				} else {
					System.err.println("Error 400: Invalid request");
					listening = false;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
