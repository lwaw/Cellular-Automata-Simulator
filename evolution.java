import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class evolution implements Serializable{
	int evolutionid, speciesid;
	double mutationnumber, mutationspeed = 0.005;
	String parameter;
	ArrayList<Double> parameterpercell = new ArrayList<Double>();
	ArrayList<Double> tempparameterpercell = new ArrayList<Double>();
	Color color;
	
	public int setevolutionid(int evolutioncounter) {
		evolutioncounter++;
		evolutionid = evolutioncounter;
		return evolutioncounter;
	}
	
	public int getevolutionid() {
		//return prey;
		return evolutionid;
	}
	
	public Color getevolutioncolor() {
		return color;
	}
	
	public void setevolutioncolor() {
		int r = (int) (Math.random()*255);
		int g = (int) (Math.random()*255);
		int b = (int) (Math.random()*255);
		color = new Color(r, g, b);
	}
	
	public void setevospecies(int value) {
		//prey = value;
		speciesid = value;
	}
	
	public int getevospecies() {
		//return prey;
		return speciesid;
	}
	
	public void setparameter(String value) {
		//prey = value;
		parameter = value;
	}
	
	public String getparameter() {
		//return prey;
		return parameter;
	}
	
	public void setmutationspeed(double value) {
		//return prey;
		mutationspeed = value;
	}
	
	public double getmutationspeed() {
		//return prey;
		return mutationspeed;
	}
	
	public double getmutationnumber() {
		//return prey;
		return mutationnumber;
	}
	
	public void setmutationnumber(double value) {
		//prey = value;
		mutationnumber = value;
	}
	
	public ArrayList<Double> getparameterpercell() {
		//return prey;
		return parameterpercell;
	}
	
	public void setparameterpercell(cell cells[]) {//add parameter values to a arraylist for every cell
		for(cell item : cells) {
			species cellspecies = item.getspeciesvalue();//get species in cell
			ArrayList<Object> viruspecies = item.getvirusincell();
			double initialparameterval = 0;
			if(cellspecies != null) {
				int cellspeciesid = cellspecies.getspeciesid();//get species id
				
				for(Object virus : viruspecies) {//check is species is a virus
					virus virus2 = (virus) virus;
					int virusid = virus2.getspeciesid();
					if(virusid == speciesid) {
						cellspecies = null;
						cellspecies = virus2;
						cellspeciesid = cellspecies.getspeciesid();
					}
				}
				
				if("growthrate".equals(parameter)) {
					initialparameterval = cellspecies.getspeciesgrowthrate();
				}else if("densitydependentdeath".equals(parameter)) {
					initialparameterval = cellspecies.getspeciesdensitydependentdeathe();
				}else if("backgrounddeathrate".equals(parameter)) {
					initialparameterval = cellspecies.getspeciesbackgroundbackgrounddeathrate();
				}else if("mutationrate".equals(parameter)) {
					initialparameterval = mutationspeed;
				}else if("evolvingparameter".equals(parameter)) {
					initialparameterval = cellspecies.getspeciesevolvingparameter();
				}else if("lethality".equals(parameter)) {
					if(cellspecies instanceof virus) {
						virus cellspecies2 = (virus) cellspecies;
						initialparameterval = cellspecies2.getlethality();
					}
				}else if("resourceproduction".equals(parameter)) {
					initialparameterval = cellspecies.getspeciesresourcechance();
				}
				
				if(cellspeciesid == speciesid) {//different species
					parameterpercell.add(initialparameterval);
				}else {
					parameterpercell.add(0.0);
				}
			}else {//empty cell
				parameterpercell.add(0.0);
			}
			

		}
	}
	
	public void setparameterupdate(int position, double value) {
		//System.out.println("setparameter" + value + " " + position);
		parameterpercell.set(position, value);
	}
	
	public double getparameterincell(int position) {
		double value = parameterpercell.get(position);
		//System.out.println("getparameter" + value + " " + position);
		return value;
	}
	
	public void setmutatedparameter(int position, int replicatefromposition) {//set new mutated parametervalue
		double parametervalue = parameterpercell.get(replicatefromposition);
		double randnumber = Math.random();
		
		if(mutationnumber <= randnumber) {
			if(randnumber <= 0.49) {//random - or +
				parametervalue = parametervalue + mutationspeed;
				if(parametervalue > 1) {
					parametervalue = 1;
				}
			}else if(randnumber >= 0.51){
				parametervalue = parametervalue - mutationspeed;
				if(parametervalue <= 0.001) {
					parametervalue = 0.001;
				}
			}else if(parametervalue > 0.49 && parametervalue < 0.51){
				parametervalue = parametervalue;
			}
			
			parameterpercell.set(position, parametervalue);
		}
	}
	
	public void resetparameterpercell() {//set new parametervalue
		for(int i = 0; i < parameterpercell.size(); i++) {
			parameterpercell.set(i, 0.0);
		}
	}
	
	public void setparameterinitial(species cellspecies, int cellnumber) {//set intial parametervalue for new cells
		double initialparameterval = 0;
		if("growthrate".equals(parameter)) {
			initialparameterval = cellspecies.getspeciesgrowthrate();
		}else if("densitydependentdeath".equals(parameter)) {
			initialparameterval = cellspecies.getspeciesdensitydependentdeathe();
		}else if("backgrounddeathrate".equals(parameter)) {
			initialparameterval = cellspecies.getspeciesbackgroundbackgrounddeathrate();
		}else if("mutationrate".equals(parameter)) {
			initialparameterval = mutationspeed;
		}else if("evolvingparameter".equals(parameter)) {
			initialparameterval = cellspecies.getspeciesevolvingparameter();
		}else if("lethality".equals(parameter)) {
			if(cellspecies instanceof virus) {
				virus cellspecies2 = (virus) cellspecies;
				initialparameterval = cellspecies2.getlethality();
			}
		}else if("resourceproduction".equals(parameter)) {
			initialparameterval = cellspecies.getspeciesresourcechance();
		}
		
		parameterpercell.set(cellnumber, initialparameterval);
	}
	
	public void changegridevolution(double value) {//set new parameter array
		tempparameterpercell.add(value);
	}
	
	public void changegridevolution2() {//set new parameterarray as new
		parameterpercell.clear();
		for(double parameter : tempparameterpercell) {
			parameterpercell.add(parameter);
		}
		tempparameterpercell.clear();
	}
}
