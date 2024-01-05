package csci6461;
import java.util.BitSet;


/**
 * utility class to implement BitSet and word conversions
 */
public class Util {
	
	public static BitSet int2BitSet(int value) {
		//System.out.println("****************Int2BitSet:"+value+"***********");
		BitSet bits = new BitSet();
	    int index = 0;
	    while (value != 0) {
	      if (value % 2 != 0) {
	        bits.set(index);
	      }
	      ++index;
	      value = value >>> 1;
	    }
	    return bits;
	}
	
	public static BitSet int2BitSetSigned(int value) {
		BitSet bits = new BitSet();
		if (value < 0) {
			bits.set(15);
			//value = - 32768 - value; // 2's complement
		} 
	    int index = 0;
	    while (value != 0) { 
	      if (value % 2 != 0) {
	        bits.set(index);
	      }
	      ++index;
	      value = value >>> 1;
	    }
	    return bits;
	}
		
	public static int bitSet2Int(BitSet bitSet) {
        int intValue = 0;
        for (int bit = 0; bit < bitSet.length(); bit++) {
            if (bitSet.get(bit)) {
                intValue |= (1 << bit);
            }
        }
        return intValue;
    }
	
	public static int bitSet2IntSigned(BitSet bitSet) {
		//System.out.println("Bitset:"+bitSet);
        int intValue = 0;
        int maxValue = 1 << bitSet.length()-1;
        boolean sign = bitSet.get(15);
        bitSet.set(15, false);
        for (int bit = 0; bit < bitSet.length(); bit++) {
            if (bitSet.get(bit)) {
                intValue |= (1 << bit);
            }
        }
        if (sign) {
        	bitSet.set(15);
        	return intValue-maxValue;
        }
        System.out.println("IntValue:"+intValue);
        return intValue;
    }
		
	
	
	public static void bitSetDeepCopy(BitSet source, int sourceBits,
			BitSet destination, int destinationBits) {
		if (sourceBits <= destinationBits) {
			destination.clear();
			for (int i = 0; i < sourceBits; i++) {
				destination.set(i, source.get(i));
			}
		} else {
			destination.clear();
			for (int i = 0; i < destinationBits; i++) {
				destination.set(i, source.get(i));
			}
		}
	}
	public static Word int2Word(int value) {
		Word w = new Word();
	    int index = 0;
	    while (value != 0) {
	      if (value % 2 != 0) {
	        w.set(index);
	      }
	      ++index;
	      value = value >>> 1;
	    }
	    return w;
	}
	public static Word int2WordSigned(int value) {
		Word bits = new Word();
		if (value < 0) {
			bits.set(15);
		} 
	    int index = 0;
	    while (value != 0) {
	      if (value % 2 != 0) {
	        bits.set(index);
	      }
	      ++index;
	      value = value >>> 1;
	    }
	    return bits;
	}
	public static String intToHex(String value) {
		String s= Integer.toHexString(Integer.parseInt(value)).toUpperCase();
		while(s.length()<4) {
			s=0+s;
		}
		return s;
	}
	public static String intToBinR(String value) {
		String s=Integer.toBinaryString(Integer.parseInt(value));
		while(s.length()<2) {
			s=0+s;
		}
		return s;
	}
	public static String intToBinA(String value) {
		String s=Integer.toBinaryString(Integer.parseInt(value));
		while(s.length()<5) {
			s=0+s;
		}
		return s;
	}
}
