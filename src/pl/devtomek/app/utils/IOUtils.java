package pl.devtomek.app.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystemNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utils methods for IO operations on files.
 *
 * @author DevTomek.pl
 */
public class IOUtils {

    private static final Logger LOG = LogManager.getLogger(IOUtils.class);

    private static final String rootPath = new File(IOUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getPath();

    private IOUtils() {
        // prevents the creation of class instances
    }

    public static String readTextFromResources(String pathToFile, boolean isExternalResources) {
        try {

            InputStream inputStream = isExternalResources ? getExternalResourcesAsStream(pathToFile) : getResourcesAsStream(pathToFile);

            if (inputStream.available() == 0) {
                LOG.error("Not found stream as resources for: {}", pathToFile);
                return StringUtils.EMPTY;
            }

            LOG.debug("Founded resources for: {}", pathToFile);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
                return in.lines().collect(Collectors.joining(System.lineSeparator()));
            }

        } catch (IOException e) {
            LOG.error("Not found file: {}", pathToFile);
        } catch (FileSystemNotFoundException e) {
            LOG.error("FileSystemNotFoundException for: {}", pathToFile);
        }

        return StringUtils.EMPTY;
    }

    public static String readJavaScript(String file, Map<String, String> parameters, boolean isExternalResources) {
        String result = readTextFromResources(file, isExternalResources);

        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            result = result.replaceAll(parameter.getKey(), parameter.getValue());
        }

        return result;
    }

    public static <T> List<T> mapToJson(String pathToFile, Class<T> _class, boolean isExternalResources) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = readTextFromResources(pathToFile, isExternalResources);
            JsonParser jsonParser = factory.createParser(json);
            MappingIterator<T> mappingIterator = mapper.readValues(jsonParser, _class);
            return mappingIterator.readAll();
        } catch (IOException e) {
            LOG.error("Error during parsing [{}] to [{}] \n {}", pathToFile, _class, e.getMessage());
        }

        return Collections.emptyList();
    }

    public static InputStream getExternalResourcesAsStream(String pathToExternalResources) {
        String path = rootPath + pathToExternalResources;

        try {
            File initialFile = new File(path);
            return new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            LOG.error("Not found file [{}]", path);
        }

        return InputStream.nullInputStream();
    }

    public static InputStream getResourcesAsStream(String pathToExternalResources) {
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(pathToExternalResources);
        return resourceAsStream != null ? resourceAsStream : InputStream.nullInputStream();
    }

    public static String getRootPath() {
        return rootPath;
    }
}
