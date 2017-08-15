package org.weka.ml;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class Arquivo {
    
    public void transformaCSVParaArff(String caminho){
        try {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(caminho));
            Instances instanciaDados = loader.getDataSet();
            
            ArffSaver saver = new ArffSaver();
            saver = new ArffSaver();
            saver.setInstances(instanciaDados);// set the dataset we want to convert
            String analise_arff = caminho+".arff";
            saver.setFile(new File(analise_arff));
            saver.writeBatch();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}