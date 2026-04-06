package com.roan.align.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Filter to add logout button to FF4j console and enforce role-based access control.
 */
public class FF4jConsoleScriptInjectionFilter implements Filter {

    private static final String FF4J_CONSOLE_PATH = "/ff4j-web-console/";
    private static final String INJECTION_JS = 
        "<script>\n" +
        "  // Role-based access control for FF4J console\n" +
        "  var userRoles = null;\n" +
        "  \n" +
        "  // Try to get roles from security context (injected by server)\n" +
        "  if (window.ff4jUserRoles) {\n" +
        "    userRoles = window.ff4jUserRoles;\n" +
        "  }\n" +
        "  \n" +
        "  function hasAdminRole() {\n" +
        "    return userRoles && userRoles.indexOf('ADMIN') !== -1;\n" +
        "  }\n" +
        "  \n" +
        "  // Override the original toggle function\n" +
        "  var originalToggle = window.toggle;\n" +
        "  window.toggle = function(checkbox) {\n" +
        "    if (!hasAdminRole()) {\n" +
        "      alert('Access denied. ADMIN role required to toggle features.');\n" +
        "      // Revert checkbox state\n" +
        "      checkbox.checked = !checkbox.checked;\n" +
        "      return false;\n" +
        "    }\n" +
        "    return originalToggle ? originalToggle(checkbox) : true;\n" +
        "  };\n" +
        "  \n" +
        "  // Override the original delete function\n" +
        "  var originalDelete = window.deleteFeature;\n" +
        "  window.deleteFeature = function(uid) {\n" +
        "    if (!hasAdminRole()) {\n" +
        "      alert('Access denied. ADMIN role required to delete features.');\n" +
        "      return false;\n" +
        "    }\n" +
        "    return originalDelete ? originalDelete(uid) : true;\n" +
        "  };\n" +
        "  \n" +
        "  // Hide write operation buttons for non-admin users\n" +
        "  function initRBAC() {\n" +
        "    if (!hasAdminRole()) {\n" +
        "      // Hide New, Copy, Rename, Toggle Group buttons\n" +
        "      var navItems = document.querySelectorAll('.subnavbar a');\n" +
        "      navItems.forEach(function(item) {\n" +
        "        var href = item.getAttribute('href') || '';\n" +
        "        if (href.indexOf('modalCreate') !== -1 ||\n" +
        "            href.indexOf('modalCopyFeature') !== -1 ||\n" +
        "            href.indexOf('modalRenameFeature') !== -1 ||\n" +
        "            href.indexOf('modalToggle') !== -1) {\n" +
        "          item.style.display = 'none';\n" +
        "        }\n" +
        "      });\n" +
        "      \n" +
        "      // Hide edit and delete icons in feature list\n" +
        "      var actionCells = document.querySelectorAll('table td:last-child, table td:nth-child(7)');\n" +
        "      actionCells.forEach(function(cell) {\n" +
        "        cell.style.display = 'none';\n" +
        "      });\n" +
        "    }\n" +
        "  }\n" +
        "  \n" +
        "  (function() {\n" +
        "    var done = false;\n" +
        "    function inject() {\n" +
        "      if (done && document.getElementById('ff4j-logout-btn')) return;\n" +
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
        "      \n" +
        "      // Initialize RBAC after DOM is ready\n" +
        "      initRBAC();\n" +
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
            
            // Get user roles from security context
            String rolesJs = "window.ff4jUserRoles = [];";
            try {
                // Try to get from request attribute (set by Ff4jSecurityFilter)
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
                // If anything goes wrong, default to empty roles
            }
            
            // Inject role-based JS
            String rolesScript = "<script>" + rolesJs + "</script>";
            if (content.contains("<head>")) {
                content = content.replace("<head>", "<head>" + rolesScript);
            }
            
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
