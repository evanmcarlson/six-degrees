import java.util.ArrayList;
import java.util.HashMap;

public class AdjList {
    public HashMap<String, ArrayList<Actor>> graph = new HashMap<>(); // actor -> costar (neighbor) array

    public AdjList(int vertices, ArrayList<String> a) { // takes in number of vertices and list of all actors
        for(int i = 0; i < vertices; i++) {
            ArrayList<Actor> costars = new ArrayList<>();
            graph.put(a.get(i), costars);
        }
    }

    public void addEdge(Actor one, Actor two){
        String name1 = one.getName();
        String name2 = two.getName();
        if(graph.get(name1) != null && graph.get(name2) != null) {
            (graph.get(name1)).add(two);
            (graph.get(name2)).add(one);
        }
    }
}
