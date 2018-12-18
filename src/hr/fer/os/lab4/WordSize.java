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
	
	public static WordSize forSize(int size) {
		if(size==1) return WordSize.byte_1;
		if(size==2) return WordSize.byte_2;
		if(size==4) return WordSize.byte_4;
		if(size==8) return WordSize.byte_8;
		return null;
	}

}
