package hr.fer.os.lab4;

public enum WordSize {
	
	byte_1(1), byte_2(2), byte_4(4), byte_8(8);
	
	private int sizeInBytes;

	private WordSize(int sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}
	
	public int getSizeInBytes() {
		return sizeInBytes;
	}
	
	public int getSizeInBits() {
		return sizeInBytes*2;
	}

}
