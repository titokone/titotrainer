package fi.helsinki.cs.titotrainer.framework.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * <p>Interface to an object that is able to write something
 * meaningful to an output stream.</p>
 */
public interface ResponseBodyWriter {
    /**
     * <p>Writes data to an output stream.</p>
     * 
     * <p>A character set is provided for creating an
     * {@link OutputStreamWriter} object if character data is to be output.
     * In the case of binary data, the charset parameter may be ignored.</p>
     * 
     * @param os The output stream to write to.
     * @param charset The preferred character set.
     * @throws IOException
     */
    public void writeResponse(OutputStream os, Charset charset) throws IOException;
}
