public class Actor { // node for graph

    private String name;
    private int distance; // distance from source
    private boolean visited;
    Actor previous;

    public Actor(String n) {
        name = n;
        distance = Integer.MAX_VALUE; // start at infinity
        visited = false;
        previous = null;
    }

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    public String getPrevious() {
        return previous.getName();
    }

    public boolean isVisited() {
        return visited;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setPrevious(Actor a) {
        previous = a;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

}
