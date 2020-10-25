CAS (Cellular Automata Simulator) is a tool that allows to place
different cellular automata on a single grid. The following cellular
automata are included:

- Grower: this is the most basic cellular automaton and can only die or
replicate in a neighbouring cell; (parameters\*: growth rate, density
dependent death rate, background death rate, number of replicators
required to replicate, minimum number of replicators required to
survive, resource id consumed, resource id produced).

- Predator: this cellular automaton dies and replicates into another
cell that is considered their respective prey; (parameters\*: growth
rate, density dependent death rate, background death rate, number of
replicators required to replicate, minimum number of replicators
required to survive, resource id consumed, resource id produced, prey
id).

- Cooperator: the cooperator dies and replicates only in cells that are
neighbouring a cell that is occupied by their cooperator; (parameters\*:
growth rate, density dependent death rate, background death rate, number
of replicators required to replicate, minimum number of replicators
required to survive, resource id consumed, resource id produced,
cooperator id).

- Game of life: This is the cellular automaton invented by John Conway
in which a cell dies when it has 4 or more neighbours of their own kind
and also when it has less than 2 neighbours. Propagation happens when a
cell is surrounded by three neighbours of its own kind.; (minimum number
of replicators required to survive, resource id consumed, resource id
produced).

- Virus: A virus can only be placed in a currently alive cell and will
stay there as long as the incubation time lasts. If the host cell dies
during this time the virus will also die. After the incubation time the
virus will have a chance to spread to all neighbouring cells based on
its growth rate. In this process the virus has a chance of killing its
host which is based on the lethality of the virus. The virus will always
die after the incubation time has ended.; (parameters: growth rate, host
species id, incubation time, virus lethality).

Resources can be added to control the growth of automata as well as to
enforce interactions between automata. When an resource is added all
cells on the grid will get the amount that is specified. From there on
each step the new amount that is available per cell will be calculated
depending on the current status of the cell. (parameters\*: amount
available per cell, regeneration, diffusion). There are two types of
resources: local and global resources. Global resources will be
available globally on the grid and will according to the regenerate
parameter regenerate in each cell. Local resources can only regenerate
on specified cells on the grid. Marking cells as regenerating cells can
be done by clicking on the respective resource and then clicking on a
cell in the grid. Diffusion of resources can be assigned either by
entering 0 or 1 in the form. When diffusion is turned on cells with a
higher amount of resources will diffuse to surrounding cells with lower
amounts of nutrients.

Evolution is a trait that can be added to specific parameters of each
automaton. The parameters that can be added are:

- Growth rate: evolution in this parameter will allow the growth rate to
change randomly with the mutation speed every reproduction cycle.

- Density dependent death rate: evolution in this parameter will allow
the density dependent death rate to change randomly with the mutation
speed every reproduction cycle.

- Background death rate: evolution in this parameter will allow the
background death rate to change randomly with the mutation speed every
reproduction cycle.

- Mutation rate: evolution in this parameter will allow the mutation
rate rate to change randomly with the mutation speed every reproduction
cycle.

- Evolving parameter: the evolving parameter is a parameter that does
not correspond to any parameter that can be manually set when creating a
new automaton but is instead for every new automaton set to 0.5.
Allowing evolution in this parameter has different effects depending on
the interaction between automata. If the evolving parameter between two
interacting automate is bigger than 0.1 no interaction will take place.
If the difference is smaller than 0.1 the replication chance is
calulated as follows: replicaterate = replicaterate \* ((0.10 -
evolvingparameterdiff) \* 10). For example a predator with evolving
parameter 0.5 can not eat a prey with evolving parameter 0.2, a
cooperator can not replicate next to its cooperator when the difference
is too big and a virus can not infect a neighbouring host cell if the
difference is too big.

- Lethality (virus only): evolution in this parameter will allow the
lethality rate to change randomly with the mutation speed every
reproduction cycle.

- Resource production: evolution in this parameter will allow the
resource production to change randomly with the mutation speed every
reproduction cycle.

(parameters\*: species id, mutation chance, mutation speed)

Synchronous updating will update each cell simultaneously while
asynchronous updating will update each cell individually.

Turning diffusion on will make sure that each cell is always filled.
empty cells will be filled randomly.

\*All parameters when not specified are set to 0

\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*

Version 1.3

This project is licensed under the terms of the Creative Commons
Attribution-ShareAlike 4.0 International Public License license

Copyright 2020, Laurens Edwards, All rights reserved.

https://github.com/lwaw/Cellular\_Automata\_Simulator

\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*
