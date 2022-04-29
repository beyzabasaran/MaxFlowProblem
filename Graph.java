import java.util.ArrayList;

public class Graph {
    private int numOfVertices;
    private int[][] adj_matrix;

    public Graph(int numOfVertices){
        this.numOfVertices= numOfVertices;
        adj_matrix= new int[numOfVertices][numOfVertices];

        for(int i=0; i<numOfVertices;i++){
            for(int j=0; j<numOfVertices;j++){
                adj_matrix[i][j]=0;
            }
        }
    }
    public void addEdge(int src, int dest, int capacity){
        adj_matrix[src][dest]= capacity;

    }

    public int size(){
        return numOfVertices;

    }

    public boolean hasEdge(int src,int dest){
        if(adj_matrix[src][dest] !=0){
            return  true;
        }
        return  false;
    }

    public int getEdge(int src,int dest){
        return adj_matrix[src][dest];

    }

    public void setEdge(int src, int dest, int capacity){
        adj_matrix[src][dest]= capacity;
    }

    public int incomingCapacity(Graph graph,int dest){
        int incoming_capacity=0;
        for(int i=0; i< graph.size();i++){
            if(graph.hasEdge(i,dest)&& dest !=0){
                // System.out.println("from:"+i+"-to:"+pair.source);
                incoming_capacity+= graph.getEdge(i,dest);
            }
        }
        // System.out.println("incomingCap:"+incoming_capacity);
        return incoming_capacity;

    }
    public int outgoingCapacity(Graph graph,int src){
        int outgoing_capacity=0;
        for(int i=0; i< graph.size();i++){
            if(graph.hasEdge(src,i)&& src!=graph.size()-1){
                // System.out.println("from:"+pair.source+"-to:"+i);
                outgoing_capacity+= graph.getEdge(src,i);
            }
        }
        //System.out.println("outgoingCap:"+outgoing_capacity);
        return  outgoing_capacity;
    }

    public void printOptimization(ArrayList<Pair>cutSet,Graph newGraph,ArrayList<String> vertices) {

        System.out.println("-----------------------------");
        for(int i=0;i<cutSet.size();i++) {
            Pair pair=cutSet.get(i);
            if (newGraph.getEdge(pair.source,pair.destination) > pair.initialCapacity) {
                int x = newGraph.getEdge(pair.source,pair.destination) - pair.initialCapacity;
                System.out.println(vertices.get(pair.source) + "-" + vertices.get(pair.destination) + " increased by " + x);
            }
        }
    }

}
