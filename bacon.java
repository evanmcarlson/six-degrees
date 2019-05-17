import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.*;
import java.io.*;

public class Bacon {

    public ArrayList<String> allMovies; // string array of all movie title
    public ArrayList<String> allActors; // string array of all actor names
    public ArrayList<String> path;  // string array of shortest path

    public HashMap<String, Actor> actorMap; // actor name -> actor object
    public HashMap<String, ArrayList<Actor>> movieToCastMap; // movie name -> cast

    public AdjList adjacency;

    public Bacon() {
        allMovies = new ArrayList<>();
        allActors = new ArrayList<>();
        path = new ArrayList<>();
        actorMap = new HashMap<>();
        movieToCastMap = new HashMap<>();
    }

    public void readFile(BufferedReader in) { // step one - read data

        System.out.println("Please wait while data is read. This should only take a few seconds...");
        JSONParser jsonParser = new JSONParser();

        try {
            String line = in.readLine(); // skip header

            while ((line = in.readLine()) != null) { // while there's more data

                ArrayList<Actor> cast = new ArrayList<>(); // cast of THIS movie. changes with each while() iteration

                String[] identifiers = line.split(",", 3); // parse movie title
                String movie = identifiers[1];

                if (line.contains("[{")) { // a bunch of hard-coded parsing stuff for converting JSON

                    String jsonCast = line.substring(line.indexOf("[{"), line.indexOf("}]") + 2); // string of entire cast in JSON

                    String[] jsonCastArray; // split up actors into an array, still in JSON
                    jsonCastArray = jsonCast.split("},");

                    jsonCastArray[0] = jsonCastArray[0].substring(1); // remove the [ from first actor
                    jsonCastArray[jsonCastArray.length - 1] = jsonCastArray[jsonCastArray.length - 1].substring(0, jsonCastArray[jsonCastArray.length - 1].length() - 2); // remove ] from the last actor


                    for (int i = 0; i < jsonCastArray.length; i++) { // for each actor in JSON format
                        jsonCastArray[i] += "}"; // was removed during our split into array - we add so the array indices are again in valid JSON
                        jsonCastArray[i] = jsonCastArray[i].replace("\"\"", "\""); // replace all "" with "

                        JSONObject jsonObject = null;

                        try {
                            jsonObject = (JSONObject) jsonParser.parse(jsonCastArray[i]); // parse element using json-simple
                        } catch (ParseException parse) {
                            parse.printStackTrace();
                        }

                        String nameString = (String) jsonObject.get("name"); // grab name of actor

                        nameString = nameString.toLowerCase(); // to disable case sensitivity

                        Actor a;

                        if(!allActors.contains(nameString)) { // actor doesn't have a vertex yet
                            allActors.add(nameString); // add name to list
                            a = new Actor(nameString); // make a new instance
                            actorMap.put(nameString, a); // add the instance to actor map
                        }
                        else {
                            a = actorMap.get(nameString); // grab the instance of existing vertex
                        }

                        cast.add(a); // add actor to movie cast
                    }
                    allMovies.add(movie); // add movie to list
                    movieToCastMap.put(movie, cast); // map movie name to its cast array
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void configureGraph() { // step two - configure graph

        adjacency = new AdjList(allActors.size(), allActors); // initialize graph with each actor as a vertex with no edges

        for (int i = 0; i < allMovies.size(); i++) { // iterate through each movie

            String m = allMovies.get(i); // current movie title

            if(movieToCastMap.get(m) != null) { // cast of movie exists (ArrayList<Actor>)

                // add an edge between every actor in the movie
                for (int j = 0; j < movieToCastMap.get(m).size() - 1; j++) {
                    for (int k = j + 1; k < movieToCastMap.get(m).size(); k++) {
                        adjacency.addEdge(movieToCastMap.get(m).get(j), movieToCastMap.get(m).get(k));
                    }
                }

            }

        }

    }


    public void traverse(Actor start, Actor end) { // step three - traverse with BFS (breadth first search)

        start.setDistance(0);

        Queue<Actor> toVisit = new LinkedList<Actor>(); // our queue of actors to visit
        toVisit.add(start);

        while (!toVisit.isEmpty()) { // while queue isn't empty

            Actor curr = toVisit.poll(); // pop head - FIFO

            curr.setVisited(true);

            for (int i = 0; i < adjacency.graph.get(curr.getName()).size(); i++) { // for every one of current actors' costars/neighbors

                if (!adjacency.graph.get(curr.getName()).get(i).isVisited()) { // if actor has not been visited

                    toVisit.add(adjacency.graph.get(curr.getName()).get(i)); // add actor to queue
                    adjacency.graph.get(curr.getName()).get(i).setDistance(curr.getDistance() + 1); // set distance = curr's + 1
                    adjacency.graph.get(curr.getName()).get(i).setPrevious(curr); // set previous for backtracking

                    if (adjacency.graph.get(curr.getName()).get(i).getName().equals(end.getName())) { // costar[i].name = end.name

                        Actor it = adjacency.graph.get(curr.getName()).get(i); // get the actor instance

                        for(int y = 0; y < it.getDistance(); y++) { // backtrack and document path
                            path.add(it.getPrevious());
                            it = it.previous;
                        }

                        return; // found shortest path

                    }
                }
            }
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = "/Users/evancarlson/IdeaProjects/Six Degrees/src/tmdb_5000_credits.csv";
        Bacon bacon = new Bacon();
        File input = new File(fileName);

        /*if (0 < args.length) {
            input = new File(args[0]);
        } else {
            throw new FileNotFoundException();
        }*/

        try {
            Reader reader = new FileReader(input);
            BufferedReader in = new BufferedReader((reader));

            bacon.readFile(in);

            in.close();
            reader.close();
        }
        catch (IOException io) {
            io.printStackTrace();
        }

        bacon.configureGraph();

        while(true) {

            Scanner scanner = new Scanner(System.in);
            Actor from = null;
            Actor to = null;

            while(from == null) { // make sure actor exists in graph
                System.out.print("Actor 1 name: ");
                String x = scanner.nextLine();
                x = x.toLowerCase();
                if(bacon.adjacency.graph.keySet().contains(x)) {
                    from = bacon.actorMap.get(x);
                }
                else {
                    System.out.println("That actor doesn't exist.");
                }
            }

            while(to == null) { // make sure actor exists in graph
                System.out.print("Actor 2 name: ");
                String x = scanner.nextLine();
                x = x.toLowerCase();
                if(bacon.adjacency.graph.keySet().contains(x)) {
                    to = bacon.actorMap.get(x);
                }
                else {
                    System.out.println("That actor doesn't exist.");
                }
            }

            bacon.traverse(from, to); // bfs find shortest path

            // print chain
            System.out.print(from.getName() + " -> ");
            Collections.reverse(bacon.path);
            for(int i = 0; i < bacon.path.size() - 1; i++) {
                System.out.print(bacon.path.get(i) + " -> ");
            }
            System.out.println(to.getName());

            System.out.println("Program complete.");

        }

    }

}
