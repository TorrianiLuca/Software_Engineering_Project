package JSONReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONCardReader {
    // returns a Json Object given an index
    public static JSONObject jsonCardReader(int index) {
        try {
            String content = JSONReaderUtil.readJsonFromResources("json/cards.json");
            JSONArray cardArray = new JSONArray(content);

            if(index >= 0 && index < cardArray.length()) {
                return cardArray.getJSONObject(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
