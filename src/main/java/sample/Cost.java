package sample;

import java.time.LocalTime;

public class Cost implements ICost
{
    LocalTime departureTime;
    LocalTime arrivalTime;
    int ticketPrice;

    public enum CurrentCost
    {
        PRICE,
        TIME
    }

    public static CurrentCost currentCost = CurrentCost.PRICE;
    public static int maxPrice = 0;
    public static int maxTime = 0;

    //public static LocalTime startTime = null;

    CostVector costVector;


    public Cost(double ticketPrice, LocalTime departureTime, LocalTime arrivalTime)
    {
        this.ticketPrice = (int)(ticketPrice * 100);
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        costVector = new CostVector(this.ticketPrice,
                (arrivalTime.getHour() * 60 + arrivalTime.getMinute())
                        - (departureTime.getHour() * 60 + departureTime.getMinute()));
    }


    @Override
    public boolean canVisit(CostVector sum)
    {
        return (maxPrice == 0 || sum.price <= maxPrice) && (maxTime == 0 || sum.minutes <= maxTime);
    }

    @Override
    public CostVector getCostVector()
    {
        return costVector;
    }


    @Override
    public CostVector getCost(CostVector sum, boolean isSource)
    {
        if(isSource)
            sum.startTime = departureTime;


        int departureMinutes = departureTime.getHour() * 60 + departureTime.getMinute();
        int arrivalMinutes = arrivalTime.getHour() * 60 + arrivalTime.getMinute();
        int tripTime = arrivalMinutes < departureMinutes ? arrivalMinutes + 24 * 60 - departureMinutes : arrivalMinutes - departureMinutes;

        LocalTime currentTime = sum.startTime.plusMinutes(sum.minutes);
        int cmp = currentTime.compareTo(departureTime);
        if(cmp > 0) // Paveluota, reikia laukti kitos dienos
        {
            //System.out.println("Pvaluota: " + (24 * 60 - (currentTime.getHour() * 60 + currentTime.getMinute())) + " - " + (arrivalTime.getHour() * 60 + arrivalTime.getMinute()));
            return new CostVector(ticketPrice, 24 * 60 - (currentTime.getHour() * 60 + currentTime.getMinute())
                    + arrivalMinutes); // Skaiciuojame nuo 00:00, todel departureTime nepaisome
        }
        else if(cmp == 0) // Atvykta tobulu laiku
        {
            //System.out.println(arrivalTime.getHour() * 60 + arrivalTime.getMinute() + " - " + (departureTime.getHour() * 60 + departureTime.getMinute()));
            return new CostVector(ticketPrice, tripTime);
        }
        else // Atvykta anksciau
        {
            //System.out.println("Anksciau: " + arrivalTime.getHour() * 60 + arrivalTime.getMinute() + " - " + (currentTime.getHour() * 60 + currentTime.getMinute()));
            return new CostVector(ticketPrice, tripTime + departureMinutes
                    - (currentTime.getHour() * 60 + currentTime.getMinute()));
        }
    }

    @Override
    public String toString()
    {
        return "(€" + (double)ticketPrice / 100 + "; " + departureTime.toString() + "; " + arrivalTime.toString()+ ")";
    }

    public LocalTime getArrivalTime() { return arrivalTime; }
    public LocalTime getDepartureTime() { return departureTime; }
}
