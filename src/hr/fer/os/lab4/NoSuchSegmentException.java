package hr.fer.os.lab4;

public class NoSuchSegmentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoSuchSegmentException(long segID) {
		super(Long.toString(segID));
	}

}
