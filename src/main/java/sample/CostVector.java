package sample;

import java.time.LocalTime;

public class CostVector implements ICostVector<CostVector>, Comparable<CostVector>
{
    public int minutes = 0;
    public int price = 0;

    public LocalTime startTime = null;

    public CostVector()
    {
        price = Integer.MAX_VALUE;
        minutes = Integer.MAX_VALUE;
    }


    public CostVector(int price, int minutes)
    {
        this.price = price;
        this.minutes = minutes;
    }

    public CostVector(int price, int minutes, LocalTime startTime)
    {
        this(price, minutes);
        this.startTime = startTime;
    }

    @Override
    protected Object clone()
    {
        return new CostVector(price, minutes);
    }

    @Override
    public CostVector add(CostVector addend)
    {
        return new CostVector(this.price + addend.price, this.minutes + addend.minutes, this.startTime);
    }

    @Override
    public boolean isMax()
    {
        return Cost.currentCost == Cost.CurrentCost.PRICE ? this.price == Integer.MAX_VALUE : this.minutes == Integer.MAX_VALUE;
    }

    @Override
    public int compareTo(CostVector o)
    {
        return Cost.currentCost == Cost.CurrentCost.PRICE ?
                Integer.compare(this.price, o.price) :
                Integer.compare(this.minutes, o.minutes);
    }

    @Override
    public String toString()
    {
        return "(€" + (double)price / 100 + "; " + minutes / 60 + "h" + (minutes - (minutes / 60) * 60) + "m)";
    }
}
