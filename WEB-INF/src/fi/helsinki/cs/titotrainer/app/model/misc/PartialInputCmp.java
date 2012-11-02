package fi.helsinki.cs.titotrainer.app.model.misc;

import java.util.Comparator;

import fi.helsinki.cs.titotrainer.app.model.Input;

/**
 * Orders inputs by their input strings.
 */
public class PartialInputCmp implements Comparator<Input> {
    @Override
    public int compare(Input o1, Input o2) {
        String a = ArgumentUtils.emptyOnNull(o1.getInput());
        if (o2 == null)
            return a.isEmpty() ? 0 : 1;
        String b = ArgumentUtils.emptyOnNull(o2.getInput());
        return a.compareTo(b);
    }
}
