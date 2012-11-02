package fi.helsinki.cs.titotrainer.app.model.fileconv;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.misc.IdComparator;

/**
 * Saves a task with its criteria and inputs into XML.
 */
public class TaskFileMaker {
    
    private static void add(Element parent, String name, TString contents) {
        for (Map.Entry<Locale, String> ent : contents.getTranslations().entrySet()) {
            parent.addElement(name).addAttribute("lang", ent.getKey().toString())
                                   .addText(ent.getValue());
        }
    }
    
    private static void add(Element parent, String name, Object value) {
        if (value != null) {
            assert(!(value instanceof TString)); // (other overload)
            parent.addElement(name).addText(value.toString());
        }
    }
    
    private static void addWithWhitespace(Element parent, String name, String value) {
        if (value != null) {
            parent.addElement(name).addCDATA(value);
        }
    }
    
    private static void addWithWhitespace(Element parent, String name, TString contents) {
        if (contents != null && contents.getTranslations() != null) {
            for (Map.Entry<Locale, String> ent : contents.getTranslations().entrySet()) {
                parent.addElement(name).addAttribute("lang", ent.getKey().toString())
                                       .addCDATA(ent.getValue());
            }
        }
    }
    
    private static void addCriterion(Element parent, String elementName, Criterion crit) {
        Element critEl = parent.addElement(elementName);
        critEl.addAttribute("type", crit.getClass().getSimpleName());
        addWithWhitespace(critEl, "acceptMessage", crit.getAcceptMessage());
        addWithWhitespace(critEl, "rejectMessage", crit.getRejectMessage());
        add(critEl, "qualityCriterion", crit.isQualityCriterion() ? "true" : "false");
        add(critEl, "parameters", crit.getParameters());
    }
    
    private static void addTask(Element parent, String elementName, Task task) {
        Element taskEl = parent.addElement(elementName);
        
        add(taskEl, "title", task.getTitle());
        if (task.getCategory() != null)
            add(taskEl, "category", task.getCategory().getName());
        
        addWithWhitespace(taskEl, "description", task.getDescription());
        
        add(taskEl, "type", task.getType());
        add(taskEl, "hidden", task.getHidden() ? "true" : "false");
        add(taskEl, "difficulty", task.getDifficulty());
        add(taskEl, "maxSteps", task.getMaxSteps());
        addWithWhitespace(taskEl, "modelSolution", task.getModelSolution());
        addWithWhitespace(taskEl, "preCode", task.getPreCode());
        addWithWhitespace(taskEl, "postCode", task.getPostCode());
        
        // Save criteria sorted by their ID so their order stays consistent
        Criterion[] criteria = task.getCriteria().toArray(new Criterion[task.getCriteria().size()]);
        Arrays.sort(criteria, new IdComparator());
        
        // Global criteria
        for (Criterion crit : criteria) {
            if (crit.getInput() == null) {
                addCriterion(taskEl, "globalCriterion", crit);
            }
        }
        
        // Inputs and their criteria
        for (Input input : task.getInputs()) {
            Element inputEl = taskEl.addElement("input");
            add(inputEl, "secret", input.isSecret() ? "true" : "false");
            add(inputEl, "input", input.getInput());
            
            for (Criterion c : criteria) {
                if (c.getInput() == input) {
                    addCriterion(inputEl, "criterion", c);
                }
            }
        }
    }
    
    public static Document makeTasksXML(Collection<Task> tasks) {
        Document doc = DocumentHelper.createDocument();
        doc.setXMLEncoding("utf-8");
        
        Element root = doc.addElement("tasks");
        
        for (Task task : tasks)
            addTask(root, "task", task);
        return doc;
    }
    
    public static Document makeTaskXML(Task task) {
        return makeTasksXML(Collections.singleton(task));
    }
    
}
