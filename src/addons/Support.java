package addons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Support {
    public static <E> Iterable<E> concat(
            final Iterable<? extends E> i1,
            final Iterable<? extends E> i2) {
        return new Iterable<E>() {
            public Iterator<E> iterator() {
                return new Iterator<E>() {
                    Iterator<? extends E> listIterator = i1.iterator();
                    Boolean checkedHasNext;
                    E nextValue;
                    private boolean startTheSecond;

                    void theNext() {
                        if (listIterator.hasNext()) {
                            checkedHasNext = true;
                            nextValue = listIterator.next();
                        } else if (startTheSecond)
                            checkedHasNext = false;
                        else {
                            startTheSecond = true;
                            listIterator = i2.iterator();
                            theNext();
                        }
                    }

                    public boolean hasNext() {
                        if (checkedHasNext == null)
                            theNext();
                        return checkedHasNext;
                    }

                    public E next() {
                        if (!hasNext())
                            throw new NoSuchElementException();
                        checkedHasNext = null;
                        return nextValue;
                    }

                    public void remove() {
                        listIterator.remove();
                    }
                };
            }
        };
    }

    public static <E> List<E> makeListFromIterable(Iterable<E> iter) {
        List<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }
}
