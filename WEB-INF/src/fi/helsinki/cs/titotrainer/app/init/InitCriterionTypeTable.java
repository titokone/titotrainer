package fi.helsinki.cs.titotrainer.app.init;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;

import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.CriterionType;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;

/**
 * <p>Makes sure there is exactly one {@link CriterionType} instance for each criterion type.</p>
 * 
 * <p>This also removes {@link CriterionType} records of criterion classes that no longer exist.</p>
 */
public class InitCriterionTypeTable {

    private Logger log = Logger.getLogger(InitCriterionTypeTable.class);
    
    @SuppressWarnings("unchecked")
    private Collection<Class<? extends Criterion>> getCriterionClasses(Session hs) {
        Collection<Class<? extends Criterion>> ret = new LinkedList<Class<? extends Criterion>>();
        
        for (ClassMetadata cm : (Collection<ClassMetadata>)hs.getSessionFactory().getAllClassMetadata().values()) {
            Class<?> cls = cm.getMappedClass(EntityMode.POJO);
            if (Criterion.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers()))
                ret.add((Class<? extends Criterion>)cls);
        }
        return ret;
    }
    
    private void verifyCriterionClass(Session hs, TitoTranslation tt, Class<? extends Criterion> cls) {
        
        CriterionType ct = (CriterionType)hs.get(CriterionType.class, cls.getName());
        
        if (ct == null) {
            log.info("Initializing criterion type " + cls.getSimpleName());
            ct = new CriterionType();
            ct.setClassName(cls.getName());
            
            for (Locale locale : tt.getSupportedLocales()) {
                Translator translator = tt.getClassTranslator(locale, InitCriterionTypeTable.class);
                
                String acceptKey = cls.getSimpleName() + "_accept";
                String acceptMsg = translator.tr(acceptKey);
                if (acceptMsg.equals(acceptKey))
                    acceptMsg = "";
                ct.getDefaultAcceptMessage().set(locale, acceptMsg);
                
                String rejectKey = cls.getSimpleName() + "_reject";
                String rejectMsg = translator.tr(rejectKey);
                if (rejectMsg.equals(rejectKey))
                    rejectMsg = "";
                ct.getDefaultRejectMessage().set(locale, rejectMsg);
            }
            
            hs.save(ct);
        }
    }
    
    public void run(Session hs, TitoTranslation tt) throws Exception {
        Collection<Class<? extends Criterion>> criterionClasses = getCriterionClasses(hs);
        
        // Check that all criteria classes have records
        for (Class<? extends Criterion> cls : criterionClasses) {
            verifyCriterionClass(hs, tt, cls);
        }
        hs.flush();
        
        // Remove obsolete classes
        for (Object o : hs.createQuery("FROM CriterionType").list()) {
            String className = ((CriterionType)o).getClassName();
            
            boolean found = false;
            for (Class<? extends Criterion> cls : criterionClasses) {
                if (cls.getName().equals(className))
                    found = true;
            }
            
            if (!found) {
                log.warn("Deleting obsolete criterion type record " + className);
                hs.delete(o);
            }
        }
        hs.flush();
    }
    
}
