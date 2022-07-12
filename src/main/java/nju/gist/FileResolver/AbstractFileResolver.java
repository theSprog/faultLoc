package nju.gist.FileResolver;

import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class AbstractFileResolver implements FileResolver{
    protected static final Logger logger = Logger.getLogger(AbstractFileResolver.class);

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
