package stops;

import java.util.*;

public class RoutingTable extends Object{
    Map<Stop,RoutingEntry> des = new HashMap<Stop, RoutingEntry>();
    Stop initialStop;
    public RoutingTable(Stop initialStop){
        // destination is the initial stop
        this.initialStop = initialStop;
        this.addOrUpdateEntry(initialStop,0,initialStop);
        //des.put(initialStop,new RoutingEntry(initialStop,0));
    }
    public void addNeighbour(Stop neighbour){
        if(!des.containsKey(neighbour)) {
            this.addOrUpdateEntry(neighbour, initialStop.distanceTo(neighbour), neighbour);
            //des.put(neighbour,new RoutingEntry(neighbour,initialStop.distanceTo(neighbour)));
        }
        else{}
    }

    public boolean addOrUpdateEntry(Stop destination, int newCost, Stop intermediate){
        if(!des.containsKey(destination)){
            des.put(destination,new RoutingEntry(intermediate,newCost));
            return true;
        }
        else if (des.containsKey(destination) && des.get(destination).cost>newCost){
            des.put(destination,new RoutingEntry(intermediate,newCost));
            return true;
        }
        else{
            return false;
        }
    }

    public int costTo(Stop stop){
        if(des.containsKey(stop)) return des.get(stop).cost;
        else return Integer.MAX_VALUE;}

    public Map<Stop,Integer> getCosts(){
        Map<Stop, Integer> out = new HashMap<Stop, Integer>();
        for (Stop stop: des.keySet()){
            out.put(stop,des.get(stop).cost);
        }
        return out;
    }

    public Stop getStop(){return initialStop;}

    public Stop nextStop(Stop destination){
        if(des.containsKey(destination)) {
            return des.get(destination).nextStop;
        }
        else {
            return null;
        }
    }

    public void synchronise(){
        List<Stop> stops = this.traverseNetwork();
        Boolean flag;
        do {
            flag = false;
            for (Stop stop : stops) {
                flag = flag||this.transferEntries(stop);
            }
        }while(flag);
    }

    public boolean transferEntries(Stop other){
        Boolean flag = false;
        /*
        if(!this.getStop().getNeighbours().contains(other)){
            return false;
        }

         */
        RoutingTable otherTable = other.getRoutingTable();
        for(Stop stop:des.keySet()){
            if(!otherTable.des.containsKey(stop))
                flag = flag || otherTable.addOrUpdateEntry(stop,other.distanceTo(stop),des.get(stop).nextStop);
            else{
                if(otherTable.getCosts().get(stop)>des.get(stop).getCost()&&des.get(stop).getCost()!=0){
                    flag = flag || otherTable.addOrUpdateEntry(stop,other.distanceTo(initialStop),initialStop);
                }
            }
            /*
            if (!otherTable.traverseNetwork().contains(stop)){
                flag = flag || otherTable.addOrUpdateEntry(stop,des.get(stop).getCost(),des.get(stop).nextStop);
            }

             */
        }
        return flag;
    }

    public List<Stop> traverseNetwork(){
        List<Stop> list = new ArrayList<Stop>();
        Stack<Stop> stack = new Stack<Stop>();
        for(Stop item: des.keySet()){
            stack.push(item);
        }
        while(!stack.empty()){
            Stop current = stack.pop();
            for(Stop neighbour:current.getNeighbours()){
                if(!list.contains(neighbour))
                    stack.push(neighbour);
            }
            if(!list.contains(current)) list.add(current);
        }
        return list;
    }
}
