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
     * Initialize RBAC by hiding write operation elements for non-admin users
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

            // Hide edit and delete icons in feature list
            var actionCells = document.querySelectorAll('table td:last-child, table td:nth-child(7)');
            actionCells.forEach(function(cell) {
                cell.style.display = 'none';
            });
        }
    }

    /**
     * Override original toggle function to enforce RBAC
     */
    function overrideToggleFunction() {
        var originalToggle = window.toggle;
        window.toggle = function(checkbox) {
            if (!hasAdminRole()) {
                alert('Access denied. ADMIN role required to toggle features.');
                // Revert checkbox state
                checkbox.checked = !checkbox.checked;
                return false;
            }
            return originalToggle ? originalToggle(checkbox) : true;
        };
    }

    /**
     * Override original delete function to enforce RBAC
     */
    function overrideDeleteFunction() {
        var originalDelete = window.deleteFeature;
        window.deleteFeature = function(uid) {
            if (!hasAdminRole()) {
                alert('Access denied. ADMIN role required to delete features.');
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

        // Hide write operation elements
        initRBAC();
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
        initRBAC: initRBAC
    };
})();