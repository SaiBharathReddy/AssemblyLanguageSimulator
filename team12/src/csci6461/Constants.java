package csci6461;

public class Constants {
	public static final int UNSIGNED_MAX = 65535;
	public static final int SIGNED_MIN = -32767;
	public static final int SIGNED_MAX = 32767;
	public static final float FLOAT_MIN = -(float) (2 - Math.pow(2, -7) * Math.pow(2, 256 - 1));
	public static final float FLOAT_MAX = (float) (2 - Math.pow(2, -7) * Math.pow(2, 256 - 1));
	public static final int CC_OVERFLOW = 0;
	public static final int CC_UNDERFLOW = 1;
	public static final int CC_DIVZERO = 2;
	public static final int CC_EQUALORNOT = 3;
}
