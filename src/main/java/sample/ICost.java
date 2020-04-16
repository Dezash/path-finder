package sample;

public interface ICost<E>
{
    //int getCost(int weightSum);
    CostVector getCost(CostVector weightSum, boolean isSource);
    //boolean canVisit(int weightSum);
    boolean canVisit(CostVector weightSum);
    ICostVector<E> getCostVector();
}
