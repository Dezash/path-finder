package edu.ktu.ds.benchmark;

import sample.CostVector;
import sample.ICost;
import sample.ICostVector;

public class TestCost implements ICost
{
    CostVector costVector;
    int num;

    public TestCost(int num)
    {
        this.num = num;
        costVector = new CostVector(this.num, 0);
    }

    @Override
    public CostVector getCost(CostVector weightSum, boolean isSource)
    {
        return new CostVector(num, 0);
    }

    @Override
    public boolean canVisit(CostVector weightSum)
    {
        return true;
    }

    @Override
    public ICostVector getCostVector()
    {
        return costVector;
    }
}
