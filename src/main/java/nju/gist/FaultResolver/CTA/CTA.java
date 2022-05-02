package nju.gist.FaultResolver.CTA;

import nju.gist.FaultResolver.AbstractFaultResolver;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CTA {
    private Instances data;

    public CTA(String filePath) {
        NumericToNominal filter = new NumericToNominal();
        CSVLoader loader = new CSVLoader();
        try {
            loader.setSource(new File(filePath));
            data = loader.getDataSet();
            data.setClassIndex(0);
            filter.setInputFormat(data);
            data = Filter.useFilter(data, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String constructClassifier() throws Exception {
        J48 classifier = new J48();
        String[] options = new String[3];
        options[0] = "-U";
        options[1] = "-M";
        options[2] = "1";

        classifier.setOptions(options);
        classifier.setConfidenceFactor((float) 0.25);
        classifier.buildClassifier(data);
        return classifier.toString();
    }

    private int depth(String str) {
        int depth = 0;
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == '|')
                depth++;
        return depth;
    }

    public List<List<Integer>> parseTree(String tree) {
        if(tree == null)
            throw new NullPointerException("parseTree got a null pointer !");

        ArrayList<List<Integer>> res = new ArrayList<>();
        int[] fault = new int[data.numAttributes()-1];
        String[] strs = tree.split("\n");
        for (String line : strs) {
            if (line.contains("=")){
                if (line.contains(":")) {   // leaf
                    if (!line.contains("pass")){    // not pass
                        int depth = depth(line);
                        int faultFactor = Integer.parseInt(line.split(":")[0].split("=")[1].strip());
                        inject(fault, depth, faultFactor);
                        List<Integer> faultList = Arrays.stream(fault).boxed().collect(Collectors.toList());
                        res.add(faultList);
                    }

                }else { // not leaf
                    int depth = depth(line);
                    int faultFactor = Integer.parseInt(line.split(" = ")[1].strip());
                    inject(fault, depth, faultFactor);
                }
            }
        }
        return res;
    }

    private void inject(int[] fault, int depth, int faultFactor) {
        fault[depth] = faultFactor;
        for (int i = depth + 1; i < fault.length; i++) {
            fault[i] = 0;
        }
    }
}
