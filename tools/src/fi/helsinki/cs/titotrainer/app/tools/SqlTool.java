package fi.helsinki.cs.titotrainer.app.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.hibernate.SessionFactory;

import fi.helsinki.cs.titotrainer.app.misc.PlainAppConfiguration;

/**
 * <p>A very minimal SQL shell.</p>
 */
public class SqlTool {
    
    private char commandSeparator = ';';
    private Connection connection;
    
    protected Reader inputReader;
    protected PrintWriter outputWriter;
    
    
    public SqlTool(Reader input, PrintWriter output, Connection connection) {
        if (input == null)
            throw new NullPointerException("input may not be null");
        if (output == null)
            throw new NullPointerException("output may not be null");
        if (connection == null)
            throw new NullPointerException("connection may not be null");
        this.inputReader = input;
        this.outputWriter = output;
        this.connection = connection;
    }
    
    public char getCommandSeparator() {
        return commandSeparator;
    }
    
    public void setCommandSeparator(char commandSeparator) {
        this.commandSeparator = commandSeparator;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public Reader getInputReader() {
        return inputReader;
    }
    
    public PrintWriter getOutputWriter() {
        return outputWriter;
    }
    
    protected String readCommand() throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        do {
            ch = inputReader.read();
            if (ch == -1)
                return null;
            
            if (ch != commandSeparator)
                sb.append((char)ch);
        } while (ch != commandSeparator);
        
        return sb.toString();
    }
    
    public boolean executeCommand() throws IOException, SQLException {
        String command = "";
        while (command.trim().isEmpty()) {
            command = readCommand();
            if (command == null)
                return false;
        }
        
        PreparedStatement ps = this.getConnection().prepareStatement(command);
        boolean isResultSet = ps.execute();
        
        if (isResultSet) {
            this.handleResultSet(ps.getResultSet());
        } else {
            this.handleUpdateCount(ps.getUpdateCount());
        }
        
        return true;
    }
    
    protected void handleResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        outputWriter.print("* ");
        for (int col = 1; col <= meta.getColumnCount(); ++col) {
            if (col > 1)
                outputWriter.print(" | ");
            outputWriter.print(meta.getColumnName(col));
        }
        outputWriter.println();
        
        int row = 0;
        while (rs.next()) {
            ++row;
            outputWriter.print(row);
            outputWriter.print(" ");
            for (int col = 1; col <= meta.getColumnCount(); ++col) {
                if (col > 1)
                    outputWriter.print(" | ");
                outputWriter.print(rs.getObject(col).toString());
            }
            outputWriter.println();
        }
    }
    
    protected void handleUpdateCount(int updateCount) {
        outputWriter.println("Rows affected: " + updateCount);
    }
    
    public static void main(String[] args) throws Exception {
        PlainAppConfiguration pac = new PlainAppConfiguration(".");
        SessionFactory hibSf = pac.getHibernateInstance().getSessionFactory();
        Connection conn = hibSf.openStatelessSession().connection();
        
        SqlTool tool = new SqlTool(new InputStreamReader(System.in), new PrintWriter(System.out), conn);
        
        final String PROMPT = "> ";
        boolean eof = false;
        do {
            tool.getOutputWriter().print(PROMPT);
            tool.getOutputWriter().flush();
            try {
                eof = !tool.executeCommand();
            } catch (SQLException e) {
                tool.getOutputWriter().println(e.getMessage());
            }
        } while (!eof);
        
        conn.commit();
        tool.getOutputWriter().println();
        tool.getOutputWriter().println("Have a nice day, boss!");
    }
}
