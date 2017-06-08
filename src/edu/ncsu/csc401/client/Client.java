package edu.ncsu.csc401.client;

import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) {
		//Try to open a connection to the server
        Socket serverSocket = null;
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
        	//ncsu-nat1-8.ncstate.net (7733)
        	//152.7.224.8 (5000)
        	//10.139.68.246
            serverSocket = new Socket("", 5000);
            bw = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }

        //Check if connection has been successfully opened
	    if (serverSocket != null && bw != null && br != null) {
            try {
            	bw.write("client has connected to you");
				// keep on reading from/to the socket till we receive the "Ok" from SMTP,
				// once we received that then we want to break.
		        String responseLine;
		        while ((responseLine = br.readLine()) != null) {
		            System.out.println("Server: " + responseLine);
		            if (responseLine.indexOf("Ok") != -1) {
		              break;
		            }
		        }
		        serverSocket.close();
		        bw.close();
		        br.close();
		    } catch (UnknownHostException e) {
		        System.err.println("Trying to connect to unknown host: " + e);
		    } catch (IOException e) {
		        System.err.println("IOException:  " + e);
		    }
        }
    }
}
