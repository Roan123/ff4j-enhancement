/**
 * FF4J Console Script Injection
 * Injects logout button into FF4J web console navigation
 */

(function() {
    'use strict';

    // Updated selector - the actual HTML uses .nav.pull-right directly
    var INJECTION_SELECTOR = '.nav.pull-right';
    var BUTTON_ID = 'ff4j-logout-btn';
    var CONSOLE_PATH = '/ff4j-web-console/';

    /**
     * Inject logout button into FF4J console navigation
     */
    function injectLogoutButton() {
        // Check if already injected
        if (document.getElementById(BUTTON_ID)) {
            return;
        }

        var nav = document.querySelector(INJECTION_SELECTOR);
        if (!nav) {
            // Retry later if navigation not ready
            setTimeout(injectLogoutButton, 1000);
            return;
        }

        // Create logout button
        var li = document.createElement('li');
        li.id = BUTTON_ID;

        var a = document.createElement('a');
        a.href = '#';
        a.style.cssText = 'cursor:pointer;font-weight:bold;';
        a.innerHTML = '🚪 Logout';
        a.onclick = function(e) {
            e.preventDefault();
            fetch('/align/api/logout', {
                method: 'POST',
                credentials: 'same-origin'
            }).then(function() {
                window.location.href = '/align/login.html';
            }).catch(function(err) {
                console.error('Logout failed:', err);
                // Fallback: redirect anyway
                window.location.href = '/align/login.html';
            });
        };

        li.appendChild(a);

        // Insert before dropdown menu if exists
        var dropdown = nav.querySelector('li.dropdown');
        if (dropdown) {
            nav.insertBefore(li, dropdown);
        } else {
            nav.appendChild(li);
        }
    }

    /**
     * Initialize injection
     */
    function init() {
        // Only inject on FF4J console pages (path starts with /align/ff4j-web-console/)
        if (!window.location.pathname.startsWith('/align/ff4j-web-console/')) {
            return;
        }

        injectLogoutButton();
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // Expose for debugging
    window.FF4jInjection = {
        inject: injectLogoutButton
    };
})();