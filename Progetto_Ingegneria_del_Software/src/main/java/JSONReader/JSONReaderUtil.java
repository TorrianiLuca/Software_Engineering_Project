package JSONReader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class JSONReaderUtil {

    public static String readJsonFromResources(String resourcePath) {
        // Use ClassLoader to access the file in the classpath
        InputStream inputStream = JSONReaderUtil.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new RuntimeException("File non trovato: " + resourcePath);
        }

        // Converts the stream in a string
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
        String content = scanner.useDelimiter("\\A").next();
        scanner.close();

        return content;
    }
}