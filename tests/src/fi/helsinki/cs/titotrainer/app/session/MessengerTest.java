package fi.helsinki.cs.titotrainer.app.session;

import static org.junit.Assert.*;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

public class MessengerTest {
    
    private Messenger messenger;
    
    @Before
    public void setUp() {
        this.messenger = new Messenger();
    }
    
    @Test
    public void getMessagesMethodShouldReturnNonNullEmptyListIfThereAreNoMessages() {
        assertTrue(messenger.getMessages("foo").isEmpty());
    }
    
    @Test
    public void consumeMessagesMethodShouldReturnNonNullEmptyListIfThereAreNoMessages() {
        assertTrue(messenger.consumeMessages("foo").isEmpty());
    }
    
    @Test
    public void messagesShouldBeAddedMessagesToSpecificCategory() {
        messenger.appendMessage("cat1", "mid");
        messenger.appendMessage("cat1", "post");
        messenger.appendMessage("cat2", "other");
        messenger.prependMessage("cat1", "pre");
        
        String[] cat1Expected = {"pre", "mid", "post"};
        String[] cat2Expected = {"other"};
        
        assertArrayEquals(cat1Expected, messenger.getMessages("cat1").toArray());
        assertArrayEquals(cat2Expected, messenger.getMessages("cat2").toArray());
    }
    
    @Test
    public void consumeMessagesMethodShouldRemoveMessagesFromCategory() {
        messenger.appendMessage("cat1", "msg1");
        messenger.appendMessage("cat2", "msg2");
        
        String[] cat1Expected = {"msg1"};
        String[] cat2Expected = {"msg2"};
        
        assertArrayEquals(cat1Expected, messenger.consumeMessages("cat1").toArray());
        assertTrue(messenger.consumeMessages("cat1").isEmpty());
        
        assertArrayEquals(cat2Expected, messenger.getMessages("cat2").toArray());
    }
    
    @Test
    public void hoppingShouldExpireMessagesByTimeToLiveValue() {
        messenger.prependMessage("cat1", "msg3", 3);
        messenger.prependMessage("cat1", "msg2", 2);
        messenger.prependMessage("cat1", "msg1", 1);
        messenger.prependMessage("cat1", "msg0", 0);
        
        String[] expected = {"msg0", "msg1", "msg2", "msg3"};
        
        do {
            assertArrayEquals(expected, messenger.getMessages("cat1").toArray());
            messenger.hop();
            expected = (String[])ArrayUtils.remove(expected, 0);
        } while (expected.length > 0);
        assertTrue(messenger.getMessages("cat1").isEmpty());
    }
    
    @Test
    public void messagesWithNegativeTimeToLiveShouldNotBeAdded() {
        messenger.appendMessage("cat1", "msg1", -1);
        messenger.prependMessage("cat2", "msg2", -9);
        
        assertTrue(messenger.getMessages("cat1").isEmpty());
        assertTrue(messenger.getMessages("cat2").isEmpty());
    }
    
}
