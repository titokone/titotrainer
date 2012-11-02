package fi.helsinki.cs.titotrainer.framework.request.coercer;

/**
 * Contains all built-in coercers.
 */
public class DefaultCoercerSet extends CoercerSet {
    
    private DefaultCoercerSet() {
        coercers.add(new BooleanCoercer());
        coercers.add(new IntegerCoercer());
        coercers.add(new LongCoercer());
        coercers.add(new StringCoercer());
        coercers.add(new MapCoercer());
        coercers.add(new FileItemCoercer());
        
        coercers.add(new ArrayCoercer<Integer>(new IntegerCoercer()));
        coercers.add(new ArrayCoercer<Long>(new LongCoercer()));
        coercers.add(new ArrayCoercer<String>(new StringCoercer()));
        
        coercers.trimToSize();
    }
    
    private static DefaultCoercerSet instance = new DefaultCoercerSet();
    
    public static DefaultCoercerSet getInstance() {
        return instance;
    }
    
}
