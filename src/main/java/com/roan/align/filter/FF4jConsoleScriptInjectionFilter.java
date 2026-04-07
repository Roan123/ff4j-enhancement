package com.roan.align.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Filter to inject external JavaScript files into FF4j console.
 * - ff4j-rbac.js: Role-based access control for the console
 * - ff4j-injection.js: Logout button injection
 */
public class FF4jConsoleScriptInjectionFilter implements Filter {

    private static final String FF4J_CONSOLE_PATH = "/ff4j-web-console/";
    
    /**
     * Script tags to inject - loads external JS files
     */
    private static final String SCRIPTS_TO_INJECT = 
        "<script src='/align/ff4j-rbac.js'></script>\n" +
        "<script src='/align/ff4j-injection.js'></script>\n";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        
        // Only process HTML page requests (not static assets like images, fonts, CSS)
        // Include: /ff4j-web-console/, /ff4j-web-console/features, /ff4j-web-console/index.html, etc.
        // Exclude: /ff4j-web-console/static/*.css, /ff4j-web-console/images/*, /ff4j-web-console/*.js (files with extensions under static/)
        boolean isHtmlConsolePage = false;
        if (uri != null && uri.contains(FF4J_CONSOLE_PATH)) {
            // Get the path after /ff4j-web-console/
            String consolePath = uri.substring(uri.indexOf(FF4J_CONSOLE_PATH) + FF4J_CONSOLE_PATH.length());
            // Process if: empty, ends with /, ends with .html, or has no extension (console pages like features, properties, etc.)
            // Exclude if: contains /static/ or has a known static file extension
            boolean isStaticResource = consolePath.contains("/static/") || 
                consolePath.matches(".*\\.(css|js|png|jpg|jpeg|gif|woff|ttf|svg|ico|map)$");
            if (!isStaticResource) {
                isHtmlConsolePage = true;
            }
        }
        
        if (isHtmlConsolePage) {
            // Wrap response to capture output
            ResponseWrapper wrapper = new ResponseWrapper(httpResponse);
            chain.doFilter(request, wrapper);
            
            String content = wrapper.toString();
            
            // Get user roles from security context
            String rolesJs = "window.ff4jUserRoles = [];";
            try {
                Object userObj = httpRequest.getAttribute("ff4j.user");
                if (userObj != null && userObj instanceof com.roan.align.security.Ff4jSecurityContext.Ff4jUser) {
                    com.roan.align.security.Ff4jSecurityContext.Ff4jUser ff4jUser = 
                        (com.roan.align.security.Ff4jSecurityContext.Ff4jUser) userObj;
                    if (ff4jUser.getRoles() != null && !ff4jUser.getRoles().isEmpty()) {
                        String roles = String.join(",", ff4jUser.getRoles());
                        rolesJs = "window.ff4jUserRoles = '" + roles + "'.split(',');";
                    }
                }
            } catch (Exception e) {
                // Default to empty roles if anything goes wrong
            }
            
            // Inject roles script in <head>
            String rolesScript = "<script>" + rolesJs + "</script>";
            if (content.contains("<head>")) {
                content = content.replace("<head>", "<head>" + rolesScript);
            }
            
            // Inject external JS scripts before </body>
            if (content.contains("</body>")) {
                content = content.replace("</body>", SCRIPTS_TO_INJECT + "</body>");
            }
            
            httpResponse.getWriter().write(content);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

    private static class ResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
        private final CharArrayWriter writer = new CharArrayWriter();

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override
                public void write(int b) { writer.write(b); }
                @Override
                public boolean isReady() { return true; }
                @Override
                public void setWriteListener(WriteListener listener) {}
            };
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(writer);
        }

        @Override
        public String toString() {
            return writer.toString();
        }
    }
}
