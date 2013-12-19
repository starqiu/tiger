package util;

public class JmpBuf {
	private static int count = 0;

	private JmpBuf() {
	}

	/**
	 * @return the count
	 */
	public static int getCount() {
		return count;
	}

	// Factory pattern
	public static String next() {
		return "buf_" + (JmpBuf.count++);
	}
}
