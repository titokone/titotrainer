package fi.helsinki.cs.titotrainer.app.model.misc;

import java.util.Comparator;

import fi.helsinki.cs.titotrainer.app.model.AbstractTitoEntity;

/**
 * <p>Compares entities by their IDs so that smaller IDs come first.</p>
 */
public class IdComparator implements Comparator<AbstractTitoEntity> {
    @Override
    public int compare(AbstractTitoEntity o1, AbstractTitoEntity o2) {
        return (int)(o1.getId() - o2.getId());
    }
}
