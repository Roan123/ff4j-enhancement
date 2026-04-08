-- FF4J core tables (required)
CREATE TABLE IF NOT EXISTS FF4J_FEATURES (
    FEAT_UID VARCHAR(100) PRIMARY KEY,
    ENABLE INTEGER NOT NULL,
    DESCRIPTION VARCHAR(1000),
    STRATEGY VARCHAR(1000),
    EXPRESSION VARCHAR(255),
    GROUPNAME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS FF4J_ROLES (
    FEAT_UID VARCHAR(100) REFERENCES FF4J_FEATURES(FEAT_UID),
    ROLE_NAME VARCHAR(100) NOT NULL,
    PRIMARY KEY (FEAT_UID, ROLE_NAME)
);

-- Custom properties table for features
CREATE TABLE IF NOT EXISTS FF4J_CUSTOM_PROPERTIES (
    PROPERTY_ID VARCHAR(100) NOT NULL,
    CLAZZ VARCHAR(255) NOT NULL,
    CURRENTVALUE VARCHAR(255),
    FIXEDVALUES VARCHAR(1000),
    DESCRIPTION VARCHAR(1000),
    FEAT_UID VARCHAR(100) REFERENCES FF4J_FEATURES(FEAT_UID),
    PRIMARY KEY (PROPERTY_ID, FEAT_UID)
);

-- User roles table (custom)
CREATE TABLE IF NOT EXISTS FF4J_USER_ROLES (
    USERNAME VARCHAR(100) NOT NULL,
    ROLE_NAME VARCHAR(100) NOT NULL,
    PRIMARY KEY (USERNAME, ROLE_NAME)
);

-- =============================================
-- Insert test data for WrapperController testing
-- =============================================

-- Feature 1: Enabled for all (no country/channel restrictions)
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-all-users', 1, 'Enabled for all users', 'test');

-- Feature 2: Enabled only for US
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-us-only', 1, 'Enabled only for US country', 'test');

-- Feature 3: Enabled only for China
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-china-only', 1, 'Enabled only for China country', 'test');

-- Feature 4: Enabled only for web channel
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-web-only', 1, 'Enabled only for web channel', 'test');

-- Feature 5: Enabled only for mobile channel
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-mobile-only', 1, 'Enabled only for mobile channel', 'test');

-- Feature 6: Enabled for US AND web
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-us-web', 1, 'Enabled for US and web', 'test');

-- Feature 7: Enabled for China AND mobile
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-china-mobile', 1, 'Enabled for China and mobile', 'test');

-- Feature 8: Disabled feature (should remain disabled)
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-disabled', 0, 'This feature is disabled', 'test');

-- Feature 9: Multi-country (US, China, Japan)
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-multi-country', 1, 'Enabled for US, China, Japan', 'test');

-- Feature 10: Multi-channel (web, mobile, api)
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-multi-channel', 1, 'Enabled for web, mobile, api', 'test');

-- Feature 11: Multi-country AND Multi-channel
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-multi-both', 1, 'Enabled for US/China + web/mobile', 'test');

-- Feature 12: Case insensitive test (lowercase)
INSERT INTO FF4J_FEATURES (FEAT_UID, ENABLE, DESCRIPTION, GROUPNAME) VALUES 
('feature-case-insensitive', 1, 'Test case insensitive matching', 'test');

-- =============================================
-- Custom properties for features
-- =============================================

-- feature-us-only: country = US
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('country', 'org.ff4j.property.PropertyString', 'US', 'feature-us-only');

-- feature-china-only: country = China
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('country', 'org.ff4j.property.PropertyString', 'China', 'feature-china-only');

-- feature-web-only: channel = web
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('channel', 'org.ff4j.property.PropertyString', 'web', 'feature-web-only');

-- feature-mobile-only: channel = mobile
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('channel', 'org.ff4j.property.PropertyString', 'mobile', 'feature-mobile-only');

-- feature-us-web: country = US, channel = web
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('country', 'org.ff4j.property.PropertyString', 'US', 'feature-us-web');
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('channel', 'org.ff4j.property.PropertyString', 'web', 'feature-us-web');

-- feature-china-mobile: country = China, channel = mobile
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('country', 'org.ff4j.property.PropertyString', 'China', 'feature-china-mobile');
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('channel', 'org.ff4j.property.PropertyString', 'mobile', 'feature-china-mobile');

-- feature-multi-country: multi-country (comma-separated)
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('country', 'org.ff4j.property.PropertyString', 'US,China,Japan', 'feature-multi-country');

-- feature-multi-channel: multi-channel (comma-separated)
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('channel', 'org.ff4j.property.PropertyString', 'web,mobile,api', 'feature-multi-channel');

-- feature-multi-both: multi-country AND multi-channel
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('country', 'org.ff4j.property.PropertyString', 'US,China', 'feature-multi-both');
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('channel', 'org.ff4j.property.PropertyString', 'web,mobile', 'feature-multi-both');

-- feature-case-insensitive: lowercase value
INSERT INTO FF4J_CUSTOM_PROPERTIES (PROPERTY_ID, CLAZZ, CURRENTVALUE, FEAT_UID) VALUES 
('country', 'org.ff4j.property.PropertyString', 'us', 'feature-case-insensitive');

-- User roles
INSERT INTO FF4J_USER_ROLES (USERNAME, ROLE_NAME) VALUES 
('admin', 'ADMIN'),
('admin', 'READONLY'),
('user1', 'READONLY'),
('user1', 'USER');
