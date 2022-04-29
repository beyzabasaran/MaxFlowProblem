import java.io.*;
import java.util.*;

public class Main {
    public static void main (String[] args) throws FileNotFoundException {

        try {

            ArrayList<String> vertices = new ArrayList<>();//Keeping vertex names based on their indexes
            BufferedReader br = new BufferedReader(new FileReader("graph.txt"));

            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equalsIgnoreCase("")) {
                    String[] data = line.split("\\s+");
                    String source = data[0];
                    String destination = data[1];

                    if (!vertices.contains(source)) {
                        vertices.add(source);
                    }
                    if (!vertices.contains(destination)) {
                        vertices.add(destination);
                    }
                }
            }
            int numOfVertices = vertices.size();
            HashMap<String, Integer> hm_vertices = new HashMap(numOfVertices);
            Graph graph = new Graph(numOfVertices);

            for (int i = 0; i < vertices.size(); i++) {
                hm_vertices.put(vertices.get(i), i);
            }

            br = new BufferedReader(new FileReader("graph.txt"));
            while ((line = br.readLine()) != null) {
                if (!line.equalsIgnoreCase("")) {
                    String[] data = line.split("\\s+");
                    String source = data[0];
                    String destination = data[1];
                    int src = hm_vertices.get(source);//using hash map to get given key's value
                    int dest = hm_vertices.get(destination);//in this case, it's key is string and it's value is index
                    int capacity = Integer.valueOf(data[2]);
                    graph.addEdge(src, dest, capacity);
                }
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the source:");
            String src = scanner.nextLine().toUpperCase();
            System.out.println("Enter the destination:");
            String dest = scanner.nextLine().toUpperCase();
            int source = hm_vertices.get(src);
            int destination = hm_vertices.get(dest);
            FordFulkerson fordFulkerson = new FordFulkerson(numOfVertices);
            int maxFlow = fordFulkerson.maxFlowMinCut(graph, source, destination);
            System.out.println("Maximum package delivery: " + maxFlow);

            //---------------------------------------------------------------
            //Max flow Optimization by increasing edge capacities
            Graph newGraph = graph;
            ArrayList<Pair> cutting_edges_List = new ArrayList<Pair>();//keeping cutting edges in a list to have constant size loop
            Iterator<Pair> iterator = fordFulkerson.getCutSet(); //Set<Pair> --> iterator

            int increase = 1; //capacity increase value
            int newFlow =0;
            int maxIncrease=0;
            while (iterator.hasNext()) {//Create cutting edge list
                Pair pair = iterator.next();
                cutting_edges_List.add(pair);

                int x= Math.min(graph.incomingCapacity(graph, pair.source),graph.outgoingCapacity(graph, pair.destination));
                int y =Math.abs(x- graph.getEdge(pair.source,pair.destination));
                if(y>maxIncrease){
                   maxIncrease=y;//find max capacity difference between in and out capacities of cutting edges
                    //for example, B-E has 4 capacity it can increase maximum by 5 since its incoming capacity is 9, G-F by 6 and B-D by 2
                    //if we take maximum difference 6 as limit we can assure that we have tried all possible increase amount of the remaining edges
                    //During optimization in big graph if the edge can not be optimized, increase was getting too big to finish the while loop
                    //That's why I found the max increase amount this way instead of sum of outgoing edges of source vertex.
                }
            }

            System.out.println("Cutting edges are:");
            fordFulkerson.printCutSet(cutting_edges_List,vertices);

            while (true) {
                //Increasing cutting-edges until income and outcome flow capacities do not allow
                for (int i = 0; i < cutting_edges_List.size(); i++) {
                    Pair pair = cutting_edges_List.get(i);

                    int src_outgoing_capacity = graph.outgoingCapacity(graph, pair.source);
                    int src_incoming_flow = fordFulkerson.maxFlowMinCut(graph,source, pair.source);
                    int dest_outgoing_capacity = graph.outgoingCapacity(graph, pair.destination);
                    int path_flow = fordFulkerson.maxFlowMinCut(graph,pair.source, pair.destination);

                    maxFlow = fordFulkerson.maxFlowMinCut(newGraph, source, destination);//flow before increasing edge
                    //            income > outcome                     flow < outcome
                    if (src_incoming_flow>src_outgoing_capacity && path_flow< dest_outgoing_capacity) {
                        int newCapacity = graph.getEdge(pair.source, pair.destination) + 1;//increase
                        newGraph.setEdge(pair.source, pair.destination, newCapacity);
                    }

                    newFlow = fordFulkerson.maxFlowMinCut(newGraph, source, destination);//flow after increasing edge
                    if(!(newFlow>maxFlow)){ //After increase of an edge if maxFlow does not increase than it was unnecessary so, decrease
                        int newCapacity = graph.getEdge(pair.source, pair.destination) - 1;
                        newGraph.setEdge(pair.source, pair.destination, newCapacity);
                    }

                }
                /*newGraph.printOptimization(cutting_edges_List, newGraph, vertices); //examining optimization
                System.out.println("Flow is " + newFlow);*/

                int optimizedFlow = graph.incomingCapacity(graph, destination);//sum of dest incoming edges
                int maxStartFlow = graph.outgoingCapacity(graph, source);//sum of src outgoing edges

                if (maxFlow>= optimizedFlow ||!(increase <= maxIncrease)) {

                    System.out.println("--------Optimized Final Graph---------");
                    newGraph.printOptimization(cutting_edges_List, newGraph, vertices);
                    if(cutting_edges_List.size()>0) {
                        System.out.println("Optimized max delivery from source: " + optimizedFlow);
                    }
                    else{
                        System.out.println("Optimized max delivery from source: " + maxFlow);
                    }

                    System.out.println("Max possible delivery from source: " + maxStartFlow);
                    System.out.println("-----------------------------\n"+
                            "--------------------------------------");
                    break;
                }
                increase++;


            }


        }catch (FileNotFoundException e) {
            System.err.println("File Not Found!");
            e.printStackTrace();
        }
        catch (InputMismatchException inputMismatch) {
            System.err.println("Wrong Input Format!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            System.err.println("Exception! "+e.getMessage());
        }

    }
}
