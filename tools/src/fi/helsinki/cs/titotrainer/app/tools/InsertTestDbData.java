package fi.helsinki.cs.titotrainer.app.tools;

import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.misc.PlainAppConfiguration;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.criteria.CodeSizeCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.DataReferencesCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.DataSizeCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.ForbiddenInstructionsCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.MaxStackSizeCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.MemoryReferencesCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.ModelRegisterCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.ModelScreenOutputCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.ModelSymbolCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.RegisterCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.Relation;
import fi.helsinki.cs.titotrainer.app.model.criteria.RequiredInstructionsCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.ScreenOutputCriterion;
import fi.helsinki.cs.titotrainer.app.model.criteria.SymbolCriterion;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.model.TransactionalTask;

public class InsertTestDbData {
    
    private static Locale ENGLISH = Locale.ENGLISH;
    private static Locale FINNISH = new Locale("fi");
    
    private Course autumn09Course;
    private Course spring10Course;
    
    private Category easyCategory;
    private Category hardCategory;
    
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Task task5;
    private Task englishOnlyTask;
    private Task allCriteriaTask;

    private User admin;
    private User student;
    
    private Answer answer1;
    private Answer answer2;
    
    private InsertTestDbData() {
    }
    
    private static int[] makeArray(int ... a) {
        return a;
    }

    private String lines(String ... s) {
        return StringUtils.join(s, "\n");
    }
    
    private void addAdmins(Session session) {
        admin = new User();
        admin.setUsername("admin");
        admin.setParentRole(TitoBaseRole.ADMINISTRATOR);
        admin.setPasswordSha1(User.hashPassword("admin"));
        session.save(admin);
    }

    private void addCategories(Session session) {
        easyCategory = new Category();
        easyCategory.setCourse(autumn09Course);
        this.autumn09Course.getCategories().add(easyCategory);
        easyCategory.setName(ENGLISH, "Easy tasks");
        easyCategory.setName(FINNISH, "Helpot tehtävät");
        session.save(easyCategory);
        
        hardCategory = new Category();
        hardCategory.setCourse(autumn09Course);
        this.autumn09Course.getCategories().add(hardCategory);
        hardCategory.setName(ENGLISH, "Hard tasks");
        hardCategory.setName(FINNISH, "Hankalat tehtävät");
        session.save(hardCategory);
    }

    private void addCourses(Session session) {
        autumn09Course = new Course();
        autumn09Course.setName(ENGLISH, "TiTo Autumn 2009");
        autumn09Course.setName(FINNISH, "TiTo Syksy 2009");
        session.save(autumn09Course);
        
        spring10Course = new Course();
        spring10Course.setName(ENGLISH, "TiTo Spring 2010");
        spring10Course.setName(FINNISH, "TiTo Kevät 2010");
        session.save(spring10Course);
    }
    
    private void addTasks(Session session) {
        {
            this.task1 = new Task();
            this.task1.setTitle(ENGLISH, "Output a number");
            this.task1.setTitle(FINNISH, "Tulosta luku");
            this.task1.setType(Task.Type.PROGRAMMING);
            this.task1.setDescription(ENGLISH, "Write a program that outputs the integer 3. Also leave the integer 3 in the register R1.");
            this.task1.setDescription(FINNISH, "Kirjoita ohjelma, joka tulostaa luvun 3. Jätä luku 3 myös rekisteriin R1.");
            this.task1.setDifficulty(100);
            this.task1.setCourse(autumn09Course);
            this.autumn09Course.getTasks().add(this.task1);
            this.task1.setCategory(easyCategory);
            this.easyCategory.getTasks().add(this.task1);
            this.task1.setCreator(admin);
            this.task1.setCreationTime(new GregorianCalendar(2008, 11, 11, 11, 11, 11).getTime());
            
            {
                Criterion c = new ScreenOutputCriterion(makeArray(3));
                c.setAcceptMessage(ENGLISH, "The output was correct.");
                c.setAcceptMessage(FINNISH, "Tuloste oli oikein.");
                c.setRejectMessage(ENGLISH, "The output was incorrect.");
                c.setRejectMessage(FINNISH, "Tuloste oli väärin.");
                c.setTask(this.task1);
                this.task1.getCriteria().add(c);
            }
            
            {
                Criterion c = new RegisterCriterion(1, Relation.EQ, 3l);
                c.setRejectMessage(ENGLISH, "The value of R1 was not correct.");
                c.setRejectMessage(FINNISH, "R1:n arvo oli väärin.");
                c.setTask(this.task1);
                this.task1.getCriteria().add(c);
            }
            
            {
                this.task1.getInputs().add(new Input(this.task1, "", false));
            }
            
            session.save(this.task1);
        }
        
        {
            this.task2 = new Task();
            this.task2.setTitle(ENGLISH, "Compute a sum");
            this.task2.setTitle(FINNISH, "Laske summa");
            this.task2.setType(Task.Type.PROGRAMMING);
            this.task2.setDescription(ENGLISH, "Write a program that reads two integers as input and writes their sum as output.");
            this.task2.setDescription(FINNISH, "Kirjoita ohjelma, joka lukee syötteestä kaksi lukua ja tulostaa niiden summan.");
            this.task2.setDifficulty(110);
            this.task2.setCourse(autumn09Course);
            this.autumn09Course.getTasks().add(this.task2);
            this.task2.setCategory(easyCategory);
            this.easyCategory.getTasks().add(this.task2);
            this.task2.setCreator(admin);
            this.task2.setCreationTime(new GregorianCalendar(2008, 11, 7, 12, 42, 9).getTime());
            
            this.task2.setModelSolution(lines(
                "IN R1,=KBD",
                "IN R2,=KBD",
                "ADD R1,R2",
                "OUT R1,=CRT",
                "SVC SP,=HALT"
                ));
            
            {
                Criterion c = new ModelScreenOutputCriterion();
                c.setAcceptMessage(ENGLISH, "The output was correct.");
                c.setAcceptMessage(FINNISH, "Tuloste oli oikein.");
                c.setRejectMessage(ENGLISH, "The output was incorrect.");
                c.setRejectMessage(FINNISH, "Tuloste oli väärin.");
                c.setTask(this.task2);
                this.task2.getCriteria().add(c);
            }
            
            {
                this.task2.getInputs().add(new Input(this.task2, "3,5", false));
                this.task2.getInputs().add(new Input(this.task2, "-9,55", false));
                this.task2.getInputs().add(new Input(this.task2, "1,7", false));
            }
            
            session.save(this.task2);
        }
        
        {
            this.task3 = new Task();
            this.task3.setTitle(ENGLISH, "Compute a factorial <n!>"); // < and > must be escaped in html
            this.task3.setTitle(FINNISH, "Laske kertoma <n!>");
            this.task3.setType(Task.Type.PROGRAMMING);
            this.task3.setDescription(ENGLISH, "Write a program that reads an integer as input and writes its factorial as output.");
            this.task3.setDescription(FINNISH, "Kirjoita ohjelma, joka lukee syötteestä luvun ja tulostaa sen kertoman.");
            this.task3.setDifficulty(300);
            this.task3.setCourse(spring10Course);
            this.spring10Course.getTasks().add(this.task3);
            this.task3.setCreator(admin);
            this.task3.setCreationTime(new GregorianCalendar(2008, 11, 12, 14, 17, 19).getTime());
            
            this.task3.setModelSolution(lines(
                "        IN R1,=KBD",
                "        LOAD R2,=1",
                "loop    JNPOS R1,end",
                "        MUL R2,R1",
                "        SUB R1,=1",
                "        JUMP loop",
                "end     OUT R2,=CRT",
                "        SVC SP,=HALT"
                ));
            
            session.save(this.task3);
        }
        
        {
            this.englishOnlyTask = new Task();
            this.englishOnlyTask.setTitle(ENGLISH, "Compute a^b");
            this.englishOnlyTask.setType(Task.Type.PROGRAMMING);
            this.englishOnlyTask.setDescription(ENGLISH, "Write a program that reads two integers as input and writes the first integer to the power of the other as output.");
            this.englishOnlyTask.setDifficulty(290);
            this.englishOnlyTask.setCourse(spring10Course);
            this.spring10Course.getTasks().add(this.englishOnlyTask);
            this.englishOnlyTask.setCreator(admin);
            this.englishOnlyTask.setCreationTime(new GregorianCalendar(2008, 12, 7, 8, 30, 55).getTime());
            this.englishOnlyTask.setHidden(true);
            session.save(this.englishOnlyTask);
        }
        
        {
            this.task4 = new Task();
            this.task4.setTitle(ENGLISH, "Save a value");
            this.task4.setTitle(FINNISH, "Talleta arvo");
            this.task4.setType(Task.Type.FILL_IN);
            this.task4.setDescription(ENGLISH, "Fill in the program so it saves the value in R1 to memory location directed by the value of x");
            this.task4.setDescription(FINNISH, "Täydennä tehtävä siten että R1:ssä oleva arvo tallettuu x:n osoittamaan muistipaikkaan");
            this.task4.setDifficulty(250);
            this.task4.setCourse(spring10Course);
            this.spring10Course.getTasks().add(this.task4);
            this.task4.setCreator(admin);
            this.task4.setPreCode("x DC 5\nLOAD R1, =100");
            this.task4.setPostCode("SVC SP, =HALT");
            this.task4.setCreationTime(new GregorianCalendar(2008, 11, 8, 7, 24, 23).getTime());
            
            {
                Criterion c = new SymbolCriterion("x", Relation.EQ, 100);
                c.setAcceptMessage(ENGLISH, "Symbol value correct.");
                c.setAcceptMessage(FINNISH, "Symbolin arvo oikein.");
                c.setRejectMessage(ENGLISH, "Symbol value incorrect.");
                c.setRejectMessage(FINNISH, "Symbolin arvo väärin.");
                c.setTask(this.task4);
                this.task4.getCriteria().add(c);
            }
            
            session.save(this.task4);
        }
        
        {
            this.task5 = new Task();
            this.task5.setTitle(ENGLISH, "Echo");
            this.task5.setTitle(FINNISH, "Kaiutus");
            this.task5.setType(Task.Type.PROGRAMMING);
            this.task5.setDescription(ENGLISH, "Make a program that reads one input value, stores it in R3 and then outputs it.");
            this.task5.setDescription(FINNISH, "Kirjoita ohjelma, joka lukee yhden syötearvon, tallentaa sen R3:een ja tulostaa sen ruudulle.");
            this.task5.setDifficulty(110);
            this.task5.setCourse(autumn09Course);
            this.autumn09Course.getTasks().add(this.task5);
            this.task5.setCreator(admin);
            this.task5.setPreCode("x DC 5\nLOAD R1, =100");
            this.task5.setPostCode("SVC SP, =HALT");
            this.task5.setCreationTime(new GregorianCalendar(2008, 11, 8, 7, 24, 25).getTime());
            
            {
                Input i = new Input(this.task5, "123", false);
                this.task5.getInputs().add(i);
                
                Criterion c = new RegisterCriterion(3, Relation.EQ, 123);
                c.setInput(i);
                c.setAcceptMessage(ENGLISH, "Register value correct.");
                c.setAcceptMessage(FINNISH, "Rekisterin arvo oikein.");
                c.setRejectMessage(ENGLISH, "Register value incorrect.");
                c.setRejectMessage(FINNISH, "Rekisterin arvo väärin.");
                c.setTask(this.task5);
                this.task5.getCriteria().add(c);
            }
            
            {
                Input i = new Input(this.task5, "456", true);
                this.task5.getInputs().add(i);
                
                Criterion c = new RegisterCriterion(3, Relation.EQ, 456);
                c.setInput(i);
                c.setAcceptMessage(ENGLISH, "Register value correct.");
                c.setAcceptMessage(FINNISH, "Rekisterin arvo oikein.");
                c.setRejectMessage(ENGLISH, "Register value incorrect.");
                c.setRejectMessage(FINNISH, "Rekisterin arvo väärin.");
                c.setTask(this.task5);
                this.task5.getCriteria().add(c);
            }
            
            session.save(this.task5);
        }
        
        {
            this.allCriteriaTask = new Task();
            this.allCriteriaTask.setTitle(ENGLISH, "Testing all criteria");
            this.allCriteriaTask.setDescription(ENGLISH, "The purpose of this task is to test the admin interface with all criteria.");
            this.allCriteriaTask.setDifficulty(999);
            this.allCriteriaTask.setCourse(autumn09Course);
            this.autumn09Course.getTasks().add(this.allCriteriaTask);
            this.allCriteriaTask.setCategory(hardCategory);
            this.hardCategory.getTasks().add(this.allCriteriaTask);
            this.allCriteriaTask.setCreator(admin);
            this.allCriteriaTask.setCreationTime(new GregorianCalendar(2009, 1, 8, 7, 24, 25).getTime());
            
            this.allCriteriaTask.setType(Task.Type.FILL_IN);
            this.allCriteriaTask.setPreCode("x DC 5\nLOAD R1,x");
            this.allCriteriaTask.setPostCode("SVC SP,=HALT");
            
            {
                Criterion[] criteria = {
                    new CodeSizeCriterion(Relation.LTE, 100),
                    new DataReferencesCriterion(Relation.LTE, 1000),
                    new DataSizeCriterion(Relation.LTE, 1000),
                    new ForbiddenInstructionsCriterion("DIV"),
                    new MaxStackSizeCriterion(Relation.LTE, 1000),
                    new MemoryReferencesCriterion(Relation.LTE, 1000),
                    new ModelRegisterCriterion(5, Relation.EQ),
                    new ModelScreenOutputCriterion(),
                    new ModelSymbolCriterion("x", Relation.EQ),
                    new RegisterCriterion(5, Relation.EQ, 5),
                    new RequiredInstructionsCriterion("SVC", "LOAD"),
                    new ScreenOutputCriterion(1, 2, 3),
                    new SymbolCriterion("x", Relation.EQ, 5)
                };
                
                for (Criterion c : criteria) {
                    c.setTask(this.allCriteriaTask);
                    c.setAcceptMessage(ENGLISH, "Correct (" + c.getClass().getSimpleName() + ")");
                    c.setRejectMessage(ENGLISH, "Incorrect (" + c.getClass().getSimpleName() + ")");
                    this.allCriteriaTask.getCriteria().add(c);
                }
            }
            
            session.save(this.allCriteriaTask);
        }
    }
    
    private void addStudents(Session session) {
        student = new User();
        student.setUsername("student");
        student.setParentRole(TitoBaseRole.STUDENT);
        student.setPasswordSha1(User.hashPassword("student"));
        student.setStudentNumber("1234512654");
        student.setCourseId(autumn09Course.getId());
        session.save(student);
    }
    
    private void addAnswers(Session session) {
        this.answer1 = new Answer();
        this.answer1.setTask(this.task1);
        this.task1.getAnswers().add(this.answer1);
        this.answer1.setCode("LOAD R1,=3\nOUT R1,=CRT\nSVC SP,=HALT");
        this.answer1.setUser(this.student);
        //this.answer1.setTimestamp(new GregorianCalendar(2008, 11, 11, 11, 11, 11).getTime());
        session.save(this.answer1);
        
        this.answer2 = new Answer();
        this.answer2.setTask(this.task2);
        this.task1.getAnswers().add(this.answer2);
        this.answer2.setCode("IN R1,=KBD\nIN R2,=KBD\nADD R1,R2\nOUT R1,=CRT\nSVC SP,=HALT");
        this.answer2.setUser(this.student);
        //this.answer2.setTimestamp(new GregorianCalendar(2008, 11, 12, 14, 17, 19).getTime());
        session.save(this.answer2);
    }
    
    public static void main(String[] args) throws Exception {
        PlainAppConfiguration pac = new PlainAppConfiguration(".");
        SessionFactory hibSf = pac.getHibernateInstance().getSessionFactory();
        
        Session hs = hibSf.openSession();
        (new TransactionalTask<None<?>>() {
            @Override
            protected None<?> run(Session hs) throws Exception {
                InsertTestDbData inserter = new InsertTestDbData();
                
                System.out.println("* Adding admins...");
                inserter.addAdmins(hs);
                
                System.out.println("* Adding courses...");
                inserter.addCourses(hs);
                
                System.out.println("* Adding task categories...");
                inserter.addCategories(hs);
                
                System.out.println("* Adding tasks...");
                inserter.addTasks(hs);
                
                System.out.println("* Adding students...");
                inserter.addStudents(hs);
                
                System.out.println("* Adding answers...");
                inserter.addAnswers(hs);
                
                return null;
            }
        }).invoke(hs);
        
        hs.flush();
        
        System.out.println("Test data inserted successfully.");
    }
    
}
