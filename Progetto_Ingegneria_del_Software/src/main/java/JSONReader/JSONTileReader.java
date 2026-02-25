package JSONReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONTileReader implements JSONReader {
    // returns a Json Object given an index
    public static JSONObject JSONTileReader(int index) {
        try {
            String content = JSONReaderUtil.readJsonFromResources("json/tiles.json");
            JSONArray componentTileArray = new JSONArray(content);

            if(index >= 0 && index < componentTileArray.length()) {
                return componentTileArray.getJSONObject(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}