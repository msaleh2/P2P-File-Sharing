package edu.ncsu.csc401.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import edu.ncsu.csc401.data.Peer;
import edu.ncsu.csc401.data.RFC;

public class Server extends Thread {

	private final static int PORT_NUMBER = 7733;
	private static boolean running;
	private static ArrayList<Peer> peers;
	private static ArrayList<RFC> rfcIndex;
	private Socket cSocket;
	
	private static final int OK = 200;
	private static final int BAD_REQUEST = 400;
	private static final int NOT_FOUND = 404;
	private static final int VERSION_NOT_SUPPORTED = 505;

	public static void main(String args[]) {
		peers = new ArrayList<Peer>();
		rfcIndex = new ArrayList<RFC>();
		ServerSocket masterSocket = null;
		running = true;
		try {
			masterSocket = new ServerSocket(7733);
			while (running) {
				new Server(masterSocket.accept());
			}
		} catch (IOException e) {
			System.err.println("Problem with connection.");
			System.exit(1);
		} finally {
			try {
				masterSocket.close();
			} catch (IOException e) {
				System.err.println("Problem closing socket.");
				System.exit(1);
			}
		}
	}

	public Server(Socket cSocket) {
		running = true;
		this.cSocket = cSocket;
		this.start();
	}

	public void run() {
		System.out.println("Connection Established: " + cSocket.getInetAddress().getHostName());

		PrintWriter output = null;
		BufferedReader input = null;
		boolean connected = true;
		String lineIn = null;
		String cmd = null;
		int rfc;
		String version = null;
		String hostName = null;
		int portNumber;
		String rfcTitle = null;

		Scanner scan = null;

		try {
			output = new PrintWriter(cSocket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
			
			String init = input.readLine();
			scan = new Scanner(init);
			peers.add(new Peer(scan.next(), scan.nextInt()));
			
			while (connected) {
				if ((lineIn = input.readLine()) != null) {
					scan = new Scanner(lineIn);
					cmd = scan.next();
					if (cmd.equals("ADD")) {

						scan.next();
						rfc = scan.nextInt();
						version = scan.next();

						lineIn = input.readLine();
						scan = new Scanner(lineIn);

						scan.next();
						hostName = scan.next();

						lineIn = input.readLine();
						scan = new Scanner(lineIn);

						scan.next();
						portNumber = scan.nextInt();

						lineIn = input.readLine();
						scan = new Scanner(lineIn);

						scan.next();
						rfcTitle = scan.nextLine();

						Peer p = null;
						for(int i = 0; i < peers.size(); i++){
							if(peers.get(i).getHostName().equals(hostName) && peers.get(i).getPort() == portNumber){
								p = peers.get(i);
								break;
							}
						}
						rfcIndex.add(new RFC(rfc, rfcTitle, p));

					} else if (cmd.equals("LOOKUP")) {
						scan.next();
						rfc = scan.nextInt();
						version = scan.next();

						lineIn = input.readLine();
						scan = new Scanner(lineIn);
						scan.next();
						hostName = scan.next();

						lineIn = input.readLine();
						scan = new Scanner(lineIn);
						scan.next();
						portNumber = scan.nextInt();
						
						lineIn = input.readLine();
						scan = new Scanner(lineIn);
						scan.next();
						rfcTitle = scan.nextLine();
						
						ArrayList<RFC> search = new ArrayList<RFC>();
						for (int i = 0; i < rfcIndex.size(); i++) {
							if (rfcIndex.get(i).getRfcNumber() == rfc) {
								search.add(rfcIndex.get(i));
							}							
						}
						if(search.size() == 0) {
							output.println(version + " " + NOT_FOUND + " Not Found");
						} else if (search.size() > 0) {
							output.println(version + " " + OK + " OK");
							for(int j = 0; j < search.size(); j++) {
								output.println("RFC " + search.get(j).getRfcNumber() + " "
										+ search.get(j).getTitle() + " " 
										+ search.get(j).getPeer().getHostName() + " " 
										+ search.get(j).getPeer().getPort());
							}
						}
						System.out.println("Returned " + search.size() + " result");
						output.println("-1");
					} else if (cmd.equals("LIST")) {
						scan.next();
						version = scan.next();

						lineIn = input.readLine();
						scan = new Scanner(lineIn);
						scan.next();
						hostName = scan.next();

						lineIn = input.readLine();
						scan = new Scanner(lineIn);
						scan.next();
						portNumber = scan.nextInt();
						
						if(rfcIndex.size() == 0) {
							output.println(version + " " + NOT_FOUND + " Not Found");
						} else if (rfcIndex.size() > 0) {
							output.println(version + " " + OK + " OK");
							for(int j = 0; j < rfcIndex.size(); j++) {
								output.println("RFC " + rfcIndex.get(j).getRfcNumber() + " "
										+ rfcIndex.get(j).getTitle() + " " 
										+ rfcIndex.get(j).getPeer().getHostName() + " " 
										+ rfcIndex.get(j).getPeer().getPort());
							}
						}
						System.out.println("Returned " + rfcIndex.size() + " result");
						output.println("-1");
					} else if(cmd.equals("CLOSE")) {
						hostName = scan.next();
						connected = false;
						
					} else {
						System.err.println("Invalid Command.");
						cSocket.close();
						connected = false;
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Problem with connection.");
			try {
				output.close();
				input.close();
				cSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		for(int i = 0; i < rfcIndex.size(); i++){
			if(rfcIndex.get(i).getPeer().getHostName().equals(cSocket.getInetAddress().getHostAddress())){
				rfcIndex.remove(i);
			}
		}
		for(int i = 0; i < peers.size(); i++){
			if(peers.get(i).getHostName().equals(cSocket.getInetAddress().getHostAddress())){
				peers.remove(i);
			}
		}
		System.out.println("Client disconnected: " + hostName);
	}
}
