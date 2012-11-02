package fi.helsinki.cs.titotrainer.app.model.fileconv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.dom4j.Document;
import org.dom4j.Element;

import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.Task.Type;

/**
 * <p>Loads tasks from an XML file produced by {@link TaskFileMaker}.</p>
 * 
 * <p>The resulting tasks should be assigned to a course and correct categories.</p>
 */
public class TaskFileLoader {
    
    private static final Package CRITERIA_PACKAGE = fi.helsinki.cs.titotrainer.app.model.criteria.Relation.class.getPackage();
    
    @SuppressWarnings("unchecked")
    private static TString loadTString(Element parent, String elementName) {
        TString ts = new TString();
        for (Iterator<Element> i = parent.elementIterator(elementName); i.hasNext(); ) {
            Element e = i.next();
            String lang = e.attributeValue("lang");
            if (lang != null) {
                ts.set(new Locale(lang), e.getText());
            }
        }
        if (ts.getTranslations() != null && !ts.getTranslations().isEmpty())
            return ts;
        else
            return null;
    }
    
    private static Boolean loadBoolean(Element parent, String elementName) {
        String text = parent.elementTextTrim(elementName);
        if (text != null) {
            if (text.equals("true"))
                return true;
            else
                return false;
        }
        return null;
    }
    
    private static Integer loadInt(Element parent, String elementName) {
        String text = parent.elementTextTrim(elementName);
        if (text != null && !text.isEmpty())
            return Integer.parseInt(text);
        return null;
    }
    
    private static Criterion loadCriterion(Element critEl) throws Exception {
        String type = critEl.attributeValue("type");
        if (type == null)
            throw new RuntimeException("Criterion missing type attribute");

        Class<?> cls = Class.forName(CRITERIA_PACKAGE.getName() + "." + type);
        if (!Criterion.class.isAssignableFrom(cls))
            throw new RuntimeException("Not a criterion type: " + type);
        
        Criterion crit = (Criterion)cls.newInstance();
        crit.setAcceptMessage(loadTString(critEl, "acceptMessage"));
        crit.setRejectMessage(loadTString(critEl, "rejectMessage"));
        crit.setQualityCriterion(loadBoolean(critEl, "qualityCriterion"));
        crit.setParameters(critEl.elementTextTrim("parameters"));
        
        return crit;
    }
    
    @SuppressWarnings("unchecked")
    private static void loadInputIntoTask(Task task, Element inputEl) throws Exception {
        Input input = new Input();
        
        input.setTask(task);
        task.getInputs().add(input);
        
        input.setSecret(loadBoolean(inputEl, "secret"));
        input.setInput(inputEl.elementTextTrim("input"));
        
        input.setCriteria(new LinkedList<Criterion>());
        for (Iterator<Element> i = inputEl.elementIterator("criterion"); i.hasNext(); ) {
            Criterion crit = loadCriterion(i.next());
            task.getCriteria().add(crit);
            crit.setTask(task);
            input.getCriteria().add(crit);
            crit.setInput(input);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static Result loadTask(Element taskEl) throws Exception {
        Task task = new Task();
        
        task.setTitle(loadTString(taskEl, "title"));
        task.setDescription(loadTString(taskEl, "description"));
        
        TString categoryName = loadTString(taskEl, "category");
        
        task.setType(Type.valueOf(taskEl.elementTextTrim("type")));
        task.setHidden(loadBoolean(taskEl, "hidden"));
        task.setDifficulty(loadInt(taskEl, "difficulty"));
        task.setMaxSteps(loadInt(taskEl, "maxSteps"));
        task.setModelSolution(taskEl.elementText("modelSolution"));
        task.setPreCode(taskEl.elementText("preCode"));
        task.setPostCode(taskEl.elementText("postCode"));
        
        // We want to preserve the order of criteria and inputs
        task.setCriteria(new LinkedList<Criterion>());
        task.setInputs(new LinkedList<Input>());
        
        for (Iterator<Element> i = taskEl.elementIterator("globalCriterion"); i.hasNext(); ) {
            Criterion crit = loadCriterion(i.next());
            task.getCriteria().add(crit);
            crit.setTask(task);
        }
        
        for (Iterator<Element> i = taskEl.elementIterator("input"); i.hasNext(); ) {
            loadInputIntoTask(task, i.next());
        }
        
        return new Result(task, categoryName);
    }
    
    public static class Result {
        private Task task;
        private TString categoryName;
        
        public Result(Task task, TString categoryName) {
            this.task = task;
            this.categoryName = categoryName;
        }
        
        public Task getTask() {
            return task;
        }
        
        public TString getCategoryName() {
            return categoryName;
        }
        
        /**
         * <p>Returns the category this result's task should be assigned to by default.</p>
         * 
         * <p>Returns null if no category matched or this result had no category.</p>
         */
        public Category matchingCategory(Collection<Category> categories, Locale currentLocale) {
            if (this.categoryName == null)
                return null;
            if (this.categoryName.get(currentLocale) == null)
                return null;
            for (Category cat : categories) {
                if (cat.getName(currentLocale).equals(this.categoryName.get(currentLocale))) {
                    return cat;
                }
            }
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Result> loadTasksFromXML(Document doc) throws Exception {
        List<Result> results = new ArrayList<Result>();
        
        for (Iterator<Element> i = doc.getRootElement().elementIterator("task"); i.hasNext(); ) {
            results.add(loadTask(i.next()));
        }
        
        return results;
    }
    
}
