package fi.helsinki.cs.titotrainer.app.session;

import java.io.Serializable;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Stores categories for the user between page requests.</p>
 * 
 * <p>For example, a controller may store an error message
 * in the messenger.</p>
 * 
 * <p>This class is thread-safe.</p>
 */
public class Messenger implements Serializable {
    
    /**
     * The category for global error messages, which are
     * consumed by the default page header.
     */
    public static final String GLOBAL_ERROR_CATEGORY = "global.error";
    /**
     * The category for global warning messages, which are
     * consumed by the default page header.
     */
    public static final String GLOBAL_WARNING_CATEGORY = "global.warning";
    /**
     * The category for global status messages, which are
     * consumed by the default page header.
     */
    public static final String GLOBAL_STATUS_CATEGORY = "global.status";
    /**
     * The category for global success messages, which are
     * consumed by the default page header.
     */
    public static final String GLOBAL_SUCCESS_CATEGORY = "global.success";
    
    /**
     * The default number of hops a message lives unless consumed.
     */
    public static int DEFAULT_TTL = 30;
    
    private static class Message implements Serializable {
        public String msg;
        public int ttl;
        public Message(String msg, int ttl) {
            this.msg = msg;
            this.ttl = ttl;
        }
    }
    
    private Map<String, Deque<Message>> categories;
    
    public Messenger() {
        this.categories = new HashMap<String, Deque<Message>>();
    }
    
    private Deque<Message> getOrCreateCategory(String category) {
        Deque<Message> cat = this.categories.get(category);
        if (cat == null) {
            cat = new LinkedList<Message>();
            this.categories.put(category, cat);
        }
        
        return cat;
    }

    /**
     * <p>Stores a message in the messenger before other categories in its category.</p>
     * 
     * <p>The message is stored for <code>ttl</code> hops.
     * That is, a ttl value of 0 has the message survive until
     * the next {@link #hop()} call.</p>
     * 
     * @param category The category to add the message to.
     * @param message The message.
     * @param ttl The number of hops the message should survive.
     *            If negative, the message is not stored.
     */
    public synchronized void prependMessage(String category, String message, int ttl) {
        if (ttl < 0)
            return;
        
        Message msg = new Message(message, ttl);
        this.getOrCreateCategory(category).addFirst(msg);
    }
    
    /**
     * Calls {@link #prependMessage(String, String, int)} with {@link #DEFAULT_TTL} as the TTL value.
     * 
     * @see #prependMessage(String, String, int)
     */
    public synchronized void prependMessage(String category, String message) {
        prependMessage(category, message, DEFAULT_TTL);
    }
    
    /**
     * <p>Stores a message in the messenger after other categories in its category.</p>
     * 
     * <p>The message is stored for <code>ttl</code> hops.
     * That is, a ttl value of 0 has the message survive until
     * the next {@link #hop()} call.</p>
     * 
     * @param category The category to add the message to.
     * @param message The message.
     * @param ttl The number of hops the message should survive.
     *            If negative, the message is not stored.
     */
    public synchronized void appendMessage(String category, String message, int ttl) {
        if (ttl < 0)
            return;
        
        Message msg = new Message(message, ttl);
        this.getOrCreateCategory(category).addLast(msg);
    }
    
    /**
     * Calls {@link #appendMessage(String, String, int)} with {@link #DEFAULT_TTL} as the TTL value.
     * 
     * @see #appendMessage(String, String, int)
     */
    public synchronized void appendMessage(String category, String message) {
        appendMessage(category, message, DEFAULT_TTL);
    }
    
    /**
     * Returns a copy of all messages in the given category.
     * 
     * @param category The category to return.
     * @return A copy of the list of strings in the category.
     *         May be empty but not null.
     */
    public synchronized List<String> getMessages(String category) {
        Deque<Message> cat = this.categories.get(category);
        if (cat == null)
            return Collections.emptyList();
        
        List<String> msgList = new LinkedList<String>();
        for (Message m : cat) {
            msgList.add(m.msg);
        }
        return msgList;
    }
    
    
    /**
     * Returns a copy of all messages in a given category and removes
     * the category.
     * 
     * @param category The category to return.
     * @return A copy of the list of strings in the category.
     *         May be empty but not null.
     */
    public synchronized List<String> consumeMessages(String category) {
        List<String> messages = this.getMessages(category);
        this.categories.remove(category);
        return messages;
    }
    
    /**
     * <p>Reduces the Time-to-live of all stored categories and
     * deletes categories whose TTL goes below zero.</p>
     */
    public synchronized void hop() {
        for (Deque<Message> cat : this.categories.values()) {
            Iterator<Message> i = cat.iterator();
            while (i.hasNext()) {
                Message msg = i.next();
                assert(msg.ttl >= 0);
                --msg.ttl;
                if (msg.ttl < 0) {
                    i.remove();
                }
            }
        }
        
        // Clear empty categories
        Iterator<String> i = this.categories.keySet().iterator();
        while (i.hasNext()) {
            if (this.categories.get(i.next()).isEmpty())
                i.remove();
        }
    }
    
}
