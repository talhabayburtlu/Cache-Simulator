import java.util.ArrayList;

public class Cache {
	private String name;
	private int s;
	private int b;
	private int miss;
	private int hit;
	private int eviction;
	private Set[] sets;
	
	public Cache(String name, int s, int E, int b) {
		setName(name);
		setS(s);
		setB(b);
		
		Set[] sets = new Set[(int)Math.pow(2, s)]; // Creating sets.
		for (int i = 0; i < (int)Math.pow(2, s) ; i++)
			sets[i] = new Set(i,E,b);
		setSets(sets);
	}
	
	public boolean load(int address, char[] data, int time) { // Process load operation for given cache.
		int setIndex = this.getSetIndex(address); // Getting set index.
		int tag = this.getTag(address); // Getting tag.
		boolean returnValue = false; // Default behavior of return value is miss.
		boolean shouldEvict = true; 
		int minTime = Integer.MAX_VALUE;
		int minTimeIndex = -1;
		
		
		Line lines[] = this.sets[setIndex].getLines();
		for (int i = 0; i < lines.length ; i++) { // Loops through all lines of current set.
			
			if (lines[i].getV() != 0 && lines[i].getTag() == tag) { // Checking hit condition.
				this.hit++;
				returnValue = true; // Hit case.
				shouldEvict = false;
				break;
			}
			
			if (lines[i].getV() == 0) { // Checking empty line condition.
				lines[i].setV(1);
				lines[i].setData(data);
				lines[i].setTag(tag);
				lines[i].setTime(time);
				
				shouldEvict = false;
				this.miss++; // Miss case.
				break;
			}
			
			if (lines[i].getTime() < minTime) { // Finding line that has minimum time for eviction purpose.
				minTime = lines[i].getTime();
				minTimeIndex = i;
			}
		}
		
		if (shouldEvict) { // Operating eviction.
			lines[minTimeIndex].setData(data);
			lines[minTimeIndex].setTag(tag);
			lines[minTimeIndex].setTime(time);
			this.miss++; // Miss case.
			this.eviction++; // Eviction case.
		}
		this.sets[setIndex].setLines(lines);
		return returnValue;
	}
	
	public boolean store(int address, int processBytes , ArrayList<String> replacedData) { // Processing store operation for current cache.
		int setIndex = getSetIndex(address); // Getting set index.
		int tag = getTag(address); // Getting tag.
		int byteOffset = getByteOffset(address); // Getting byte offset.
		
		Line lines[] = sets[setIndex].getLines();
		for (int i = 0 ; i < lines.length ; i++ ) { // Looping through all lines of set.
			if (lines[i].getTag() == tag) { // Checking the tags are the same or not. 
				char[] data = lines[i].getData();
				for (int j = 0 ; j < processBytes ; j++) {
					// Overwriting data to corresponding place.
					data[2*(j + byteOffset)] = replacedData.get(j).charAt(0); 
					data[2*(j + byteOffset)+ 1] = replacedData.get(j).charAt(1);
				}
				lines[i].setData(data);
				this.hit++; // Hit case.
				return true;
			}
		}
		this.miss++; // Miss case.
		return false;
	}
	
	public int getSetIndex(int address) { // Gets set index by right shifting address with b bits and making an and operation.
		return ( address >> this.b ) & ((int)Math.pow(2, this.s - 1));
	}
	
	public int getTag(int address) { // Gets tag by right shifting address with b+s bits.
		return ( address >> (this.b + this.s));
	}
	
	public int getByteOffset(int adress) { // Gets byte offset by making an and operation.
		return ( adress & ((int)(Math.pow(2, b))) - 1);
	}
	
	// Getter and setter methods.
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public Set[] getSets() {
		return sets;
	}

	public void setSets(Set[] sets) {
		this.sets = sets;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getMiss() {
		return miss;
	}

	public void setMiss(int miss) {
		this.miss = miss;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getEviction() {
		return eviction;
	}

	public void setEviction(int eviction) {
		this.eviction = eviction;
	}
}
