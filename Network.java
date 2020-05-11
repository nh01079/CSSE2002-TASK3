package network;

import exceptions.DuplicateStopException;
import exceptions.TransportFormatException;
import routes.Route;
import stops.Stop;
import vehicles.PublicTransport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Network {
    // arrays: Stop stops[] = new Stop[2];
    List<Stop> stops;
    List<Route> routes;
    List<PublicTransport> vehicles;

    public Network(){
        // creates a new empty network with no stops,
        // vehicles, or routes
        vehicles = new ArrayList<PublicTransport>();
        routes = new ArrayList<Route>();
        stops = new ArrayList<Stop>();
    }

    public Network(String filename) throws IOException, TransportFormatException {
        int input = 0;
        Scanner scnr;
        vehicles = new ArrayList<PublicTransport>();
        routes = new ArrayList<Route>();
        stops = new ArrayList<Stop>();
        try {
            File text = new File(filename);
            scnr = new Scanner(text);
        }
        catch(FileNotFoundException | NullPointerException e) {
            throw new IOException();
        }
        // stop, route, vehicles
        String line = null;
        try {
        while(scnr.hasNextLine()&&input<=3){
            int entries = 0;
            if ((line = scnr.nextLine()).isEmpty())
                throw new TransportFormatException();
            // new row
            try {
                entries = Integer.parseInt(line);
            }
            catch(NumberFormatException e){
                throw new TransportFormatException();
            }
            input ++;
            while(entries>0){
                if (!scnr.hasNextLine()) throw new TransportFormatException();
                line = scnr.nextLine();
                switch(input){
                    case (1): {
                        // stops
                        Stop stop = Stop.decode(line);
                        stops.add(stop);
                        break;
                    }
                    case(2):{
                        //routes
                        Route route = Route.decode(line, this.getStops());
                        routes.add(route);
                        break;
                    }
                    case(3):{
                        //vehicles
                        vehicles.add(PublicTransport.decode(line,this.getRoutes()));
                        break;
                    }
                }
                entries--;
            }}

        }catch (ArrayIndexOutOfBoundsException e) {throw new TransportFormatException();}
        if(scnr.hasNextLine())throw new IOException();
        scnr.close();
    }

    public void addStop(Stop stop) throws DuplicateStopException {
        if(stop==null) return;
        if(stops.contains(stop)) throw new DuplicateStopException();
        stops.add(stop);
    }

    public void addStops(List<Stop> stops) throws DuplicateStopException{
        if(stops.contains(null)) return;
        for(Stop stop: stops){
            if(this.stops.contains(stop)) throw new DuplicateStopException();
        }
        this.stops.addAll(stops);
    }

    public void addVehicle(PublicTransport vehicle){
        if(vehicle==null) return;
        vehicles.add(vehicle);
    }

    public void addRoute(Route route){
        if(route==null) return;
        routes.add(route);
    }
    public List<Route> getRoutes(){
        return new ArrayList<Route>(routes);
    }

    public List<Stop> getStops(){
        return new ArrayList<Stop>(stops);
    }

    public List<PublicTransport> getVehicles(){
        return new ArrayList<PublicTransport>(vehicles);
    }

    public void save(String filename) throws IOException{
        if(filename==null)
            return;
        String outStr= String.valueOf(stops.size());
        for(Stop stop: stops){
            outStr = outStr + "\r\n"+ stop.encode();
        }
        outStr = outStr + "\r\n" + String.valueOf(routes.size());
        for(Route route: routes){
            outStr = outStr + "\r\n"+ route.encode();
        }
        outStr = outStr + "\r\n" + String.valueOf(vehicles.size());
        for(PublicTransport vehicle: vehicles){
            outStr = outStr + "\r\n"+ vehicle.encode();
        }
        Path path = Paths.get(filename);
        byte[] strToBytes = outStr.getBytes();
        Files.write(path, strToBytes);

    }
}
