package fi.helsinki.cs.titotrainer.app.model.fileconv;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.dom4j.Document;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.fileconv.TaskFileLoader.Result;
import fi.helsinki.cs.titotrainer.app.model.misc.IdComparator;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;

public class TaskFileTest extends TitoTestCase {
    
    private Session hs;
    private SampleTasks sampleTasks;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.sampleTasks = new SampleTasks(hs);
    }
    
    private void assertSameTranslations(TString a, TString b) {
        assertEquals(a.getTranslations(), b.getTranslations());
    }
    
    private void assertCriterionPreserved(Criterion in, Criterion out) {
        assertSameTranslations(in.getAcceptMessage(), out.getAcceptMessage());
        assertSameTranslations(in.getRejectMessage(), out.getRejectMessage());
        
        assertEquals(in.getInput() == null, out.getInput() == null);
        assertEquals(in.getClass(), out.getClass());
        assertEquals(in.getParameters(), out.getParameters());
        
        assertEquals(in.isQualityCriterion(), out.isQualityCriterion());
    }
    
    private void assertCriteriaPreserved(Collection<Criterion> in, Collection<Criterion> out) {
        assertEquals(in.size(), out.size());
        
        Criterion[] inArray = in.toArray(new Criterion[in.size()]);
        Criterion[] outArray = out.toArray(new Criterion[out.size()]);
        Arrays.sort(inArray, new IdComparator()); // They should have been saved and loaded in this order
        
        for (int i = 0; i < inArray.length; ++i) {
            assertCriterionPreserved(inArray[i], outArray[i]);
        }
    }
    
    private void assertInputPreserved(Input in, Input out) {
        assertArrayEquals(in.getInputNumbers(), out.getInputNumbers());
        assertEquals(in.isSecret(), out.isSecret());
        
        assertCriteriaPreserved(in.getCriteria(), out.getCriteria());
    }
    
    private void assertInputsPreserved(Collection<Input> in, Collection<Input> out) {
        assertEquals(in.size(), out.size());
        
        Input[] inArray = in.toArray(new Input[in.size()]);
        Input[] outArray = out.toArray(new Input[out.size()]);
        Arrays.sort(inArray, new IdComparator());
        
        for (int i = 0; i < inArray.length; ++i) {
            assertInputPreserved(inArray[i], outArray[i]);
        }
    }
    
    private void assertTaskPreserved(Task in, Task out) throws Exception {
        assertSameTranslations(in.getTitle(), out.getTitle());
        assertSameTranslations(in.getDescription(), out.getDescription());
        
        assertEquals(in.getType(), out.getType());
        assertEquals(in.getHidden(), out.getHidden());
        assertEquals(in.getDifficulty(), out.getDifficulty());
        assertEquals(in.getMaxSteps(), out.getMaxSteps());
        
        assertEquals(in.getModelSolution(), out.getModelSolution());
        assertEquals(in.getPreCode(), out.getPreCode());
        assertEquals(in.getPostCode(), out.getPostCode());
        
        assertCriteriaPreserved(in.getCriteria(), out.getCriteria());
        
        assertInputsPreserved(in.getInputs(), out.getInputs());
    }
    
    private void doTestLoadShouldBeAbleToReadOutputOfMaker(Task in) throws Exception {
        Document doc = TaskFileMaker.makeTaskXML(in);
        Collection<Result> outs = TaskFileLoader.loadTasksFromXML(doc);
        assertEquals(1, outs.size());
        Task out = outs.iterator().next().getTask();
        
        assertTaskPreserved(in, out);
        assertSameTranslations(in.getCategory().getName(), outs.iterator().next().getCategoryName());
    }
    
    @Test
    public void loadShouldBeAbleToReadOutputOfMaker() throws Exception {
        doTestLoadShouldBeAbleToReadOutputOfMaker(sampleTasks.minimalTask);
        doTestLoadShouldBeAbleToReadOutputOfMaker(sampleTasks.standardTask);
        doTestLoadShouldBeAbleToReadOutputOfMaker(sampleTasks.sumTask);
    }
    
    private void doTestShouldBeAbleToSaveLoadedTasks(Task in) throws Exception {
        Document doc = TaskFileMaker.makeTaskXML(in);
        Collection<Result> outs = TaskFileLoader.loadTasksFromXML(doc);
        assertEquals(1, outs.size());
        
        Task out = outs.iterator().next().getTask();
        in.getCourse().getTasks().add(out);
        out.setCourse(in.getCourse());
        hs.save(out);
        hs.flush();
    }
    
    @Test
    public void shouldBeAbleToSaveLoadedTasks() throws Exception {
        doTestShouldBeAbleToSaveLoadedTasks(sampleTasks.minimalTask);
        doTestShouldBeAbleToSaveLoadedTasks(sampleTasks.standardTask);
        doTestShouldBeAbleToSaveLoadedTasks(sampleTasks.sumTask);
    }
    
}
