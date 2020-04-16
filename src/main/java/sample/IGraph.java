package sample;

public interface IGraph<E, U> extends Iterable<E>
{
    boolean isEmpty();

    int size();

    int getNodeCount();

    void clear();

    void add(E from, E to, U cost);

    boolean remove(E elemFrom, E elemTo);

    boolean removeNode(E element);

    boolean contains(E element);

}
