package csci6461;

import java.io.File;
import java.util.BitSet;
import java.util.Scanner;

/**
 * This class has methods that perform all operations 
 * like Load, Store, Fetch, Decode and moves data between registers and memory.
 */

public class Simulator {

	private static final Simulator INSTANCE = new Simulator();
	
	private Register PC;
	private Register IR;
	private Register MAR;
	private Register MBR;
	private Register MFR;
	private Register CC;
	
	private Register R0;
	private Register R1;
	private Register R2;
	private Register R3;

	private Register X1;
	private Register X2;
	private Register X3;

	private byte opcode;

	private byte ix;
	private byte r;
	private byte i;
	private byte addr;


	private File f;
	private int lines;
	private Scanner s;
	private static Memory memory = Memory.getInstance();
	private static int addrLength;
	
	private Simulator() {
		// initialize registers
		PC = new Register(12);
		IR = new Register(16);
		MAR = new Register(12);
		MBR = new Register(16);
		MFR = new Register(4);
		CC= new Register(4);
		
		R0 = new Register(16);
		R1 = new Register(16);
		R2 = new Register(16);
		R3 = new Register(16);

		X1 = new Register(16);
		X2 = new Register(16);
		X3 = new Register(16);

		lines = 0;

	}

	public static Simulator getInstance() {
		return INSTANCE;
	}

	

	BitSet Str2BitSet(String s) {
		assert(s.length() == 16);
		BitSet bs = new BitSet();
		for (int i = 0; i < 16; i++) {
			if (s.charAt(i) == '1') bs.set(15 - i);
		}
		return bs;
	}
	/**
	 * Method to set all the UI components/Registers with default value '0'
	 */
	
	public void initializeRegisters() {
		System.out.println("Inside Initialize");
		setRegister(R0, 0);
		setRegister(R1, 0);
		setRegister(R2, 0);
		setRegister(R3, 0);
		setRegister(X1, 0);
		setRegister(X2, 0);
		setRegister(X3, 0);
		setRegister(MAR, 0);
		setRegister(MBR, 0);
		setRegister(MFR, 0);
		setRegister(IR, 0);
		setRegister(CC, 0);
		//setRegister(PC, 0);
	}
	
	/**
	 * Method to initialize the Simulator
	 */
	public void init(String path) {
		//test();
		lines = 0;
		loadFile(path);
		try {
			 s= new Scanner(f);
			while (s.hasNextLine()) {
				String s1 = s.nextLine();
				String[] sa = s1.split(" ");
				// setting the memory
				int addr = Integer.parseInt(sa[0].trim(), 16);
				System.out.println(lines+"***"+addr);
				Word content = Util.int2Word(Integer.parseInt(sa[1].trim(), 16));
				memory.write(content, addr);
				//lines++;
			}
			addrLength=memory.getLength();
			if(lines==0) {
				
				setRegister(PC, Util.int2BitSet(memory.getAddress()));
				System.out.println("PC:"+PC);
				lines++;
				}
			s.close();
		} catch (Exception ex) {
			System.out.println("Exception occured in input file" + ex);
		}
		initializeRegisters();
	}
	
	/**
	 * Method to run instructions single step at a time. 
	 * It executes one instruction at a time. PC points to the next instruction to be executed
	 */
	public void singleStep() {
		System.out.println(addrLength);
		if(addrLength>0) {
			System.out.println("LINES:"+lines);
		// fetch instruction
		loadInstruction();
		// ir decode
		int ir = Util.bitSet2Int(IR);
		irDecode(ir);
		// operation
		System.out.println("PC:"+PC+"\n"+"MAR:"+MAR+"\n"+"MBR:"+MBR+"\n"+"IR:"+IR);
		getInstance().operation();
		if(addrLength>1) {
			System.out.println("currLength"+addrLength);
	setRegister(PC,Util.int2BitSet(memory.getAddress()));
			}
		}
		
		

	}

/* Function to setup a file pointer to the input file
 * 
 */
	public void loadFile(String path) 
	{
		f = new File(path);
	}

/**
 * loads instruction at the given address
*/
	public void loadInstruction() {

		setRegister(MAR, PC);
		//System.out.println("MAR:"+MAR);
		setRegister(MBR, memory.read(Util.bitSet2Int(MAR)));

		setRegister(IR, MBR);
		//memory.deleteAddress(Util.bitSet2Int(MAR));
		addrLength--;

	}
	
	
	/**
	 * Decodes the given instruction based on opcode
	 */	
	public void irDecode(int ir) {
		// constructing ir as string
		String ir_binary = Integer.toBinaryString(ir);
		System.out.println("IR Binary:"+ir_binary);
		int zeros = 16 - ir_binary.length();
		System.out.println(zeros);
		for (int i = 0; i < zeros; i++) {
			ir_binary = "0" + ir_binary;
		}
		opcode = (byte) Integer.parseInt(ir_binary.substring(0, 6), 2);
		System.out.println("OPCODE:"+opcode);

		if ((opcode >= 1 && opcode <= 7) || opcode == 41 || opcode == 42 ) {
			// LD and STR operations
			r = (byte) Integer.parseInt(ir_binary.substring(6, 8), 2);
			ix = (byte) Integer.parseInt(ir_binary.substring(8, 10), 2);
			i = (byte) Integer.parseInt(ir_binary.substring(10, 11), 2);
			addr = (byte) Integer.parseInt(ir_binary.substring(11, 16), 2);}
		System.out.println(r+""+Integer.parseInt(ir_binary.substring(11, 16), 2));

	}
	
	/**
	 * Calculate the effective address from given indirect(i), indexregister(ix), Address
	 */
	public int calculateEA(byte i, byte ix, byte address) {
		int ea = 0; // return value
		// no indirect
		if (i == 0) {
			if (ix == 0) {
				ea = address;
				return ea;
			} else if (ix <= 3 && ix >= 1) {
				ea = Util.bitSet2Int(getIXR(ix)) + address;
				return ea;
			}
		}
		// with indirect
		if (i == 1) {
			if (ix == 0) {
				ea = Util.bitSet2Int(memory.read(address));
				return ea;
			} else if (ix <= 3 && ix >= 1) {
				// variable for c(IX) + c(Address Field)
				int tmpAddr = Util.bitSet2Int(getIXR(ix)) + address;
				// fetch content at given address
				ea = Util.bitSet2Int(memory.read(tmpAddr));
				return ea;
			}
		}

		return ea;
	}
	/**
	 * Method to map register string to register
	 */
	private Register regStr2Name(String r) {
		if (r == "PC") {
			return PC;
		}
		if (r == "MAR") {
			return MAR;
		}
		if (r == "MBR") {
			return MBR;
		}
		if (r == "IR") {
			return IR;
		}
		if (r=="CC") {
			return CC;
		}
		if (r == "R0") {
			return R0;
		}
		if (r == "R1") {
			return R1;
		}
		if (r == "R2") {
			return R2;
		}
		if (r == "R3") {
			return R3;
		}
		if (r == "X1") {
			return X1;
		}
		if (r == "X2") {
			return X2;
		}
		if (r == "X3") {
			return X3;
		}
		return null;

	}
	
	/**
	 * Method to map Register name to its string
	 */
	private String regName2Str(Register r) {
		if (r == PC)
			return "PC";
		if (r == MAR)
			return "MAR";
		if (r == MBR)
			return "MBR";
		if (r == IR)
			return "IR";
		if (r==CC)
			return "CC";
		if (r == R0)
			return "R0";
		if (r == R1)
			return "R1";
		if (r == R2)
			return "R2";
		if (r == R3)
			return "R3";
		if (r == X1)
			return "X1";
		if (r == X2)
			return "X2";
		if (r == X3)
			return "X3";
		return null;
	}
	

	
	/**
	 * Method to set register with content
	 */
	public void setRegister(Register r, int content) {
		//System.out.println("UI update");
		BitSet w = Util.int2BitSet(content);
		Util.bitSetDeepCopy(w, 16, r, r.getSize());
		MainFrame.updateRegUI(regName2Str(r), r, r.getSize());

	}
	
	/**
	 * Method to set register with bitset
	 */
	public void setRegister(Register r, BitSet src) {
		System.out.println(regName2Str(r));
		int srcData = Util.bitSet2Int(src);
		setRegister(r, srcData);

	}
	
	public void setRegisterSigned(Register r, int content) {
		BitSet w = Util.int2BitSetSigned(content);
		System.out.println("MBR value"+w+" "+content);
		Util.bitSetDeepCopy(w, 16, r, r.getSize());
		MainFrame.updateRegUI(regName2Str(r), r, r.getSize());
	}
	/**
	 * Method to get General Purpose Register from register number
	 */
	public Register getGPR(int r) {
		switch (r) {
		case 0:
			return R0;
		case 1:
			return R1;
		case 2:
			return R2;
		case 3:
			return R3;
		}
		return R0;
	}
	
	/**
	 * Method to get Index Register from index register number
	 */
	public Register getIXR(int ix) {
		switch (ix) {
		case 1:
			return X1;
		case 2:
			return X2;
		case 3:
			return X3;
		}
		return X1;
	}
	/**
	 * Method to load data 
	 * loads Memory Address Register and sets Memory buffer register
	 */
	public void load() {
		int dataAddr = Util.bitSet2Int(MAR);
		int data = Util.bitSet2Int(memory.read(dataAddr));
		setRegister(MBR, data);
	}

	/**
	 * Method to Store Data
	 * 
	 * Reads Memory Buffer register and writes to address in memory address register
	 */
	public void store() {
		int dataAddr = Util.bitSet2Int(MAR);
		Word data = Util.int2Word(Util.bitSet2Int(MBR));
		memory.write(data, dataAddr);
	}

	/**
	 * Method to load Register with input register number with input value
	 */
	public void loadRegisterFromInput(String regStr, String input) {
		int value = Integer.parseInt(input, 2); // opcode||R||IX|I|Address
		setRegister(regStr2Name(regStr), value);
	}

	/**
	 * Method to implement operations
	 * Switches to various instructions using opcode and performs appropriate action
	 */
	public int operation() {
		int ea;

		switch (opcode) {
		
		// Load Register From Memory
		case OpCodes.LDR:
			ea = calculateEA(i, ix, addr);
			setRegister(MAR, ea);
			memory.deleteAddress(Util.bitSet2Int(MAR));
			int dataAddr = Util.bitSet2Int(MAR);
			int data = Util.bitSet2IntSigned(memory.read(dataAddr));
			setRegisterSigned(MBR, data);
			setRegister(getGPR(r), MBR);
			//setRegister(PC, memory.getAddress());
			System.out.println("LDR R" + r + ", @$" + dataAddr);
			break;
			
		// Store Register to memory
		case OpCodes.STR:
			System.out.println("STR");
			ea = calculateEA(i, ix, addr);
			setRegister(MAR, ea);
			memory.deleteAddress(Util.bitSet2Int(MAR));
			setRegisterSigned(MBR, Util.bitSet2IntSigned(getGPR(r)));
			memory.write(Util.bitSet2IntSigned(MBR), Util.bitSet2Int(MAR));
			//setRegister(PC, memory.getAddress());
			System.out.println(Util.bitSet2IntSigned(MBR) + " @ $" + Util.bitSet2Int(MAR));
			break;
		
		// Load Register with address
		case OpCodes.LDA:
			System.out.println("LDA");
			ea = calculateEA(i, ix, addr);
			setRegister(MAR, ea);
			memory.deleteAddress(Util.bitSet2Int(MAR));
			setRegister(MBR, ea);
			setRegister(getGPR(r), MBR);
			//setRegister(PC, memory.getAddress());
			break;
		
		// Load index register form memory
		case OpCodes.LDX:
			System.out.println("LDX");

			ea = calculateEA((byte) 0, (byte) 0, addr);
			setRegister(MAR, ea);
			memory.deleteAddress(Util.bitSet2Int(MAR));
			int dataAddr_1 = Util.bitSet2Int(MAR);
			int data_1 = Util.bitSet2Int(memory.read(dataAddr_1));
			setRegister(MBR, data_1);
			setRegister(getIXR(ix), MBR);
			System.out.println(Util.bitSet2Int(getIXR(ix)) + " ea:" + ea + " dataAddr:" + dataAddr_1);
			//setRegister(PC, memory.getAddress());
			break;
		
		// Store Index Register to memory
		case OpCodes.STX:
			System.out.println("STX");
			ea = calculateEA((byte) 0, ix, addr);
			setRegister(MAR, ea);
			memory.deleteAddress(Util.bitSet2Int(MAR));
			setRegister(MBR, getIXR(ix));
			memory.write(Util.bitSet2Int(MBR), Util.bitSet2Int(MAR));
			//setRegister(PC, memory.getAddress());
			break;
		

		case OpCodes.HLT:
			System.out.println("HLT");
			return 1;
		}

		return 0;

	}

}
