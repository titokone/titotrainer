package fi.helsinki.cs.titotrainer.app.model.misc;

import java.util.Comparator;

import fi.helsinki.cs.titotrainer.app.model.Criterion;

/**
 * Orders criteria first by their their classes, then by their parameters.
 */
public class PartialCriterionCmp implements Comparator<Criterion> {
    @Override
    public int compare(Criterion o1, Criterion o2) {
        if (!o1.getClass().equals(o2.getClass())) {
            return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
        } else {
            return o1.getParameters().compareTo(o2.getParameters());
        }
    }
}
