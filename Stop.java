package stops;

import exceptions.NoNameException;
import exceptions.OverCapacityException;
import exceptions.TransportFormatException;
import passengers.Passenger;
import routes.Route;
import utilities.Writeable;
import vehicles.PublicTransport;

import java.util.*;

public class Stop extends Object implements Writeable {
    String name;
    int x;
    int y;
    List<Route> routes = new ArrayList<Route>();
    List<Stop> neighbours = new ArrayList<Stop>();
    List<Passenger> passengers = new ArrayList<Passenger>();
    List<PublicTransport> vehicles = new ArrayList<PublicTransport>();
    Map<Stop,Queue> nextStop = new HashMap<Stop,Queue>();
    RoutingTable table;

    public Stop(String name, int x, int y){
        // throws NoNameException if name is null or empty
        if (name == null || name.isEmpty())
            throw new NoNameException ();
        else {
            //name = name.trim();
            name = name.replaceAll("\\t","");
            name = name.replaceAll("\\r", "");
            name = name.replaceAll("\\n", "");
            this.name = name;
            this.x = x;
            this.y = y;
            table = new RoutingTable(this);
        }
    }

    public String getName() {
        return name;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void addRoute(Route route){
        if (!routes.contains(route) && route!=null)
            this.routes.add(route);
    }

    public List<Route> getRoutes(){
        return new ArrayList<Route>(routes);
    }

    public void addNeighbouringStop(Stop neighbour){
        if (!neighbours.contains(neighbour) && neighbour != null) {
            neighbours.add(neighbour);
            table.addNeighbour(neighbour);
        }
    }

    public List<Stop> getNeighbours(){
        return new ArrayList<Stop>(neighbours);
    }

    public void addPassenger (Passenger passenger){
        if(passenger == null) return;
        if(passenger.getDestination()==null) {
            passengers.add(passenger);
        }
        else{
            Stop next = table.nextStop(passenger.getDestination());
            passengers.add(passenger);
            if(nextStop.containsKey(next)){
                nextStop.get(next).add(passenger);
            }
            else {
                Queue<Passenger> queue = new LinkedList<Passenger>();
                
                queue.add(passenger);
                nextStop.put(next,queue);
            }
        }

        
    }

    public List<Passenger> getWaitingPassengers(){
        return new ArrayList<Passenger>(passengers);
    }

    public boolean isAtStop(PublicTransport transport){
        return vehicles.contains(transport);
    }

    public List<PublicTransport> getVehicles(){
        return new ArrayList<PublicTransport>(vehicles);
    }

    public void transportArrive(PublicTransport transport){
        // records a public transport vehicle arriving at this stop
        if (transport!= null && !vehicles.contains(transport)) {
            vehicles.add(transport);
            // unloading the passengers to the stop
            passengers.addAll(transport.unload());
        }
    }

    public void transportDepart(PublicTransport transport, Stop nextStop){
        if (vehicles.contains(transport) && transport!=null && nextStop!=null){
            vehicles.remove(transport);
            transport.travelTo(nextStop);
            Stop stop = null;
            Queue<Passenger> queue = null;
            Iterator<Stop> iterator = this.nextStop.keySet().iterator();
            while(iterator.hasNext()){
                stop = iterator.next();
            }
            Boolean flag = stop.hashCode()==nextStop.hashCode();
            flag = stop.equals(nextStop);
            if(this.nextStop.containsKey(nextStop)){
               // Queue<Passenger> queue = this.nextStop.get(nextStop);
                while(!queue.isEmpty()){
                    try {
                        transport.addPassenger(queue.poll());
                    }
                    catch (OverCapacityException e){
                        break;
                    }
                }

            }
        }
    }

    public int distanceTo (Stop stop){
        if (stop!=null)
            return Math.abs(stop.getX()- x) + Math.abs(stop.getY()-y);
        else
            return -1;
    }

    public boolean equals(Object other){
        if (other instanceof Stop){
            boolean flag = true;
            flag = flag && ((Stop) other).getName().equals(this.name);
            flag = flag && ((Stop) other).getX()==this.x && ((Stop) other).getY()==this.y;
            List<Route> List1 = ((Stop) other).getRoutes();
            Set<Route> set1 = new HashSet<Route>();
            for(Route route: List1){
                set1.add(route);
            }
            List<Route> List2 = this.getRoutes();
            Set<Route> set2 = new HashSet<Route>();
            for(Route route: List2){
                set2.add(route);
            }
            return flag&&set1.equals(set2);
        }
        else
            return false;
    }

    public int hashCode(){
        Set<Route> set1 = new HashSet<Route>();
        for (Route r:routes){
            set1.add(r);
        }
        return Objects.hash(name,x,y,set1);
    }


    public String toString(){
        return String.format("%s:%d:%d",name,x,y);
    }

    public static Stop decode(String stopString) throws TransportFormatException{
        try {
            String stop[] = stopString.split(":",3);
            if(stop[0]==null||stop[0].isEmpty())
                throw new TransportFormatException();
            return new Stop(stop[0], Integer.parseInt(stop[1].trim()), Integer.parseInt(stop[2].trim()));
        }
        catch (NumberFormatException| NullPointerException e){
            throw new TransportFormatException();
        }
    }

    public String encode(){
        return this.toString();
    }

    public RoutingTable getRoutingTable(){return table;}


}

