import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class resource implements Serializable{
	int resourceid, amountpercel = 0, regenerate = 0, diffusion = 0;
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
  
	public int getdiffusion() {
		return diffusion;
	}
	
	public void setdiffusion(int value) {
		diffusion = value;
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

class global_resource extends resource{	
    
}

class local_resource extends resource{	
  ArrayList<Object> cellsarraylist = new ArrayList<Object>();//list for source cells

	public void addremovesource(cell sourcecell) {
    if(cellsarraylist.contains(sourcecell)){
      cellsarraylist.remove(sourcecell);
    }else{
      cellsarraylist.add(sourcecell);
    }
	}
  
  public int issourceinlist(cell currcell){
    int inlist;
    if(cellsarraylist.contains(currcell)){
        inlist = 1;
    }else{
        inlist = 0;
    }
    
    return(inlist);
  }
}