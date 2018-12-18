package hr.fer.os.lab4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Memory {
	
	private WordSize wordSize;
	private int memSize;
	private int memSizeToSmallestLargerTen;
	private Queue<MemRequest> pending;
	private List<MemRequest> inMemory;
	private Set<Long> idSet;
	private char memory[][];
	
	public Memory(WordSize wordSize, int memSize) {
		this.wordSize = wordSize;
		this.memSize = memSize;
		this.pending = new LinkedList<>();
		this.inMemory = new ArrayList<>();
		this.idSet = new HashSet<>();
		memory = new char[this.memSize][this.wordSize.getSizeInBytes()];
		memSizeToSmallestLargerTen = toSmallestLargerTen(memSize);
	}
	
	public long malloc(MemRequest memReq) {
		while(true) {
			long rand = (long)Math.random()*memSizeToSmallestLargerTen;
			if(!idSet.add(rand)) continue;
			memReq.segmentID = rand;
			break;
		}
		pending.add(memReq);
		tick();
		return memReq.segmentID;
	}
	
	public void free(long segmentID) {
		if(!idSet.contains(segmentID)) throw new NoSuchSegmentException(segmentID);
		
		for(MemRequest m : inMemory)
			if(m.segmentID==segmentID) {
				idSet.remove(segmentID);
				inMemory.remove(m);
				tick();
				return;
			}
		
		for(MemRequest m : pending)
			if(m.segmentID==segmentID) {
				idSet.remove(segmentID);
				pending.remove(m);
				tick();
				return;
			}
		
	}
	
	private void tick() {
		//1 pogledati moze li se jos stogod staviti u memoriju provjeravanjem jeli segSize<=broja_rupa
		//2 ako moze, staviti ga u listu inMemory
		//3 staviti ga na najmanje mjesto na koje moze, ako nema takvog mjesta, defragmentirati i onda ga staviti
		//4 osvježiti memorijsko polje
	}
	
	@Override
	public String toString() {
		return null;
	}
	
	private int toSmallestLargerTen(int x) {
		int i=10;
		while(true) {
			if(i/x>1) break;
			i*=10;
		}
		return i;
	}
	
	public static class MemRequest {
		
		private long segmentID;
		private int segmentSizeInNumOfWords;
		private char segmentChar;
		private boolean isInMemory;
		private long segmentBegin;
		
		public MemRequest(int segmentSizeInNumOfWords, char segmentChar) {
			this.segmentSizeInNumOfWords = segmentSizeInNumOfWords;
			this.segmentChar = segmentChar;
			this.segmentID = -1;
			this.isInMemory = false;
			this.segmentBegin = -1;
		}

		public long getSegmentID() {
			return segmentID;
		}
		
		public boolean isInMemory() {
			return isInMemory;
		}

		public int getSegmentSize() {
			return segmentSizeInNumOfWords;
		}

		public char getSegmentChar() {
			return segmentChar;
		}

	}


}
