package sample;

import java.util.ArrayList;
import java.util.List;

public class Path<E>
{
    public List<E> elements;
    public CostVector distance;

    public Path()
    {
        elements = new ArrayList<>();
        distance = new CostVector();
    }

    public Path(List<E> elements, CostVector distance)
    {
        this.elements = elements;
        this.distance = distance;
    }
}
