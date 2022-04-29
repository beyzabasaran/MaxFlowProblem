import java.util.*;
/* Ford Fulkerson code implementation from:
https://www.sanfoundry.com/java-program-implement-ford-fulkerson-algorithm/
https://www.geeksforgeeks.org/minimum-cut-in-a-directed-graph/*/

public class FordFulkerson {

    private int numOfVertices;
    private Set<Pair> cutSet;


    public FordFulkerson(int numOfVertices) {
        this.numOfVertices = numOfVertices;
        this.cutSet = new HashSet<Pair>();

    }

    public int maxFlowMinCut(Graph graph, int src, int dest) {//finding max flow and cutting-edges of the network
        int u;
        int v;
        int maxFlow=0;
        int[][] residualGraph=new int[numOfVertices][numOfVertices];

        for (int i = 0; i < graph.size(); i++) {
            for (int j = 0; j < graph.size(); j++) {
                residualGraph[i][j]=graph.getEdge(i,j);//at first, residual graph edge capacity = graph edge capacity
            }
        }

        int[] parent = new int[numOfVertices];//for storing paths

        // Augmenting max path flows to assign residual graph edge capacities
        while (bfs(residualGraph, src, dest, parent)) {//if there is an existing path src to dest. Check by comparing with parent array in bfs

            int pathFlow = Integer.MAX_VALUE;
            for (v = dest; v != src; v = parent[v]) {
                u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);//maximum flow through the path,min capacity
            }

            // updating residual capacity of the edge and reverse edge in the residual graph based on possible max flow through that path
            for (v = dest; v != src; v = parent[v]) {//through one possible path through src->dest finding min capacity
                u = parent[v];
                residualGraph[u][v] -= pathFlow;//subtract from forward edge and that is the package left
                residualGraph[v][u] += pathFlow;//add to reverse edge since package can send
            }
            maxFlow+=pathFlow;//adding each max flow through paths to total max flow of network
        }

        // finding vertices reachable from src
        boolean[] visited = new boolean[numOfVertices];
        for(int i=0; i< visited.length; i++){
            visited[i]=false;
        }
        dfs(residualGraph, src, visited);//mark reachable vertices from source as visited

        // Cutting-edges are vertex pairs from reachable vertex to non-reachable vertex in the original graph
        for (int i = 0; i < numOfVertices; i++) {
            for (int j = 0; j < numOfVertices; j++) {
                if (graph.getEdge(i,j) > 0 && visited[i] && !visited[j]) {
                    if(i!=src && j!= dest) {
                        Pair pair = new Pair(i, j, graph.getEdge(i, j));
                        cutSet.add(pair);//cutSet=cutting-edge pairs
                    }
                }
            }
        }
       return maxFlow;
    }

    public static boolean bfs(int[][] residualGraph, int src, int dest, int[] parent) {

        boolean[] visited = new boolean[residualGraph.length];// to avoid cycles marking visited edge as true
        Queue<Integer> queue = new LinkedList<Integer>();

        queue.add(src);// enqueue source vertex and mark source vertex as visited
        visited[src] = true;
        parent[src] = -1;

        //bfs
        while (!queue.isEmpty()) {
            int u = queue.poll();//returns the element at the front
            for (int v = 0; v < residualGraph.length; v++) {
                if (residualGraph[u][v] > 0 && !visited[v]) {
                    queue.add(v);
                    visited[v] = true;
                    parent[v] = u;
                }
            }
        }
        //If  source -> dest is reachable than there is path return true,else return false
        return (visited[dest] == true);
    }

    public static void dfs(int[][] residualGraph, int src, boolean[] visited) {

        visited[src] = true;
        for (int i = 0; i < residualGraph.length; i++) {
            if (residualGraph[src][i] > 0 && !visited[i]) {//if there is path and not visited
                dfs(residualGraph, i, visited);
            }
        }
    }

    public  Iterator<Pair>  getCutSet () {//get cut set to use in main for optimization
        return  cutSet.iterator();
    }
    public void printCutSet(ArrayList<Pair>cutting_edges_List,ArrayList<String>vertices){
        for (int i = 0; i < cutting_edges_List.size(); i++) {
            Pair pair = cutting_edges_List.get(i);
            System.out.println(vertices.get(pair.source )+ "-" + vertices.get(pair.destination));
        }
    }

}

class Pair{//Nested Class to keep cutting edge pairs
    public int source;
    public  int destination;
    public  int initialCapacity;

    public Pair (int source, int destination, int initialCapacity) {
        this.source = source;
        this.destination = destination;
        this.initialCapacity=initialCapacity;
    }

}


