# webgraph

### Instructions
Download _name.graph_ and _name.properties_ from WebGraph, where _name_ is the dataset name.
````
mvn install
mvn exec:java -Dexec.mainClass="it.unimi.dsi.webgraph.BVGraph" -Dexec.args="-o -O -L name"
mvn exec:java -Dexec.mainClass="Transforma" -Dexec.args="name"
````

### Result
The result is a binary file name.adj with the following format:
- An integer with the number of nodes
- A long with the number of arcs
- The adjacency lists of each node, the start of each adjacency list is 
represented with a negative number. The first adjacecny list starts after 
reading -1 and ends before reading -2
