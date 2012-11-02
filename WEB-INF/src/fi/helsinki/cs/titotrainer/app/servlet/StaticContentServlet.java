package fi.helsinki.cs.titotrainer.app.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticContentServlet extends HttpServlet {
    
    private static String BASE_PATH = "/public";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUri = req.getRequestURI();
        if (!requestUriValid(requestUri)) {
            resp.sendError(404);
            return;
        }
        
        String resourcePath = extractResourcePath(requestUri);
        
        InputStream resInput = this.getServletContext().getResourceAsStream(resourcePath);
        if (resInput != null) {
            org.apache.commons.io.IOUtils.copyLarge(resInput, resp.getOutputStream());
        } else {
            resp.sendError(404);
            return;
        }
    }
    
    private boolean requestUriValid(String requestUri) {
        String contextPath = this.getServletContext().getContextPath();
        return requestUri.startsWith(contextPath + BASE_PATH);
    }
    
    private String extractResourcePath(String requestUri) {
        String contextPath = this.getServletContext().getContextPath();
        return requestUri.substring(contextPath.length());
    }
    
    @Override
    protected long getLastModified(HttpServletRequest req) {
        String requestUri = req.getRequestURI();
        if (requestUriValid(requestUri)) {
            String resPath = extractResourcePath(requestUri);
            try {
                URL resUrl = getServletContext().getResource(resPath);
                URLConnection resConn = resUrl.openConnection();
                long lastModified = resConn.getLastModified();
                if (lastModified != 0) {
                    return lastModified;
                }
            } catch (Exception e) {
            }
        }
        return super.getLastModified(req);
    }
}
