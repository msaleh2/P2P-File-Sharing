package edu.ncsu.csc401.util;

public class ServerLinkedList<T> {
	
	private Node<T> head;
	private int size;
	
	public ServerLinkedList() {
		head = null;
		size = 0;
	}
	
	public void add(T data) {
		Node temp = new Node(data, head);
		head = temp;
		size++;
	}
	
	public int size() {
		return size;
	}
	
	public T get(int index) {
		int count = 0;
		if(head == null) {
			return null;
		} else if(index >= size){
			throw new IndexOutOfBoundsException();
		} else if(index == 0) {
			return head.data;
		} else {
			Node current = head;
			while(count < index) {
				current = current.next;
				count++;
			}
			return (T) current.data;
		}
	}
	
	public void remove(int index) {
		int count = 0;
		if(index >= size) {
			throw new IndexOutOfBoundsException();
		} else if(index == 0) {
			head = head.next;
			size--;
		} else {
			Node current = head;
			while(count != index - 1){
				current = current.next;
				count++;
			}
			current.next = current.next.next;
			size--;
		}
	}

	public class Node<T> {
		public T data;
		public Node<T> next;
		
		public Node(T data) {
			this.data = data;
			Node next = null;
		}
		
		public Node(T data, Node next) {
			this.data = data;
			this.next = next;
		}
	}
}
