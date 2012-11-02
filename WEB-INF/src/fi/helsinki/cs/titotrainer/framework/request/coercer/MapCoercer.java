package fi.helsinki.cs.titotrainer.framework.request.coercer;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * <p>Gathers parameters named like <code>foo[abc]</code> to a map.</p>
 *
 * <p>By default the map is from strings to strings, unless otherwise specified by
 * a {@link MapTypes} annotation. This complication exists because of Java's type
 * parameter erasure.</p>
 * 
 * <p>Maps may be nested like <code>foo[abc][def]</code></p>
 * 
 * <p>Always returns at least an empty map.</p>
 */
@SuppressWarnings("unchecked")
public class MapCoercer implements FieldCoercer<Map> {
    
    private static final Pattern INDEX_PATTERN = Pattern.compile("\\[([^\\]]*)\\]");
    
    private static class Subcoercers {
        SimpleFieldCoercer<?> keyCoercer;
        SimpleFieldCoercer<?> valueCoercer;
    }
    
    private void coerceImpl(String prefix, Map<Object, Object> result, Map<String, ?> params, Subcoercers subcoercers) {
        for (Entry<String, ?> param :  params.entrySet()) {
            String key = param.getKey();
            
            if (key.startsWith(prefix)) {
                Matcher indexMatcher = INDEX_PATTERN.matcher(key.substring(prefix.length()));
                if (indexMatcher.find() && indexMatcher.start() == 0) { // One index
                    String rawIndex = indexMatcher.group(1);
                    
                    Maybe<?> coercedIndex = subcoercers.keyCoercer.coerce(rawIndex);
                    Object index;
                    if (coercedIndex.hasValue() && coercedIndex.getValue() != null)
                        index = coercedIndex.getValue();
                    else
                        continue;
                    
                    if (indexMatcher.find()) { // More indices
                        // Nested indices, so we recurse
                        if (result.get(index) instanceof Map) {
                            continue; // Already recursed with this index
                        }
                        Map<Object, Object> value = new HashMap<Object, Object>();
                        
                        coerceImpl(prefix + '[' + index + ']', value, params, subcoercers);
                        
                        result.put(index, value);
                    } else {
                        // No nested indices, so the final value is here
                        Maybe<?> value = subcoercers.valueCoercer.coerce(param.getValue().toString());
                        if (value.hasValue()) {
                            result.put(index, value.getValue());
                        }
                    }
                }
            }
            
        }
    }
    
    @Override
    public Some<Map> coerce(String field, Map<String, ?> params, Annotation[] annotations) {
        Subcoercers subcoercers = new Subcoercers();
        subcoercers.keyCoercer = new StringCoercer();
        subcoercers.valueCoercer = new StringCoercer();
        
        for (Annotation a : annotations) {
            if (a instanceof MapTypes) {
                try {
                    MapTypes mt = (MapTypes)a;
                    subcoercers.keyCoercer = mt.keyCoercer().newInstance();
                    subcoercers.valueCoercer = mt.valueCoercer().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Incorrect MapType annotation", e);
                }
            }
        }
        
        Map<Object, Object> result = new HashMap<Object, Object>();
        coerceImpl(field, result, params, subcoercers);
        
        return new Some<Map>(result);
    }

    @Override
    public Class<Map> getResultType() {
        return Map.class;
    }
    
}
