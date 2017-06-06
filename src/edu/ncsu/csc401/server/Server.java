package edu.ncsu.csc401.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import edu.ncsu.csc401.data.Peer;
import edu.ncsu.csc401.data.RFC;

public class Server {

	private final static int PORT_NUMBER = 7733;
	
	private  boolean running;
	private ArrayList<Peer> peers;
	private ArrayList<RFC> rcpIndex;
	
	public Server() {
		running = true;
		peers = new ArrayList<Peer>();
		rcpIndex = new ArrayList<RFC>();
	}
	
	public void startServer() {
		ServerSocket masterSocket = null;
		try {
			masterSocket = new ServerSocket(PORT_NUMBER);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		Socket connSocket = null;
		while(running) {
			
			connSocket = null;
			try {
				connSocket = masterSocket.accept();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		
		new Thread(new P2SConnection(connSocket)).start();
	}

	public static void main(String args[]) {

		Server server = new Server();
		server.startServer();
		
		System.exit(0);
	}
	
	public class P2SConnection implements Runnable {
		
		Socket endpoint = null;
		
		public P2SConnection(Socket endpoint) {
			this.endpoint = endpoint;
		}
		
		public void run() {
			
			BufferedWriter output = null;
			BufferedReader input = null;
			try {
				output = new BufferedWriter(new OutputStreamWriter(endpoint.getOutputStream()));
				input = new BufferedReader(new InputStreamReader(endpoint.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				output.write("Connection Established.");
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
}
