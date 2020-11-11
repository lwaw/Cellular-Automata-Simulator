import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class species implements Serializable{
	int speciesid, replicaterequirednumber = 0, minimumrequirednumber = 0;
	String speciesname;
	double backgrounddeathrate = 0, growthrate = 0, densitydependentdeath = 0, evolvingparameter = 0.5, resourcechance = 1.0;//evolving parameter is used as a trait for evolving feeding by predators on other species; resourcechance is evolution parameter for producing resource
	Color[] colorarray = {Color.BLACK,Color.RED,Color.BLUE,Color.GRAY,Color.YELLOW,Color.PINK,Color.ORANGE,Color.WHITE};//start with id 1 so no black is choosen
	Color color;
	ArrayList<Integer> resourcearraylist = new ArrayList<Integer>();
	ArrayList<Integer> resourceconsumptionlist = new ArrayList<Integer>();
	
	ArrayList<Integer> resourceproduceidarraylist = new ArrayList<Integer>();
	ArrayList<Integer> resourceproduceamountlist = new ArrayList<Integer>();
	
	public int getspeciesid() {
		return speciesid;
	}
	
	public int setspeciesid(int speciescounter) {
		speciescounter++;
		speciesid = speciescounter;
		return speciescounter;
	}
	
	public void setresource(int value, int consumption) {
		resourcearraylist.add(value);
		resourceconsumptionlist.add(consumption);
	}
	
	public void setresourceproduce(int value, int produceamount) {
		resourceproduceidarraylist.add(value);
		resourceproduceamountlist.add(produceamount);
	}
	
	public String getspeciesname() {
		return speciesname;
	}
	
	public void setspeciesname(String value) {
		speciesname = value;
	}
	
	public Double getspeciesevolvingparameter() {
		return evolvingparameter;
	}
	
	public void setspeciesevolvingparameter(Double value) {
		evolvingparameter = value;
	}
	
	public Double getspeciesresourcechance() {
		return resourcechance;
	}
	
	public void setspeciesresourcechance(Double value) {
		resourcechance = value;
	}
	
	public Color getspeciescolor() {
		return color;
	}
	
	public void setspeciescolor(int colorid) {
		int colorlength = colorarray.length - 1;
		if(colorid <= colorlength) {
			color = colorarray[colorid];
		}else {
			int r = (int) (Math.random()*255);
			int g = (int) (Math.random()*255);
			int b = (int) (Math.random()*255);
			color = new Color(r, g, b);
		}
	}
	
	public int getreplicaterequirednumber() {
		return replicaterequirednumber;
	}
	
	public void setreplicaterequirednumber(int value) {
		replicaterequirednumber = value;
	}
	
	public int getminimumrequirednumber() {
		return minimumrequirednumber;
	}
	
	public void setminimumrequirednumber(int value) {
		minimumrequirednumber = value;
	}
	
	public void setspeciesbackgroundbackgrounddeathrate(double value) {
		backgrounddeathrate = value;
	}
	
	public double getspeciesbackgroundbackgrounddeathrate() {
		return backgrounddeathrate;
	}
	
	public void setspeciesgrowthtrate(double value) {
		growthrate = value;
	}
	
	public double getspeciesgrowthrate() {
		return growthrate;
	}
	
	public void setspeciesdensitydependentdeath(double value) {
		densitydependentdeath = value;
	}
	
	public double getspeciesdensitydependentdeathe() {
		return densitydependentdeath;
	}
	
	public void update() {
		
	}
	

}

class grower extends species{	
	//replicate chance
	public double replicate(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable, ArrayList<Object> evolutionarraylist, int neighbourcell) {
		int ownspecies = 0;
		double replicaterate = 0, growthrateindividual = growthrate;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof grower) {
		        grower s = (grower) o;
		        
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }else if(o instanceof predator){
		    	predator s = (predator) o;
		    	
		    }
		}
		
		for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take growthrate
			if(o instanceof evolution) {
				evolution e = (evolution) o;
				int evolutionspecies = e.getevospecies();
				String parameter = e.getparameter();
				
				if(evolutionspecies == speciesid) {
					if("growthrate".equals(parameter)) {
						growthrateindividual = e.getparameterincell(neighbourcell);
					}
				}
			}
		}
	        
        if(replicaterequirednumber <= ownspecies) {//needed number of own neighbouring cells
        	replicaterate = growthrateindividual;
        }
        		
		return replicaterate;
	}
	
	//calculate death chance
	public double death(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable, ArrayList<Object> evolutionarraylist) {
		int ownspecies = 0;
		double deathrate = 0;
		double individualdesitydependentdeath = densitydependentdeath, individualbackgrounddeathrate = backgrounddeathrate;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof grower) {
		        grower s = (grower) o;
		        
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }else if(o instanceof predator){
		    	predator s = (predator) o;
		    	
		    }
		}
		
		for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take normal densitydependentdeath
			if(o instanceof evolution) {
				evolution e = (evolution) o;
				int evolutionspecies = e.getevospecies();
				String parameter = e.getparameter();
				
				if(evolutionspecies == speciesid) {
					if("densitydependentdeath".equals(parameter)) {
						individualdesitydependentdeath = e.getparameterincell(currcell);
					}
					if("backgrounddeathrate".equals(parameter)) {
						individualbackgrounddeathrate = e.getparameterincell(currcell);
					}
				}
			}
		}
		
		if (speciesvalue instanceof grower) {
	        grower s = (grower) speciesvalue;
	        deathrate = individualbackgrounddeathrate + ownspecies * individualdesitydependentdeath;
	        
	        //required number of neighbours to survive
	        if(ownspecies < minimumrequirednumber) {
	        	deathrate = 1;
	        }
	        
	        if(resourcearraylist.size() > 0) {//check if resource is still available
	        	for(Integer resourceid : resourcearraylist) {//resource ids
	        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
	        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
	        		
	    	        for(Object resourcepercell : resourceavailable) {//resource per cell
	    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
	    	        		int resourcerequired = resourceconsumptionlist.get(j);
	    	        		int resourceleft = (int) resourcepercell - resourcerequired;
	    	        		
	    	        		if(resourceleft < 0) {//no resource left
	    	        			deathrate = 1;
	    	        			resourceleft = 0;
	    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
	    	        		}else {
	    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
	    	        		}
	    	        	}
	    	        	i++;
	    	        }
	    	        j++;
	        	}
	        }
	        
	        if(resourceproduceidarraylist.size() > 0) {
	        	double randresourceproduction;
	        	double individualresourceproduction = resourcechance;
	        	for(Integer resourceid : resourceproduceidarraylist) {//resource ids
	        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
	        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
	        		
	    	        for(Object resourcepercell : resourceavailable) {//resource per cell
	    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
	    	        		individualresourceproduction = resourcechance;
	    	        		
	    	        		for (Object o : evolutionarraylist) {//check for evolution resource prduction parameter; if no evolution take normal densitydependentdeath
	    	        			if(o instanceof evolution) {
	    	        				evolution e = (evolution) o;
	    	        				int evolutionspecies = e.getevospecies();
	    	        				String parameter = e.getparameter();
	    	        				
	    	        				if(evolutionspecies == speciesid) {
	    	        					if( "resourceproduction".equals(parameter)) {
	    	        						//System.out.println("currcell" + currcell);
	    	        						individualresourceproduction = e.getparameterincell(currcell);
	    	        					}
	    	        				}
	    	        			}
	    	        		}
	    	        		
	    	        		randresourceproduction = Math.random();
	    	        		//System.out.println("randresourceproduction" + randresourceproduction + " " + individualresourceproduction);
	    	        		if(randresourceproduction <= individualresourceproduction) {
	    	        			//System.out.println("test");
		    	        		int resourceproduced = resourceproduceamountlist.get(j);
		    	        		int resourceleft = (int) resourcepercell + resourceproduced;
		    	        		
		    	        		if(resourceleft > 0) {//no resource left
		    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
		    	        			deathrate = deathrate + 0.001;//cost of producing resource
		    	        		}else {
		    	        			
		    	        		}
	    	        		}
	    	        	}
	    	        	i++;
	    	        }
	    	        j++;
	        	}
	        }
		}
		 return deathrate;
	}
}

class predator extends species{
	ArrayList<Integer> preyarraylist = new ArrayList<Integer>();
	//species prey;
	
	public void setprey(int value) {
		//prey = value;
		preyarraylist.add(value);
	}
	
	public ArrayList<Integer> getprey() {
		//return prey;
		return preyarraylist;
	}
	
	public double replicate(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable, ArrayList<Object> evolutionarraylist, int neighbourcell) {
		int ownspecies = 0;
		double replicaterate = 0, growthrateindividual = growthrate, evolvingparameterneighbour = 0.5000, evolvingparameterown = 0.5;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof grower) {
		        grower s = (grower) o;
		        
		    }else if(o instanceof predator){
		    	predator s = (predator) o;
		    	
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }
		}
		
		int checkevolutionparameter = 0;
		for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take growthrate
			if(o instanceof evolution) {
				evolution e = (evolution) o;
				int evolutionspecies = e.getevospecies();
				String parameter = e.getparameter();
				
				species preyspecies = cells[currcell].getspeciesvalue();
				int preyspeciesid = preyspecies.getspeciesid();
                                
				if(evolutionspecies == speciesid) {
					if("growthrate".equals(parameter)) {
						growthrateindividual = e.getparameterincell(neighbourcell);
					}else if("evolvingparameter".equals(parameter)) {
						checkevolutionparameter = 1;
						evolvingparameterneighbour = e.getparameterincell(neighbourcell);//parameter of predator
					}
				}else if (evolutionspecies == preyspeciesid) {//select evolution of prey
					if("evolvingparameter".equals(parameter)) {
						checkevolutionparameter = 1;
						evolvingparameterown = e.getparameterincell(currcell);
					}
				}
			}
		}
	        
		for(int preyid : preyarraylist) {//check if species: speciesvalue is surrounded by predator
			int speciesid = speciesvalue.getspeciesid();
	        if(replicaterequirednumber <= ownspecies && preyid == speciesid) {//needed number of own neighbouring cells
	        	replicaterate = growthrateindividual;
	        }
		}

		if(checkevolutionparameter == 1) {//if evoution on parameter value is added, check for difference between species
			double evolvingparameterdiff = Math.abs(evolvingparameterneighbour-evolvingparameterown);
			
			if(evolvingparameterdiff > 0.10) {//if difference too big then no replication
				replicaterate = 0;
			}else {//replicaterate is function of difference
				replicaterate = replicaterate * ((0.10 - evolvingparameterdiff) * 10);
			}
		}
		
		return replicaterate;
	}
	
	public double death(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable, ArrayList<Object> evolutionarraylist) {
		int ownspecies = 0;
		double deathrate = 0, individualdesitydependentdeath = densitydependentdeath, individualbackgrounddeathrate = backgrounddeathrate;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof grower) {
		        grower s = (grower) o;
		    }else if(o instanceof predator){
		    	predator s = (predator) o;
		    	
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }
		}
		
		for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take normal densitydependentdeath
			if(o instanceof evolution) {
				evolution e = (evolution) o;
				int evolutionspecies = e.getevospecies();
				String parameter = e.getparameter();
				
				if(evolutionspecies == speciesid) {
					if("densitydependentdeath".equals(parameter)) {
						individualdesitydependentdeath = e.getparameterincell(currcell);
					}
					if("backgrounddeathrate".equals(parameter)) {
						individualbackgrounddeathrate = e.getparameterincell(currcell);
					}
				}
			}
		}
		
		if (speciesvalue instanceof predator) {
	        predator s = (predator) speciesvalue;
	        deathrate = individualbackgrounddeathrate + ownspecies * individualdesitydependentdeath;
	        
	        if(ownspecies < minimumrequirednumber) {
	        	deathrate = 1;
	        }
		}
		
        if(resourcearraylist.size() > 0) {//check if resource is still available
        	for(Integer resourceid : resourcearraylist) {//resource ids
        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
        		
    	        for(Object resourcepercell : resourceavailable) {//resource per cell
    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
    	        		int resourcerequired = resourceconsumptionlist.get(j);
    	        		int resourceleft = (int) resourcepercell - resourcerequired;
    	        		
    	        		if(resourceleft < 0) {//no resource left
    	        			deathrate = 1;
    	        			resourceleft = 0;
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}else {
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}
    	        	}
    	        	i++;
    	        }
    	        j++;
        	}
        }
        
        if(resourceproduceidarraylist.size() > 0) {
        	double randresourceproduction;
        	double individualresourceproduction = resourcechance;
        	for(Integer resourceid : resourceproduceidarraylist) {//resource ids
        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
        		
    	        for(Object resourcepercell : resourceavailable) {//resource per cell
    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
    	        		individualresourceproduction = resourcechance;
    	        		
    	        		for (Object o : evolutionarraylist) {//check for evolution resource prduction parameter; if no evolution take normal densitydependentdeath
    	        			if(o instanceof evolution) {
    	        				evolution e = (evolution) o;
    	        				int evolutionspecies = e.getevospecies();
    	        				String parameter = e.getparameter();
    	        				
    	        				if(evolutionspecies == speciesid) {
    	        					if( "resourceproduction".equals(parameter)) {
    	        						individualresourceproduction = e.getparameterincell(currcell);
    	        					}
    	        				}
    	        			}
    	        		}
    	        		
    	        		randresourceproduction = Math.random();
    	        		if(randresourceproduction <= individualresourceproduction) {
	    	        		int resourceproduced = resourceproduceamountlist.get(j);
	    	        		int resourceleft = (int) resourcepercell + resourceproduced;
	    	        		
	    	        		if(resourceleft > 0) {//no resource left
	    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
	    	        			deathrate = deathrate + 0.001;//cost of producing resource
	    	        		}else {
	    	        			
	    	        		}
    	        		}
    	        	}
    	        	i++;
    	        }
    	        j++;
        	}
        }
		 return deathrate;
	}
}

class cooperator extends species{
	ArrayList<Integer> cooperatorrraylist = new ArrayList<Integer>();
	
	public void setcooperator(int value) {
		cooperatorrraylist.add(value);
	}
	
	public ArrayList<Integer> getcooperator() {
		return cooperatorrraylist;
	}
	
	public double replicate(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable, ArrayList<Object> evolutionarraylist, int neighbourcell, int[] neighbourcellsarray) {
		int ownspecies = 0;
		double replicaterate = 0, growthrateindividual = growthrate/*, evolvingparameterneighbour = 0.5000, evolvingparameterown = 0.5*/;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof grower) {
		        grower s = (grower) o;
		        
		    }else if(o instanceof cooperator){
		    	cooperator s = (cooperator) o;
		    	
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }
		}
		
		for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take growthrate
			if(o instanceof evolution) {
				evolution e = (evolution) o;
				int evolutionspecies = e.getevospecies();
				String parameter = e.getparameter();
				
				if(evolutionspecies == speciesid) {
					if("growthrate".equals(parameter)) {
						growthrateindividual = e.getparameterincell(neighbourcell);
					}
				}
			}
		}
		
		int checkevolutionparameter = 0;
		double evolvingparameterneighbour = 0.5, evolvingparameterown = 0.5;
		for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take growthrate
			if(o instanceof evolution) {
				evolution e = (evolution) o;
				int evolutionspecies = e.getevospecies();
				String parameter = e.getparameter();
				
				for(int cooperateid : cooperatorrraylist) {//go through needed species
					int i = 0;
					for (Object p : neighbourvaluessarraylist) {//check for evolution replication parameter; if no evolution take growthrate
						if(p != null) {
							species c = (species) p;
							
							int neighbourspeciesid = c.getspeciesid();
							
							if(evolutionspecies == speciesid) {//evolution belongs to own species
								if("evolvingparameter".equals(parameter)) {
									checkevolutionparameter = 1;
									evolvingparameterown = e.getparameterincell(neighbourcell);//parameter of predator
									}
							}else if (evolutionspecies == neighbourspeciesid) {//evolution belongs to neighbouring species
								if("evolvingparameter".equals(parameter)) {
									if(evolutionspecies == cooperateid) {//if neighbouringspecies cooperates with current species
										checkevolutionparameter = 1;
										evolvingparameterneighbour = e.getparameterincell(neighbourcellsarray[i]);
									}
								}
							}
							i++;
						}
					}
				}
			}
		}
	        
		for(int cooperateid : cooperatorrraylist) {//check if species: speciesvalue is cooperator
			for (Object o : neighbourvaluessarraylist) {//check for evolution replication parameter; if no evolution take growthrate
				if(o instanceof cooperator) {
					cooperator c = (cooperator) o;
					
					int neighbourspeciesid = c.getspeciesid();
			        if(replicaterequirednumber <= ownspecies && cooperateid == neighbourspeciesid) {//needed number of own neighbouring cells and check for cooperating neighbours
			        	replicaterate = growthrateindividual;
			        }else {
			        	replicaterate = 0;
			        }
				}else if(o instanceof grower) {
					grower c = (grower) o;
					
					int neighbourspeciesid = c.getspeciesid();
			        if(replicaterequirednumber <= ownspecies && cooperateid == neighbourspeciesid) {//needed number of own neighbouring cells and check for cooperating neighbours
			        	replicaterate = growthrateindividual;
			        }else {
			        	replicaterate = 0;
			        }
				}else if(o instanceof predator) {
					predator c = (predator) o;
					
					int neighbourspeciesid = c.getspeciesid();
			        if(replicaterequirednumber <= ownspecies && cooperateid == neighbourspeciesid) {//needed number of own neighbouring cells and check for cooperating neighbours
			        	replicaterate = growthrateindividual;
			        }else {
			        	replicaterate = 0;
			        }
				}else if(o instanceof gameoflife) {
					gameoflife c = (gameoflife) o;
					
					int neighbourspeciesid = c.getspeciesid();
			        if(replicaterequirednumber <= ownspecies && cooperateid == neighbourspeciesid) {//needed number of own neighbouring cells and check for cooperating neighbours
			        	replicaterate = growthrateindividual;
			        }else {
			        	replicaterate = 0;
			        }
				}
			}
		}

		if(checkevolutionparameter == 1) {//if evoution on parameter value is added, check for difference between species
			double evolvingparameterdiff = Math.abs(evolvingparameterneighbour-evolvingparameterown);
			
			if(evolvingparameterdiff > 0.10) {//if difference too big then no replication
				replicaterate = 0;
			}else {
				replicaterate = replicaterate * ((0.10 - evolvingparameterdiff) * 10);
			}
		}
        
		return replicaterate;
	}
	
	public double death(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable, ArrayList<Object> evolutionarraylist) {
		int ownspecies = 0;
		double deathrate = 0, individualdesitydependentdeath = densitydependentdeath, individualbackgrounddeathrate = backgrounddeathrate;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof grower) {
		        grower s = (grower) o;
		    }else if(o instanceof cooperator){
		    	cooperator s = (cooperator) o;
		    	
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }
		}
		
		for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take normal densitydependentdeath
			if(o instanceof evolution) {
				evolution e = (evolution) o;
				int evolutionspecies = e.getevospecies();
				String parameter = e.getparameter();
				
				if(evolutionspecies == speciesid) {
					if("densitydependentdeath".equals(parameter)) {
						individualdesitydependentdeath = e.getparameterincell(currcell);
					}
					if("backgrounddeathrate".equals(parameter)) {
						individualbackgrounddeathrate = e.getparameterincell(currcell);
					}
				}
			}
		}
		
		if (speciesvalue instanceof cooperator) {
	        cooperator s = (cooperator) speciesvalue;
	        deathrate = individualbackgrounddeathrate + ownspecies * individualdesitydependentdeath;
	        
	        if(ownspecies < minimumrequirednumber) {
	        	deathrate = 1;
	        }
		}
		
        if(resourcearraylist.size() > 0) {//check if resource is still available
        	for(Integer resourceid : resourcearraylist) {//resource ids
        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
        		
    	        for(Object resourcepercell : resourceavailable) {//resource per cell
    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
    	        		int resourcerequired = resourceconsumptionlist.get(j);
    	        		int resourceleft = (int) resourcepercell - resourcerequired;
    	        		
    	        		if(resourceleft < 0) {//no resource left
    	        			deathrate = 1;
    	        			resourceleft = 0;
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}else {
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}
    	        	}
    	        	i++;
    	        }
    	        j++;
        	}
        }
        
        if(resourceproduceidarraylist.size() > 0) {
        	double randresourceproduction;
        	double individualresourceproduction = resourcechance;
        	for(Integer resourceid : resourceproduceidarraylist) {//resource ids
        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
        		
    	        for(Object resourcepercell : resourceavailable) {//resource per cell
    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
    	        		individualresourceproduction = resourcechance;
    	        		
    	        		for (Object o : evolutionarraylist) {//check for evolution resource prduction parameter; if no evolution take normal densitydependentdeath
    	        			if(o instanceof evolution) {
    	        				evolution e = (evolution) o;
    	        				int evolutionspecies = e.getevospecies();
    	        				String parameter = e.getparameter();
    	        				
    	        				if(evolutionspecies == speciesid) {
    	        					if( "resourceproduction".equals(parameter)) {
    	        						individualresourceproduction = e.getparameterincell(currcell);
    	        					}
    	        				}
    	        			}
    	        		}
    	        		
    	        		randresourceproduction = Math.random();
    	        		if(randresourceproduction <= individualresourceproduction) {
	    	        		int resourceproduced = resourceproduceamountlist.get(j);
	    	        		int resourceleft = (int) resourcepercell + resourceproduced;
	    	        		
	    	        		if(resourceleft > 0) {//no resource left
	    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
	    	        			deathrate = deathrate + 0.001;//cost of producing resource
	    	        		}else {
	    	        			
	    	        		}
    	        		}
    	        	}
    	        	i++;
    	        }
    	        j++;
        	}
        }
		
		 return deathrate;
	}
}

class gameoflife extends species{	
	public double replicate(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable) {
		int ownspecies = 0;
		double replicaterate = 0;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof gameoflife) {
		    	gameoflife s = (gameoflife) o;
		        
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }
		}
	        
        if(ownspecies == 3) {//needed number of own neighbouring cells
        	replicaterate = 1;
        }
        
        if(resourcearraylist.size() > 0) {//check if resource is still available
        	for(Integer resourceid : resourcearraylist) {//resource ids
        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
        		
    	        for(Object resourcepercell : resourceavailable) {//resource per cell
    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
    	        		int resourcerequired = resourceconsumptionlist.get(j);
    	        		int resourceleft = (int) resourcepercell - resourcerequired;    	        		
    	        		if(resourceleft < 0) {//no resource left
    	        			replicaterate = 0;
    	        			resourceleft = 0;
    	        			
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}else {
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}
    	        	}
    	        	i++;
    	        }
    	        j++;
        	}
        }
        
        if(resourceproduceidarraylist.size() > 0) {
        	for(Integer resourceid : resourceproduceidarraylist) {//resource ids
        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
        		
    	        for(Object resourcepercell : resourceavailable) {//resource per cell
    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
    	        		int resourceproduced = resourceproduceamountlist.get(j);
    	        		int resourceleft = (int) resourcepercell + resourceproduced;
    	        		
    	        		if(resourceleft > 0) {//no resource left
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}else {
    	        			
    	        		}
    	        	}
    	        	i++;
    	        }
    	        j++;
        	}
        }
		
		return replicaterate;
	}
	
	public double death(ArrayList neighbourvaluessarraylist, cell cells[], int currcell, species speciesvalue, ArrayList resourceavailable) {
		int ownspecies = 0;
		double deathrate = 0;
		for (Object o : neighbourvaluessarraylist) {
		    if (o instanceof gameoflife) {
		    	gameoflife s = (gameoflife) o;
		        
		        if(s.getspeciesid() == speciesid) {//check for same species in neighbouring cells
		        	ownspecies++;
		        }
		    }
		}
		
		if (speciesvalue instanceof gameoflife) {
			if(ownspecies == 0 || ownspecies == 1 || ownspecies >= 4) {
				gameoflife s = (gameoflife) speciesvalue;
		        deathrate = 1;
			}
		}
		
        if(resourcearraylist.size() > 0) {//check if resource is still available
        	for(Integer resourceid : resourcearraylist) {//resource ids
        		resourceid--;//resourcarraylist starts from 1 and resourceavailable/i starts from 0
        		int i = 0, j = 0;//resource resourceavailable can be longer than resourceconsumptionlist
        		
    	        for(Object resourcepercell : resourceavailable) {//resource per cell
    	        	if(i == (int) resourceid) {//if id == at the same position as resource in arraylist
    	        		int resourcerequired = resourceconsumptionlist.get(j);
    	        		int resourceleft = (int) resourcepercell - resourcerequired;
    	        		
    	        		if(resourceleft < 0) {//no resource left
    	        			deathrate = 1;
    	        			resourceleft = 0;
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}else {
    	        			cells[currcell].updateresource(resourceid, resourceleft);//update resource
    	        		}
    	        	}
    	        	i++;
    	        }
    	        j++;
        	}
        }
		return deathrate;
	}
}

class virus extends species{	
	int incubationtime = 0;
	double lethality = 0;
	
	ArrayList<Integer> infectingarraylist = new ArrayList<Integer>();
	//species prey;
	
	public void setinfecting(int value) {
		//prey = value;
		infectingarraylist.add(value);
	}
	
	public ArrayList<Integer> getinfecting() {
		//return prey;
		return infectingarraylist;
	}
	
	public void setincubationtime(int value) {
		//prey = value;
		incubationtime = value;
	}
	
	public int getincubationtime() {
		//return prey;
		return incubationtime;
	}
	
	public void setlethality(double value) {
		//prey = value;
		lethality = value;
	}
	
	public double getlethality() {
		//return prey;
		return lethality;
	}
}
