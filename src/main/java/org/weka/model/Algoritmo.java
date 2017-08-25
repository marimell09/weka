package org.weka.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.netlib.arpack.Smout;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class Algoritmo {

    public Classifier construirModelo(Instances data, String algoritmo) {

        Classifier cls = null;
        try {
            cls = escolherAlgoritmo(algoritmo);
            cls.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cls;
    }

    // classifier -> string
    public String classificadorParaModeloString(Classifier cls) {
        String str = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SerializationHelper.write(baos, cls);
            str = baos.toString("ISO-8859-1");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    // string -> classifier
    public Classifier modeloStringParaClassificador(String modelo) {
        ByteArrayInputStream bais;
        Classifier cls = null;
        try {
            bais = new ByteArrayInputStream(modelo.getBytes("ISO-8859-1"));
            cls = (Classifier) SerializationHelper.read(bais);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cls;
    }

    public String matrizConfusao(Instances dataModelo, Instances dataTestSet, Classifier cls) {
        String matriz = null;
        try {
            Evaluation eval = new Evaluation(dataModelo);
            eval.evaluateModel(cls, dataTestSet);
            matriz = eval.toMatrixString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return matriz;
    }
    
    public Evaluation crossValidator(Instances dataModelo, Classifier cls) {
        String crossResult = null;
        Evaluation eval = null;
        try {
            eval = new Evaluation(dataModelo);
            eval.crossValidateModel(cls, dataModelo, 10, new Random(10));
            crossResult = "Estimated Accuracy: "+Double.toString(eval.pctCorrect()) + "\n" + eval.toMatrixString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return eval;
    }

    public String sumarioResultados(Instances dataModelo, Instances dataTestSet, Classifier cls, boolean complexidade) {
        Evaluation eval;
        String resultados = null;
        try {
            eval = new Evaluation(dataModelo);
            eval.evaluateModel(cls, dataTestSet);
            resultados = eval.toSummaryString("\nResultados\n======\n", complexidade);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultados;
    }

    public Instances classificarDados(Classifier modeloGerado, Instances dadosParaClassificar) throws Exception {

        // System.out.println("value" + " -> " + "prediction");
            for (int i = 0; i < dadosParaClassificar.numInstances(); i++) {
                double clsLabel = modeloGerado.classifyInstance(dadosParaClassificar.instance(i));
                dadosParaClassificar.instance(i).setClassValue(clsLabel);

                // System.out.println(dadosParaClassificar.instance(i).value(0)
                // + " -> " + dadosParaClassificar.classAttribute().value((int)
                // clsLabel));
            }
        return dadosParaClassificar;
    }

    public Instances setarIndexClasse(Instances dados, int numeroColuna) {
        // numero da coluna iniciando em 0
        dados.setClassIndex(numeroColuna);
        return dados;
    }
    
    public Classifier escolherAlgoritmo(String algoritmo){
        Classifier classificador = null;
        if (algoritmo.equals("j48")){
            classificador = new J48();
        }else if (algoritmo.equals("naive")){
            classificador = new NaiveBayes();
        }
        return classificador;
    }
    
}
