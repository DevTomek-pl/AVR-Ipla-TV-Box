package pl.devtomek.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.utils.constant.Property;

import java.io.IOException;
import java.util.Properties;

/**
 * Utils methods for application and project properties files.
 *
 * @author DevTomek.pl
 */
public class PropertiesUtils {

    private static final Logger LOG = LogManager.getLogger(PropertiesUtils.class);

    private static final String PROJECT_PROPERTIES_FILE_NAME = "project.properties";
    private static final String APPLICATION_PROPERTIES_FILE_NAME = "/application.properties";

    private static final String DEVELOPMENT = "dev";
    private static final String PRODUCTION = "prod";

    private static Properties applicationProperties;
    private static Properties projectProperties;

    static {
        LOG.info("Initialising PropertiesUtils...");

        try {
            LOG.info("Loading application properties files");
            applicationProperties = new java.util.Properties();
            applicationProperties.load(IOUtils.getExternalResourcesAsStream(APPLICATION_PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            LOG.error("Not found application properties file [{}]", e.getMessage());
        }

        try {
            LOG.info("Loading project properties files");
            projectProperties = new java.util.Properties();
            projectProperties.load(IOUtils.getResourcesAsStream(PROJECT_PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            LOG.error("Not found project properties file [{}]", e.getMessage());
        }
    }

    private PropertiesUtils() {
        // prevents the creation of class instances
    }

    public static String getProperty(Property property) {
        String value = applicationProperties.getProperty(property.toString());

        if (value != null) {
            return value;
        }

        value = projectProperties.getProperty(property.toString());

        if (value != null) {
            return value;
        }

        LOG.error("Not found value for property [{}]", property);

        return StringUtils.EMPTY;
    }

    public static String getProperty(Property key, String defaultValue) {
        String property = getProperty(key);
        return (property.isEmpty()) ? defaultValue : property;
    }

    public static Integer getIntegerProperty(Property key) {
        String property = getProperty(key);
        return (property.isEmpty()) ? 0 : Integer.parseInt(property);
    }

    public static Integer getIntegerProperty(Property key, Integer defaultValue) {
        String property = getProperty(key);
        return (property.isEmpty()) ? defaultValue : Integer.valueOf(property);
    }

    public static Double getDoubleProperty(Property key) {
        String property = getProperty(key);
        return (property.isEmpty()) ? 0.0 : Double.parseDouble(property);
    }

    public static Double getDoubleProperty(Property key, Double defaultValue) {
        String property = getProperty(key);
        return (property.isEmpty()) ? defaultValue : Double.valueOf(property);
    }

    public static Boolean getBooleanProperty(Property key, Boolean defaultValue) {
        String property = getProperty(key);
        return (property.isEmpty()) ? defaultValue : Boolean.valueOf(property);
    }

    public static boolean isProductionMode() {
        return PropertiesUtils.getProperty(Property.MODE).equalsIgnoreCase(PRODUCTION);
    }

    public static boolean isDevelopmentMode() {
        return PropertiesUtils.getProperty(Property.MODE).equalsIgnoreCase(DEVELOPMENT);
    }

}
