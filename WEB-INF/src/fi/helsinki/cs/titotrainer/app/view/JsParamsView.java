package fi.helsinki.cs.titotrainer.app.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.view.template.TemplateUtils;
import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.DefaultViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.View;

/**
 * A view that prints some constant and configuration data as Javascript
 * to configure the static Javascript files.
 */
public class JsParamsView implements View<TitoRequest> {

    private final static class Responder implements ResponseBodyWriter {

        private static String MAIN_OBJECT = "APP_CONFIG";
        
        private Writer out;
        private TitoRequest req;
        private TemplateUtils utils;
        
        public Responder(TitoRequest req) {
            this.req = req;
            this.utils = TemplateUtils.getInstance();
        }
        
        // Helpers:
        
        private void print(String s) throws IOException {
            out.write(s);
        }
        
        private void println(String s) throws IOException {
            out.write(s);
            out.write("\n");
        }
        
        // Sections:
        
        private void printBasePath() throws IOException {
            println(MAIN_OBJECT + ".basePath = " + utils.quoteJavascript(req.getBasePath()) + ";");
        }
        
        private void printSupportedLocales() throws IOException {
            print(MAIN_OBJECT + ".supportedLocales = ['");
            print(StringUtils.join(req.getContext().getTitoTranslation().getSupportedLocales(), "','"));
            println("'];");
        }
        
        private void printValidOpcodes() throws IOException {
            print(MAIN_OBJECT + ".validOpcodes = ['");
            print(StringUtils.join(TitokoneState.getOpcodeNames().values(), "','"));
            println("'];");
            println(MAIN_OBJECT + ".validOpcodes.sort();");
        }
        
        // Main:
        
        @Override
        public void writeResponse(OutputStream os, Charset charset) throws IOException {
            out = new OutputStreamWriter(os, charset);
            
            println(MAIN_OBJECT + " = {};");
            
            printBasePath();
            printSupportedLocales();
            printValidOpcodes();
            
            out.flush();
        }
        
    }
    
    @Override
    public ViewResponse handle(TitoRequest req) throws Exception {
        return new DefaultViewResponse(new Responder(req), "text/javascript; charset=utf-8");
    }

    @Override
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
}
