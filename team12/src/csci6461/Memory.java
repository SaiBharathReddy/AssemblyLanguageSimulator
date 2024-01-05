
package csci6461;

import java.util.ArrayList;


/**
 * Represents the memory of the CISC simulator, contains 4096 words.
 * Implemented using singleton design pattern.
 */
public class Memory {
	
	private static final Memory INSTANCE = new Memory();
	private static Word[] memory;
	private static Cache cache;
	private static ArrayList<Integer> addr;
	private static int currAddress=0;
	//	private constructor to prevent initialization from outside of this class
	private Memory() {
		memory = new Word[4096];
		for (int i = 0; i < 4096; i++) {
			memory[i] = new Word();
		}
		cache = new Cache();
	} 
	public static Memory getInstance() {
		return INSTANCE;
	}
	
	public Word read(int address) {
		return cache.read(address);
	}
	
	private class Cache {
		public ArrayList<Integer> address;
		public ArrayList<Word> content;
		public int length;
		public Cache() {
			address = new ArrayList<>();
			content = new ArrayList<>();
			length = 0;
		}
		
		public void add(int addr, Word cont) {
			if (length >= 16) {
				address.remove(0);
				content.remove(0);
				length--;
			}
			address.add(Integer.valueOf(addr));
			content.add(cont);
			length++;
		}
		
		public Word read(int addr) {
			System.out.println("Length:"+length);
			for (int i = 0; i < length; i++) {
				if (address.get(i).intValue() == addr) {
					// HIT(we have this address already in memory)
					content.set(i, memory[addr]);
					return content.get(i);
				}
			}
			// MISS(this address is not already present, so we will add this to memory)
			this.add(addr, memory[addr]);
			return memory[addr];
		}
		
	}
	
	public void write(Word inp, int address) {
		memory[address] = inp;
		storeAddress(address);
		
	}
	
	public void write(int inp, int address) {
		memory[address] = Util.int2Word(inp);
	}
	
	
	public void storeAddress(int address) {
		addr.add(address);
	}
	
	
	public int getAddress() {
		if(currAddress<getLength()) {
		int address=addr.get(currAddress);
		//addr.remove(addr.get(currAddress));
		System.out.print("Addr array in get:");
		for(int i=0;i<addr.size();i++) {
		System.out.print(addr.get(i)+" ");
	}
		System.out.print("\n");
		return address;}
		return 0;
		
	}
	
	
	public int getLength() {
		return addr.size();	}
	
	
	public int getFirstAddress() {
		currAddress=0;
		return addr.get(currAddress);
	}
	
	
	public void deleteAddress(int adr) {
		addr.remove(Integer.valueOf(adr));
		System.out.print("Addr array in delete:");
		for(int i=0;i<addr.size();i++) {
			System.out.print(addr.get(i)+" ");
		}
		System.out.print("\n");
	}
	public void init() {
		addr = new ArrayList<Integer>();
	}
	
	
}
