# webgraph

- Download name.graph and name.properties from WebGraph
- java it.unimi.dsi.webgraph.BVGraph -o -O -L name
- java Transforma name

The result is a binary file name.adj with the following format:
- An integer with the number of nodes
- A long with the number of arcs
- The adjacency lists of each node, the start of each adjacency list is 
represented with a negative number. The first adjacecny list starts after 
reading -1 and ends before reading -2
