package edu.ncsu.csc401.data;

public class RFC {
	
	private int rfcNumber;
	private String title;
	private Peer peer;
	
	public RFC(int num, String tit, Peer p) {
		this.rfcNumber = num;
		this.title = tit;
		this.peer = p;
	}

	public int getRfcNumber() {
		return rfcNumber;
	}

	public void setRfcNumber(int rfcNumber) {
		this.rfcNumber = rfcNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Peer getPeer() {
		return peer;
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}
}
