
public class Line {
	private int b,tag,time,v;
	private char[] data;
	
	public Line(int b) {
		setB(b);
		setData(new char[(int)Math.pow(2, b + 1)]); // Setting empty char array for data.
	}
	
	// Getter and setter methods.
	public int getB() {
		return b;
	}

	public void setB(int B) {
		b = B;
	}
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}

	public char[] getData() {
		return data;
	}

	public void setData(char[] data) {
		this.data = data;
	}

	
}
