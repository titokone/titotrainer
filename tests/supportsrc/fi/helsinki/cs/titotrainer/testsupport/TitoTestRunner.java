package fi.helsinki.cs.titotrainer.testsupport;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * <p>A slightly customized version of the default JUnit4 test runner.</p>
 * 
 * <p>This adds support for {@link BeforePriority} and guarantees that
 * <code>@Before </code> methods of a superclass are run before the
 * <code>@Before </code> methods of a subclass.</p>
 * 
 * @see TitoTestCase
 */
public class TitoTestRunner extends BlockJUnit4ClassRunner {

    public TitoTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
    
    private static class BeforeComparator implements Comparator<FrameworkMethod> {
        @Override
        public int compare(FrameworkMethod a, FrameworkMethod b) {
            // Compare by priority
            BeforePriority aPriAnn = a.getAnnotation(BeforePriority.class);
            BeforePriority bPriAnn = b.getAnnotation(BeforePriority.class);
            int aPri = (aPriAnn != null ? aPriAnn.value() : 0);
            int bPri = (bPriAnn != null ? bPriAnn.value() : 0);
            if (aPri != bPri)
                return aPri - bPri;
            
            // Priority being equal, methods in superclass come first
            Class<?> aClass = a.getMethod().getDeclaringClass();
            Class<?> bClass = b.getMethod().getDeclaringClass();
            if (!aClass.equals(bClass)) {
                if (aClass.isAssignableFrom(bClass))
                    return -1;
                if (bClass.isAssignableFrom(aClass))
                    return 1;
            }
            
            // No priority difference.
            // Since Collections.sort() is stable, the default ordering will remain.
            return 0;
        }
    }
    
    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);
        Collections.sort(befores, new BeforeComparator());
        return new RunBefores(statement, befores, target);
    }
    
}
