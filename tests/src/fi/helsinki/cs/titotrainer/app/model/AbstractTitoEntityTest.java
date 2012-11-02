package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.app.model.NonCooperativeTitoEntity;
import fi.helsinki.cs.titotrainer.testsupport.app.model.SimpleTitoEntity;

@SuppressWarnings("deprecation")
public class AbstractTitoEntityTest {
    
    private AbstractTitoEntity entityOne;
    private AbstractTitoEntity entityThree;
    private AbstractTitoEntity entityTwo;
    private AbstractTitoEntity uncooperativeEntityOne;
    private AbstractTitoEntity uncooperativeEntityTwo;
    
    @Before
    public void setUp() {
        this.entityOne = new SimpleTitoEntity();
        this.entityTwo = new SimpleTitoEntity();
        this.entityThree = new SimpleTitoEntity();
        this.uncooperativeEntityOne = new NonCooperativeTitoEntity();
        this.uncooperativeEntityTwo = new NonCooperativeTitoEntity();
    }
    
    /* Test equals(Object) */
    
    @Test
    public void equalsShouldReturnFalseIfOtherIsNull() {
        assertFalse(this.entityOne.equals(null));
    }
    
    @Test
    public void equalsShouldReturnTrueOnReflexivity() {
        assertTrue(this.entityOne.equals(this.entityOne));
    }
    
    @Test
    public void equalsShouldReturnFalseOnDifferentClasses() {
        assertFalse(this.entityOne.equals(this.uncooperativeEntityOne));
        assertFalse(this.uncooperativeEntityOne.equals(this.entityOne));
    }
    
    @Test
    public void equalsShouldReturnFalseIfGetIdIsNotSupported() {
        assertTrue(this.uncooperativeEntityOne.equals(this.uncooperativeEntityOne));
        assertFalse(this.uncooperativeEntityOne.equals(this.uncooperativeEntityTwo));
    }
    
    @Test
    public void equalsShouldReturnTrueOnDifferentEntitiesHavingTheSameId() {
        this.entityOne.setId(234);
        this.entityTwo.setId(234);
        assertTrue(this.entityOne.equals(this.entityTwo));
        assertTrue(this.entityTwo.equals(this.entityOne));
    }
    
    @Test
    public void equalsShouldReturnFalseIfBothIdsAreZeroOnDifferentEntities() {
        this.entityOne.setId(0);
        this.entityTwo.setId(0);
        assertFalse(this.entityOne.equals(this.entityTwo));
    }
    
    @Test
    public void equalsShouldReturnFalseOnDifferentIdsEvenIfOneIsZero() {
        this.entityOne.setId(0);
        this.entityTwo.setId(1);
        assertFalse(this.entityOne.equals(this.entityTwo));
        assertFalse(this.entityTwo.equals(this.entityOne));
    }
    
    @Test
    public void equalsShouldWorkTransitively() {
        this.entityOne.setId(123);
        this.entityTwo.setId(123);
        this.entityThree.setId(123);
        assertTrue(this.entityOne.equals(this.entityTwo));
        assertTrue(this.entityTwo.equals(this.entityThree));
        assertTrue(this.entityOne.equals(this.entityThree));
    }
    
    /* Test hasCompleteTranslation(Locale) */
    
    @Test(expected = NullPointerException.class)
    public void hasCompleteTranslationShouldNotAcceptNullLocale() {
        this.entityOne.hasCompleteTranslation(null);
    }
    
    @Test
    public void hasCompleteTranslationShouldAlwaysReturnTrue() {
        assertTrue(this.entityOne.hasCompleteTranslation(Locale.ENGLISH));
    }
    
    /* Test hashCode() */
    
    @Test
    public void hashCodeShouldBeEqualToIdIfInIntRange() {
        this.entityOne.setId(0);
        assertEquals(0, this.entityOne.hashCode());
        this.entityOne.setId(123456);
        assertEquals(123456, this.entityOne.hashCode());
        this.entityOne.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, this.entityOne.hashCode());
        this.entityOne.setId(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, this.entityOne.hashCode());
    }
    
    @Test
    public void hashCodeShouldWorkProperlyOnNumbersOutsideIntRange() {
        this.entityOne.setId(Integer.MAX_VALUE + 1);
        assertEquals(Integer.MIN_VALUE, this.entityOne.hashCode());
        this.entityOne.setId(Integer.MIN_VALUE - 1);
        assertEquals(Integer.MAX_VALUE, this.entityOne.hashCode());
        this.entityOne.setId(Long.MAX_VALUE);
        assertEquals(-1, this.entityOne.hashCode());
        this.entityOne.setId(Long.MIN_VALUE);
        assertEquals(0, this.entityOne.hashCode());
    }
    
    @Test
    public void hashCodeShouldReturnZeroIfGetIdIsNotSupported() {
        assertEquals(0, this.uncooperativeEntityOne.hashCode());
    }
    
}