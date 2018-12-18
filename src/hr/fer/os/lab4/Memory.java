package hr.fer.os.lab4;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class Memory {
	
	private WordSize wordSize;
	private int memSize;
	private int memSizeToSmallestLargerTen;
	private Queue<MemRequest> pending;
	private Set<MemRequest> inMemory;
	private Set<Long> idSet;
	private char memory[][];
	
	public Memory(WordSize wordSize, int memSize) {
		this.wordSize = wordSize;
		this.memSize = memSize;
		this.pending = new LinkedList<>();
		this.inMemory = new TreeSet<>();
		this.idSet = new HashSet<>();
		memory = new char[this.memSize][this.wordSize.getSizeInBytes()];
		memSizeToSmallestLargerTen = toSmallestLargerTen(memSize);
		init();
	}
	
	private void init() {
		for(int i=0; i<this.memSize; i++) {
			for(int j=0; j<this.wordSize.getSizeInBytes(); j++) {
				memory[i][j] = '-';
			}
		}
	}
	
	public long malloc(MemRequest memReq) {
		if(memReq.segmentSizeInNumOfWords<=0) return -1;
		
		while(true) {
			long rand = (long)(Math.random()*memSizeToSmallestLargerTen);
			if(!idSet.add(rand)) continue;
			memReq.segmentID = rand;
			break;
		}
		pending.add(memReq);
		
		activateFirstFromPending();
		
		return memReq.segmentID;
		
	}
	
	public void free(long segmentID) {
		if(!idSet.contains(segmentID)) throw new NoSuchSegmentException(segmentID);
		
		for(MemRequest m : pending)
			if(m.segmentID==segmentID) {
				idSet.remove(segmentID);
				pending.remove(m);
				activateFirstFromPending();
				return;
		}
		
		MemRequest segmentToFree = null;
		for(MemRequest m : inMemory)
			if(m.segmentID==segmentID) {
				segmentToFree = m;
				break;
		}
		
		for(int i=segmentToFree.segmentAdress; i<=segmentToFree.segmentAdress+segmentToFree.segmentSizeInNumOfWords-1; i++) {
			for(int j=0; j<this.wordSize.getSizeInBytes(); j++) {
				memory[i][j] = '-';
			}
		}
		
		//free it from memory array and idSet and inMemory
		idSet.remove(segmentID);
		inMemory.remove(segmentToFree);
		
		activateFirstFromPending();
		
	}
	
	private void activateFirstFromPending() {
		
		if(pending.isEmpty()) return;

		int numOfHoles = 0;
		int smallestHoleThatCanFitStart = -1;
		int smallestHoleThatCanFitSize = this.memSize+1;
		boolean foundHole = false;
		
		int pomSize = 0, pomStart = 0;
		boolean countingTheHole = false;
		for(int i=0; i<this.memSize; i++) {
			if(this.memory[i][0]=='-') {
				numOfHoles++;
				if(countingTheHole) {
					pomSize++;
				}else {
					countingTheHole = true;
					pomStart = i;
					pomSize = 1;
				}
				if(i!=this.memSize-1)continue;
			}
			if(countingTheHole) {
				countingTheHole = false;
				if(pomSize<smallestHoleThatCanFitSize && pomSize>=this.pending.peek().segmentSizeInNumOfWords) {
					foundHole = true;
					smallestHoleThatCanFitStart = pomStart;
					smallestHoleThatCanFitSize = pomSize;
				}
			}
		}	
		
		if(foundHole) {
			MemRequest m = this.pending.poll();
			m.isInMemory = true;
			m.segmentAdress = smallestHoleThatCanFitStart;
			this.inMemory.add(m);
			for(int i=m.segmentAdress; i<=m.segmentAdress+m.segmentSizeInNumOfWords-1; i++) {
				for(int j=0; j<this.wordSize.getSizeInBytes(); j++) {
					memory[i][j] = m.segmentChar;
				}
			}
		}else {
			if(numOfHoles<this.pending.peek().segmentSizeInNumOfWords) return;
			MemRequest m = this.pending.poll();
			m.isInMemory = true;
			m.segmentAdress = defrag();
			this.inMemory.add(m);
			for(int i=m.segmentAdress; i<=m.segmentAdress+m.segmentSizeInNumOfWords-1; i++) {
				for(int j=0; j<this.wordSize.getSizeInBytes(); j++) {
					memory[i][j] = m.segmentChar;
				}
			}
		}
		
	}
	
	private int defrag() {
		for(MemRequest m : inMemory) {
			if(m.segmentAdress==0) continue;
			int destAdress = -1;
			for(int i=m.segmentAdress-1; i>=0; i--) {
				if(memory[i][0]=='-') destAdress=i;
				else break;
			}
			if(destAdress==-1) continue;
			for(int i=m.segmentAdress; i<=m.segmentAdress+m.segmentSizeInNumOfWords-1; i++) {
				for(int j=0; j<this.wordSize.getSizeInBytes(); j++)
					memory[i][j] = '-';
			}
			m.segmentAdress = destAdress;
			for(int i=m.segmentAdress; i<=m.segmentAdress+m.segmentSizeInNumOfWords-1; i++) {
				for(int j=0; j<this.wordSize.getSizeInBytes(); j++)
					memory[i][j] = m.segmentChar;
			}
		}
		for(int i=0; i<this.memSize; i++)
			if(memory[i][0]=='-') return i;
		return -1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Pending: ");
		for(MemRequest mrq : pending) {
			sb.append(mrq.segmentChar + "(" + mrq.segmentSizeInNumOfWords + "," + mrq.segmentID + ")");
			sb.append(" ");
		}
		sb.append("InMemory: ");
		for(MemRequest mrq : inMemory) {
			sb.append(mrq.segmentID + "(" + mrq.segmentChar + "," + mrq.segmentSizeInNumOfWords + ")");
			sb.append(" ");
		}
		sb.append("%n");
		for(int i=0; i<this.memSize; i++) {
			sb.append(memory[i]);
			sb.append("%n");
		}
		return sb.toString();
	}
	
	private int toSmallestLargerTen(int x) {
		int i=10;
		while(true) {
			if(i/x>1) break;
			i*=10;
		}
		return i;
	}
	
	public static class MemRequest implements Comparable<MemRequest>{
		
		private long segmentID;
		private int segmentSizeInNumOfWords;
		private char segmentChar;
		private boolean isInMemory;
		private int segmentAdress;
		
		public MemRequest(int segmentSizeInNumOfWords, char segmentChar) {
			this.segmentSizeInNumOfWords = segmentSizeInNumOfWords;
			this.segmentChar = segmentChar;
			this.segmentID = -1;
			this.isInMemory = false;
			this.segmentAdress = -1;
		}

		public long getSegmentID() {
			return segmentID;
		}
		
		public boolean isInMemory() {
			return isInMemory;
		}

		public int getSegmentSizeInNumOfWords() {
			return segmentSizeInNumOfWords;
		}

		public char getSegmentChar() {
			return segmentChar;
		}
		
		public int getsegmentAdress() {
			return segmentAdress;
		}

		@Override
		public int compareTo(MemRequest o) {
			return this.segmentAdress-o.segmentAdress;
		}

	}


}
