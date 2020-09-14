import java.awt.Color;
import java.io.Serializable;

public class resource implements Serializable{
	int resourceid, amountpercel = 0, regenerate = 0;
	Color color;
	
	public int getamountpercel() {
		return amountpercel;
	}
	
	public void setamountpercel(int value) {
		amountpercel = value;
	}
	
	public int getregenerate() {
		return regenerate;
	}
	
	public void setregenerate(int value) {
		regenerate = value;
	}
	
	public int getresourceid() {
		return resourceid;
	}
	
	public int setresourceid(int resourcecounter) {
		resourcecounter++;
		resourceid = resourcecounter;
		return resourcecounter;
	}
	
	public Color getresourcecolor() {
		return color;
	}
	
	public void setresourcecolor() {
		int r = (int) (Math.random()*255);
		int g = (int) (Math.random()*255);
		int b = (int) (Math.random()*255);
		color = new Color(r, g, b);
	}
}
