package csci6461;

import java.util.BitSet;


public class Register extends BitSet {
	private static final long serialVersionUID = 1L;
	private int size;
	public int getSize() {
		return size;
	}
	public Register(int size) {
		super(size);
		this.size = size;
	}
}
