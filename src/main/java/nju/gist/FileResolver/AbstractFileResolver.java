package nju.gist.FileResolver;

import java.util.HashMap;
import java.util.List;

public class AbstractFileResolver implements FileResolver{
    public List<HashMap<Integer, Integer>> getFaultFeatures(String filePath){
        throw new UnsupportedOperationException();
    }

    public List<List<Integer>> getFaultCaseList(){
        throw new UnsupportedOperationException();
    }

    public List<List<Integer>> getHealthCaseList(){
        throw new UnsupportedOperationException();
    }
}
