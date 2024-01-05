package csci6461;
/**
 * opcodes for the simulator.
 */
public class OpCodes {
	public static final byte HLT = 00;
	public static final byte LDR = 01;
	public static final byte STR = 02;
	public static final byte LDA = 03;
	public static final byte AMR = 04;
	public static final byte SMR = 05;
	public static final byte AIR = 06;
	public static final byte SIR = 07;
	public static final byte JZ  = 10;
	public static final byte JNE = 11;
	public static final byte JCC = 12;
	public static final byte JMA = 13;
	public static final byte JSR = 14;
	public static final byte RFS = 15;
	public static final byte SOB = 16;
	public static final byte JGE = 17;
	public static final byte MLT = 20;
	public static final byte DVD = 21;
	public static final byte TRR = 22;
	public static final byte AND = 23;
	public static final byte ORR = 24;
	public static final byte NOT = 25;
	public static final byte LDX = 41;
	public static final byte STX = 42;
	public static final byte SRC = 31;
	public static final byte RRC = 32;
	public static final byte LT =  33;
	public static final byte IN  = 61;
	public static final byte OUT = 62;
	public static final byte CHK = 63;
	
}