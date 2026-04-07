/**
 * FF4J Console Role-Based Access Control (RBAC)
 * Handles client-side permission enforcement for FF4J web console
 */

(function() {
    'use strict';

    // Global user roles (injected by server)
    var userRoles = null;

    /**
     * Check if current user has ADMIN role
     */
    function hasAdminRole() {
        return userRoles && userRoles.indexOf('ADMIN') !== -1;
    }

    /**
     * Initialize RBAC by disabling write operation elements for non-admin users
     */
    function initRBAC() {
        if (!hasAdminRole()) {
            // Hide New, Copy, Rename, Toggle Group buttons in navigation
            var navItems = document.querySelectorAll('.subnavbar a');
            navItems.forEach(function(item) {
                var href = item.getAttribute('href') || '';
                if (href.indexOf('modalCreate') !== -1 ||
                    href.indexOf('modalCopyFeature') !== -1 ||
                    href.indexOf('modalRenameFeature') !== -1 ||
                    href.indexOf('modalToggle') !== -1) {
                    item.style.display = 'none';
                }
            });

            disableRbacElements();
        }
    }

    /**
     * Disable all RBAC-protected elements
     * Called both on init and when table is updated (for dynamic content)
     */
    function disableRbacElements() {
        if (hasAdminRole()) return;

        // Disable toggle checkboxes properly
        var toggleCheckboxes = document.querySelectorAll('table tbody input[type="checkbox"]');
        toggleCheckboxes.forEach(function(cb) {
            cb.disabled = true;
        });

        // Disable edit buttons/links (column 7)
        var editLinks = document.querySelectorAll('table td:nth-child(7) a, table td:nth-child(7) button');
        editLinks.forEach(function(link) {
            link.setAttribute('disabled', 'disabled');
            link.style.pointerEvents = 'none';
            link.style.opacity = '0.5';
        });

        // Disable delete buttons/links (column 8)
        var deleteLinks = document.querySelectorAll('table td:nth-child(8) a, table td:nth-child(8) button');
        deleteLinks.forEach(function(link) {
            link.setAttribute('disabled', 'disabled');
            link.style.pointerEvents = 'none';
            link.style.opacity = '0.5';
        });
    }

    /**
     * Watch for dynamic DOM changes and re-apply RBAC when needed
     */
    function observeDomChanges() {
        var observer = new MutationObserver(function(mutations) {
            disableRbacElements();
        });
        
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    }

    /**
     * Lightweight fallback - prevent any toggle attempt that bypasses disabled attribute
     * (defense in depth - shouldn't be needed with properly disabled checkboxes)
     */
    function overrideToggleFunction() {
        var originalToggle = window.toggle;
        window.toggle = function(checkbox) {
            if (!hasAdminRole()) {
                // Silently revert - no alert needed, checkbox is already visually disabled
                checkbox.checked = !checkbox.checked;
                return false;
            }
            return originalToggle ? originalToggle(checkbox) : true;
        };
    }

    /**
     * Lightweight fallback - prevent any delete attempt that bypasses disabled buttons
     */
    function overrideDeleteFunction() {
        var originalDelete = window.deleteFeature;
        window.deleteFeature = function(uid) {
            if (!hasAdminRole()) {
                // Silently ignore - button should already be visually disabled
                return false;
            }
            return originalDelete ? originalDelete(uid) : true;
        };
    }

    /**
     * Initialize RBAC when DOM is ready
     */
    function init() {
        // Get user roles from global variable (injected by server)
        if (window.ff4jUserRoles) {
            userRoles = window.ff4jUserRoles;
        }

        // Override console functions for RBAC
        overrideToggleFunction();
        overrideDeleteFunction();

        // Apply RBAC restrictions
        initRBAC();

        // Watch for dynamic DOM changes (e.g., pagination, sorting)
        observeDomChanges();
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // Expose for debugging
    window.FF4jRbac = {
        hasAdminRole: hasAdminRole,
        initRBAC: initRBAC,
        disableRbacElements: disableRbacElements
    };
})();