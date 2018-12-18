package hr.fer.os.lab4;

import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		//Testing
		/*Memory memory = new Memory(WordSize.byte_4, 10);
		System.out.printf(memory.toString());
		memory.malloc(new Memory.MemRequest(4, 'A'));
		System.out.printf(memory.toString());
		long indexb = memory.malloc(new Memory.MemRequest(4, 'B'));
		System.out.printf(memory.toString());
		long indexc =memory.malloc(new Memory.MemRequest(2, 'C'));
		System.out.printf(memory.toString());
		long indexd =memory.malloc(new Memory.MemRequest(2, 'D'));
		System.out.printf(memory.toString());
		memory.free(indexb);
		System.out.printf(memory.toString());
		memory.malloc(new Memory.MemRequest(2, 'E'));
		System.out.printf(memory.toString());
		memory.free(indexc);
		System.out.printf(memory.toString());
		memory.free(indexd);
		System.out.printf(memory.toString());
		memory.malloc(new Memory.MemRequest(4, 'F'));
		System.out.printf(memory.toString());*/
		
		//Testing 2
		/*Memory memory = new Memory(WordSize.byte_1, 2);
		System.out.printf(memory.toString());
		long indexa = memory.malloc(new Memory.MemRequest(5, 'A'));
		System.out.printf(memory.toString());
		long indexb = memory.malloc(new Memory.MemRequest(2, 'B'));
		System.out.printf(memory.toString());
		memory.free(indexa);
		System.out.printf(memory.toString());*/
		
		Scanner sc = new Scanner(System.in);
		
		Memory m = createMemory(sc);
		System.out.printf(m.toString());
		
		System.out.println("Command input:");
		
		while(sc.hasNext()) {
			commandRecognize(m, sc.nextLine());
		}
	}
	
	private static Memory createMemory(Scanner sc) {
		System.out.print("Please enter memory word size 1/2/4/8 bytes: ");
		int wordSize=0;
		while(true) {
			String input = null;
			if(sc.hasNextLine())
				input = sc.nextLine();
			try {
				wordSize = Integer.parseInt(input);
			}catch(Exception e) {
				System.out.println("NaN value. Please try again.");
				continue;
			}
			if(wordSize==1 || wordSize==2 || wordSize==4 || wordSize==8) break;
			System.out.println("Incorrect value. Please try again.");
		}
		
		System.out.print("Please enter memory size in number of words: ");
		int memSize=0;
		while(true) {
			String input = null;
			if(sc.hasNextLine())
				input = sc.nextLine();
			try {
				memSize = Integer.parseInt(input);
			}catch(Exception e) {
				System.out.println("NaN value. Please try again.");
				continue;
			}
			if(memSize>0) break;
			System.out.println("Incorrect value. Please try again.");
		}
		
		return new Memory(WordSize.forSize(wordSize), memSize);
	}

	public static void commandRecognize(Memory m, String s){
		char[] command = s.toCharArray();
		
		if(s.startsWith("malloc")) {
			StringBuilder sb = new StringBuilder();
			int i=7;
			
			while(true) {
				if(Character.isDigit(command[i])) {
					sb.append(command[i]);
					i++;
				}else {
					i++;
					break;
				}
			}
			int size = Integer.parseInt(sb.toString());
			
			StringBuilder sb2 = new StringBuilder();
			while(true) {
				if(command[i]!=')') {
					sb2.append(command[i]);
					i++;
				}else {
					break;
				}
			}
			char c = sb2.toString().trim().toCharArray()[0];
			
			long index = m.malloc(new Memory.MemRequest(size, c));
			System.out.printf("Memory at: " + index + "%n");
		}else if(s.startsWith("free")) {
			StringBuilder sb = new StringBuilder();
			int i=5;
			
			while(true) {
				if(Character.isDigit(command[i])) {
					sb.append(command[i]);
					i++;
				}else {
					i++;
					break;
				}
			}
			int id = Integer.parseInt(sb.toString());
			
			try{
				m.free(id);
			}catch(Exception e){
				System.out.println("No segment with id: " + id);
			}
		}
		
		
		System.out.printf(m.toString());
	}

}
