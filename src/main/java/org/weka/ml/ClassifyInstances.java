package org.weka.ml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.weka.model.Modelo;
import org.weka.service.ModeloService;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.DatabaseSaver;

public class ClassifyInstances extends Application {

    // private static final long serialVersionUID = 3487495895819393L;

    private static final String DATA_SET = "/trainingFile.arff";

    private J48 tree;
    private Instances data;
    private Instances validator;
    private Instances classify;

    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        loadData();
        tree = new J48();
        tree.buildClassifier(data);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ModeloService modelo = (ModeloService) context.getBean(ModeloService.class);

        Classifier cls = new J48();
        cls.buildClassifier(data);

        System.out.println("Modelo em forma de string");
        String s = cls.toString();
        System.out.println(cls.toString());

        // Evaluating
        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(cls, validator);
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));

        System.out.println("Matrix de confusão");
        System.out.println(eval.toMatrixString());

        System.out.println("Previsão das classes");
        System.out.println("value" + " -> " + "prediction");
        // Predicting
        for (int i = 0; i < classify.numInstances(); i++) {
            double clsLabel = cls.classifyInstance(classify.instance(i));
            classify.instance(i).setClassValue(clsLabel);
            System.out.println(classify.instance(i).value(0) + " -> " + classify.classAttribute().value((int) clsLabel));
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter("C:/workspace_TG/tg_dataProject_Git/wekaProject/weka/src/main/resources/classifyFinished.arff"));

        // ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ObjectOutputStream oos = new ObjectOutputStream(baos);
        // oos.writeObject(cls);

        // classifier -> string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializationHelper.write(baos, cls);
        String str = baos.toString("ISO-8859-1");

        System.out.println(baos.toString("ISO-8859-1"));

        Modelo m = new Modelo();
        m.setIdModelo(1);
        m.setAnoModelo("2017");
        m.setCursoModelo("Banco de Dados");
        m.setSemestreModelo("1 sem");
        m.setNomeModelo("Teste6");
        m.setModeloModeloString(str);

        modelo.save(m);

        Modelo m2 = modelo.findByNomeModelo("Teste2");
        String modelinho = m2.getModeloModeloString();
        System.out.println(modelinho);

        // string -> classifier
        ByteArrayInputStream bais = new ByteArrayInputStream(modelinho.getBytes("ISO-8859-1"));
        Classifier cls2 = (Classifier) SerializationHelper.read(bais);

        System.out.println("Testando classifier");
        System.out.println(cls2.toString());

        System.out.println("Escrevendo num arquivo arff a predição");
        writer.write(classify.toString());
        writer.newLine();
        writer.flush();
        writer.close();
        context.close();

        // saveDbModel(cls);

        // Criacao da arvore na tela
        /*
         * Label lblDecisionTreeTitle = new
         * Label("Decision Tree generated for the Iris dataset:"); Text txtTree
         * = new Text(tree.toString()); String graph = tree.graph(); SwingNode
         * sw = new SwingNode(); SwingUtilities.invokeLater(() -> {
         * TreeVisualizer treeVisualizer = new TreeVisualizer(null, graph, new
         * PlaceNode2()); treeVisualizer.setPreferredSize(new Dimension(300,
         * 300)); sw.setContent(treeVisualizer); });
         * 
         * 
         * StackPane spTree = new StackPane(sw); spTree.setPrefWidth(400);
         * spTree.setPrefHeight(350); spTree.cacheShapeProperty();
         * 
         * VBox vbDecisionTree = new VBox(5, lblDecisionTreeTitle, new
         * Separator(), spTree);
         * 
         * 
         * vbDecisionTree.setPrefHeight(500); vbDecisionTree.setPrefWidth(1000);
         * 
         * GridPane gpRoot = new GridPane(); gpRoot.add(vbDecisionTree, 0,0);
         * 
         * stage.setScene(new Scene(gpRoot));
         * stage.setTitle("Ã�ris dataset clustering and visualization");
         * stage.show();
         */
    }

    private void saveDbModel(Classifier cls) throws Exception {
        DatabaseSaver save = new DatabaseSaver();
        save.setUrl("jdbc:mysql://localhost:3306/weeka_test");
        save.setUser("root");
        save.setPassword("system");

        DatabaseLoader db = new DatabaseLoader();
        weka.core.SerializationHelper.write("C:/workspace_TG/tg_dataProject_Git/wekaProject/weka/src/main/resources/classify.model", cls);
        save.setInstances(classify);
        save.setRelationForTableName(false);
        save.setTableName("results");
        save.connectToDatabase();
        save.writeBatch();

    }

    private void loadData() {
        BufferedReader datafile;
        try {
            InputStream dataSetIs = getClass().getResource(DATA_SET).openStream();
            datafile = new BufferedReader(new InputStreamReader(dataSetIs));
            data = new Instances(datafile);
            data.setClassIndex(data.numAttributes() - 1);

            BufferedReader readerValidator = new BufferedReader(new FileReader("C:/workspace_TG/tg_dataProject_Git/wekaProject/weka/src/main/resources/validator.arff"));

            validator = new Instances(readerValidator);
            validator.setClassIndex(validator.numAttributes() - 1);

            BufferedReader readerClassify = new BufferedReader(new FileReader("C:/workspace_TG/tg_dataProject_Git/wekaProject/weka/src/main/resources/classify.arff"));

            classify = new Instances(readerClassify);
            classify.setClassIndex(classify.numAttributes() - 1);
        } catch (Exception e) {
            System.out.println("Exception loading data... Leaving");
            e.printStackTrace();
            System.exit(0);
        }
    }

}