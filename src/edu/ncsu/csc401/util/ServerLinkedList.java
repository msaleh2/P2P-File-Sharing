package edu.ncsu.csc401.util;

public class ServerLinkedList<T> {

	public class ServerListNode<T> {
		private T data;
		private ServerLinkedList<T> next;
		
		public ServerListNode(T data) {
			this.data = data;
			ServerListNode next = null;
		}
		
		public void next(ServerLinkedList<T> next) {
			this.next = next;
		}
		
		public T getData() {
			return data;
		}
		
		public ServerListNode<T> getNext(){
			return next;
		}
		
	}
}
