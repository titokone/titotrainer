package fi.helsinki.cs.titotrainer.testsupport.framework.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import fi.helsinki.cs.titotrainer.framework.stream.FileOpener;
import fi.helsinki.cs.titotrainer.framework.stream.InputStreamOpener;
import fi.helsinki.cs.titotrainer.framework.stream.PrefixOpener;

/**
 * Tests can create instances of these to have classes load their
 * configurations from the tests/conf dir instead.
 */
public class TestConfigOpener implements InputStreamOpener {
    
    private InputStreamOpener subopener;
    
    public TestConfigOpener() {
        this.subopener = new PrefixOpener(new FileOpener(), "tests/conf/");
    }

    @Override
    public InputStream open(String key) throws IOException, FileNotFoundException {
        return this.subopener.open(key);
    }
    
}
