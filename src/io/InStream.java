package io;

public strictfp class InStream extends DataIO {

	private final int[] bs;
	protected int index;

	protected InStream(int[] data) {
		bs = data;
		index = 0;
	}

	public boolean end() {
		return index == bs.length;
	}

	public int nextByte() {
		check(1);
		int ans = toByte(bs, index);
		index++;
		return ans;
	}

	public byte[] nextBytesB() {
		int len = nextByte();
		byte[] ints = new byte[len];
		for (int i = 0; i < len; i++)
			ints[i] = (byte) nextByte();
		return ints;
	}

	public byte[] nextBytesI() {
		int len = nextInt();
		byte[] ints = new byte[len];
		for (int i = 0; i < len; i++)
			ints[i] = (byte) nextByte();
		return ints;
	}

	public double nextDouble() {
		check(8);
		double ans = toDouble(bs, index);
		index += 8;
		return ans;
	}

	public double[] nextDoubles() {
		int len = nextByte();
		double[] ints = new double[len];
		for (int i = 0; i < len; i++)
			ints[i] = nextDouble();
		return ints;
	}

	public float nextFloat() {
		check(4);
		float ans = toFloat(bs, index);
		index += 4;
		return ans;
	}

	public int nextInt() {
		check(4);
		int ans = toInt(bs, index);
		index += 4;
		return ans;
	}

	public int[] nextIntsB() {
		int len = nextByte();
		int[] ints = new int[len];
		for (int i = 0; i < len; i++)
			ints[i] = nextInt();
		return ints;
	}

	public int[][] nextIntsBB() {
		int len = nextByte();
		int[][] ints = new int[len][];
		for (int i = 0; i < len; i++)
			ints[i] = nextIntsB();
		return ints;
	}

	public long nextLong() {
		check(8);
		long ans = toLong(bs, index);
		index += 8;
		return ans;
	}

	public int nextShort() {
		check(2);
		int ans = toShort(bs, index);
		index += 2;
		return ans;
	}

	public String nextString() {
		return new String(nextBytesB());
	}

	public String nextText() {
		return new String(nextBytesI());
	}

	public int pos() {
		return index;
	}

	public void reread() {
		index = 0;
	}

	public int size() {
		return bs.length;
	}

	public void skip(int n) {
		index += n;
	}

	public InStream subStream() {
		int n = nextInt();
		int[] bsa = new int[n];
		for (int i = 0; i < n; i++)
			bsa[i] = bs[index + i];
		InStream is = new InStream(bsa);
		index += n;
		return is;
	}

	protected void check(int i) {
	}

}
