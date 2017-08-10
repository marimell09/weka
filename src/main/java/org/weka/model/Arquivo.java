package org.weka.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class Arquivo {

    public Instances lerArquivoTransformarEmInstancias(String caminho) {
        Instances instanciaDados = null;

        try {
            BufferedReader readerValidator = new BufferedReader(new FileReader(caminho));
            instanciaDados = new Instances(readerValidator);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instanciaDados;
    }
    
    public Instances lerArquivoCSVTransformarEmInstancias(String caminho){
        Instances instanciaDados = null;
        try {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(caminho));
            instanciaDados = loader.getDataSet();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return instanciaDados;
    }

    public void escreveNoArquivo(String caminho, Instances instanciaDados) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(caminho));
            writer.write(instanciaDados.toString());
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
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
