package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.io.Serializable;
import java.util.Locale;

/**
 * Abstract superclass of all entities in the model layer. This class overrides the standard
 * {@link Object#equals(Object)} and {@link Object#hashCode()} methods with implementations
 * that use the unique id's of entities to generate hash codes and determine equality.
 */
public abstract class AbstractTitoEntity implements Serializable {

    //////////////////////
    // ABSTRACT METHODS //
    //////////////////////
    
    /**
     * Returns the unique id of this entity. If an entity has not been assigned a unique id by the persistence
     * framework yet, this function most probably returns {@code 0}, the initial value of all fields of
     * type {@code long} which are not yet set.
     * 
     * <h4 class="implementation" >Implementation Advice</h4>
     * Subclasses of {@link AbstractTitoEntity} who can not provide a meaningful and unique id should throw an
     * {@link UnsupportedOperationException} in the implementation of this method.
     * 
     * @return The unique id of this entity
     * @throws UnsupportedOperationException If the entity does not support a unique id
     */
    public abstract long getId();

    /**
     * Sets the unique id of this entity. This method should not be called directly, as it is the responsibility
     * of the persistence framework to generate unique id's and assign them to entities. 
     * 
     * <h4 class="implementation" >Implementation Advice</h4>
     * Subclasses of {@link AbstractTitoEntity} who can not provide or accept a meaningful unique id should throw
     * an {@link UnsupportedOperationException} in the implementation of this method. 
     * 
     * @deprecated This method should only be called by the persistence framework and never in application code
     * @param id The unique id of this entity
     * @throws UnsupportedOperationException If the entity does not support a unique id.
     */
    @Deprecated
    public abstract void setId(long id);

    //////////////////////
    // INSTANCE METHODS //
    //////////////////////
    
    /**
     * Tests another entity for equality with this one. Equality is determined based on the rules given in the
     * table below. They are applied in the order they are given in the table.
     *  
     * <table border="1" cellspacing="0" >
     *  <tr>
     *   <td>Case</td><td>Result</td>
     *  </tr>
     *  <tr>
     *   <td>{@code other == null}</td><td>{@code false}</td>
     *  </tr>
     *  <tr>
     *   <td>{@code this == other}</td><td>{@code true}</td>
     *  </tr>
     *  <tr>
     *   <td>{@code this.class != other.class}</td><td>{@code false}</td>
     *  </tr>
     *  <tr>
     *   <td>{@code this.getId() != other.getId()}</td><td>{@code false}</td>
     *  </tr>
     *  <tr>
     *   <td>{@code this.getId() == other.getId() != 0}</td><td>{@code true}</td>
     *  </tr>
     *  <tr>
     *   <td>{@code this.getId() == other.getId() == 0}</td><td>{@link IllegalStateException}</td>
     *  </tr>
     * </table>
     * 
     * @param other The entity to compare this one with
     * @return {@code true} - If this entity is equal to the other<br />
     *         {@code false} - Otherwise
     * @throws IllegalStateException If equality can't be determined
     */
    @Override
    public boolean equals(Object other) {
        AbstractTitoEntity otherEntity;
        long               otherId;
        long               thisId;
        
        /* Test for the very basic case of 'other' being null */
        if (other == null) {
            return false;
        }
        /* Check for the simplest possible case */
        if (other == this) {
            return true;
        }
        /* For strict consistency, do not allow subclasses! */
        if (other.getClass() != this.getClass()) {
            return false;
        }
        otherEntity = (AbstractTitoEntity)other;
        try {
            otherId = otherEntity.getId();
            thisId = this.getId();
        } catch (UnsupportedOperationException exception) {
            return false;
        }
        /* Use the identities to determine equality */
        return (thisId != 0 && thisId == otherId);
    }

    /**
     * Determines if this entity is completely localized for a given locale.
     *
     * <h4 class="implementation" >Implementation Note</h4>
     * If not overridden by a subclass, this method returns always {@code true}. This is sensible as any entity
     * that does not have localized fields is always completely translated for all locales.
     * 
     * @param locale The locale for which to determine if the localization of this entity is complete. Must not be {@code null}.
     * @return {@code true} - If this entity is completely localized for the given locale<br />
     *         {@code false} - Otherwise
     * @throws NullPointerException - If the argument {@code locale} is {@code null}
     */
    public boolean hasCompleteTranslation(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        /* It can be assumed that a subclass which does not override this method does not have any
         * localized fields, so it is completely translated for every locale. */
        return true;
    }
    
    /**
     * Returns a hash code for this entity. The hash code is based on the id of the entity. In case there is no
     * id available for the entity, the standard value {@code 0} will be returned.
     * 
     * @return A hash code for the entity
     * @see AbstractTitoEntity#getId()
     */
    @Override
    public int hashCode() {
        Long id;
        
        try {
            id = new Long(this.getId());
            return id.intValue();
        } catch (UnsupportedOperationException exception) {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(id=" + this.getId() + ")";
    }
    
}