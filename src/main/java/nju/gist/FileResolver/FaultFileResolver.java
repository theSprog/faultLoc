package nju.gist.FileResolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class FaultFileResolver extends AbstractFileResolver{
    private final List<HashMap<Integer, Integer>> faultMaps;
    private static final String ITEM_SEPARATOR = ",";
    private static final String KV_SEPARATOR = ":";

    public FaultFileResolver(){
        faultMaps = new LinkedList<>();
    }

    public List<HashMap<Integer, Integer>> getFaultFeatures(String filePath) {
        File faultFile = new File(filePath);
        String line;

        // The FileReader is not buffered and cannot read by line,
        // so it needs to be wrapped in a buffered class
        try (BufferedReader reader = new BufferedReader(new FileReader(faultFile))) {
            while ((line = reader.readLine()) != null) {
                if (line.equals(BLANK_LINE) || line.matches(COMMENTS_LINE)) continue;

                // i.e. "1:2, 3:2, 5:3" -> {"1:2", "3:2", "5:3"}
                String[] faultKeyAndValArr = line.split(ITEM_SEPARATOR);
                HashMap<Integer, Integer> faultMap = new HashMap<>();

                for (String faultKeyAndValStr : faultKeyAndValArr) {
                    // i.e. "1: 2" -> {"1", "2"}
                    String[] faultKeyAndVal = faultKeyAndValStr.split(KV_SEPARATOR);
                    Integer key = Integer.parseInt(faultKeyAndVal[0].strip());
                    Integer val = Integer.parseInt(faultKeyAndVal[1].strip());
                    faultMap.put(key, val);
                }
                faultMaps.add(faultMap);
            }

        } catch (IOException io) {
            io.printStackTrace();
        }

        return faultMaps;
    }
}
