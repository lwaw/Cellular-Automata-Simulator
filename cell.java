import java.io.Serializable;
import java.util.*;

public class cell implements Serializable{
	species speciesvalue;
	int xlocation, ylocation;
	ArrayList<Integer> resourceavailable = new ArrayList<Integer>();//list for species
	ArrayList<Object> virusincell = new ArrayList<Object>();//list for virusses
	ArrayList<Integer> virusincellincubationtime = new ArrayList<Integer>();//list for virusses incubation time
	
	public species getspeciesvalue() {
		return speciesvalue;
	}
	
	public void setxlocation(int value) {
		xlocation = value;
	}
	
	public void setylocation(int value) {
		ylocation = value;
	}
	
	public void setspeciesvalue(species value) {
		speciesvalue = value;
	}
	
	public void addvirusincell(species value) {
		virusincell.add(value);
	}
	
	public void removevirusincell() {
		virusincell.clear();
	}
	
	public ArrayList<Object> getvirusincell() {
		return virusincell;
	}
	
	public void addincubationtime(int value) {
		virusincellincubationtime.add(value);
	}
	
	public void removevincubationtime() {
		virusincellincubationtime.clear();
	}
	
	public void setresourceavailable(int value) {
		resourceavailable.add(value);
	}
	
	public int getselectedresource(int id) {
		int i = 0;
		int resourceavailableincell = 0;
		for(Integer resourceamount : resourceavailable) {
			if(i == id) {
				resourceavailableincell = resourceamount;
			}
			
			i++;
		}
		
		return resourceavailableincell;
	}
	
	//update resource in cell
	public void updateresource(int resourceid, int resourcevalue) {//sets new resource value to arraylist
		for(Integer resourceamount : resourceavailable) {
			resourceavailable.set(resourceid, resourcevalue);
		}
	}
	
	//regenerate resource in cell
	public void regenerateresource(ArrayList resourcearraylist) {//sets new resource value to arraylist
		int i = 0;
		for(Object item : resourcearraylist) {//go through all resources to get information.
		    if (item instanceof resource) {
		        resource r = (resource) item;
		    	
		        int amountpercell = r.getamountpercel();
		        int regenerate = r.getregenerate();
		        
		        int j = 0;
				for(Integer resourceamount : resourceavailable) {//go through all resources in cell
					if(i == j) {
						resourceamount = resourceamount + regenerate;
						
						if(resourceamount > amountpercell) {//max amount reached
							resourceamount = amountpercell;
						}
						
						resourceavailable.set(j, resourceamount);
					}
					j++;
				}
		    }
		    i++;
		}
	}
	
	//update cell
	public species updatecells(int gridsize, cell cells[], int currcell, ArrayList<Object> evolutionarraylist, boolean diffusion){//get species info of neighbours and calculate spread
		int gridsizeloc = gridsize;
		int[] neighbourcellsarray = new int[8];
		
		ArrayList<Object> neighbourvaluessarraylist = new ArrayList<Object>();
		
		//get values of neighbouringcells
		neighbourcellsarray = getneighbours(gridsizeloc, cells, currcell, xlocation, ylocation); //go find neighbouring cells
		
		for(int item : neighbourcellsarray) {//get values and put in array //species value is an species object
			neighbourvaluessarraylist.add(cells[item].getspeciesvalue());
		}
		
		
		if(virusincell.size() != 0) {//update virusses
			ArrayList<Object> virusincelltemp = new ArrayList<Object>();//to avoid adding and removing items while going through for loop
			for(Object v : virusincell) {
				virusincelltemp.add(v);
			}
			
			int i = 0;
			for(Object v : virusincelltemp) {
			//for(Iterator<Object> v = virusincell.iterator(); v.hasNext();) {
				if(v instanceof virus) {
					virus vir = (virus) v;
					setevolution(evolutionarraylist, currcell, currcell, vir);//mutate all viruses in cell
					
					int incubationtime = virusincellincubationtime.get(i);
					incubationtime--;
					if(incubationtime > 0) {//incubation time until virus starts spreading
						virusincellincubationtime.set(i, incubationtime);
						
						//also spread while cell stil in incubation
						double viruslethality = vir.getlethality();
						ArrayList<Integer> infectionlist = vir.getinfecting();
						double virusgrowthrate = vir.getspeciesgrowthrate();
						int virusincubationtime = vir.getincubationtime();
						int virusspecies = vir.getspeciesid();
						
						int c = 0;//cellnumber of neighbour
						for(Object neighbour : neighbourvaluessarraylist) {//check neighbors that can be infected
							if(neighbour instanceof species) {
								species neighbourspecies = (species) neighbour;
								int neighbourspeciesid = neighbourspecies.getspeciesid();
								
								for(int infectionid : infectionlist) {//go through species that can be infected
									if(neighbourspeciesid == infectionid) {//chance to infect
										ArrayList<Object> neighbourvirusses = cells[neighbourcellsarray[c]].getvirusincell();
										int q = 0;
										for(Object nv : neighbourvirusses) {//check if neighbour is already infected
											if(nv instanceof virus) {
												virus nvv = (virus) nv;
												if(nvv == vir) {//if neighbour has same as current virus then dont spread
													q++;
												}
											}
										}
										
										if(q == 0) {//not the same virus in neighbour
											double rand = Math.random();
											double evolvingparameter = 0.5, evolvingneighbourparameter = 0.5;
											
					    		        	if(evolutionarraylist.size() != 0) {//check for evolution in parameters if dead then set to 0.0
					    		        		for(Object evo : evolutionarraylist) {
					    		        			if(evo instanceof evolution) {
					    		        				evolution e = (evolution) evo;
					    		        				int evolutionspecies = e.getevospecies();
					    		        				
					    		        				
					    		        				if(evolutionspecies == virusspecies) {
					    		        					String parameter = e.getparameter();
					    		        					if(parameter == "growthrate") {
					    		        						virusgrowthrate = e.getparameterincell(currcell);
					    		        					}else if(parameter == "evolvingparameter") {
						    		        					evolvingparameter = e.getparameterincell(currcell);
						    		        				}
					    		        				}else if (evolutionspecies == cells[neighbourcellsarray[c]].getspeciesvalue().getspeciesid()) {//get neighbouring cell evolution parameter
					    		        					String parameter = e.getparameter();
					    		        					if(parameter == "evolvingparameter") {
					    		        						evolvingneighbourparameter = e.getparameterincell(neighbourcellsarray[c]);
						    		        				}
					    		        				}
					    		        			}
					    		        		}
					    		        	}
					    		        	
					    					double evolvingparameterdiff = Math.abs(evolvingneighbourparameter-evolvingparameter);//abs value
					    					
					    					if(evolvingparameterdiff > 0.10) {//if difference too big then no replication
					    						virusgrowthrate = 0;
					    					}else {
					    						virusgrowthrate = virusgrowthrate * ((0.10 - evolvingparameterdiff) * 10);
					    					}
					    		        	
											if(rand < virusgrowthrate) {//infect cell
												cells[neighbourcellsarray[c]].addvirusincell(vir);
												cells[neighbourcellsarray[c]].addincubationtime(virusincubationtime);
												setevolution(evolutionarraylist, neighbourcellsarray[c],currcell , vir);//currcel and neighbouring cell are switched because virus replicates from currcell instead of other way around
											}
										}
									}
								}
							}
							c++;
						}
						
    		        	if(evolutionarraylist.size() != 0) {//check for evolution in lethality
    		        		for(Object evo : evolutionarraylist) {
    		        			if(evo instanceof evolution) {
    		        				evolution e = (evolution) evo;
    		        				int evolutionspecies = e.getevospecies();
    		        				String parameter = e.getparameter();
    		        				
    		        				if(evolutionspecies == virusspecies) {
    		        					if(parameter == "lethality") {
    		        						viruslethality = e.getparameterincell(currcell);
    		        					}
    		        				}
    		        			}
    		        		}
    		        	}
					}else if(incubationtime <= 0) {//spread virus and cell has chance to die
						double viruslethality = vir.getlethality();
						ArrayList<Integer> infectionlist = vir.getinfecting();
						double virusgrowthrate = vir.getspeciesgrowthrate();
						int virusincubationtime = vir.getincubationtime();
						int virusspecies = vir.getspeciesid();
						
						int c = 0;//cellnumber of neighbour
						for(Object neighbour : neighbourvaluessarraylist) {//check neighbors that can be infected
							if(neighbour instanceof species) {
								species neighbourspecies = (species) neighbour;
								int neighbourspeciesid = neighbourspecies.getspeciesid();
								
								for(int infectionid : infectionlist) {//go through species that can be infected
									if(neighbourspeciesid == infectionid) {//chance to infect
										ArrayList<Object> neighbourvirusses = cells[neighbourcellsarray[c]].getvirusincell();
										int q = 0;
										for(Object nv : neighbourvirusses) {//check if neighbour is already infected
											if(nv instanceof virus) {
												virus nvv = (virus) nv;
												if(nvv == vir) {//if neighbour has same as current virus then dont spread
													q++;
												}
											}
										}
										
										if(q == 0) {//not the same virus in neighbour
											double rand = Math.random();
											double evolvingparameter = 0.5, evolvingneighbourparameter = 0.5;
											
					    		        	if(evolutionarraylist.size() != 0) {//check for evolution in parameters if dead then set to 0.0
					    		        		for(Object evo : evolutionarraylist) {
					    		        			if(evo instanceof evolution) {
					    		        				evolution e = (evolution) evo;
					    		        				int evolutionspecies = e.getevospecies();
					    		        				
					    		        				
					    		        				if(evolutionspecies == virusspecies) {
					    		        					String parameter = e.getparameter();
					    		        					if(parameter == "growthrate") {
					    		        						virusgrowthrate = e.getparameterincell(currcell);
					    		        					}else if(parameter == "evolvingparameter") {
						    		        					evolvingparameter = e.getparameterincell(currcell);
						    		        				}
					    		        				}else if (evolutionspecies == cells[neighbourcellsarray[c]].getspeciesvalue().getspeciesid()) {//get neighbouring cell evolution parameter
					    		        					String parameter = e.getparameter();
					    		        					if(parameter == "evolvingparameter") {
					    		        						evolvingneighbourparameter = e.getparameterincell(neighbourcellsarray[c]);
						    		        				}
					    		        				}
					    		        			}
					    		        		}
					    		        	}
					    		        	
					    					double evolvingparameterdiff = Math.abs(evolvingneighbourparameter-evolvingparameter);//abs value
					    					
					    					if(evolvingparameterdiff > 0.10) {//if difference too big then no replication
					    						virusgrowthrate = 0;
					    					}else {
					    						virusgrowthrate = virusgrowthrate * ((0.10 - evolvingparameterdiff) * 10);
					    					}
					    		        	
											if(rand < virusgrowthrate) {//infect cell
												cells[neighbourcellsarray[c]].addvirusincell(vir);
												cells[neighbourcellsarray[c]].addincubationtime(virusincubationtime);
												setevolution(evolutionarraylist, neighbourcellsarray[c],currcell , vir);//currcel and neighbouring cell are switched because virus replicates from currcell instead of other way around
											}
										}
									}
								}
							}
							c++;
						}
						
    		        	if(evolutionarraylist.size() != 0) {//check for evolution in lethality
    		        		for(Object evo : evolutionarraylist) {
    		        			if(evo instanceof evolution) {
    		        				evolution e = (evolution) evo;
    		        				int evolutionspecies = e.getevospecies();
    		        				String parameter = e.getparameter();
    		        				
    		        				if(evolutionspecies == virusspecies) {
    		        					if(parameter == "lethality") {
    		        						viruslethality = e.getparameterincell(currcell);
    		        					}
    		        				}
    		        			}
    		        		}
    		        	}
						
						double rand = Math.random();
						if(rand < viruslethality) {//cell dies because of virus
							removevirusincell();//for asynchronous updates
							removevincubationtime();
							
							return null;
						}else {//virus dies
							virusincell.remove(i);
							virusincellincubationtime.remove(i);
							
	    		        	if(evolutionarraylist.size() != 0) {//check for evolution in parameters if dead then set to 0.0
	    		        		for(Object evo : evolutionarraylist) {
	    		        			if(evo instanceof evolution) {
	    		        				evolution e = (evolution) evo;
	    		        				int evolutionspecies = e.getevospecies();
	    		        				String parameter = e.getparameter();
	    		        				
	    		        				if(evolutionspecies == virusspecies) {
	    		        					e.setparameterupdate(i, 0.0);
	    		        				}
	    		        			}
	    		        		}
	    		        	}
	    		        	i--;//to get good index
						}
					}
				}
				i++;
			}
		}
		
		if(speciesvalue != null) {//if cell is not empty calculate deathrate
			double deathrate = 0;
			double replicaterate = 0;
		    if (speciesvalue instanceof grower) {
		        grower s = (grower) speciesvalue;
		    	deathrate = s.death(neighbourvaluessarraylist, cells, currcell, speciesvalue, resourceavailable, evolutionarraylist);
		    	
		    	setevolution(evolutionarraylist, currcell, currcell, s);
		    }else if(speciesvalue instanceof predator){
		    	predator s = (predator) speciesvalue;
		    	deathrate = s.death(neighbourvaluessarraylist, cells, currcell, speciesvalue, resourceavailable, evolutionarraylist);
		    	
		    	setevolution(evolutionarraylist, currcell, currcell, s);
		    }else if(speciesvalue instanceof cooperator){
		    	cooperator s = (cooperator) speciesvalue;
		    	deathrate = s.death(neighbourvaluessarraylist, cells, currcell, speciesvalue, resourceavailable, evolutionarraylist);
		    	
		    	setevolution(evolutionarraylist, currcell, currcell, s);
		    }else if(speciesvalue instanceof gameoflife){
		    	gameoflife s = (gameoflife) speciesvalue;
		    	deathrate = s.death(neighbourvaluessarraylist, cells, currcell, speciesvalue, resourceavailable);
		    	
		    	setevolution(evolutionarraylist, currcell, currcell, s);
		    }
		    
		    double deathrand = Math.random();
	        if(deathrand < deathrate) {//cell is dead
	        	removevirusincell();//remove virus from cell
	        	removevincubationtime();
	        	
	        	return null;
	        }else {//still alive
	        	int i = 0;
				for (Object o : neighbourvaluessarraylist) {//for every neighbour check chance to get predated on
				    if (o instanceof predator) {
				    	predator s = (predator) o;
				    	replicaterate = s.replicate(neighbourvaluessarraylist, cells, currcell, speciesvalue, resourceavailable, evolutionarraylist, neighbourcellsarray[i]);
				    	
				    	double replicaterand = Math.random();
				    	if(replicaterand < replicaterate) {//cell is being predated on
				        	removevirusincell();//remove virus from cell
				        	removevincubationtime();
				        	
	    		        	if(evolutionarraylist.size() != 0) {//check for evolution in parameters if then set all to 0.0
	    		        		for(Object evo : evolutionarraylist) {
	    		        			if(evo instanceof evolution) {
	    		        				evolution e = (evolution) evo;
	    		        				int evolutionspecies = e.getevospecies();
	    		        				String parameter = e.getparameter();
	    		        				
	    		        				e.setparameterupdate(currcell, 0.0);
	    		        			}
	    		        		}
	    		        	}
	    		        	
	    		        	setevolution(evolutionarraylist, currcell, neighbourcellsarray[i], s);//set new evolutionparameter for predator
				        	
				    		return s;//cell has new value
				    	}
				    }
				    i++;
				}
				
	        	return cells[currcell].getspeciesvalue();
	        }
	        
		}else {//if cell is empty search for neighbours to replicate
			int i = 0;
			for (Object o : neighbourvaluessarraylist) {//for every neihbour check chance to replicate
			    if (o instanceof grower) {
			        grower s = (grower) o;
			        double replicaterate = s.replicate(neighbourvaluessarraylist, cells, currcell, speciesvalue, resourceavailable, evolutionarraylist, neighbourcellsarray[i]);
			        
			        double replicaterand = Math.random();
			        if(replicaterand < replicaterate) {//replicate
			        	setevolution(evolutionarraylist, currcell, neighbourcellsarray[i], s);
			        	
			        	return s;
			        }
			    }else if(o instanceof predator){
			    	predator s = (predator) o;//can not replicate in empty cell
			    }else if(o instanceof cooperator) {
			    	cooperator s = (cooperator) o;
			    	
			    	double replicaterate = s.replicate(neighbourvaluessarraylist, cells, currcell, s, resourceavailable, evolutionarraylist, neighbourcellsarray[i], neighbourcellsarray);
			        double replicaterand = Math.random();
			        if(replicaterand < replicaterate) {
			        	setevolution(evolutionarraylist, currcell, neighbourcellsarray[i], s);
			        	
			        	return s;
			        }
			    }else if(o instanceof gameoflife){
			    	gameoflife s = (gameoflife) o;
			    	
			    	double replicaterate = s.replicate(neighbourvaluessarraylist, cells, currcell, speciesvalue, resourceavailable);
			        double replicaterand = Math.random();
			        if(replicaterand < replicaterate) {//replicate
			        	return s;
			        }
			    }
			    if(i == 7) {//aan einde van neighboursarray dan blijft cell leeg
			    	if(diffusion == true) {//if diffusion true then pick random neighbour to replicate
			    		ArrayList<Object> inhabitedneighbour = new ArrayList<Object>();//list for species
			    		ArrayList<Integer> neighbourcells = new ArrayList<Integer>();//list for species
			    		
			    		int j = 0;
			    		for (Object q : neighbourvaluessarraylist) {
			    			if(q instanceof grower) {
			    				grower s = (grower) q;
			    				inhabitedneighbour.add(s);
			    				neighbourcells.add(neighbourcellsarray[j]);
			    			}else if(q instanceof predator) {
			    				predator s = (predator) q;
			    				inhabitedneighbour.add(s);
			    				neighbourcells.add(neighbourcellsarray[j]);
			    			}else if(q instanceof cooperator) {
			    				cooperator s = (cooperator) q;
			    				inhabitedneighbour.add(s);
			    				neighbourcells.add(neighbourcellsarray[j]);
			    			}else if(q instanceof gameoflife) {
			    				gameoflife s = (gameoflife) q;
			    				inhabitedneighbour.add(s);
			    				neighbourcells.add(neighbourcellsarray[j]);
			    			}
			    			j++;
			    		}
			    		int randnumb = (int) Math.random()*inhabitedneighbour.size();
			    		
			    		if(inhabitedneighbour.size() != 0) {//not empty cell
			    			if(inhabitedneighbour.get(randnumb) instanceof grower) {
			    				grower s = (grower) inhabitedneighbour.get(randnumb);
			    				setevolution(evolutionarraylist, currcell, neighbourcells.get(randnumb), s);
			    				return s;
			    			}else if(inhabitedneighbour.get(randnumb) instanceof predator) {
			    				predator s = (predator) inhabitedneighbour.get(randnumb);
			    				setevolution(evolutionarraylist, currcell, neighbourcells.get(randnumb), s);
			    				return s;
			    			}else if(inhabitedneighbour.get(randnumb) instanceof cooperator) {
			    				cooperator s = (cooperator) inhabitedneighbour.get(randnumb);
			    				setevolution(evolutionarraylist, currcell, neighbourcells.get(randnumb), s);
			    				return s;
			    			}else if(inhabitedneighbour.get(randnumb) instanceof gameoflife) {
			    				gameoflife s = (gameoflife) inhabitedneighbour.get(randnumb);
			    				setevolution(evolutionarraylist, currcell, neighbourcells.get(randnumb), s);
			    				return s;
			    			}
			    		}else {//no neighbours
			    			
			    		}
			    	}
			    	return null;//if diffusion is false
			    }
			    i++;
			}
		}
		return null;
	}
	
	//mutate evolution parameter if needed
	public void setevolution(ArrayList evolutionarraylist, int currcell, int neighbourcell, species s) {
    	if(evolutionarraylist.size() != 0) {//check for evolution in parameters mutate on reproducing
    		for(Object evo : evolutionarraylist) {
    			if(evo instanceof evolution) {
    				evolution e = (evolution) evo;
    				int evolutionspecies = e.getevospecies();
    				
    				if(evolutionspecies == s.getspeciesid()) {//mutate parameter
    					e.setmutatedparameter(currcell, neighbourcell);
    				}
    			}
    		}
    	}
	}
	
	//get neighbouring cells and return array
	public int[] getneighbours(int gridsize, cell cells[], int currcell, int xlocation, int ylocation){
		int nofneighbours = 8;
		int gridsizeloc = gridsize;
		int[] neighbourcellsarray = new int[nofneighbours]; //for every cell create neighboursarray with cells
		int placeholdercell;
		
		neighbourcellsarray[0] = gettopneighbour(gridsizeloc, currcell, xlocation, ylocation);
		neighbourcellsarray[1] = getbottomneighbour(gridsizeloc, currcell, xlocation, ylocation);
		neighbourcellsarray[2] = getleftneighbour(gridsizeloc, currcell, xlocation, ylocation);
		neighbourcellsarray[3] = getrightneighbour(gridsizeloc, currcell, xlocation, ylocation);
		
		//topleft
		placeholdercell = gettopneighbour(gridsizeloc, currcell, xlocation, ylocation);
		neighbourcellsarray[4] = getleftneighbour(gridsizeloc, placeholdercell, xlocation, ylocation);
		
		//topright
		placeholdercell = gettopneighbour(gridsizeloc, currcell, xlocation, ylocation);
		neighbourcellsarray[5] = getrightneighbour(gridsizeloc, placeholdercell, xlocation, ylocation);
		
		//bottomleft
		placeholdercell = getbottomneighbour(gridsizeloc, currcell, xlocation, ylocation);
		neighbourcellsarray[6] = getleftneighbour(gridsizeloc, placeholdercell, xlocation, ylocation);
		
		//bottomright
		placeholdercell = getbottomneighbour(gridsizeloc, currcell, xlocation, ylocation);
		neighbourcellsarray[7] = getrightneighbour(gridsizeloc, placeholdercell, xlocation, ylocation);
		
		return neighbourcellsarray;
	}
	
	//calculate all neighbour locations
	public int gettopneighbour(int gridsize, int currcell, int xlocation, int ylocation) {
		int neighbourvalue;
		
		ylocation = ylocation - 1;
		if(ylocation >= 0) {
			currcell = currcell - gridsize;
		}else {
			currcell = currcell + (gridsize * (gridsize - 1));
		}
		return currcell;
	}
	
	public int getbottomneighbour(int gridsize, int currcell, int xlocation, int ylocation) {
		int neighbourvalue;
		int ymaxvalue = gridsize - 1;
		
		ylocation = ylocation + 1;
		if(ylocation <= ymaxvalue) {
			currcell = currcell + gridsize;
		}else {
			currcell = currcell - (gridsize * (gridsize - 1));
		}
		return currcell;
	}
	
	public int getleftneighbour(int gridsize, int currcell, int xlocation, int ylocation) {
		int neighbourvalue;
		int xmaxvalue = gridsize - 1;
		
		xlocation = xlocation - 1;
		if(xlocation >= 0) {
			currcell = currcell - 1;
		}else {
			currcell = currcell + (gridsize - 1);
		}
		return currcell;
	}
	
	public int getrightneighbour(int gridsize, int currcell, int xlocation, int ylocation) {
		int neighbourvalue;
		int xmaxvalue = gridsize - 1;
		
		xlocation = xlocation + 1;
		if(xlocation <= xmaxvalue) {
			currcell = currcell + 1;
		}else {
			currcell = currcell - (gridsize - 1);
		}
		return currcell;
	}
}
