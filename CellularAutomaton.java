import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Random; 

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.MaskFormatter;

public class CellularAutomaton implements Serializable{
	JFrame frame;
	JLabel labelstepcounter;
	int gridsize = 100;
	int totalgrid = gridsize * gridsize;
	int screensize = 850;
	int speciescounter = 0, resourcecounter = 0, evolutioncounter = 0;//count total number of species
	int stepcounter = 0;
	int sizesquares;
	int shownewspeciesform = 0;
	int selectedcell1 = 0, selectedcell2 = 0;
	boolean boolrun = false;
	boolean onesteprun = false;
	boolean onlypaint = false;
	boolean synchronous = true;
	boolean diffusion = false;
	JFormattedTextField growthratefield, densitydependentdeathfield, backgrounddeathratefield, replicaterequirednumberfield, minimumrequirednumberfield,amountnumberfield,regeneratenumberfield, diffusionnumberfield, mutationnumberfield, mutationspeedlabelnumberfield, incubationnumberfield, lethalityfield, diffusionparameternumberfield;
	JTextField preyfield, resourcefield, resourceproducefield, evospeciesfield, cooperatefield, gridsizefield, infectfield;
	String selection, selection2, selection3;
	JPanel panelsouth, panelnorth, panelsouth_east, panelwest, panelwestcontainer,panelsouthcontainer;
	JButton runbutton, onestepbutton, newspeciesbutton, resetbutton, resetgridbutton, synchronousbutton, diffusionbutton, distributerbutton, savebutton, loadbutton, gridsizebutton, infobutton,changecolorbutton;
	cell[] cells = new cell[totalgrid];
	ArrayList<Object> speciesarraylist = new ArrayList<Object>();//list for species
	ArrayList<Object> resourcearraylist = new ArrayList<Object>();//list for species
	ArrayList<Object> evolutionarraylist = new ArrayList<Object>();//list for species
	String[] listoptions = {"grower","predator","cooperator","gameoflife","virus","resource","evolution"};//add new species form
	String[] listoptions2 = {"growthrate","densitydependentdeath","backgrounddeathrate","mutationrate","evolvingparameter","lethality (virus)","resourceproduction"};//add new species form
	String[] listoptions3 = {"global resource","local resource"};//add new resource form
  JPanel[] panelarray, rpanelarray, epanelarray;
	species selectedspecies = null;
	resource selectedresource = null;
	evolution selectedevolution = null;
  String lastselectedobject = null;
	String draw = "species";
	
	public static void main (String[] args) {
		CellularAutomaton CellularAutomaton1 = new CellularAutomaton();
		CellularAutomaton1.startUp();
	}
	
	//setup initial conditions
	//build grid and create cell object for every cell
	public void startUp() {
		int x = 0, y = 0;
		
		for(int i = 0; i < totalgrid; i++) {			
			cells[i] = new cell();
			cells[i].setxlocation(x);
			cells[i].setylocation(y);
			
			x++;
			
			if(x == (gridsize)) {//van 0 tot 99 op een rij
				x = 0;
				y++;
			}
		}
		
		buildGui();
	}
	
	
    //class to update each cell; continuously goes through each cell
    class UpdateTask implements Runnable{
    	ArrayList<Object> updatespeciesarraylist = new ArrayList<Object>();
    	public void run() {
    		//boolrun runs continuously; onesteprun only runs once
    		while(boolrun == true || onesteprun == true) {
	    		int i = 0;
          Random r = new Random();
          int xlocation, ylocation, getcellnumber;
          ArrayList<Integer> numberofcellslist = new ArrayList<Integer>();
          ArrayList<cell> randomnumberofcellslist = new ArrayList<cell>();

          //create list of random cell order and update all cells in this order
          int x = 0;
          int numberofcells = cells.length;

          //create list with cell numbers
          while(x < numberofcells){
            numberofcellslist.add(x);
            x++;
          }

          x = 0;
          while(numberofcellslist.size() > 0){
            int randomNumberindex = r.nextInt(numberofcellslist.size()); 
            int randomNumber = numberofcellslist.get(randomNumberindex);

            randomnumberofcellslist.add(cells[randomNumber]);
            numberofcellslist.remove(randomNumberindex);

            x++;
          }
          
          if(synchronous == false){
            x = 0;
            for(cell y : randomnumberofcellslist){
              xlocation = y.getxlocation();
              ylocation = y.getylocation();
              getcellnumber = ( gridsize * ylocation ) + xlocation;

              species s = y.updatecells(gridsize, cells, getcellnumber, evolutionarraylist, diffusion);//update cells user cells & i array to find neighbours
              y.setspeciesvalue(s);

              y.regenerateresource(resourcearraylist, gridsize, cells, getcellnumber, xlocation, ylocation);//regenerate resources asynchronous
              
              if(s == null){
                y.removevincubationtime();//if not remove again virusses could spread while cell is already dead in synchronous updates
	    			   	y.removevirusincell();
                
	    		     	if(evolutionarraylist.size() != 0) {//check for evolution in parameters if dead then set to 0.0
	    		     		for(Object evo : evolutionarraylist) {
	    		     			if(evo instanceof evolution) {
	    		     				evolution e = (evolution) evo;
	    		     				int evolutionspecies = e.getevospecies();
	    		     				String parameter = e.getparameter();
	    		        				
	    		     				e.setparameterupdate(getcellnumber, 0.0);
	    		     			}
	    		     		}
	    		     	}
              }
              
              x++;
            }
          }else{
            //check for updates in each cell; synchronous update draws all changes at the end of each cycle once while asynchronous draws on the go
            for(cell item : randomnumberofcellslist) {
              xlocation = item.getxlocation();
              ylocation = item.getylocation();
              getcellnumber = ( gridsize * ylocation ) + xlocation;
                
              species s = item.updatecells(gridsize, cells, getcellnumber, evolutionarraylist, diffusion);//update cells user cells & i array to find neighbours
              updatespeciesarraylist.add(s);

              i++;
            }
          }
	    		
	    		//after all cells are updated draw new updated values for synchronous updating
	    		i = 0;
	    		if(synchronous == true) {
	    			for(Object o : updatespeciesarraylist){
              xlocation = randomnumberofcellslist.get(i).getxlocation();
              ylocation = randomnumberofcellslist.get(i).getylocation();
              getcellnumber = ( gridsize * ylocation ) + xlocation;
                
	    			  if (o instanceof grower) {
	    			      grower s = (grower) o;
	    			      randomnumberofcellslist.get(i).setspeciesvalue(s);
	    			  }else if(o instanceof predator){
	    			  	predator s = (predator) o;
	    			  	randomnumberofcellslist.get(i).setspeciesvalue(s);
	    			  }else if(o instanceof cooperator){
	    			  	cooperator s = (cooperator) o;
	    			   	randomnumberofcellslist.get(i).setspeciesvalue(s);
	    			  }else if(o instanceof gameoflife){
	    			   	gameoflife s = (gameoflife) o;
	    			   	randomnumberofcellslist.get(i).setspeciesvalue(s);
	    			  }else if(o == null) {
	    			   	randomnumberofcellslist.get(i).setspeciesvalue(null);
	    			   	
	    			   	randomnumberofcellslist.get(i).removevincubationtime();//if not remove again virusses could spread while cell is already dead in synchronous updates
	    			   	randomnumberofcellslist.get(i).removevirusincell();
	    			    	
	    		     	if(evolutionarraylist.size() != 0) {//check for evolution in parameters if dead then set to 0.0
	    		     		for(Object evo : evolutionarraylist) {
	    		     			if(evo instanceof evolution) {
	    		     				evolution e = (evolution) evo;
	    		     				int evolutionspecies = e.getevospecies();
	    		     				String parameter = e.getparameter();
	    		        				
	    		     				e.setparameterupdate(getcellnumber, 0.0);
	    		     			}
	    		     		}
	    		     	}
	    			  }
	    				i++;
	    			}
	    			updatespeciesarraylist.clear();
	    		}
	    		
	    		//regenerate resource synchronous
	    		if(synchronous == true) {
            i = 0;
	    			for(cell item : randomnumberofcellslist) {//always update resource in random order
              xlocation = item.getxlocation();
              ylocation = item.getylocation();
              getcellnumber = ( gridsize * ylocation ) + xlocation;
              
	    				item.regenerateresource(resourcearraylist, gridsize, cells, getcellnumber, xlocation, ylocation);
	    			  i++;
            }
	    		}
	    		stepcounter++;
	    		
	    		String stringlabel = Integer.toString(stepcounter);
	    		labelstepcounter.setText("Step: " + stringlabel);
	    		
	    		//sleep to give other thread time to update
	    		try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    		
	    		onesteprun = false;
    		}
    	}
    }
    
    //draw cells
    class UpdatePaint implements Runnable{
    	public void run() {
    		while(boolrun == true || onesteprun == true || onlypaint == true) {
    			frame.repaint();
    			
    			//sleep to give other thread time to update
        		try {
    				Thread.sleep(50);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        		
        		onesteprun = false;
        		onlypaint = false;
    		}
    	}
    }
	
    //create two thread to do the update task and draw task simultaneously
	  public void runhelper() throws InterruptedException {	
		//new thread to update cells
		if(onlypaint == false) {//not when only painting
	        Thread updatecells = new Thread(new UpdateTask());
	        updatecells.start();
		}
        
        //new thread to update painting
        Thread updatepaint = new Thread(new UpdatePaint());
        updatepaint.start();
	}
	
	//build graphic user interface; consists of jpanels and containers which can contain forms
	public void buildGui() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panelwest = new JPanel();
		panelwest.setBackground(Color.BLACK);
		
		panelwestcontainer = new JPanel();
		panelwestcontainer.setBackground(Color.BLACK);
		
		panelsouth = new JPanel();
		panelsouth.setBackground(Color.BLACK);
        
		panelsouthcontainer = new JPanel();
		panelsouthcontainer.setBackground(Color.BLACK);
		
		panelnorth = new JPanel();
		panelnorth.setBackground(Color.BLACK);
		
		String stringlabel = Integer.toString(stepcounter);
		labelstepcounter = new JLabel("Step: " + stringlabel);
		labelstepcounter.setForeground(Color.WHITE);
		panelnorth.add(labelstepcounter);
		
		runbutton = new JButton("run");
		runbutton.addActionListener(new runlistener());
		panelsouth.add(runbutton);
		
		onestepbutton = new JButton("run 1 step");
		onestepbutton.addActionListener(new onesteplistener());
		panelsouth.add(onestepbutton);
		
		newspeciesbutton = new JButton("add a new object");
		newspeciesbutton.addActionListener(new newspecieslistener());
		panelsouth.add(newspeciesbutton);
		
		synchronousbutton = new JButton("update asynchronous");
		synchronousbutton.addActionListener(new synchronouslistener());
		panelsouth.add(synchronousbutton);
		
		diffusionbutton = new JButton("turn diffusion on");
		diffusionbutton.addActionListener(new diffusionlistener());
		panelsouth.add(diffusionbutton);
		
		resetbutton = new JButton("reset");
		resetbutton.addActionListener(new resetlistener());
		panelsouth.add(resetbutton);
		
		resetgridbutton = new JButton("reset grid");
		resetgridbutton.addActionListener(new resetgridlistener());
		panelsouth.add(resetgridbutton);
		
		gridsizebutton = new JButton("change grid size");
		gridsizebutton.addActionListener(new gridsizelistener());
		panelsouth.add(gridsizebutton);
		
		
		distributerbutton = new JButton("distribute random");
		distributerbutton.addActionListener(new distributerlistener());
		panelsouth.add(distributerbutton);
        
		changecolorbutton = new JButton("change color");
		changecolorbutton.addActionListener(new changecolorlistener());
		panelsouth.add(changecolorbutton);
		
		savebutton = new JButton("save");
		savebutton.addActionListener(new savelistener());
		panelsouth.add(savebutton);
		
		loadbutton = new JButton("load save file");
		loadbutton.addActionListener(new loadlistener());
		panelsouth.add(loadbutton);
		
		infobutton = new JButton("info");
		infobutton.addActionListener(new infolistener());
		panelsouth.add(infobutton);
        
        //order panelsouth
        panelsouth.setLayout(new BoxLayout(panelsouth, BoxLayout.X_AXIS));
        
        panelsouthcontainer.setLayout(new BoxLayout(panelsouthcontainer, BoxLayout.Y_AXIS));
        panelsouthcontainer.add(panelsouth);
        frame.add(BorderLayout.SOUTH, panelsouthcontainer);
        
        //make scrollbar if panel is too wide
        JScrollPane scroller4 = new JScrollPane(panelsouth);
        scroller4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroller4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelsouthcontainer.add(scroller4);
        
		//add mouselistener to let users click on grid
		MyDrawPanel drawpanel = new MyDrawPanel();	
		drawpanel.addMouseListener(new Mouse());
		
		frame.getContentPane().add(BorderLayout.CENTER, drawpanel);
        frame.getContentPane().add(BorderLayout.SOUTH, panelsouthcontainer);
		
		frame.getContentPane().add(BorderLayout.NORTH, panelnorth);
		frame.setSize(screensize, screensize);
		frame.setVisible(true);
		
	}
	
	public void buildGuipanelsouth_east() {//show species info
		
	}
    
	//build graphic user interface for panelwest; panel is used to show species info; this panel is clickable to let users select a species
	public void buildGuipanelwest() {
		int arraysize = speciesarraylist.size();//for species
		panelarray = new JPanel[arraysize];
		
		int rarraysize = resourcearraylist.size();//for resources
		rpanelarray = new JPanel[rarraysize];
		
		int earraysize = evolutionarraylist.size();//for resources
		epanelarray = new JPanel[earraysize];
		
		if(arraysize != 0 || rarraysize != 0) {
			int speciesid, resourceid, resourceregenerate, resourcediffusion, evolutionspecies, incubationtime;
			double speciesgrowthrate, speciesdensitydependentdeathe, speciesbackgroundbackgrounddeathrate, evolutionmutation, evolutionmutationspeed, lethality, resourcediffusionparameter;
			String evolutionparameter;
			Color speciescolor, resourcecolor, evolutioncolor;
			int labelarraysize = arraysize*5;//number of labels per species
			int rlabelarraysize = rarraysize*5;//number of labels per resource
			int elabelarraysize = earraysize*4;//number of labels per evolution
				
			//reset panel to redraw
			if(arraysize > 1) {
				panelwestcontainer.removeAll();
			}
			
			//ordering things nicely
			panelwest.setLayout(new BoxLayout(panelwest, BoxLayout.Y_AXIS));
			
			panelwestcontainer.setLayout(new BoxLayout(panelwestcontainer, BoxLayout.X_AXIS));
			panelwestcontainer.add(panelwest);
			frame.add(BorderLayout.WEST, panelwestcontainer);
			
			//make scrollbar if panel is too high
			JScrollPane scroller3 = new JScrollPane(panelwest);
			scroller3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroller3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			panelwestcontainer.add(scroller3);
			
			//show evloution objects info
			int i = 0, j = 0;
			for(Object e : evolutionarraylist) {
				if(e instanceof evolution) {
					evolution s = (evolution) e;
					
					JLabel[] elabelarray = new JLabel[elabelarraysize];
					
			        epanelarray[j] = new JPanel();//create new panel for each species
			        epanelarray[j].setLayout(new BoxLayout(epanelarray[j], BoxLayout.Y_AXIS));
			        epanelarray[j].setBackground(Color.BLACK);
			        epanelarray[j].addMouseListener(new Mouse());//select label
			        
			        elabelarray[i] = new JLabel();
			        evolutioncolor = s.getevolutioncolor();
			        evolutionparameter = s.getparameter();
			        elabelarray[i].setText("type: evolution" + ", " + evolutionparameter);
			        elabelarray[i].setForeground(evolutioncolor);
			        epanelarray[j].add(elabelarray[i]);
			        i++;
			        
			        elabelarray[i] = new JLabel();
			        evolutionspecies = s.getevospecies();
			        elabelarray[i].setText("speciesid: " + evolutionspecies);
			        elabelarray[i].setForeground(evolutioncolor);
			        epanelarray[j].add(elabelarray[i]);
			        i++;
			        
			        elabelarray[i] = new JLabel();
			        evolutionmutation = s.getmutationnumber();
			        elabelarray[i].setText("mutationchance: " + evolutionmutation);
			        elabelarray[i].setForeground(evolutioncolor);
			        epanelarray[j].add(elabelarray[i]);
			        i++;
			        
			        elabelarray[i] = new JLabel();
			        evolutionmutationspeed = s.getmutationspeed();
			        elabelarray[i].setText("mutationspeed: " + evolutionmutationspeed);
			        elabelarray[i].setForeground(evolutioncolor);
			        epanelarray[j].add(elabelarray[i]);
			        i++;
			        
			        j++;
				}
			}
			
			//show resource info
			i = 0; j = 0;
			for(Object r : resourcearraylist) {
				if (r instanceof resource) {
					resource s = (resource) r;
					JLabel[] rlabelarray = new JLabel[rlabelarraysize];
					
			        rpanelarray[j] = new JPanel();//create new panel for each species
			        rpanelarray[j].setLayout(new BoxLayout(rpanelarray[j], BoxLayout.Y_AXIS));
			        rpanelarray[j].setBackground(Color.BLACK);
			        rpanelarray[j].addMouseListener(new Mouse());//select label
			        
			        rlabelarray[i] = new JLabel();
			        resourcecolor = s.getresourcecolor();
			        rlabelarray[i].setText("type: resource");
			        rlabelarray[i].setForeground(resourcecolor);
			        rpanelarray[j].add(rlabelarray[i]);
			        i++;
			        
			        rlabelarray[i] = new JLabel();
			        resourceid = s.getresourceid();
			        rlabelarray[i].setText("id: " + resourceid);
			        rlabelarray[i].setForeground(resourcecolor);
			        rpanelarray[j].add(rlabelarray[i]);
			        i++;
			        
			        rlabelarray[i] = new JLabel();
			        resourceregenerate = s.getregenerate();
			        rlabelarray[i].setText("regenerate: " + resourceregenerate);
			        rlabelarray[i].setForeground(resourcecolor);
			        rpanelarray[j].add(rlabelarray[i]);
			        i++;
              
			        rlabelarray[i] = new JLabel();
			        resourcediffusion = s.getdiffusion();
              if(resourcediffusion == 1){
                  rlabelarray[i].setText("diffusion: TRUE");
              }else{
                  rlabelarray[i].setText("diffusion: FALSE");
              }
			        rlabelarray[i].setForeground(resourcecolor);
			        rpanelarray[j].add(rlabelarray[i]);
			        i++;
              
			        rlabelarray[i] = new JLabel();
			        resourcediffusionparameter = s.getdiffusionparameter();
			        rlabelarray[i].setText("diffusion parameter: " + resourcediffusionparameter);
			        rlabelarray[i].setForeground(resourcecolor);
			        rpanelarray[j].add(rlabelarray[i]);
			        i++;
			        
			        j++;
				}
			}
			
			//show species info
			i = 0; j = 0;
			for(Object o : speciesarraylist){
			    if (o instanceof grower) {
			        grower s = (grower) o;
			        JLabel[] labelarray = new JLabel[labelarraysize];
			        
			        panelarray[j] = new JPanel();//create new panel for each species
			        panelarray[j].setLayout(new BoxLayout(panelarray[j], BoxLayout.Y_AXIS));
			        panelarray[j].setBackground(Color.BLACK);
			        panelarray[j].addMouseListener(new Mouse());//select label
			        
			        labelarray[i] = new JLabel();
			        speciescolor = s.getspeciescolor();
			        labelarray[i].setText("type: grower");
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        
			        labelarray[i] = new JLabel();
			        speciesid = s.getspeciesid();
			        labelarray[i].setText("id: " + speciesid);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesgrowthrate = s.getspeciesgrowthrate();
			        labelarray[i].setText("growth rate: " + speciesgrowthrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesdensitydependentdeathe = s.getspeciesdensitydependentdeathe();
			        labelarray[i].setText("density dependent deathe: " + speciesdensitydependentdeathe);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesbackgroundbackgrounddeathrate = s.getspeciesbackgroundbackgrounddeathrate();
			        labelarray[i].setText("background deathrate: " + speciesbackgroundbackgrounddeathrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        j++;
			    }else if(o instanceof predator){
			    	predator s = (predator) o;
			    	labelarraysize = labelarraysize + 1;
			    	JLabel[] labelarray = new JLabel[labelarraysize];
			    	
			        panelarray[j] = new JPanel();//create new panel for each species
			        panelarray[j].setLayout(new BoxLayout(panelarray[j], BoxLayout.Y_AXIS));
			        panelarray[j].setBackground(Color.BLACK);
			        panelarray[j].addMouseListener(new Mouse());//select label
			        
			        labelarray[i] = new JLabel();
			        speciescolor = s.getspeciescolor();
			        labelarray[i].setText("type: predator");
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        
			        labelarray[i] = new JLabel();
			        speciesid = s.getspeciesid();
			        labelarray[i].setText("id: " + speciesid);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesgrowthrate = s.getspeciesgrowthrate();
			        labelarray[i].setText("growth rate: " + speciesgrowthrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesdensitydependentdeathe = s.getspeciesdensitydependentdeathe();
			        labelarray[i].setText("density dependent deathe: " + speciesdensitydependentdeathe);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesbackgroundbackgrounddeathrate = s.getspeciesbackgroundbackgrounddeathrate();
			        labelarray[i].setText("background deathrate: " + speciesbackgroundbackgrounddeathrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        //create string containign all prey species numbers
			        labelarray[i] = new JLabel();
			        ArrayList<Integer> prey = s.getprey();
			        StringBuffer sb = new StringBuffer();
			        for(int preyid : prey) {
			        	sb.append(preyid);
			        	sb.append(", ");
			        }
			        
			        labelarray[i].setText("id of prey: " + sb);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        j++;
			    }else if(o instanceof cooperator){
			    	cooperator s = (cooperator) o;
			    	labelarraysize = labelarraysize + 1;
			    	JLabel[] labelarray = new JLabel[labelarraysize];
			    	
			        panelarray[j] = new JPanel();//create new panel for each species
			        panelarray[j].setLayout(new BoxLayout(panelarray[j], BoxLayout.Y_AXIS));
			        panelarray[j].setBackground(Color.BLACK);
			        panelarray[j].addMouseListener(new Mouse());//select label
			        
			        labelarray[i] = new JLabel();
			        speciescolor = s.getspeciescolor();
			        labelarray[i].setText("type: cooperator");
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesid = s.getspeciesid();
			        labelarray[i].setText("id: " + speciesid);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesgrowthrate = s.getspeciesgrowthrate();
			        labelarray[i].setText("growth rate: " + speciesgrowthrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesdensitydependentdeathe = s.getspeciesdensitydependentdeathe();
			        labelarray[i].setText("density dependent deathe: " + speciesdensitydependentdeathe);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesbackgroundbackgrounddeathrate = s.getspeciesbackgroundbackgrounddeathrate();
			        labelarray[i].setText("background deathrate: " + speciesbackgroundbackgrounddeathrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        //built string with all cooperator species
			        labelarray[i] = new JLabel();
			        ArrayList<Integer> cooperator = s.getcooperator();
			        StringBuffer sb = new StringBuffer();
			        for(int cooperatorid : cooperator) {
			        	sb.append(cooperatorid);
			        	sb.append(", ");
			        }
			        
			        labelarray[i].setText("id of cooperator: " + sb);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        j++;
			    }else if(o instanceof gameoflife) {
			    	gameoflife s = (gameoflife) o;
			        JLabel[] labelarray = new JLabel[labelarraysize];
			        
			        panelarray[j] = new JPanel();//create new panel for each species
			        panelarray[j].setLayout(new BoxLayout(panelarray[j], BoxLayout.Y_AXIS));
			        panelarray[j].setBackground(Color.BLACK);
			        panelarray[j].addMouseListener(new Mouse());//select label
			        
			        labelarray[i] = new JLabel();
			        speciescolor = s.getspeciescolor();
			        labelarray[i].setText("type: gameoflife");
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesid = s.getspeciesid();
			        labelarray[i].setText("id: " + speciesid);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesgrowthrate = s.getspeciesgrowthrate();
			        labelarray[i].setText("growth rate: " + speciesgrowthrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesdensitydependentdeathe = s.getspeciesdensitydependentdeathe();
			        labelarray[i].setText("density dependent deathe: " + speciesdensitydependentdeathe);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesbackgroundbackgrounddeathrate = s.getspeciesbackgroundbackgrounddeathrate();
			        labelarray[i].setText("background deathrate: " + speciesbackgroundbackgrounddeathrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        j++;
			    }else if(o instanceof virus){
			    	virus s = (virus) o;
			    	labelarraysize = labelarraysize + 3;
			    	JLabel[] labelarray = new JLabel[labelarraysize];
			    	
			        panelarray[j] = new JPanel();//create new panel for each species
			        panelarray[j].setLayout(new BoxLayout(panelarray[j], BoxLayout.Y_AXIS));
			        panelarray[j].setBackground(Color.BLACK);
			        panelarray[j].addMouseListener(new Mouse());//select label
			        
			        labelarray[i] = new JLabel();
			        speciescolor = s.getspeciescolor();
			        labelarray[i].setText("type: virus");
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesid = s.getspeciesid();
			        labelarray[i].setText("id: " + speciesid);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesgrowthrate = s.getspeciesgrowthrate();
			        labelarray[i].setText("growth rate: " + speciesgrowthrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesdensitydependentdeathe = s.getspeciesdensitydependentdeathe();
			        labelarray[i].setText("density dependent deathe: " + speciesdensitydependentdeathe);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        speciesbackgroundbackgrounddeathrate = s.getspeciesbackgroundbackgrounddeathrate();
			        labelarray[i].setText("background deathrate: " + speciesbackgroundbackgrounddeathrate);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        //create string with all species that can be infected
			        labelarray[i] = new JLabel();
			        ArrayList<Integer> prey = s.getinfecting();
			        StringBuffer sb = new StringBuffer();
			        for(int preyid : prey) {
			        	sb.append(preyid);
			        	sb.append(", ");
			        }
			        
			        labelarray[i].setText("id of infecting: " + sb);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        incubationtime = s.getincubationtime();
			        labelarray[i].setText("incubation time: " + incubationtime);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        labelarray[i] = new JLabel();
			        lethality = s.getlethality();
			        labelarray[i].setText("lethality: " + lethality);
			        labelarray[i].setForeground(speciescolor);
			        panelarray[j].add(labelarray[i]);
			        i++;
			        
			        j++;
			    }
			}
			
			//recreate previously removed panel
			for(JPanel epanel : epanelarray) {
				panelwest.add(epanel);
			}
			for(JPanel rpanel : rpanelarray) {
				panelwest.add(rpanel);
			}
			for(JPanel panel : panelarray) {
				panelwest.add(panel);
			}
			
			//draw
			panelwest.revalidate();
		}
	}
	
	//removel panelwest
	public void emptypanelwest() {//show species info
		panelwest.removeAll();
	}
	
	
	//mouselistener for mouse clicks
	public class Mouse extends MouseAdapter{

		@Override
		public void mouseClicked(MouseEvent e) {
			//if mouse click is on the grid
			if(e.getSource() instanceof MyDrawPanel) {
				int xcoordinate, ycoordinate, xpixelselector = 1, ypixelselector = 1, xcounter = 0, ycounter = 0, cellnumber = 0;
				xcoordinate = e.getX();
				ycoordinate = e.getY();
				
				//determine the cell coordinates by counting cell sizes
				//as long as xpixelselector < xcoordinate {add cellsize in pixels}; every loop means one cell
				while(xpixelselector < xcoordinate) {
					xpixelselector = xpixelselector + sizesquares;
					xcounter++;
				}
				while(ypixelselector < ycoordinate) {
					ypixelselector = ypixelselector + sizesquares;
					ycounter++;
				}
				
				//only if clicked on grid
				if(xcounter <= gridsize && ycounter <= gridsize) {
					cellnumber = ((ycounter - 1) * gridsize) + (xcounter - 1);
					species cellspecies = cells[cellnumber].getspeciesvalue();
					
					if(selectedevolution != null) {
						System.out.println(selectedevolution.getparameterincell(cellnumber));
					}
					
          if(lastselectedobject == "species"){
            if(cellspecies != selectedspecies && selectedspecies != null) {//if cell is empty or not the same as selected species than change cell to new species
              if(selectedspecies instanceof virus) {//virus can only grow on other species
                virus selectedspeciesvirus = (virus) selectedspecies;
                int virusincubationtime = selectedspeciesvirus.getincubationtime();

                int cellspeciesid = cellspecies.getspeciesid();
                ArrayList<Integer> infectionlist = selectedspeciesvirus.getinfecting();
                for(int id : infectionlist) {//if species in cell it can be infected by virus
                  if(id == cellspeciesid) {
                    cells[cellnumber].addvirusincell(selectedspecies);
                    cells[cellnumber].addincubationtime(virusincubationtime);
                  }
                }
              }else {//set cell to selected species
                cells[cellnumber].setspeciesvalue(selectedspecies);
              }

              //every evolution has array of all cells; now check if this needs to be updated
              if(evolutionarraylist.size() != 0) {//update evolution if applicable
                int speciesid = selectedspecies.getspeciesid();
                for (Object o : evolutionarraylist) {//check for evolution replication parameter; if no evolution take growthrate
                  if(o instanceof evolution) {
                    evolution evo = (evolution) o;
                    int evolutionspecies = evo.getevospecies();
                    String parameter = evo.getparameter();
                    if(speciesid == evolutionspecies) {
                      if("growthrate".equals(parameter)) {
                      evo.setparameterupdate(cellnumber, selectedspecies.growthrate);
                      }else if("densitydependentdeath".equals(parameter)) {
                        evo.setparameterupdate(cellnumber, selectedspecies.getspeciesdensitydependentdeathe());
                      }else if("backgrounddeathrate".equals(parameter)) {
                        evo.setparameterupdate(cellnumber, selectedspecies.getspeciesbackgroundbackgrounddeathrate());
                      }else if("mutationrate".equals(parameter)) {
                        evo.setparameterupdate(cellnumber, evo.getmutationspeed());
                      }else if("evolvingparameter".equals(parameter)) {
                        evo.setparameterupdate(cellnumber, selectedspecies.getspeciesevolvingparameter());
                      }else if("lethality".equals(parameter)) {//only if virus
                        if(selectedspecies instanceof virus) {
                          virus selectedspecies2 = (virus) selectedspecies;
                          evo.setparameterupdate(cellnumber, selectedspecies2.getlethality());
                        }
                      }else if("resourceproduction".equals(parameter)) {
                        evo.setparameterupdate(cellnumber, selectedspecies.getspeciesresourcechance());
                      }
                    }
                  }
                }
              }
            }else {//empty cell
              cells[cellnumber].setspeciesvalue(null);

              if(evolutionarraylist.size() != 0) {//empty evolution
                for (Object o : evolutionarraylist) {
                  if(o instanceof evolution) {
                    evolution evo = (evolution) o;
                    evo.setparameterupdate(cellnumber, 0.0);
                  }
                }
              }
            }
          }else if("resource".equals(lastselectedobject)){//add local resource source when clicked on grid
            if(selectedresource instanceof local_resource) {
              local_resource selectedresource_local = (local_resource) selectedresource;
              int amountpercell = selectedresource.getamountpercel();
              int resourceid = selectedresource.getresourceid() - 1;
              cell cellobject = cells[cellnumber];
              
              selectedresource_local.addremovesource(cellobject);
              cellobject.setselectedresource(resourceid, amountpercell);
            }
              
          }
					
					onlypaint = true;//repaint grid once
					try {
						runhelper();
					} catch (InterruptedException ev) {
						ev.printStackTrace();
					}
					
				}
			}
			
			//check if mouseclick is on a species selection panel and not on grid
			for(JPanel panel : panelarray) {
				if(e.getSource() == panel) {
					int i = 0;
					int selectedpanelid = 0;
					for(JPanel item : panelarray) {
						//create border to show selected species
						item.setBorder(BorderFactory.createEmptyBorder());
						
						if(item == (JPanel) e.getSource()) {//select panel id to get species id
							selectedpanelid = i;
						}
						i++;
					}
					
					int j = 0;
					for(Object o : speciesarraylist){//get species id
					    if (o instanceof grower) {
					        grower s = (grower) o;
					        
					        if(j == selectedpanelid) {
					        	selectedspecies = s;
                    lastselectedobject = "species";
					        }
					    }else if(o instanceof predator) {
					    	predator s = (predator) o;
					    	
					        if(j == selectedpanelid) {
					        	selectedspecies = s;
                    lastselectedobject = "species";
					        }
					    }else if(o instanceof cooperator) {
					    	cooperator s = (cooperator) o;
					    	
					        if(j == selectedpanelid) {
					        	selectedspecies = s;
                    lastselectedobject = "species";
					        }
						}else if(o instanceof gameoflife) {
					    	gameoflife s = (gameoflife) o;
					    	
					        if(j == selectedpanelid) {
					        	selectedspecies = s;
                    lastselectedobject = "species";
					        }
            }else if(o instanceof virus) {
              virus s = (virus) o;

              //change to virus view
                if(j == selectedpanelid) {
                  selectedspecies = s;
                  lastselectedobject = "species";
                  if(draw != "virus") {
                    draw = "virus";
                  }else {
                    draw = "species";
                  }
                }

              onlypaint = true;//repaint grid once
              try {
                runhelper();
              } catch (InterruptedException ev) {
                ev.printStackTrace();
              }
            }
            j++;
					}
					
					//draw border
					JPanel sourcepanel = (JPanel) e.getSource();
					sourcepanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
					sourcepanel.revalidate();
				}
			}
			
			for(JPanel panel : epanelarray) {
				if(e.getSource() == panel) {
					int i = 0;
					int selectedpanelid = 0;
					for(JPanel item : epanelarray) {
						item.setBorder(BorderFactory.createEmptyBorder());
						
						
						if(item == (JPanel) e.getSource()) {//select panel id to get species id
							selectedpanelid = i;
						}
						i++;
					}
					
					int j = 0;
					for(Object o : evolutionarraylist){//get resource
					    if (o instanceof evolution) {
					    	evolution r = (evolution) o;
					        
					    	//change to evolution view
					        if(j == selectedpanelid) {
					        	if(selectedevolution != r) {//if not already selected
					        		selectedevolution = r;
                      lastselectedobject = "evolution";
					        		draw = "evolution";
					        	}else {//else draw species
					        		selectedevolution = null;
					        		draw = "species";
                      lastselectedobject = "evolution";
					        	}
					        }
					    }
					    j++;
					}
					
					//draw border
					JPanel sourcepanel = (JPanel) e.getSource();
					sourcepanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
					sourcepanel.revalidate();
					
					onlypaint = true;//repaint grid once
					try {
						runhelper();
					} catch (InterruptedException ev) {
						ev.printStackTrace();
					}
				}
			}
			
			//check if mouseclick is on a resource selection panel and not on grid
			for(JPanel panel : rpanelarray) {
				if(e.getSource() == panel) {
					int i = 0;
					int selectedpanelid = 0;
					for(JPanel item : rpanelarray) {
						item.setBorder(BorderFactory.createEmptyBorder());
						
						
						if(item == (JPanel) e.getSource()) {//select panel id to get species id
							selectedpanelid = i;
						}
						i++;
					}
					
					int j = 0;
					for(Object o : resourcearraylist){//get resource
					    if (o instanceof resource) {
					        resource r = (resource) o;
					        
					        //set to resource view
					        if(j == selectedpanelid) {
					        	if(selectedresource != r) {//if not already selected
					        		selectedresource = r;
                      lastselectedobject = "resource";
					        		draw = "resource";
					        	}else {
					        		//panel.setBorder(null);
					        		selectedresource = null;
					        		draw = "species";
                      lastselectedobject = "resource";
					        	}
					        }
					    }
					    j++;
					}
					
					JPanel sourcepanel = (JPanel) e.getSource();
					sourcepanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
					sourcepanel.revalidate();
					
					onlypaint = true;//repaint grid once
					try {
						runhelper();
					} catch (InterruptedException ev) {
						// TODO Auto-generated catch block
						ev.printStackTrace();
					}
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//draw the grid
	class MyDrawPanel extends JPanel{
		species cellspecies;
		int pixelselector, numberofpixels, xcoordinate, ycoordinate, resourceavailable;
		double parameteravailable;
		
		public void paintComponent (Graphics g) {
			float height = this.getHeight(), width = this.getWidth();//convert to float to use round method
			//if on smallscreen take max width else take height to calculate max size of cells
			sizesquares = (int) (Math.round(height / gridsize));
			if(this.getWidth() < this.getHeight()) {
				sizesquares = (int) (Math.round(width / gridsize));
			}
			int sizesquaresfill = sizesquares - 1;
			numberofpixels = 0;
			numberofpixels = sizesquares * gridsize;//max width and height of grid
			
			g.setColor(Color.black);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			//for every cell in gridsize calculate pixel and draw black line
			pixelselector = 0;
			for(int i = 0; i < gridsize; i++) {
				pixelselector = pixelselector + sizesquares;
				
				g.setColor(Color.black);//color of lines
				g.fillRect(pixelselector, 0, 1, numberofpixels);//border
				g.fillRect(0, pixelselector, numberofpixels, 1);//max width should be the same as the height
			}
			
			//draw white border
			g.setColor(Color.white);//color of top line
			g.fillRect(0, 0, 1, numberofpixels);
			g.fillRect(0, 0, numberofpixels, 1);
			g.fillRect(numberofpixels, 0, 1, numberofpixels);
			g.fillRect(0, numberofpixels, numberofpixels, 1);
			
			//fill cells
			xcoordinate = 1; ycoordinate = 1; int i = 0;//lines are 1 pixel wide so start on 1,1
			for (cell item : cells) {//set all cells to species 1
				if("species".equals(draw)) {
					cellspecies = item.getspeciesvalue();
					
					if(cellspecies != null) {
						//g.setColor(Color.RED);
						g.setColor(cellspecies.getspeciescolor());
						g.fillRect(xcoordinate, ycoordinate, sizesquaresfill, sizesquaresfill);
					}
					
					xcoordinate = xcoordinate + sizesquares;
					if(xcoordinate >= (gridsize * sizesquares)) {//0 to 99 on one row
						xcoordinate = 1;
						ycoordinate = ycoordinate + sizesquares;
					}
				}else if("resource".equals(draw)) {
					Color[] colorarray = new Color[10];
					
					int amountpercell = selectedresource.getamountpercel();
					Color resourcecolor = selectedresource.getresourcecolor();
					int resourceid = selectedresource.getresourceid();
					resourceid--;//array starts with 0
          int regenerate = selectedresource.getregenerate();
					
					//calculate the intervals for each color
					int halfamountpercell = (int) (amountpercell / 2);
					int amountpercolor = amountpercell / 10;
          
          //different colors if no regeneration because maxamount will be different
          if(regenerate == 0){
              amountpercolor = (int) amountpercell / (gridsize * gridsize);
              if(amountpercolor < 1){ amountpercolor = 1; }//minimum steps of 1
              halfamountpercell = (int) 5 * amountpercolor;
          }
					
					resourceavailable = item.getselectedresource(resourceid);//get resource amount
          
					if(resourceavailable != 0) {//only paint if cell resource is not empty
						int ncolordeviation = (int) (resourceavailable - halfamountpercell);//determine half the max amount
						int ntimescolorchange = (int) Math.abs((ncolordeviation / amountpercolor));//calculate the amount of times the color has to change
						
						Color paintcolor = resourcecolor;
						for(int ccounter = 0; ccounter <= ntimescolorchange; ccounter++) {//change color amount of times
							if(ncolordeviation < 0) {
								paintcolor = paintcolor.brighter();
							}else if (ncolordeviation > 0) {
								paintcolor = paintcolor.darker();
							}
						}
						
						g.setColor(paintcolor);
						g.fillRect(xcoordinate, ycoordinate, sizesquaresfill, sizesquaresfill);
					}else {
						g.setColor(Color.white);
						g.fillRect(xcoordinate, ycoordinate, sizesquaresfill, sizesquaresfill);
					}
          
          if(regenerate > 0){
            if(selectedresource instanceof local_resource){
              local_resource selected_local_resource = (local_resource) selectedresource;
              int sourcecell = selected_local_resource.issourceinlist(item);
              if(sourcecell == 1){
                g.setColor(Color.black);
                g.fillRect(xcoordinate, ycoordinate, sizesquaresfill, sizesquaresfill);
              }
            }
          }
					
					xcoordinate = xcoordinate + sizesquares;
					if(xcoordinate >= (gridsize * sizesquares)) {//van 0 tot 99 op een rij
						xcoordinate = 1;
						ycoordinate = ycoordinate + sizesquares;
					}
				}else if ("evolution".equals(draw)){
					Color[] colorarray = new Color[10];
					
					double amountpercell = 1;//max amount of variables
					Color evolutioncolor = selectedevolution.getevolutioncolor();
					int evolutionid = selectedevolution.getevolutionid();
					evolutionid--;//array starts with 0
					
					double halfamountpercell = (double) (amountpercell / 2);
					double amountpercolor = amountpercell / 10;
					
					
					parameteravailable = selectedevolution.getparameterincell(i);//get evolution of parameter in cell
					
					if(parameteravailable != 0) {//only paint if cell resource is not empty
						double ncolordeviation = (double) (parameteravailable - halfamountpercell);//determine half the max amount
						double ntimescolorchange = (double) Math.abs((ncolordeviation / amountpercolor));//calculate the amount of time the color has to change
						
						Color paintcolor = evolutioncolor;
						for(int ccounter = 0; ccounter <= ntimescolorchange; ccounter++) {//change color amount of times
							if(ncolordeviation < 0) {
								paintcolor = paintcolor.brighter();
							}else if (ncolordeviation > 0) {
								paintcolor = paintcolor.darker();
							}
						}
						
						g.setColor(paintcolor);
						g.fillRect(xcoordinate, ycoordinate, sizesquaresfill, sizesquaresfill);
					}else {
						g.setColor(Color.black);
						g.fillRect(xcoordinate, ycoordinate, sizesquaresfill, sizesquaresfill);
					}
					
					xcoordinate = xcoordinate + sizesquares;
					if(xcoordinate >= (gridsize * sizesquares)) {//van 0 tot 99 op een rij
						xcoordinate = 1;
						ycoordinate = ycoordinate + sizesquares;
					}
					
				}else if("virus".equals(draw)) {
					ArrayList<Object> virusincell = item.getvirusincell();
					
					if(virusincell.size() != 0) {
						for(Object v : virusincell) {
							if(v instanceof virus) {
								virus vi = (virus) v;
								if(selectedspecies == vi) {
									g.setColor(selectedspecies.getspeciescolor());
									g.fillRect(xcoordinate, ycoordinate, sizesquaresfill, sizesquaresfill);
								}
							}
						}
					}
					
					xcoordinate = xcoordinate + sizesquares;
					if(xcoordinate >= (gridsize * sizesquares)) {//van 0 tot 99 op een rij
						xcoordinate = 1;
						ycoordinate = ycoordinate + sizesquares;
					}
				}
				i++;
			}
		}
	}
	
	//only update once
	class onesteplistener implements ActionListener{//run 1 step
		public void actionPerformed(ActionEvent event) {
			boolrun = false;
			onesteprun = true;
			try {
				runhelper();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//update continuously
	class runlistener implements ActionListener{//run while true
		public void actionPerformed(ActionEvent event) {
			//change button text
			JButton clicked = (JButton) event.getSource();
			String buttonText = clicked.getText();
			if("run".equals(buttonText)) {
				clicked.setText("pause");
			}else {
				clicked.setText("run");
			}
			
			if(boolrun == false) {
				boolrun = true;
			}else {
				boolrun = false;
			}
			
			try {
				runhelper();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//update synchronously
	class synchronouslistener implements ActionListener{//run while true
		public void actionPerformed(ActionEvent event) {
			//change button text
			JButton clicked = (JButton) event.getSource();
			String buttonText = clicked.getText();
			if("update asynchronous".equals(buttonText)) {
				clicked.setText("update synchronous");
			}else {
				clicked.setText("update asynchronous");
			}
			
			if(synchronous == true) {
				synchronous = false;
			}else {
				synchronous = true;
			}
		}
	}
	
	//add diffusion
	class diffusionlistener implements ActionListener{//run while true
		public void actionPerformed(ActionEvent event) {
			//change button text
			JButton clicked = (JButton) event.getSource();
			String buttonText = clicked.getText();
			if("turn diffusion on".equals(buttonText)) {
				clicked.setText("turn diffusion off");
			}else {
				clicked.setText("turn diffusion on");
			}
			
			if(diffusion == false) {
				diffusion = true;
			}else {
				diffusion = false;
			}
		}
	}
	
	//add new species
	class newspecieslistener implements ActionListener, ListSelectionListener{//add species form
		JList newspecies, newevolution, newresource;
		int newspeciesclick = 0;
		
		//show panel south_east; located on east
		public void actionPerformed(ActionEvent event) {	
			//determine if panel should show
			if(shownewspeciesform == 0) {
				shownewspeciesform++;
			}else if(shownewspeciesform == 1) {
				shownewspeciesform--;
				frame.remove(panelsouth_east);
				frame.revalidate();//update panel
			}else if(shownewspeciesform == 2) {
				shownewspeciesform = 0;
				frame.remove(panelsouth_east);
				frame.revalidate();//update panel
			}
			
			if(shownewspeciesform == 1) {
				panelsouth_east = new JPanel();
				panelsouth_east.setBackground(Color.WHITE);
				panelsouth_east.setLayout(new BoxLayout(panelsouth_east, BoxLayout.Y_AXIS));
					
				frame.add(BorderLayout.EAST, panelsouth_east);
					
				//add scrollbar
				newspecies = new JList(listoptions);
				newspecies.setName("specieslist");
				JScrollPane scroller = new JScrollPane(newspecies);
				scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				panelsouth_east.add(scroller);
				newspecies.setVisibleRowCount(4);
				newspecies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
				int i = 0;
				for(String item : listoptions) {//automatically select item that is the same as selection to reload form when predator is choosen
					if(item == selection) {
						newspecies.setSelectedIndex(i);
					}
					i++;
				}
				newspecies.addListSelectionListener(this);
				
				//add forms with textfields and buttons
				if("grower".equals(selection) || "predator".equals(selection) || "cooperator".equals(selection) || "gameoflife".equals(selection) || "virus".equals(selection)) {					
					JLabel growthratelabel = new JLabel("growth rate: ");
					panelsouth_east.add(growthratelabel);
					growthratefield = new JFormattedTextField(getMaskFormatter("#.###"));
					panelsouth_east.add(growthratefield);
					
					JLabel densitydependentdeathlabel = new JLabel("density dependent death rate: ");
					panelsouth_east.add(densitydependentdeathlabel);
					densitydependentdeathfield = new JFormattedTextField(getMaskFormatter("#.###"));//"density dependent death rate"
					panelsouth_east.add(densitydependentdeathfield);
					
					JLabel backgrounddeathratelabel = new JLabel("back ground death rate: ");
					panelsouth_east.add(backgrounddeathratelabel);
					backgrounddeathratefield = new JFormattedTextField(getMaskFormatter("#.###"));
					panelsouth_east.add(backgrounddeathratefield);
					
					JLabel replicaterequirednumberlabel = new JLabel("number of replicators required to replicate: ");
					panelsouth_east.add(replicaterequirednumberlabel);
					replicaterequirednumberfield = new JFormattedTextField(getMaskFormatter("#"));
					panelsouth_east.add(replicaterequirednumberfield);
					
					JLabel minimumrequirednumberlabel = new JLabel("minimum number of replicators required to survive: ");
					panelsouth_east.add(minimumrequirednumberlabel);
					minimumrequirednumberfield = new JFormattedTextField(getMaskFormatter("#"));
					panelsouth_east.add(minimumrequirednumberfield);
					
					JLabel resourcelabel = new JLabel("insert the id of the resource & the amount it consumes ie 1&3,2&1; multiple resources are separated by ,");
					panelsouth_east.add(resourcelabel);
					resourcefield = new JTextField();
					panelsouth_east.add(resourcefield);
					
					JLabel resourceproducelabel = new JLabel("insert the id of the resource & the amount it produces ie 1&3,2&1; multiple resources are separated by ,");
					panelsouth_east.add(resourceproducelabel);
					resourceproducefield = new JTextField();
					panelsouth_east.add(resourceproducefield);
					
					//specific questions per species
					if("predator".equals(selection)) {
						JLabel preylabel = new JLabel("insert the id of species that this species will predate on; separate by ,");
						panelsouth_east.add(preylabel);
						preyfield = new JTextField();
						panelsouth_east.add(preyfield);
					}
					
					if("cooperator".equals(selection)) {
						JLabel cooperatelabel = new JLabel("insert the id of species that this species requires to propagate; separate by ,");
						panelsouth_east.add(cooperatelabel);
						cooperatefield = new JTextField();
						panelsouth_east.add(cooperatefield);
					}
					
					if("virus".equals(selection)) {
						backgrounddeathratelabel.setVisible(false);
						backgrounddeathratefield.setVisible(false);
						replicaterequirednumberlabel.setVisible(false);
						replicaterequirednumberfield.setVisible(false);
						densitydependentdeathlabel.setVisible(false);
						densitydependentdeathfield.setVisible(false);
						minimumrequirednumberlabel.setVisible(false);
						minimumrequirednumberfield.setVisible(false);
						resourcelabel.setVisible(false);
						resourcefield.setVisible(false);
						resourceproducelabel.setVisible(false);
						resourceproducefield.setVisible(false);
						
						
						JLabel infectlabel = new JLabel("insert the id of species that this species will infect; separate by ,");
						panelsouth_east.add(infectlabel);
						infectfield = new JTextField();
						panelsouth_east.add(infectfield);
						
						JLabel incubationlabel = new JLabel("incubation time: ");
						panelsouth_east.add(incubationlabel);
						incubationnumberfield = new JFormattedTextField(getMaskFormatter("##"));
						panelsouth_east.add(incubationnumberfield);
						
						JLabel lethalitylabel = new JLabel("virus lethality: ");
						panelsouth_east.add(lethalitylabel);
						lethalityfield = new JFormattedTextField(getMaskFormatter("#.###"));
						panelsouth_east.add(lethalityfield);
					}
					
					JButton addspecies = new JButton("add species");
					addspecies.addActionListener(new addpecieslistener());
					panelsouth_east.add(addspecies);
				}
				
				if("resource".equals(selection)) {
					newresource = new JList(listoptions3);
					newresource.setName("resourcelist");
            
					JScrollPane scroller5 = new JScrollPane(newresource);
					scroller5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					scroller5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					panelsouth_east.add(scroller5);
					newresource.setVisibleRowCount(2);
					newresource.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					
					int e = 0;
					for(String item : listoptions3) {//automatically select item that is the same as selection to reload form when predator is choosen
						if(item == selection3) {
							newresource.setSelectedIndex(e);
						}
						e++;
					}
					newresource.addListSelectionListener(this);
            
					JLabel amountnumberlabel = new JLabel("amount available per cel: ");
					panelsouth_east.add(amountnumberlabel);
					amountnumberfield = new JFormattedTextField(getMaskFormatter("###"));
					panelsouth_east.add(amountnumberfield);
          
          JLabel diffusionlabel = new JLabel("diffusion of resource 0,1");
					panelsouth_east.add(diffusionlabel);
					diffusionnumberfield = new JFormattedTextField(getMaskFormatter("#"));
					panelsouth_east.add(diffusionnumberfield);
          
					JLabel diffusionparameterlabel = new JLabel("diffusion parameter: ");
					panelsouth_east.add(diffusionparameterlabel);
					diffusionparameternumberfield = new JFormattedTextField(getMaskFormatter("0.##"));
					panelsouth_east.add(diffusionparameternumberfield);
					
					JLabel regeneratenumberlabel = new JLabel("regenerate: ");
					panelsouth_east.add(regeneratenumberlabel);
					regeneratenumberfield = new JFormattedTextField(getMaskFormatter("###"));
					panelsouth_east.add(regeneratenumberfield);
					
					JButton addresource = new JButton("add resource");
					addresource.addActionListener(new addresourcelistener());
					panelsouth_east.add(addresource);
				}
				
				if("evolution".equals(selection)) {
					//add option list
					newevolution = new JList(listoptions2);
					newevolution.setName("evolutionlist");
					
					JScrollPane scroller2 = new JScrollPane(newevolution);
					scroller2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					scroller2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					panelsouth_east.add(scroller2);
					newevolution.setVisibleRowCount(4);
					newevolution.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					
					int e = 0;
					for(String item : listoptions2) {//automatically select item that is the same as selection to reload form when predator is choosen
						if(item == selection2) {
							newevolution.setSelectedIndex(e);
						}
						e++;
					}
					newevolution.addListSelectionListener(this);
					
					JLabel evospecieslabel = new JLabel("Species id: ");
					panelsouth_east.add(evospecieslabel);
					evospeciesfield = new JTextField();
					panelsouth_east.add(evospeciesfield);
					
					JLabel mutationlabel = new JLabel("mutationchance: ");
					panelsouth_east.add(mutationlabel);
					mutationnumberfield = new JFormattedTextField(getMaskFormatter("#.###"));
					panelsouth_east.add(mutationnumberfield);
					
					JLabel mutationspeedlabel = new JLabel("mutationspeed: ");
					panelsouth_east.add(mutationspeedlabel);
					mutationspeedlabelnumberfield = new JFormattedTextField(getMaskFormatter("#.###"));
					panelsouth_east.add(mutationspeedlabelnumberfield);
					
					JButton addevolution = new JButton("add evolution");
					addevolution.addActionListener(new addevolutionlistener());
					panelsouth_east.add(addevolution);
				}
				panelsouthcontainer.revalidate();//update panel
			}
	        
		}
		
		//used to fix the number of decimals
		private MaskFormatter getMaskFormatter(String format) {//create format for inserting doubles
		    MaskFormatter mask = null;
		    try {
		        mask = new MaskFormatter(format);
		        mask.setPlaceholderCharacter('0');
		    }catch (ParseException ex) {
		        ex.printStackTrace();
		    }
		    return mask;
		}
		
		public void valueChanged(ListSelectionEvent lse) {//jlist listener
			if(!lse.getValueIsAdjusting()) {
				if(lse.getSource() == newspecies) {//check for scource list
					if((String) newspecies.getSelectedValue() != null) {
						selection = (String) newspecies.getSelectedValue();
						
					}
					System.out.println(newspecies.getName() + " , " + selection);
				}
				
        if(lse.getSource() == newevolution) {//check for scource list
          if((String) newevolution.getSelectedValue() != null) {
            selection2 = (String) newevolution.getSelectedValue();
          }
          System.out.println(newevolution.getMaxSelectionIndex() + " , " + selection2);
        }
        
        if(lse.getSource() == newresource) {//check for scource list
          if((String) newresource.getSelectedValue() != null) {
            selection3 = (String) newresource.getSelectedValue();
          }
          System.out.println(newresource.getMaxSelectionIndex() + " , " + selection3);
        }

        newspeciesbutton.doClick();
        newspeciesbutton.doClick();
			}
		}
	}
	
	//addspecies
	class addpecieslistener implements ActionListener{//add species form
		public void actionPerformed(ActionEvent event) {
			//get form information
			double growthrate = (double) Double.valueOf(growthratefield.getText());
			double densitydependentdeath = (double) Double.valueOf(densitydependentdeathfield.getText());
			double backgrounddeathrate = (double) Double.valueOf(backgrounddeathratefield.getText());
			double replicaterequirednumber = (double) Double.valueOf(replicaterequirednumberfield.getText());
			int replicaterequirednumberint = (int) replicaterequirednumber;//convert to int
			double minimumrequirednumber = (double) Double.valueOf(minimumrequirednumberfield.getText());
			int minimumrequirednumberint = (int) minimumrequirednumber;//convert to int
			
			//for every species insert new object
			if("grower".equals(selection)) {//add new species
				grower newspecies = new grower();
				
				newspecies.setspeciesgrowthtrate(growthrate);
				newspecies.setspeciesdensitydependentdeath(densitydependentdeath);
				newspecies.setspeciesbackgroundbackgrounddeathrate(backgrounddeathrate);
				newspecies.setreplicaterequirednumber(replicaterequirednumberint);
				newspecies.setminimumrequirednumber(minimumrequirednumberint);
				
				newspecies.setspeciesid(speciescounter);
				speciescounter++;
				int speciesid = newspecies.getspeciesid();
				newspecies.setspeciescolor(speciesid);
				
				String resourcefieldstring = resourcefield.getText();//set resource required
				if(resourcefieldstring.isEmpty() == false) {
					String[] split = resourcefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresource(values[0],values[1]);
						values = null;
					}
				}
				
				String resourceproducefieldstring = resourceproducefield.getText();//set production of resource
				if(resourceproducefieldstring.isEmpty() == false) {
					String[] split = resourceproducefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresourceproduce(values[0],values[1]);
						values = null;
					}
				}
				
				speciesarraylist.add(newspecies);
				
				frame.repaint();
			}else if("predator".equals(selection)){
				predator newspecies = new predator();
				
				newspecies.setspeciesgrowthtrate(growthrate);
				newspecies.setspeciesdensitydependentdeath(densitydependentdeath);
				newspecies.setspeciesbackgroundbackgrounddeathrate(backgrounddeathrate);
				newspecies.setreplicaterequirednumber(replicaterequirednumberint);
				newspecies.setminimumrequirednumber(minimumrequirednumberint);
				
				String preyfieldstring = preyfield.getText();
				
				if(preyfieldstring.isEmpty() == false) {
					String[] split = preyfieldstring.split(",");
					for(String item : split) {
						double i = (double) Double.valueOf(item);
						int j = (int) i;
						
						newspecies.setprey(j);
					}
				}
				
				newspecies.setspeciesid(speciescounter);
				speciescounter++;
				int speciesid = newspecies.getspeciesid();
				newspecies.setspeciescolor(speciesid);
				
				String resourcefieldstring = resourcefield.getText();//set resource required
				if(resourcefieldstring.isEmpty() == false) {
					String[] split = resourcefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresource(values[0],values[1]);
						values = null;
					}
				}
				
				String resourceproducefieldstring = resourceproducefield.getText();//set production of resource
				if(resourceproducefieldstring.isEmpty() == false) {
					String[] split = resourceproducefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresourceproduce(values[0],values[1]);
						values = null;
					}
				}
				
				speciesarraylist.add(newspecies);
				
				frame.repaint();
			}else if("cooperator".equals(selection)) {
				cooperator newspecies = new cooperator();
				
				newspecies.setspeciesgrowthtrate(growthrate);
				newspecies.setspeciesdensitydependentdeath(densitydependentdeath);
				newspecies.setspeciesbackgroundbackgrounddeathrate(backgrounddeathrate);
				newspecies.setreplicaterequirednumber(replicaterequirednumberint);
				newspecies.setminimumrequirednumber(minimumrequirednumberint);
				
				String cooperatorstring = cooperatefield.getText();
				
				if(cooperatorstring.isEmpty() == false) {
					String[] split = cooperatorstring.split(",");
					for(String item : split) {
						double i = (double) Double.valueOf(item);
						int j = (int) i;
						
						newspecies.setcooperator(j);
					}
				}
				
				newspecies.setspeciesid(speciescounter);
				speciescounter++;
				int speciesid = newspecies.getspeciesid();
				newspecies.setspeciescolor(speciesid);
				
				String resourcefieldstring = resourcefield.getText();//set resource required
				if(resourcefieldstring.isEmpty() == false) {
					String[] split = resourcefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresource(values[0],values[1]);
						values = null;
					}
				}
				
				String resourceproducefieldstring = resourceproducefield.getText();//set production of resource
				if(resourceproducefieldstring.isEmpty() == false) {
					String[] split = resourceproducefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresourceproduce(values[0],values[1]);
						values = null;
					}
				}
				
				speciesarraylist.add(newspecies);
				
				frame.repaint();
			
			}else if("gameoflife".equals(selection)) {//add new species
				gameoflife newspecies = new gameoflife();
				
				newspecies.setspeciesgrowthtrate(growthrate);
				newspecies.setspeciesdensitydependentdeath(densitydependentdeath);
				newspecies.setspeciesbackgroundbackgrounddeathrate(backgrounddeathrate);
				newspecies.setreplicaterequirednumber(replicaterequirednumberint);
				newspecies.setminimumrequirednumber(minimumrequirednumberint);
				
				newspecies.setspeciesid(speciescounter);
				speciescounter++;
				int speciesid = newspecies.getspeciesid();
				newspecies.setspeciescolor(speciesid);
				
				String resourcefieldstring = resourcefield.getText();//set resource required
				if(resourcefieldstring.isEmpty() == false) {
					String[] split = resourcefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresource(values[0],values[1]);
						values = null;
					}
				}
				
				String resourceproducefieldstring = resourceproducefield.getText();//set production of resource
				if(resourceproducefieldstring.isEmpty() == false) {
					String[] split = resourceproducefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresourceproduce(values[0],values[1]);
						values = null;
					}
				}
				
				speciesarraylist.add(newspecies);
				
				frame.repaint();
				
			}else if("virus".equals(selection)){
				virus newspecies = new virus();
				
				double lethalitynumber = (double) Double.valueOf(lethalityfield.getText());
				double incubationtimenumber = (double) Double.valueOf(incubationnumberfield.getText());
				int incubationint = (int) incubationtimenumber;//convert to int
				
				newspecies.setspeciesgrowthtrate(growthrate);
				newspecies.setspeciesdensitydependentdeath(densitydependentdeath);
				newspecies.setspeciesbackgroundbackgrounddeathrate(backgrounddeathrate);
				newspecies.setreplicaterequirednumber(replicaterequirednumberint);
				newspecies.setminimumrequirednumber(minimumrequirednumberint);
				newspecies.setincubationtime(incubationint);
				newspecies.setlethality(lethalitynumber);
				
				String infectionfieldstring = infectfield.getText();
				
				if(infectionfieldstring.isEmpty() == false) {
					String[] split = infectionfieldstring.split(",");//seperate by ,
					for(String item : split) {
						double i = (double) Double.valueOf(item);
						int j = (int) i;
						
						newspecies.setinfecting(j);;
					}
				}
				
				newspecies.setspeciesid(speciescounter);
				speciescounter++;
				int speciesid = newspecies.getspeciesid();
				newspecies.setspeciescolor(speciesid);
				
				String resourcefieldstring = resourcefield.getText();//set resource required
				if(resourcefieldstring.isEmpty() == false) {
					String[] split = resourcefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresource(values[0],values[1]);
						values = null;
					}
				}
				
				String resourceproducefieldstring = resourceproducefield.getText();//set production of resource
				if(resourceproducefieldstring.isEmpty() == false) {
					String[] split = resourceproducefieldstring.split(",");
					for(String item : split) {//check for different resources separated by ,
						String[] split2 = item.split("&");
						int[] values = new int [2];
						int q = 0;
						for(String item2 : split2) {//get resourceid and consumption separated by &
							double i = (double) Double.valueOf(item2);
							int j = (int) i;
							
							values[q] = j;
							q++;
						}
						newspecies.setresourceproduce(values[0],values[1]);
						values = null;
					}
				}
				
				speciesarraylist.add(newspecies);
				
				frame.repaint();
			}
			panelsouthcontainer.revalidate();//update panel
			emptypanelwest();
			buildGuipanelwest();//add species info to panel
		}
	}
	
	//add resource
	class addresourcelistener implements ActionListener{//add resource form
		public void actionPerformed(ActionEvent event) {
      if(selection3 != null) { 
        double amountnumber = (double) Double.valueOf(amountnumberfield.getText());
        int amountnumberint = (int) amountnumber;//convert to int
        double regeneratenumber = (double) Double.valueOf(regeneratenumberfield.getText());
        int regeneratenumberint = (int) regeneratenumber;//convert to int
        double diffusionnumber = (double) Double.valueOf(diffusionnumberfield.getText());
        int diffusionnumberint = (int) diffusionnumber;//convert to int
        double diffusionparameter = (double) Double.valueOf(diffusionparameternumberfield.getText());
        
        resource newresource = new resource();
        if("global resource".equals(selection3)) {
          newresource = new global_resource();
          
          for(cell item : cells) {
            item.setresourceavailable(amountnumberint);
          }
				}else if("local resource".equals(selection3)) {
          newresource = new local_resource();
          
          for(cell item : cells) {
            item.setresourceavailable(0);
          }
        }
        
        if(diffusionnumberint == 1){
            newresource.setdiffusion(1);
        }
        newresource.setamountpercel(amountnumberint);
        newresource.setregenerate(regeneratenumberint);
        newresource.setresourceid(resourcecounter);
        if(diffusionparameter != 0){
            newresource.setdiffusionparameter(diffusionparameter);
        }
        resourcecounter++;
        newresource.setresourcecolor();
        resourcearraylist.add(newresource);

        panelsouthcontainer.revalidate();//update panel
        emptypanelwest();
        buildGuipanelwest();//add species info to panel
      }
		}
	}
	
	//add evolution
	class addevolutionlistener implements ActionListener{//add evolution form
		public void actionPerformed(ActionEvent event) {
			if(selection2 != null) {
				if(speciesarraylist.size() >= Double.valueOf(evospeciesfield.getText())) {//check if species exists
					evolution newevolution = new evolution();
					
					double mutationnumber = (double) Double.valueOf(mutationnumberfield.getText());
					newevolution.setmutationnumber(mutationnumber);
					
					double mutationspeed = (double) Double.valueOf(mutationspeedlabelnumberfield.getText());
					newevolution.setmutationspeed(mutationspeed);
					
					String evospeciesfieldstring = evospeciesfield.getText();
					if(evospeciesfieldstring.isEmpty() == false) {
						double i = (double) Double.valueOf(evospeciesfieldstring);
						int j = (int) i;
						
						newevolution.setevospecies(j);
					}
					
					if("lethality (virus)".equals(selection2)) {
						selection2 = "lethality";
					}
					
					newevolution.setevolutioncolor();
					newevolution.setparameter(selection2);
					newevolution.setevolutionid(evolutioncounter);
					evolutioncounter++;
					newevolution.setparameterpercell(cells);//set all cells with species in evolution
					evolutionarraylist.add(newevolution);
				}
				panelsouthcontainer.revalidate();//update panel
				emptypanelwest();
				buildGuipanelwest();//add species info to panel
			}
		}
	}
	
	//delete all species, resource and evolution objects
	class resetlistener implements ActionListener{//reset everything
		public void actionPerformed(ActionEvent event) {
			for(cell item : cells) {
				item.setspeciesvalue(null);
				item.resourceavailable.clear();//remove resources from cells
				
				item.removevirusincell();//remove virusses in cell
				item.removevincubationtime();
			}
			speciescounter = 0;
			resourcecounter = 0;
			evolutioncounter = 0;
			stepcounter = 0;
			speciesarraylist.clear();
			resourcearraylist.clear();
			evolutionarraylist.clear();
			selectedresource = null;
			selectedevolution = null;
			draw = "species";
      lastselectedobject = null;
			
			onlypaint = true;//repaint grid
			try {
				runhelper();
			} catch (InterruptedException ev) {
				ev.printStackTrace();
			}
			
			panelwest.removeAll();
			panelwest.revalidate();
			panelwestcontainer.removeAll();
			panelwestcontainer.revalidate();
		}
		
	}
	
	//empty all cells
	class resetgridlistener implements ActionListener{//reset everything
		public void actionPerformed(ActionEvent event) {
			for(cell item : cells) {
				item.setspeciesvalue(null);
				item.removevirusincell();
				item.removevincubationtime();
			}
			
			for(Object evo : evolutionarraylist) {//reset evolution
    			if(evo instanceof evolution) {
    				evolution e = (evolution) evo;
    				e.resetparameterpercell();
    			}
			}
      
      int i = 0;
			for(Object resource : resourcearraylist) {//reset resource
    		if(resource instanceof local_resource) {
    		  local_resource r = (local_resource) resource;
    			r.removeallsources();
            
          for(cell item : cells) {
            item.setselectedresource(i, 0);
          }
    	  }else if(resource instanceof global_resource){
          global_resource r = (global_resource) resource;
          int amountpercell = r.getamountpercel();
            
          for(cell item : cells) {
            item.setselectedresource(i, amountpercell);
          }
        }
        i++;
			}
			
			onlypaint = true;//repaint grid
			try {
				runhelper();
			} catch (InterruptedException ev) {
				ev.printStackTrace();
			}
		}
		
	}
	
	//distribute random on grid
	class distributerlistener implements ActionListener{//distribute species over cells
		public void actionPerformed(ActionEvent event) {
			int listlength = speciesarraylist.size();
			int cellnumber = 0;
			for(cell item : cells) {
				int randnumber = (int) (Math.random() * listlength);//get random integer
				
				while(speciesarraylist.get(randnumber) instanceof virus) {//virus can't grow in cell
					randnumber = (int) (Math.random() * listlength);
				}
				
				//check for every species what will grow
			    if (speciesarraylist.get(randnumber) instanceof grower) {
			        grower s = (grower) speciesarraylist.get(randnumber);
			        item.setspeciesvalue(s);
			        
			        int speciesid = s.getspeciesid();//set evolution parameters for species
			        if(evolutionarraylist.size() != 0) {
		        		for(Object evo : evolutionarraylist) {
		        			if(evo instanceof evolution) {
		        				evolution e = (evolution) evo;
		        				
		        				if(e.getevospecies() == s.getspeciesid()) {//mutate parameter
		        					e.setparameterinitial(s, cellnumber);
		        				}
		        			}
		        		}
			        }
			    }else if(speciesarraylist.get(randnumber) instanceof predator){
			    	predator s = (predator) speciesarraylist.get(randnumber);
			    	item.setspeciesvalue(s);
			    	
			        int speciesid = s.getspeciesid();//set evolution parameters
			        if(evolutionarraylist.size() != 0) {
		        		for(Object evo : evolutionarraylist) {
		        			if(evo instanceof evolution) {
		        				evolution e = (evolution) evo;
		        				
		        				if(e.getevospecies() == s.getspeciesid()) {//mutate parameter
		        					e.setparameterinitial(s, cellnumber);
		        				}
		        			}
		        		}
			        }
			    }else if(speciesarraylist.get(randnumber) instanceof cooperator){
			    	cooperator s = (cooperator) speciesarraylist.get(randnumber);
			    	item.setspeciesvalue(s);
			    	
			        int speciesid = s.getspeciesid();//set evolution parameters
			        if(evolutionarraylist.size() != 0) {
		        		for(Object evo : evolutionarraylist) {
		        			if(evo instanceof evolution) {
		        				evolution e = (evolution) evo;
		        				
		        				if(e.getevospecies() == s.getspeciesid()) {//mutate parameter
		        					e.setparameterinitial(s, cellnumber);
		        				}
		        			}
		        		}
			        }
			    }else if(speciesarraylist.get(randnumber) instanceof gameoflife){
			    	gameoflife s = (gameoflife) speciesarraylist.get(randnumber);
			    	item.setspeciesvalue(s);
			    	
			        int speciesid = s.getspeciesid();//set evolution parameters
			        if(evolutionarraylist.size() != 0) {
		        		for(Object evo : evolutionarraylist) {
		        			if(evo instanceof evolution) {
		        				evolution e = (evolution) evo;
		        				
		        				if(e.getevospecies() == s.getspeciesid()) {//mutate parameter
		        					e.setparameterinitial(s, cellnumber);
		        				}
		        			}
		        		}
			        }
			    }
			    
			    cellnumber++;
			}
			
			onlypaint = true;//repaint grid
			try {
				runhelper();
			} catch (InterruptedException ev) {
				// TODO Auto-generated catch block
				ev.printStackTrace();
			}
		}
		
	}
	
	
	//change color
	class changecolorlistener implements ActionListener{//select save file
		public void actionPerformed(ActionEvent event) {
            if("species".equals(lastselectedobject)){
                selectedspecies.setspeciescolor(99);
            }else if("evolution".equals(lastselectedobject)){
                selectedevolution.setevolutioncolor();
            }else if("resource".equals(lastselectedobject)){
                selectedresource.setresourcecolor();
            }
            frame.repaint();
            
			panelwest.removeAll();
			panelwestcontainer.removeAll();
            buildGuipanelwest();
            panelwest.revalidate();
			panelwestcontainer.revalidate();
        }
	}
    
	//save objects
	class savelistener implements ActionListener{//select save file
		public void actionPerformed(ActionEvent event) {
			JFileChooser filesave = new JFileChooser();
			filesave.showSaveDialog(frame);
			saveFile(filesave.getSelectedFile());
		}
	}
	
	//do the actual saving
	private void saveFile(File file) {
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
			
			//all saved variables
			os.writeObject(gridsize);
			os.writeObject(totalgrid);
			os.writeObject(diffusion);
			os.writeObject(stepcounter);
			os.writeObject(speciescounter);
			os.writeObject(resourcecounter);
			os.writeObject(evolutioncounter);
			os.writeObject(cells);
			os.writeObject(speciesarraylist);
			os.writeObject(resourcearraylist);
			os.writeObject(evolutionarraylist);
			
			os.close();
		}catch(IOException ex){
			
		}
	}
	
	//load savefile
	class loadlistener implements ActionListener{//select save file
		public void actionPerformed(ActionEvent event) {
			JFileChooser fileload = new JFileChooser();
			fileload.showOpenDialog(frame);
			loadFile(fileload.getSelectedFile());
		}
	}
	
	//do the loading
	private void loadFile(File file) {
		try {
			//BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			speciesarraylist.clear();
			resourcearraylist.clear();
			evolutionarraylist.clear();
			panelwest.removeAll();
			
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
			
			//load variables and store in memory
			gridsize = (int) is.readObject();
			totalgrid = (int) is.readObject();
			diffusion = (boolean) is.readObject();
			stepcounter = (int) is.readObject();
			speciescounter = (int) is.readObject();
			resourcecounter = (int) is.readObject();
			evolutioncounter = (int) is.readObject();
			cells = (cell[]) is.readObject();
			speciesarraylist = (ArrayList<Object>) is.readObject();
			resourcearraylist = (ArrayList<Object>) is.readObject();
			evolutionarraylist = (ArrayList<Object>) is.readObject();
			
			is.close();
                        
			if(diffusion == true) {
				diffusionbutton.setText("turn diffusion off");
			}else {
				diffusionbutton.setText("turn diffusion on");
			}
			panelsouthcontainer.revalidate();
			
			buildGuipanelwest();
			
			onlypaint = true;
			try {
				runhelper();
			} catch (InterruptedException ev) {
				// TODO Auto-generated catch block
				ev.printStackTrace();
			}
		}catch(Exception ex){
			
		}
	}
	
	//show copyright info
	class infolistener implements ActionListener{//select save file
		public void actionPerformed(ActionEvent event) {
			JOptionPane.showMessageDialog(null, "Copyright 2020, Laurens Edwards, All rights reserved.");
		}
	}
	
	//change gridsize button listener
	class gridsizelistener implements ActionListener{//select save file
		public void actionPerformed(ActionEvent event) {
			if(shownewspeciesform == 1) {
				shownewspeciesform = 0;
				
				frame.remove(panelsouth_east);
				frame.revalidate();//update panel
				System.out.println(shownewspeciesform);
			}else if (shownewspeciesform == 2) {
				shownewspeciesform = 0;
				
				frame.remove(panelsouth_east);
				frame.revalidate();//update panel
				System.out.println(shownewspeciesform);
			}else if (shownewspeciesform == 0) {
				shownewspeciesform = 2;
			}
			
			//show gridsize form
			if(shownewspeciesform == 2) {
				panelsouth_east = new JPanel();
				panelsouth_east.setBackground(Color.BLACK);
				panelsouth_east.setLayout(new BoxLayout(panelsouth_east, BoxLayout.Y_AXIS));
					
				frame.add(BorderLayout.EAST, panelsouth_east);
				
				JLabel gridsizelabel = new JLabel("grid size:");
				panelsouth_east.add(gridsizelabel);
				gridsizefield = new JTextField();
				panelsouth_east.add(gridsizefield);
				
				JButton changegridsize = new JButton("change gridsize");
				changegridsize.addActionListener(new changegridsizelistener());
				panelsouth_east.add(changegridsize);
				
			}
			panelsouthcontainer.revalidate();
		}
	}
	
	//change the gridsize
	class changegridsizelistener implements ActionListener{//add species form
		public void actionPerformed(ActionEvent event) {
			String gridsizestring = gridsizefield.getText();
			
			if(gridsizestring.isEmpty() == false) {//no empty field allowed as input
				int oldgridsize = gridsize, oldtotalgrid = totalgrid, oldi = 0, newi = 0;
				double gridsizedouble = (double) Double.valueOf(gridsizestring);
				gridsize = (int) gridsizedouble;
				totalgrid = gridsize * gridsize;
				int numberofcells = cells.length, x = 0, y = 0, availableresource;
				cell[] tempcells = new cell[totalgrid];
				
				if(totalgrid > numberofcells) {//grid gets bigger; add new cells
					for(int i = 0; i < totalgrid; i++) {//go through allcells
						if(x < oldgridsize && y < oldgridsize) {//check if old cell; if yes add old cell to temp array
							tempcells[i] = cells[oldi];
							tempcells[i].setxlocation(x);
							tempcells[i].setylocation(y);
							
							if(resourcearraylist.size() != 0) {
								int q = 0;
								for(Object item : resourcearraylist) {//for each resource get selected resource and add to new cell
									if(item instanceof resource) {
										resource r = (resource) item;
										availableresource = cells[oldi].getselectedresource(q);
										tempcells[i].setresourceavailable(availableresource);
									}
									q++;
								}
							}
							
							if(evolutionarraylist.size() != 0) {//for every evolution in new cell add 0.0
								species cellspecies = cells[oldi].getspeciesvalue();
								
								int cellspeciesid;
								if(cellspecies != null) {
								cellspeciesid = cellspecies.getspeciesid();
								}else {
									cellspeciesid = 0;
								}
								
								for(Object item : evolutionarraylist) {
									if(item instanceof evolution) {
										evolution e = (evolution) item;
										if(cellspeciesid != 0) {//set parameter of old cell to new parameter
											double oldparameter = e.getparameterincell(oldi);
											e.changegridevolution(oldparameter);
										}else {
											e.changegridevolution(0.0);//empty cell
										}
									}
								}
							}
							
							oldi++;//count cell number of old cells array
						}else {//else new cell; add new cell to temp array
							tempcells[i] = new cell();
							tempcells[i].setxlocation(x);
							tempcells[i].setylocation(y);
							
							if(resourcearraylist.size() != 0) {
								for(Object item : resourcearraylist) {//for each resource get selected resource and add to new cell
									if(item instanceof resource) {
										resource r = (resource) item;
										int amountpercell = r.getamountpercel();
                    if(r instanceof global_resource){
                      tempcells[i].setresourceavailable(amountpercell);
                    }else{
                        tempcells[i].setresourceavailable(0);
                    }
									}
								}
							}
							
							if(evolutionarraylist.size() != 0) {//for every evolution in new cell add 0.0
								for(Object item : evolutionarraylist) {
									if(item instanceof evolution) {
										evolution e = (evolution) item;
										
										e.changegridevolution(0.0);
									}
								}
							}
						}
						
						x++;
						if(x == (gridsize)) {//0 to gridsize on one row
							x = 0;
							y++;
						}
					}
				}else if(totalgrid < numberofcells) {//grid gets smaller; remove cells
					for(int i = 0; i < oldtotalgrid; i++) {//old grid is now bigger than new grid
						if(x < gridsize && y < gridsize) {//add removable old cells to array
							tempcells[newi] = cells[i];
							tempcells[newi].setxlocation(x);
							tempcells[newi].setylocation(y);
							
							if(resourcearraylist.size() != 0) {
								int q = 0;
								for(Object item : resourcearraylist) {//for each resource get selected resource and add to new cell
									if(item instanceof resource) {
										resource r = (resource) item;
										availableresource = cells[i].getselectedresource(q);
										tempcells[newi].setresourceavailable(availableresource);
									}
									q++;
								}
							}
							
							if(evolutionarraylist.size() != 0) {//for every evolution in new cell add 0.0
								species cellspecies = cells[i].getspeciesvalue();
								
								int cellspeciesid;
								if(cellspecies != null) {
								cellspeciesid = cellspecies.getspeciesid();
								}else {
									cellspeciesid = 0;
								}
								
								for(Object item : evolutionarraylist) {
									if(item instanceof evolution) {
										evolution e = (evolution) item;
										if(cellspeciesid != 0) {//set parameter of old cell to new parameter
											double oldparameter = e.getparameterincell(i);
											e.changegridevolution(oldparameter);
										}else {
											e.changegridevolution(0.0);//empty cell
										}
									}
								}
							}
							newi++;
						}else {//nothing happens because they are outside of grid
							//System.out.println("Skip cell ");
						}
						
						x++;
						if(x == (oldgridsize)) {//0 to gridsize on one row
							x = 0;
							y++;
						}
					}
				}
				
				//add new cells
				cells = tempcells;
				
				if(evolutionarraylist.size() != 0) {//for every evolution in new cell add 0.0
					for(Object item : evolutionarraylist) {
						if(item instanceof evolution) {
							evolution e = (evolution) item;
							
							e.changegridevolution2();
						}
					}
				}
				
				onlypaint = true;
				try {
					runhelper();
				} catch (InterruptedException ev) {
					ev.printStackTrace();
				}
			}
		}
	}
}