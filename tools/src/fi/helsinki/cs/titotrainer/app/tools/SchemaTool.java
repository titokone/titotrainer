package fi.helsinki.cs.titotrainer.app.tools;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;

import fi.helsinki.cs.titotrainer.app.misc.PlainAppConfiguration;

/**
 * <p>An ad-hoc tool to print out the database schema as generated
 * by Hibernate.</p>
 */
public class SchemaTool {
    
    private static enum Action {PRINT, EXEC};
    private static enum ScriptType {CREATE, DROP};
    
    public static String getHelpText() {
        return "Usage: SchemaTool {print|exec} {create|drop}";
    }
    
    public static void main(String[] args) throws Exception {
        
        if (args.length != 2) {
            System.err.println(getHelpText());
            System.exit(1);
        }
        
        Action action;
        if (args[0].equals("print")) {
            action = Action.PRINT;
        } else if (args[0].equals("exec")) {
            action = Action.EXEC;
        } else {
            System.err.println(getHelpText());
            System.exit(1);
            return;
        }
        
        ScriptType scriptType;
        if (args[1].equals("create")) {
            scriptType = ScriptType.CREATE;
        } else if (args[1].equals("drop")) {
            scriptType = ScriptType.DROP;
        } else {
            System.err.println(getHelpText());
            System.exit(1);
            return;
        }
        
        PlainAppConfiguration pac = new PlainAppConfiguration(".");
        Configuration hibConf = pac.getHibernateInstance().getConfiguration();
        SessionFactory hibSf = pac.getHibernateInstance().getSessionFactory();
        
        String[] script = null;
        switch (scriptType) {
        case CREATE:
            script = hibConf.generateSchemaCreationScript(Dialect.getDialect(hibConf.getProperties()));
            break;
        case DROP:
            script = hibConf.generateDropSchemaScript(Dialect.getDialect(hibConf.getProperties()));
            break;
        }
        assert(script != null);
        
        switch (action) {
        case PRINT:
            for (String line : script) {
                System.out.println(line);
            }
            break;
        case EXEC:
            int errorCount = 0;
            for (String line : script) {
                System.err.println(line);
                try {
                    StatelessSession s = hibSf.openStatelessSession();
                    Connection c = s.connection();
                    c.createStatement().executeUpdate(line);
                    c.commit();
                    s.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    ++errorCount;
                }
            }
            
            if (errorCount > 0) {
                System.err.println("Failed commands: " + errorCount);
                System.exit(1);
            } else {
                System.err.println("Completed successfully.");
            }
            break;
        }
    }
}
