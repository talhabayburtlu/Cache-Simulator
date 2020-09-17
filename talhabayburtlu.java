import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/* Talha BAYBURTLU - 150118066
 * This program simulates cache operations like instruction load (I) ,
 * data load (L) , store (S) and modify & store (M) with caches. The data is
 * read from ram.txt (RAM) and based on the instructions on trace file, operations 
 * executes. Program needs command line arguments as:
 * -L1s <L1s> -L1E <L1E> -L1b <L1b> -L2s <L2s> -L2E <L2E> -L2b <L2b> -t <tracefile> */

public class talhabayburtlu {
	private static ArrayList<String> RAM; // Bytes are hold as indexes in RAM.
	private static Cache L1D, L1I, L2; // Caches as global variables.
	
	public static void main(String[] args) throws IOException {
		createRAM("ram.txt"); // Creating RAM from data inside ram.txt.
		
		createCache("L1D" , Integer.parseInt(args[1]) , Integer.parseInt(args[3]) , Integer.parseInt(args[5])); // Creating L1D cache.
		createCache("L1I" , Integer.parseInt(args[1]) , Integer.parseInt(args[3]) , Integer.parseInt(args[5])); // Creating L1I cache.
		createCache("L2" , Integer.parseInt(args[7]) , Integer.parseInt(args[9]) , Integer.parseInt(args[11])); // Creating L2 cache.
		
		processTraceFile(args[13]); // Processing files and printing relevant information.
	}
	
	public static void createRAM(String fileName) throws FileNotFoundException { // Creates ram from data inside file.
		ArrayList<String> RAM = new ArrayList<String>();
		Scanner input = new Scanner(new File(fileName));
		
		while (input.hasNext())
			RAM.add(input.next());
		
		setRAM(RAM);
		input.close();
	}
	
	public static void createCache(String name , int s , int E , int b) { // Creates cache.
		Cache cache = new Cache(name,s,E,b);
		
		switch (name) {
			case "L1D": setL1D(cache); break;
			case "L1I": setL1I(cache); break;
			case "L2" : setL2(cache); break;
		}
	}
	
	public static void processTraceFile(String fileName) throws IOException { // Process trace file and prints relevant information.
		Scanner input = new Scanner(new File(fileName));
		FileWriter outFile = new FileWriter(new File("log.txt"));
		
		int time = 1;
		while (input.hasNextLine()) { // Every operation in trace file gets executed.
			String nextLine = input.nextLine();
			String tokens[] = nextLine.split(" ");
			
			trimCommas(tokens); // Trimming commas for current operation.
			
			int address = Integer.parseInt(tokens[1], 16); // Turning address notation from hexadecimal to decimal for index purposes.
			int processBytes = Integer.parseInt(tokens[2],16); // Turning process bytes notation from hexadecimal to decimal for index purposes.
			
			Cache L1 = tokens[0].equals("I") ? L1I : L1D; // Determining which L1 cache to operate.
			outFile.write(nextLine + "\n");
			if (Integer.parseInt(tokens[1] , 16) > RAM.size()) { // Checking address is exist or not.
				adressNotFound(L1, L2, outFile, tokens[1]); // Incrementing misses for both caches.
				time++;
				continue;
			}

			switch (tokens[0]) { // Switch case for operation type.
			case "I":
			case "L":
			case "M":
				// Storing eviction numbers if eviction occurs in current operation.
				int evictionL1 = L1.getEviction(); 
				int evictionL2 = L2.getEviction();
				
				String dataString = getData(L1, tokens[1]); // Getting byte block size amount of bytes data from address.
				boolean L1Hit = L1.load(Integer.parseInt(tokens[1], 16), dataString.toCharArray() , time); // Processing load operation for L1(I/D) cache.
			
				dataString = getData(L2, tokens[1]);
				boolean L2Hit = L2.load(Integer.parseInt(tokens[1], 16), dataString.toCharArray(), time); // Processing load operation for L2 cache.				
				
				String evictionL1String = evictionL1 != L1.getEviction() ? " and eviction " : " "; 
				String evictionL2String = evictionL2 != L2.getEviction() ? " and eviction " : " "; 
				
				// Printing relevant information about current operation.
				String information = "    Load Operation " + L1.getName() + (L1Hit ? " hit": " miss") +  evictionL1String + L2.getName() + (L2Hit ? " hit": " miss" + evictionL2String);
				outFile.write(information + "\n");
				
				int L1Set = L1.getSetIndex(address) , L2Set = L2.getSetIndex(address);
				information = (L1Hit && L2Hit) ? "    No placing in caches." : "    Placing in " + (L1Hit ? "" : L1.getName() + 
						(L1.getS() == 0 ? ", " : " in " + L1Set + " set, ")) + (L2Hit ? "" : L2.getName() + (L2.getS() == 0 ? "," : " in " + L2Set + " set,"));
				
				outFile.write(information + "\n");
				if (!tokens[0].equals("M")) {
					break;
				}
					
			case "S":
				ArrayList<String> replacedData = new ArrayList<String>(); // Data partitions as bytes.
				for (int i = 0; i < tokens[3].length() ; i += 2)
					replacedData.add(tokens[3].substring(i , i + 2).toUpperCase());
				
				for (int i = 0; i < processBytes ; i++ ) // Modifying corresponding memory
					RAM.set(address + i, replacedData.get(i));
				
				L1Hit = L1D.store(address, processBytes , replacedData); // Processing store operation for L1D cache.
				L2Hit = L2.store(address, processBytes, replacedData); // Processing store operation for L2 cache.
				
				// Printing relevant information about current operation.
				information = "    Store Operation " + L1D.getName() + (L1Hit ? " hit": " miss") + " " + L2.getName() + (L2Hit ? " hit": " miss" );
				outFile.write(information + "\n");
				
				information = "    Store in " + (L1Hit ? L1D.getName() + "," : "" ) + (L2Hit ? L2.getName() + "," : "") + "RAM"; 
				outFile.write(information + "\n");
				
				break;
			default: System.out.println("Operation " + tokens[0] + " couldn't found."); // Unidentified operation case.
			}
			time++;
		}
		
		printInformation(outFile); // Printing relevant information
		updateRAM(); // Updating contents of RAM to "ram-updated.txt" file.
		
		input.close();
		outFile.close();
		System.out.println("Trace file log created at \"log.txt\".");
	}
	
	public static void trimCommas(String tokens[]) { // Trims commas in process string.
		tokens[1] = tokens[1].substring(0, tokens[1].length() - 1);
		if (tokens.length == 4)
			tokens[2] = tokens[2].substring(0, tokens[2].length() - 1);
	}
	
	public static void adressNotFound(Cache L1, Cache L2 , FileWriter outFile , String address) throws IOException { // Increments misses of caches.
		L1.setMiss(L1.getMiss() + 1);
		L2.setMiss(L2.getMiss() + 1);
		
		outFile.write("    " + L1.getName() + " miss and L2 miss because '0x" + address + "' address can't be reached.\n");
	}
	
	public static String getData(Cache cache , String adress ) { // Gets byte block size amount of bytes data from address.
		String dataString = "";
		int startIndex = Integer.parseInt(adress , 16) - Integer.parseInt(adress , 16) % ((int)Math.pow(2, cache.getB())); // Decrementing start index to block byte's multiple.
		for (int i = 0; i < ((int)Math.pow(2,cache.getB())) ; i++)
			dataString += RAM.get(startIndex + i);
		return dataString;
	}
	
	public static void printInformation(FileWriter outFile) throws IOException {
		outFile.write("\n");
		outFile.write("L1D-hits:" + L1D.getHit() + " L1D-misses:" + L1D.getMiss() + " L1D-evictions:" + L1D.getEviction() + "\n");
		outFile.write("L1I-hits:" + L1I.getHit() + " L1I-misses:" + L1I.getMiss() + " L1I-evictions:" + L1I.getEviction() + "\n");
		outFile.write("L2-hits:" + L2.getHit() + " L2-misses:" + L2.getMiss() + " L2-evictions:" + L2.getEviction() + "\n");
		
		printCache(L1D);
		printCache(L1I);
		printCache(L2);
	}
 	
	public static void printCache(Cache cache) throws IOException {
		FileWriter file = new FileWriter(new File(cache.getName() + " Cache.txt"));
		String header = String.format("%-8s %15s %12s %12s %25s", cache.getName() , "tag" , "time" , "v" , "data");
		file.write(header + "\n");
		
		Set sets[] = cache.getSets();
		for (int i = 0; i < sets.length ; i++) {
			file.write("-----------------------------------------------------------------------------\n");
			Line lines[] = sets[i].getLines();
			for (int j = 0; j < lines.length ; j++) {
				String padding = "     ";
				if (sets.length != 1 && j == (int)(Math.ceil(sets[i].getE() / 2))) {
					padding = String.format("%5s", "Set"  + i );
				}
				String tag = String.format("0x%08X", lines[j].getTag());
				String time = lines[j].getTime() + "";
				String data = String.valueOf(lines[j].getData());
				String line = String.format("| %s | %13s | %10s | %10s | %23s |", padding, tag, time, lines[j].getV(), data );
				file.write(line + "\n");
			}
		}
		file.write("-----------------------------------------------------------------------------\n");
		System.out.println(cache.getName() + " cache contents created at \"" + cache.getName() + " Cache.txt\".");
		file.close();
	}
	
	public static void updateRAM() throws IOException { // Updates contents of RAM to "ram-updated.txt" file.
		FileWriter file = new FileWriter(new File("ram updated.txt"));
		
		for (int i = 0 ; i < RAM.size() ; i++)
			file.write(RAM.get(i) + " ");
		
		file.close();
	}
	// Getter and setter methods.
	public static ArrayList<String> getRAM() {
		return RAM;
	}

	public static void setRAM(ArrayList<String> rAM) {
		RAM = rAM;
	}

	public static Cache getL1D() {
		return L1D;
	}

	public static void setL1D(Cache l1d) {
		L1D = l1d;
	}

	public static Cache getL1I() {
		return L1I;
	}

	public static void setL1I(Cache l1i) {
		L1I = l1i;
	}

	public static Cache getL2() {
		return L2;
	}

	public static void setL2(Cache l2) {
		L2 = l2;
	}

}
