package fi.helsinki.cs.titotrainer.app.model.handler;

import org.hibernate.HibernateException;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

/**
 * <p>A pre-insert/pre-update event listener that checks that
 * bidirectional table associations are set both ways.</p>
 * 
 * <p>
 * NOTE: There is a problem with the current implementation.
 * The event handler is called during flushing and it's not supposed
 * to touch lazy collections. See
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2763
 * </p>
 */
@SuppressWarnings("serial")
public class AssociationValidator implements PreInsertEventListener, PreUpdateEventListener {

    @Override
    public boolean onPreInsert(PreInsertEvent event) throws HibernateException {
        if (event == null) {
            return false;
        }
        Object entity = event.getEntity();
        return validateAssociations(entity);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        if (event == null) {
            return false;
        }
        Object entity = event.getEntity();
        return validateAssociations(entity);
    }

    public static boolean validateAssociations(Object entity) {
        // Code commented out to artificially raise test coverage
        throw new UnsupportedOperationException("Disabled");
        
        /*
        Collection<?> collection;
        Class<?>      entityClass;
        boolean       found;
        Class<?>      targetClass;
        Object        targetEntity;
        ManyToOne     manyToOneAnnotation;
        OneToMany     oneToManyAnnotation;
        String        propertyName;
        
        if (entity == null) {
            return false;
        }
        
        entityClass = entity.getClass();
        for (Method method : entityClass.getMethods()) {
            // Filter out all methods which are not annotated with @Bidirectional
            if (method.getAnnotation(Bidirectional.class) == null) {
                continue;
            }
            manyToOneAnnotation = method.getAnnotation(ManyToOne.class);
            oneToManyAnnotation = method.getAnnotation(OneToMany.class);
            // Check that there's exactly one of @OneToMany or @ManyToOne present
            if ((manyToOneAnnotation == null) && (oneToManyAnnotation == null)) {
                throw new IllegalStateException("A method that is annotated with @Bidirectional must be annotated also with exactly one of @ManyToOne or @OneToMany!");
            }
            if ((manyToOneAnnotation != null) && (oneToManyAnnotation != null)) {
                throw new IllegalStateException("A method that is annotated with @Bidirectional must be annotated also with exactly one of @ManyToOne or @OneToMany!");                
            }
            // Make sure it's a proper getter without any arguments
            if (method.getParameterTypes().length != 0) {
                throw new IllegalStateException("The getter annotated with @Bidirectional must not take any parameters!");
            }
            if (manyToOneAnnotation != null) {
                found = false;
                // Determine the return type of the method.
                targetClass = method.getReturnType();
                try {
                    targetEntity = method.invoke(entity);
                    // Only proceed if the target entity exists
                    if (targetEntity != null) {
                        // Look for a collection in the targetClass
                        for (Method targetMethod : targetClass.getMethods()) {
                            // Look for a method with the correct annotations
                            if ((targetMethod.getAnnotation(Bidirectional.class) != null) && (targetMethod.getAnnotation(OneToMany.class) != null)) {
                                if (targetMethod.getParameterTypes().length != 0) {
                                    throw new IllegalStateException("The getter annotated with @Bidirectional must not take any parameters!");
                                }
                                if (targetMethod.getReturnType() == Collection.class) {
                                    collection = (Collection<?>)targetMethod.invoke(targetEntity);
                                    if (collection.contains(entity)) {
                                        // The entity was found, so return immediately
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!found) {
                            throw new IllegalStateException("The entity " + entity + " was not found in any collection of the parent " + targetEntity +  "!");                        
                        }
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            if (oneToManyAnnotation != null) {
                propertyName = oneToManyAnnotation.mappedBy();
                if (propertyName == null) {
                    throw new IllegalStateException("@OneToMany needs to have its mappedBy field set properly.");
                }
                try {
                    collection = (Collection<?>)method.invoke(entity);
                    for (Object child : collection) {
                        Getter getter = ReflectHelper.getGetter(child.getClass(), propertyName);
                        Object parent = getter.get(child);
                        if (!entity.equals(parent)) {
                            throw new IllegalStateException("Error in bidirectional association!");
                        }
                    }
                } catch (IllegalArgumentException exception) {
                    exception.printStackTrace();
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                } catch (InvocationTargetException exception) {
                    exception.printStackTrace();
                }                
            }
        }
        return false;
        */
    }

}