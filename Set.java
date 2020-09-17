
public class Set {
	private int place,E;
	private Line[] lines;
	
	public Set(int place, int E, int b) {
		setPlace(place); // Setting place (index) of set. 
		setE(E);
		
		Line[] lines = new Line[E];
		
		for (int i = 0; i < E ; i++) // Creating lines.
			lines[i] = new Line(b);
		
		setLines(lines);
	}
	
	// Getter and setter methods.
	public int getE() {
		return E;
	}
	public void setE(int E) {
		this.E = E;
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public Line[] getLines() {
		return lines;
	}

	public void setLines(Line[] lines) {
		this.lines = lines;
	}
}
