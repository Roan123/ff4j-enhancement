package com.roan.align.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Filter to add logout button to FF4j console.
 */
public class FF4jConsoleScriptInjectionFilter implements Filter {

    private static final String FF4J_CONSOLE_PATH = "/ff4j-web-console/";
    private static final String INJECTION_JS = 
        "<script>\n" +
        "  (function() {\n" +
        "    var done = false;\n" +
        "    function inject() {\n" +
        "      if (done || document.getElementById('ff4j-logout-btn')) return;\n" +
        "      var nav = document.querySelector && document.querySelector('.nav-collapse .nav.pull-right');\n" +
        "      if (!nav) { setTimeout(inject, 1000); return; }\n" +
        "      done = true;\n" +
        "      var li = document.createElement('li'); li.id = 'ff4j-logout-btn';\n" +
        "      var a = document.createElement('a'); a.href = '#';\n" +
        "      a.style.cssText = 'cursor:pointer;font-weight:bold;';\n" +
        "      a.innerHTML = '🚪 Logout';\n" +
        "      a.onclick = function(e) {\n" +
        "        e.preventDefault();\n" +
        "        fetch('/align/api/logout', {method: 'POST', credentials: 'same-origin'})\n" +
        "          .then(function() { window.location.href = '/align/login.html'; });\n" +
        "      };\n" +
        "      li.appendChild(a);\n" +
        "      var d = nav.querySelector && nav.querySelector('li.dropdown');\n" +
        "      if (d) nav.insertBefore(li, d); else nav.appendChild(li);\n" +
        "    }\n" +
        "    if (document.readyState === 'loading') {\n" +
        "      document.addEventListener('DOMContentLoaded', inject);\n" +
        "    } else { inject(); }\n" +
        "  })();\n" +
        "</script>";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        
        if (uri != null && uri.contains(FF4J_CONSOLE_PATH)) {
            // Wrap response to capture output
            ResponseWrapper wrapper = new ResponseWrapper(httpResponse);
            chain.doFilter(request, wrapper);
            
            String content = wrapper.toString();
            
            // Inject script before </body>
            if (content.contains("</body>")) {
                content = content.replace("</body>", INJECTION_JS + "\n</body>");
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
