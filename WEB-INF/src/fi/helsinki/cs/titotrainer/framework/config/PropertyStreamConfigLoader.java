package fi.helsinki.cs.titotrainer.framework.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import fi.helsinki.cs.titotrainer.framework.stream.InputStreamOpener;

/**
 * <p>Loads configurations from a property input stream.</p>
 * 
 * <p>The configuration name given will be treated as a file path.
 * The .properties suffix may be omitted.</p>
 */
public class PropertyStreamConfigLoader implements ConfigLoader {
    
    protected InputStreamOpener opener;
    
    /**
     * <p>Default constructor.</p>
     * 
     * @param opener An opener of the property stream.
     */
    public PropertyStreamConfigLoader(InputStreamOpener opener) {
        this.opener = opener;
    }
    
    @Override
    public Config load(String name) throws FileNotFoundException, IOException {
        InputStream ios;
        
        try {
            ios = this.opener.open(name);
        } catch (FileNotFoundException e) {
            if (!name.endsWith(".properties"))
                return load(name + ".properties");
            else
                throw e;
        }
        
        Properties props = new Properties();
        props.load(new InputStreamReader(ios));
        return new PropertyConfig(props);
    }
}
