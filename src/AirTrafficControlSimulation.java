import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.ArrayList;



public class AirTrafficControlSimulation {

  public static final int MIN_FLIGHT_SPACING =10 ;
    ArrayDeque<Flight> arrivalQueue = new ArrayDeque<>();
    ArrayDeque<Flight> departureQueue = new ArrayDeque<>();
    ArrayList<Flight> arrivalStatistics = new ArrayList<>();
    ArrayList<Flight> departureStatistics = new ArrayList<>();

    int numberOfDivertedArrivals = 0;
    int numberOfDepartures = 0;
    int timeInterval = 0;
    int flightNumberCounter;
    int fltSpacingCounter = 0;
    int numberOfArrivals = 0;
    int timerCounter = 0;

    int numberofdenieddepartures = 0;

    int arrivalQueueEmpty = 0;

    int departureQueueEmpty = 0;

    Random r = new Random(System.nanoTime());
    public int getPoissonRandom(double mean) {
        double L = Math.exp(-mean);
        int x = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            x++;
        } while (p > L);
        return x - 1;
    }






    public static void main(String[] args) {
        double meanArrivalFreq = 0.0;
        double meanDepartureFreq = 0.0;
        AirTrafficControlSimulation simulation = new AirTrafficControlSimulation();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter mean departure frequency (0.0 < df > 1.0): ");
        if(scanner.hasNextDouble());
            meanDepartureFreq = scanner.nextDouble();
        System.out.println("Enter mean arrival frequency (0.0 < af <1.0): ");
        if (scanner.hasNextDouble());;
            meanArrivalFreq = scanner.nextDouble();
            for (int i = 0; i < 720; i++){
                simulation.processArrival(meanArrivalFreq);
                simulation.processDeparture(meanDepartureFreq);
                if (simulation.arrivalQueue.size() == 0 )
                    simulation .arrivalQueueEmpty ++;
                if (simulation.departureQueue.size() == 0 )
                    simulation.departureQueueEmpty++;
            }

                simulation.printSimulationsummaryStatistics();

    }

    enum FlightType {Arrival, Departure};
    class Flight {
        String flightNumber;
        FlightType flightType;
        int minuteInQueue;
        int minuteOutQueue;



        public Flight(String flightNumber, FlightType flightType) {
            this.flightNumber = flightNumber;
            this.flightType = flightType;
        }

        public String toString() {
            return flightType + ": " + flightNumber;
        }


        public void setMinuteInQueue(int minute) {
            this.minuteInQueue = minute;
        }


        public void setMinuteOutQueue(int minute) {
            this.minuteOutQueue = minute;
        }

        public int timeInQueue() {
            return minuteOutQueue - minuteInQueue;
        }
    }






    public void addToArrivalQueue(int count)
    {
        for (int i = 0; i < count; i++) {
            Flight arrivalFlight = new Flight("AA" + flightNumberCounter++, FlightType.Arrival);
            if (arrivalQueue.size() < 5) {
                arrivalFlight.setMinuteInQueue(timeInterval);
                arrivalQueue.add(arrivalFlight);

            } else {
                this.numberOfDivertedArrivals++;
                System.out.println("Arrival queue full. Flight " + arrivalFlight + " rerouted at: "  + timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) +  " hours");
            }
        }
    }

    public void removeFromArrivalQueue() {
        if (arrivalQueue.size() > 0) {
            Flight arrivalFlight = arrivalQueue.removeFirst();
            arrivalFlight.setMinuteOutQueue(timeInterval);
            arrivalStatistics.add(arrivalFlight);
            System.out.println("Flight " + arrivalFlight + " arrived at: " +     + timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) + " hours");
            numberOfArrivals++;
        }
    }

    public void addToDepartureQueue(int count){
        for (int i = 0; i < count; i++) {
            Flight departureFlight = new Flight("AA" + flightNumberCounter++, FlightType.Arrival);
            if (departureQueue.size() < 5) {
                departureFlight.setMinuteInQueue(timeInterval);
                departureQueue.add(departureFlight);

            } else {
                this.numberOfDivertedArrivals++;
                System.out.println("Departure queue full. Flight " + departureFlight + " cancelled: "  + timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) +  " hours");
            }
        }

    }

    public void removeFromDepartureQueue(){
        if (departureQueue.size() > 0) {
            Flight departureFlight = departureQueue.removeFirst();
            departureFlight.setMinuteOutQueue(timeInterval);
            departureStatistics.add(departureFlight);
            System.out.println("Flight " + departureFlight + " departed at: " +     + timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) + " hours");
            numberOfDepartures++;
        }


    }


    public void processDeparture(double meanDepartureFreq) {
        int count = 0;
        fltSpacingCounter++;
        timeInterval++;
        if ((count = getPoissonRandom(meanDepartureFreq)) > 0)
            addToDepartureQueue(count);
        if (fltSpacingCounter >= MIN_FLIGHT_SPACING) {
            if (departureQueue.size() > 0 && arrivalQueue.size()  == 0 ) {
                removeFromDepartureQueue();
                fltSpacingCounter = 0;
            }
        }
    }
    void processArrival(double meanArrivalFreq) {
        int count = 0;
        timerCounter++;
        timeInterval++;
        if ((count = getPoissonRandom(meanArrivalFreq)) > 0)
            addToArrivalQueue(count);
        if (timerCounter >= 10) {
            if (arrivalQueue.size() > 0) {
                removeFromArrivalQueue();
                timerCounter = 0;
            }
        }
    }

   private void printSimulationsummaryStatistics(){
        int ttlDepartureTimeInQueue = 0;
        int ttlArrivalTimeInQueue = 0;
        double avgDepartureQueueTime = 0.0;
        double avgArrivalQueueTime = 0.0;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("\n *********************************************************");
        System.out.println("Automated Air Traffic Control Simulation Summary Statistics");
        System.out.println("************************************************************");
        System.out.println("Time period simulated :" + timeInterval / 60 + ":" + String.format("%02d", timeInterval % 60));
        System.out.println("Number of Arrivals : " + numberOfArrivals);
        System.out.println("Number of Departures : " + numberOfDepartures);
        int totalFlights = numberOfArrivals + numberOfDepartures;
        System.out.println("Total number of Flights handled : " + totalFlights);
        System.out.println("Average number of arrivals per hour :  " + String.format("%.2f",(double)(numberOfArrivals/(timeInterval/60))));
        System.out.println("Average number of departures per hour :  " + String.format("%.2f",(double)(numberOfDepartures/(timeInterval/60))));
        System.out.println("Departures remaining in Queue : " + departureQueue.size());
        System.out.println("Arrivals remaining in Queue : " + arrivalQueue.size());
        System.out.println("Number of diverted arrivals = " + this.numberOfDivertedArrivals);
        System.out.println("Number of denied departures = " + this.numberofdenieddepartures);

        for(Flight flt : departureStatistics){
            ttlDepartureTimeInQueue = ttlDepartureTimeInQueue + flt.timeInQueue();
        }
        if(departureStatistics.size() > 0);
        avgDepartureQueueTime = ttlDepartureTimeInQueue / departureStatistics.size();
    for(Flight flt : arrivalStatistics){
        avgArrivalQueueTime = ttlArrivalTimeInQueue + flt.timeInQueue();
    }
    if (arrivalStatistics.size() > 0);
    avgArrivalQueueTime = ttlArrivalTimeInQueue / arrivalStatistics.size();

    int emptyQueueInstance = arrivalQueueEmpty + departureQueueEmpty;
    double pctIdle = (double) emptyQueueInstance/timeInterval * 100.0;
    System.out.println("Percent time idle runway : " + df.format(pctIdle) + "%");
    System.out.println("Average departures time in queue : " + df.format( avgDepartureQueueTime) + " minutes");
    System.out.println("Average arrival time in queue : " + df.format( avgArrivalQueueTime) + " minutes");

}

}