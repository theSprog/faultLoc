package nju.gist.FileResolver;

import nju.gist.Common.TestCase;

import java.util.HashMap;
import java.util.List;

public class AbstractFileResolver implements FileResolver{
    public List<HashMap<Integer, Integer>> getFaultFeatures(String filePath){
        throw new UnsupportedOperationException();
    }

    public List<TestCase> getFaultCaseList(){
        throw new UnsupportedOperationException();
    }

    public List<TestCase> getHealthCaseList(){
        throw new UnsupportedOperationException();
    }
}
