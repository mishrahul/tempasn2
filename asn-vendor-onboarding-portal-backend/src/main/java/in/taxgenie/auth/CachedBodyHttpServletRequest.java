package in.taxgenie.auth;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * HTTP Servlet Request wrapper that caches the request body
 * Allows multiple reads of the request body for logging and processing
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    
    private final byte[] cachedBody;

    /**
     * Constructor that caches the request body
     * @param request Original HTTP servlet request
     * @throws IOException if error reading request body
     */
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        cachedBody = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }
            
            @Override
            public boolean isReady() {
                return true;
            }
            
            @Override
            public void setReadListener(ReadListener readListener) {}
            
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * Gets the cached request body as string
     * @return Request body as string
     */
    public String getCachedBodyAsString() {
        return new String(cachedBody, StandardCharsets.UTF_8);
    }
}
