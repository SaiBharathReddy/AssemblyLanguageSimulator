package csci6461;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class has methods that perform all operations 
 * like Load, Store, Fetch, Decode and moves data between registers and memory.
 */

public class Simulator {

	private static final Simulator INSTANCE = new Simulator();
	private static String end="0000";
	private static  Map<String,String>endMap= new HashMap();
	private static ArrayList<String> endAccess=new ArrayList<String>();	
	private static int lastAddr,prevAddr;
	private static boolean hlt=false;
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
	private byte ry;
	private byte al;
	private byte lr;
	private byte count;
	private byte dev_id;
	private byte trap_code;


	private File f;
	private File original;
	private File inputFile;
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
		setRegister(PC, 0);
		hlt=false;
	}
	
	/**
	 * Method to initialize the Simulator
	 */
	public void init(String path) {
		//test();
		lines = 0;
		loadFile(path);
		initializeRegisters();
		memory.init();
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
				prevAddr=lastAddr;
				lastAddr=addr;
				//lines++;
			}
			addrLength=memory.getLength();
			if(lines==0) {
				System.out.println("*******First Address:******"+Util.int2BitSet(memory.getAddress()));
				setRegister(PC, Util.int2BitSet(memory.getAddress()));
				System.out.println("PC:"+PC);
				lines++;
				}
			s.close();
		} catch (Exception ex) {
			System.out.println("Exception occured in input file" + ex);
		}
//		for(int i:endAccess) {
//			memory.write(Util.int2Word(Integer.parseInt(end)), i);
//		}
		
	}
	
	/**
	 * Method to run instructions single step at a time. 
	 * It executes one instruction at a time. PC points to the next instruction to be executed
	 */
	public void singleStep() {
		System.out.println("AddrLength:"+addrLength);
		//if(addrLength>0) {
			System.out.println("LINES:"+lines);
		// fetch instruction
		loadInstruction();
		// ir decode
		int ir = Util.bitSet2Int(IR);
		System.out.println("IR:"+ir+" "+"END:"+Integer.parseInt(end));
		System.out.println("LastAdd:"+prevAddr+" "+"PC:"+(Util.bitSet2Int(PC)-1));
		irDecode(ir);
		// operation
		System.out.println("PC:"+PC+"\n"+"MAR:"+MAR+"\n"+"MBR:"+MBR+"\n"+"IR:"+IR);
		getInstance().operation();
		if(ir==Integer.parseInt(end)||prevAddr==Util.bitSet2Int(PC)-1) {
			hlt=true;
			ir=0;
			MainFrame.updateLabel();
		}
//		if(addrLength>1) {
//			System.out.println("currLength"+addrLength);
//	setRegister(PC,Util.int2BitSet(memory.getAddress()));
//			}
		//}
		
		

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
		String[] bin= {"000","001","010","011","100","101","110","111"};
		for (int i = 0; i < zeros; i++) {
			ir_binary = "0" + ir_binary;
		}
		String op=Arrays.asList(bin).indexOf(ir_binary.substring(0, 3))+""+Arrays.asList(bin).indexOf(ir_binary.substring(3, 6));
		opcode = (byte) Integer.parseInt(op);
		System.out.println("OPCODE:"+opcode);

		if ((opcode >= 1 && opcode <= 7) || opcode == 41 || opcode == 42 || (opcode >= 10 && opcode <= 17)
				|| (opcode >= 33 && opcode <= 37) || opcode == 50 || opcode == 51) {
			// LD and STR, add and sub, floating point and vector operations
			r = (byte) Integer.parseInt(ir_binary.substring(6, 8), 2);
			ix = (byte) Integer.parseInt(ir_binary.substring(8, 10), 2);
			i = (byte) Integer.parseInt(ir_binary.substring(10, 11), 2);
			addr = (byte) Integer.parseInt(ir_binary.substring(11, 16), 2);

		} else if (opcode >= 20 && opcode <= 25) {
			// MLT and DIV
			r = (byte) Integer.parseInt(ir_binary.substring(6, 8), 2);
			ry = (byte) Integer.parseInt(ir_binary.substring(8, 10), 2);
		} else if (opcode == 30) {
			// trap
			trap_code = (byte) Integer.parseInt(ir_binary.substring(12, 16), 2);
		} else if (opcode == 31 || opcode == 32) {
			// shift and rotate
			r = (byte) Integer.parseInt(ir_binary.substring(6, 8), 2);
			al = (byte) Integer.parseInt(ir_binary.substring(8, 9), 2);
			lr = (byte) Integer.parseInt(ir_binary.substring(9, 10), 2);
			count = (byte) Integer.parseInt(ir_binary.substring(12, 16), 2);
		}
		else if (opcode >= 61 && opcode <= 63) {
			// IN and OUT
			//System.out.println("************************IN********************8");
			r = (byte) Integer.parseInt(ir_binary.substring(6, 8), 2);
			dev_id = (byte) Integer.parseInt(ir_binary.substring(11, 16), 2);
		}
		System.out.println(r+" "+Integer.parseInt(ir_binary.substring(11, 16), 2));

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
	 * Method to set overflow, underflow, divzero and equalornot flags
	 */
	public void setCC(int i, boolean bit) {
		int bitIndex = 3 - i;
		// OVERFLOW = 0
		// UNDERFLOW = 1
		// DIVZERO = 2
		// EQUALORNOT = 3
		CC.set(bitIndex, bit);
		MainFrame.updateRegUI("CC", CC, 4);
	}
	/**
	 * Method to get the values of Overflow, underflow, divzero, equalornot flags from CC
	 */
	public byte getCC(int i) {
		int bitIndex = 3 - i;
		// OVERFLOW = 0
		// UNDERFLOW = 1
		// DIVZERO = 2
		// EQUALORNOT = 3
		boolean r = CC.get(bitIndex);
		if (r)
			return 1;
		else
			return 0;
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
			setRegister(PC,Util.bitSet2Int(PC)+1);
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
			setRegister(PC,Util.bitSet2Int(PC)+1);
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
			setRegister(PC,Util.bitSet2Int(PC)+1);
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
			setRegister(PC,Util.bitSet2Int(PC)+1);
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
			setRegister(PC,Util.bitSet2Int(PC)+1);
			//setRegister(PC, memory.getAddress());
			break;
		case OpCodes.JZ:
			System.out.println("JZ");
			ea = calculateEA((byte) 0, ix, addr);
			System.out.println(ea);
			if(Util.bitSet2Int(getGPR(r))==0) {
				setRegister(PC, ea);
			}
			else {
				setRegister(PC,Util.bitSet2Int(PC)+1);
			}
			break;
			
		case OpCodes.JNE:
			System.out.println("JNE");
			ea = calculateEA((byte) 0, (byte) 0, addr);
			if(Util.bitSet2Int(getGPR(r))!=0) {
				setRegister(PC, ea);
			}
			else {
				setRegister(PC,Util.bitSet2Int(PC)+1);
			}
			break;
		case OpCodes.JCC:
			System.out.println("JCC");
			byte cc = getCC(r);
			ea = calculateEA(r, ix, addr);
			if (cc == 1) {
				setRegister(PC, ea);
			} else {
				setRegister(PC, Util.bitSet2Int(PC) + 1); // PC <- PC + 1
			}
			break;

		// Unconditional Jump to address
		case OpCodes.JMA:
			System.out.println("JMA");
			ea = calculateEA(i, ix, addr);
			// Unconditional Jump To Address
			// R is ignored in this instruction
			setRegister(PC, ea); // PC <- EA
			break;
		
		// Jump and Save Return Address:
		case OpCodes.JSR:
			System.out.println("JSR");
			ea = calculateEA(i, ix, addr);

			// Jump and Save Return Address
			setRegister(R3, Util.bitSet2Int(PC) + 1);
			setRegister(PC, ea);
			break;
		
		//Return From Subroutine w/ return code as Immed
		//	portion (optional) stored in the instruction’s
		//	address field.
		case OpCodes.RFS:
			System.out.println("RFS");
			setRegister(R0, addr);
			setRegister(PC, R3);
			break;
		
		// Subtract One and Branch
		case OpCodes.SOB:
			System.out.println("SOB");
			ea = calculateEA(i, ix, addr);

			int CR = Util.bitSet2IntSigned(getGPR(r));
			setRegisterSigned(getGPR(r), CR - 1);
			System.out.println(Util.bitSet2IntSigned(getGPR(r)));
			if (CR >= 0) {
				setRegister(PC, ea);
			} else {
				setRegister(PC, Util.bitSet2Int(PC) + 1); // PC <- PC + 1
			}
			break;

		// Jump Greater Than or Equal To
		case OpCodes.JGE:
			System.out.println("JGE");
			ea = calculateEA(i, ix, addr);
			CR = -1;
			System.out.println("Address"+ea);
			CR = Util.bitSet2IntSigned(getGPR(r));
			System.out.println(CR);
			if (CR >= 0) {
				setRegister(PC, ea);
			} else {
				setRegister(PC, Util.bitSet2Int(PC) + 1); // PC <- PC + 1
			}
			break;

		// Multiply Register by Register
		case OpCodes.MLT:
			System.out.println("MLT");

			if ((r == 0 || r == 2) && (ry == 0 || ry == 2)) {
				int crx = 0;
				int cry = 0;

				crx = Util.bitSet2Int(getGPR(r));
				cry = Util.bitSet2Int(getGPR(ry));

				int result = crx * cry;
				int upper = result >> 16;
				int lower = result - (upper << 16);

				if (upper > (2 ^ 16 - 1)) {
					setCC(Constants.CC_OVERFLOW, true); // setting OVERFLOW cc(0)
					upper = upper - ((upper >> 16) << 16);
				}

				if (r == 0) {
					setRegister(R0, upper);
					setRegister(R1, lower);
				} else {
					setRegister(R2, upper);
					setRegister(R3, lower);
				}
			} else {
				// some kind of machine fault?
			}
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;
		
		// Divide Register by Register
		case OpCodes.DVD:
			System.out.println("DVD");

			if ((r == 0 || r == 2) && (ry == 0 || ry == 2)) {
				int crx = 0;
				int cry = 0;

				crx = Util.bitSet2Int(getGPR(r));
				cry = Util.bitSet2Int(getGPR(ry));

				if (cry == 0) {
					setCC(Constants.CC_DIVZERO, true); // setting DIVZERO
					setRegister(PC, Util.bitSet2Int(PC) + 1);
					break;
				}

				int result = crx / cry;
				int upper = result;
				int lower = crx - (cry * upper);

				if (r == 0) {
					setRegister(R0, upper);
					setRegister(R1, lower);
				} else {
					setRegister(R2, upper);
					setRegister(R3, lower);
				}
			} else {
				// some kind of machine fault?
			}
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		// Test the Equality of Register and Register
		case OpCodes.TRR:
			System.out.println("TRR");

			int crx = Util.bitSet2Int(getGPR(r));
			int cry = Util.bitSet2Int(getGPR(ry));
			if (crx == cry) {
				setCC(Constants.CC_EQUALORNOT, true); // cc(4) <- 1
			} else {
				setCC(Constants.CC_EQUALORNOT, false);
			}
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		// Logical And of Register and Register
		case OpCodes.AND:
			System.out.println("AND");
			getGPR(r).and(getGPR(ry));
			setRegister(getGPR(r), getGPR(r));
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		//	Logical Or of Register and Register
		case OpCodes.ORR:
			System.out.println("ORR");
			getGPR(r).or(getGPR(ry));
			setRegister(getGPR(r), getGPR(r));
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		// Logical Not of Register To Register 
		case OpCodes.NOT:
			System.out.println("NOT");
			getGPR(r).flip(0, getGPR(r).length());
			setRegister(getGPR(r), getGPR(r));
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		// Add Memory To Register
		case OpCodes.AMR:
			System.out.println("AMR");
			ea = calculateEA(i, ix, addr);
			setRegister(MAR, ea);
			dataAddr = Util.bitSet2Int(MAR);
			data = Util.bitSet2Int(memory.read(dataAddr));
			setRegister(MBR, data);
			int result = Util.bitSet2IntSigned(getGPR(r)) + Util.bitSet2IntSigned(MBR);
			setRegisterSigned(getGPR(r), result);
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		// Subtract Memory From Register
		case OpCodes.SMR:
			System.out.println("SMR");
			ea = calculateEA(i, ix, addr);
			setRegister(MAR, ea);
			dataAddr = Util.bitSet2Int(MAR);
			data = Util.bitSet2Int(memory.read(dataAddr));
			setRegister(MBR, data);

			result = Util.bitSet2IntSigned(getGPR(r)) - Util.bitSet2IntSigned(MBR);
			System.out.println(ea + " " + i + " " + ix + " " + addr);
			System.out.println(data + " @ $" + dataAddr);

			System.out.println(Util.bitSet2IntSigned(getGPR(r)) + "-" + Util.bitSet2IntSigned(MBR) + "=" + result);
			setRegisterSigned(getGPR(r), result);
			setRegister(PC, Util.bitSet2Int(PC) + 1);

			break;

		// Add Immediate to Register,
		case OpCodes.AIR:
			System.out.println("AIR");
			result = Util.bitSet2IntSigned(getGPR(r)) + addr;
			setRegisterSigned(getGPR(r), result);
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			System.out.println(Util.bitSet2IntSigned(getGPR(r)) + " + " + addr + " = " + result);
			break;

		// Subtract Immediate from Register
		case OpCodes.SIR:
			System.out.println("SIR");
			result = Util.bitSet2IntSigned(getGPR(r)) - addr;
			setRegisterSigned(getGPR(r), result);
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		// Shift Register by Count 
		case OpCodes.SRC:
			System.out.println("SRC");
			// arithmetic
			if (al == 0) {
				if (lr == 0) {
					result = Util.bitSet2IntSigned(getGPR(r)) << count;
					if (result > Constants.SIGNED_MAX || result < Constants.SIGNED_MIN) {
						setCC(Constants.CC_OVERFLOW, true);
					}
					setRegisterSigned(getGPR(r), result);
				}

				else if (lr == 1) {
					result = Util.bitSet2IntSigned(getGPR(r)) >> count;
					if (Util.bitSet2IntSigned(getGPR(r)) != (result << count)) {
						setCC(Constants.CC_UNDERFLOW, true);
					}
					setRegisterSigned(getGPR(r), result);
				}

			}
			// logical
			else if (al == 1) {
				if (lr == 0) {
					result = Util.bitSet2Int(getGPR(r)) << count;
					if (result > Constants.UNSIGNED_MAX) {
						setCC(Constants.CC_OVERFLOW, true);
					}
					setRegister(getGPR(r), result);
				} else if (lr == 1) {
					result = Util.bitSet2Int(getGPR(r)) >>> count;
					if (Util.bitSet2Int(getGPR(r)) != (result << count)) {
						setCC(Constants.CC_UNDERFLOW, true);
					}
					setRegister(getGPR(r), result);
				}
			}
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		
		// Rotate Register by Count 
		case OpCodes.RRC:
			System.out.println("RRC");
			CR = Util.bitSet2Int(getGPR(r));
			if (al == 1) {
				if (lr == 0)
					setRegister(getGPR(r), (CR << count) | (CR >> (16 - count)));
				else if (lr == 1)
					setRegister(getGPR(r), (CR >> count) | (CR << (16 - count)));
			}
			setRegister(PC, Util.bitSet2Int(PC) + 1);

			break;
		case OpCodes.LT:
			System.out.println("IN LT");
			ea = calculateEA(i, ix, addr);
			System.out.println("EA"+ea);
			setRegister(MAR, ea);
			memory.deleteAddress(Util.bitSet2Int(MAR));
			setRegisterSigned(MBR, Util.bitSet2IntSigned(getGPR(r)));
			System.out.println("values"+Util.bitSet2IntSigned(MBR)+"    "+memory.read(Util.bitSet2Int(MAR))+"      "+Util.bitSet2IntSigned(memory.read(Util.bitSet2Int(MAR))));
			if(Util.bitSet2IntSigned(MBR)<=Util.bitSet2IntSigned(memory.read(Util.bitSet2Int(MAR)))) {
			memory.write(Util.bitSet2IntSigned(MBR), Util.bitSet2Int(MAR));
			System.out.println("Test"+Util.bitSet2IntSigned(getGPR(1)));
			memory.write(Util.bitSet2IntSigned(getGPR(1)), 0);
			}
			//setRegister(PC, memory.getAddress());
			System.out.println(Util.bitSet2IntSigned(MBR) + " @ $" + Util.bitSet2Int(MAR));
			setRegister(PC,Util.bitSet2Int(PC)+1);
			break;
		// Input Character To Register from Device, r = 0..3
		case OpCodes.IN:
			// Get devid
			// devid = 0 keyboard
			System.out.println("IN");
			int inp = 0;
			if (dev_id == 0) {
				inp = MainFrame.getKeyboard();
				setRegister(getGPR(r), inp);
			}
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		// Output Character to Device from Register, r = 0..3
		case OpCodes.OUT:
			// devid = 1 printer
			System.out.println("OUT");
			if (dev_id == 1) {
				MainFrame.setPrinter(Util.bitSet2Int(getGPR(r)));
			}
			setRegister(PC, Util.bitSet2Int(PC) + 1);
			break;

		case OpCodes.CHK:
			break;
			
		case OpCodes.HLT:
			if(hlt) {
				System.out.println("HLT");
				return 1;
			}
			setRegister(PC,Util.bitSet2Int(PC)+1);
			break;
		default:
			setRegister(PC,Util.bitSet2Int(PC)+1);
		}

		return 0;

	}
	
	/**
	 * This method takes the input file from the user and converts the contents of it to Hexadecimal
	 * format and writes to a new file.
	 * Uses switch case to match the Instructions and converts them to hexadecimal format
	 */
	public void encodeInputFile(String path) {
		inputFile = new File(path);
		original=new File(path);
		System.out.println("Input File");
		 try {
	        	   
	        		  FileWriter Writer = new FileWriter("Temporary.txt");
	        		  BufferedWriter myWriter = new BufferedWriter(Writer);
	  
	  			s= new Scanner(inputFile);
	  			String s1,mainCode;
  				int loc=6,len,number;
  				String[] sa,splitInst;
  				if (s.hasNextLine()) {
  					s1 = s.nextLine().strip();
  					System.out.println(s1.length());
  				
  					sa = s1.split(" ");
  					System.out.println(sa.length+" "+ sa[0]+" "+sa[1]);
  	  				loc=Integer.parseInt(sa[1].strip());
  				}	
	  			while (s.hasNextLine()) {
	  				s1 = s.nextLine().strip();
	  				if(s1.length()==0) {
	  					break;
	  				}
	  				System.out.println("S1:"+s1);
	  				System.out.println("S1 len:"+s1.length());
	  				sa = s1.split(" ");
	  				System.out.println(sa.length+" "+sa[0]);
	  				System.out.println(sa.length+" "+ sa[0]+" "+sa[1]);
	  				switch(sa[0].toUpperCase().strip()) {
	  				
	  				case "LOC":
	  					end=sa[1].strip();
	  					break;
	  					
	  					
	  				case "DATA":
	  					myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  					System.out.println("DATA");
	  					if(sa[1].toUpperCase().strip().equals("END")) {
	  						System.out.println("END");
	  						myWriter.write(Util.intToHex(end));
	  						endMap.put(String.valueOf(loc),"END");
	  						endAccess.add(Util.intToHex(Integer.toString(loc)));
	  					}
	  					else {
	  						System.out.println(Util.intToHex(sa[1]));
	  					myWriter.write(Util.intToHex(sa[1]));
	  					}
	  					myWriter.newLine();
	  					//myWriter.write(Integer.toHexString(loc)+" "+Integer.toHexString(Integer.parseInt(sa[1].strip())));
	  					break;
	  					
	  					
	  				case "LDR":
	  						myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
			  				splitInst=getInstance().splitInstruction(sa[1]);
			  				len=splitInst.length;
			  				mainCode=octToBinary(OpCodes.LDR);
			  				mainCode+=hexCode(splitInst);
			  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
			  				myWriter.newLine();
	  				break;
	  				
	  				
	  				case "LDX":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
			  				splitInst=getInstance().splitInstruction(sa[1]);
			  				len=splitInst.length;
			  				mainCode=octToBinary(OpCodes.LDX);
			  				if(len==3) {
			  					mainCode+="00"+Util.intToBinR(splitInst[0])+splitInst[2]+Util.intToBinA(splitInst[1]);
			  				}
			  				else if(len==2) {
			  					mainCode+="00"+Util.intToBinR(splitInst[0])+"0"+Util.intToBinA(splitInst[1]);
			  				}
			  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
			  				myWriter.newLine();
	  				break;
	  				
	  				
	  				case "LDA":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.LDA);
	  				mainCode+=hexCode(splitInst);
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				
	  				case "STR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.STR);
	  				mainCode+=hexCode(splitInst);
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				
	  				case "STX":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.STX);
	  				if(len==3) {
	  					mainCode+="00"+Util.intToBinR(splitInst[0])+splitInst[2]+Util.intToBinA(splitInst[1]);
	  				}
	  				else if(len==2) {
	  					mainCode+="00"+Util.intToBinR(splitInst[0])+"0"+Util.intToBinA(splitInst[1]);
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				
	  				case "JZ":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.JZ);
	  				mainCode+=hexCode(splitInst);
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				
	  				case "JNE":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.JNE);
	  				mainCode+=hexCode(splitInst); 
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				
	  				case "JCC":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.JCC);
	  				mainCode+=hexCode(splitInst); 
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "JMA":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.JMA);
	  				if(len==3) {
	  					mainCode+="00"+Util.intToBinR(splitInst[0])+splitInst[2]+Util.intToBinA(splitInst[1]);
	  				}
	  				else if(len==2) {
	  					mainCode+="00"+Util.intToBinR(splitInst[0])+"0"+Util.intToBinA(splitInst[1]);
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "JSR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.JSR);
	  				if(len==3) {
	  					mainCode+="00"+Util.intToBinR(splitInst[0])+splitInst[2]+Util.intToBinA(splitInst[1]);
	  				}
	  				else if(len==2) {
	  					mainCode+="00"+Util.intToBinR(splitInst[0])+"0"+Util.intToBinA(splitInst[1]);
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "RFS":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.JSR)+"00"+"00"+"0"+Util.intToBinA(splitInst[0]);
	  				break;
	  				
	  				case "SOB":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.SOB);
	  				mainCode+=hexCode(splitInst); 
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "JGE":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.JGE);
	  				mainCode+=hexCode(splitInst); 
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "AMR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.AMR);
	  				mainCode+=hexCode(splitInst); 
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "SMR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.SMR);
	  				mainCode+=hexCode(splitInst); 
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "SIR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.SIR);
	  
	  				if(len==2) {
	  					mainCode+=Util.intToBinR(splitInst[0])+"00"+"0"+Util.intToBinA(splitInst[1]);
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "AIR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.AIR);
	  				
	  				if(len==2) {
	  					mainCode+=Util.intToBinR(splitInst[0])+"00"+"0"+Util.intToBinA(splitInst[1]);
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "LT":
	  					System.out.println("LTLTLTLT");
	  				myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.LT);
	  				System.out.println(len);
	  				if(len==3) {
	  					System.out.println(mainCode);
	  					mainCode+=Util.intToBinR(splitInst[0])+Util.intToBinR(splitInst[1])+"0"+Util.intToBinA(splitInst[2]);
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "MLT":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.MLT);
	  				if(len==2) {
	  					mainCode+=Util.intToBinR(splitInst[0])+Util.intToBinR(splitInst[1])+"0"+"00000";
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "DVD":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.DVD);
	  				if(len==2) {
	  					mainCode+=Util.intToBinR(splitInst[0])+Util.intToBinR(splitInst[1])+"0"+"00000";
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "TRR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.TRR);
	  				if(len==2) {
	  					mainCode+=Util.intToBinR(splitInst[0])+Util.intToBinR(splitInst[1])+"0"+"00000";
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "AND":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.AND);
	  				if(len==2) {
	  					mainCode+=Util.intToBinR(splitInst[0])+Util.intToBinR(splitInst[1])+"0"+"00000";
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "ORR":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.ORR);
	  				if(len==2) {
	  					mainCode+=Util.intToBinR(splitInst[0])+Util.intToBinR(splitInst[1])+"0"+"00000";
	  				}
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "NOT":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.NOT);
	  				mainCode+=Util.intToBinR(splitInst[0])+"00"+"0"+"00000";
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "SRC":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.SRC);
  					mainCode+=Util.intToBinR(splitInst[0])+splitInst[3]+splitInst[2]+"0"+Util.intToBinA(splitInst[1]);
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "RRC":myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.RRC);
	  				//System.out.println("&&&&&RRC:&&&&&&"+splitInst[0]+splitInst[3]+splitInst[2]+"0"+splitInst[1]);
  					mainCode+=Util.intToBinR(splitInst[0])+splitInst[3]+splitInst[2]+"0"+Util.intToBinA(splitInst[1]);
	  				myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
	  				myWriter.newLine();
	  				break;
	  				
	  				case "IN":System.out.println("IN"); myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.IN);
  					mainCode+=Util.intToBinR(splitInst[0])+"00"+"0"+Util.intToBinA(splitInst[1]);
  					myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
  					myWriter.newLine();
	  				break;
	  				
	  				case "OUT": myWriter.write(Util.intToHex(Integer.toString(loc))+" ");
	  				splitInst=getInstance().splitInstruction(sa[1]);
	  				len=splitInst.length;
	  				mainCode=octToBinary(OpCodes.OUT);
  					mainCode+=Util.intToBinR(splitInst[0])+"00"+"0"+Util.intToBinA(splitInst[1]);
  					myWriter.write(Util.intToHex(String.valueOf(Integer.parseInt(mainCode, 2))));
  					myWriter.newLine();
	  				break;
	  				
	  				case "END:":
	  					myWriter.write(Util.intToHex(end)+" "+"0000");
	  					myWriter.newLine();
	  					break;
	  					
	  				}

	  				loc+=1;
	  			}
	  			myWriter.close();
	  			inputFile=new File("Temporary.txt");
	  			s= new Scanner(inputFile);
	  			FileWriter Writers = new FileWriter("CustomInput.txt");
      		  BufferedWriter myWriters = new BufferedWriter(Writers);
	  			while (s.hasNextLine()) {
	  				s1 = s.nextLine().strip();
	  				System.out.println("***********S1:"+s1.length());
	  				sa = s1.split(" ");
	  				if(endAccess.contains(sa[0])) {
	  					myWriters.write(sa[0]+" "+Util.intToHex(end));
	  				}
	  				else {
	  					myWriters.write(sa[0]+" "+sa[1]);
	  				}
	  				myWriters.newLine();
	  			}
	  			myWriters.close();
	           
	 
	      } catch (Exception e) {
	           e.printStackTrace();
	      }
		 init("CustomInput.txt");

}
/* Method to split the Instruction using "," as delimiter.
 * 
 */
	private String[] splitInstruction(String string) {
		String s[]=string.split(",");
		return s;
		
	}
	
	/*
	 * Method to convert Opcodes(octal format) to Binary format.
	 */
	private String octToBinary(int val) {
		int rem,n=val;
		String retVal="";
		String[] bin= {"000","001","010","011","100","101","110","111"};
		while(val>0) {
			rem=val%10;
			retVal=bin[rem]+retVal;
			val/=10;
		}
		if(retVal.length()<6) {
			retVal=bin[0]+retVal;
		}
		return retVal;
	}
	
	/*
	 * Method to convert the Instructions into hexadecimal string. 
	 */
	private String hexCode(String[] sarr) {
		String s="";
		int len=sarr.length;
		System.out.println("(((((((((((((((((((((((((((((((((((((((((((Length))))))))"+len);
		if(len==4) {
				s+=Util.intToBinR(sarr[0])+Util.intToBinR(sarr[1])+sarr[2]+Util.intToBinA(sarr[3]);
			}
			else if(len==3) {
				s+=Util.intToBinR(sarr[0])+Util.intToBinR(sarr[1])+"0"+Util.intToBinA(sarr[2]);
			}
			else if(len==2) {
				s+=Util.intToBinR(sarr[0])+"00"+"0"+Util.intToBinA(sarr[1]);
			}
		
		return s;
	}

	public void run() {
		System.out.println("RUN");
		if (original.getName().equals("program1.txt")) {
			while(!hlt) {
				singleStep();
			}
			//MainFrame.setPrinter("closest number: " + Util.bitSet2Int(memory.read(202)) + "\n");
		}
		else if(original.getName().equals("CustomInput.txt")) {
		while(!hlt) {
			singleStep();
		}}
		
	}
	
}
