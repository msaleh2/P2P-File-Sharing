package edu.ncsu.csc401.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc401.util.ServerLinkedList;

public class ServerLinkedListTest {
	ServerLinkedList<Integer> list;
	
	@Before
	public void setUp() {
		list = new ServerLinkedList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
	}

	@Test
	public void testServerLinkedList() {
		list = new ServerLinkedList<Integer>();
		assertEquals(0, list.size());
	}

	@Test
	public void testAdd() {
		assertEquals(6, list.get(0).intValue());
		assertEquals(5, list.get(1).intValue());
		assertEquals(4, list.get(2).intValue());
		assertEquals(3, list.get(3).intValue());
		assertEquals(2, list.get(4).intValue());
		assertEquals(1, list.get(5).intValue());
		assertEquals(6, list.size());
	}

	@Test
	public void testRemove() {
		list.remove(0);
		assertEquals(5, list.size());
		assertEquals(5, list.get(0).intValue());
		list.remove(2);
		assertEquals(2, list.get(2).intValue());
		assertEquals(4, list.size());
		assertEquals(1, list.get(3).intValue());
		list.remove(0);
		assertEquals(3, list.size());
		assertEquals(2, list.get(1).intValue());
	}
	
	@Test void testAddThenRemove() {
		
	}

}
