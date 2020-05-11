package stops;

public class RoutingEntry {
    Stop nextStop;
    int cost;

    public RoutingEntry(){
        nextStop=null;
        cost=Integer.MAX_VALUE;
    }

    public RoutingEntry(Stop next, int cost){
        if(next==null||cost<0){
            nextStop=null;
            this.cost=Integer.MAX_VALUE;
        }
        else{
            nextStop=next;
            this.cost=cost;
        }
    }
    public int getCost(){return cost;}
    public Stop getNext(){return nextStop;}
}
