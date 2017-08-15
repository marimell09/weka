package org.weka.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.weka.model.Arquivo;
import org.weka.model.ArvoreDecisao;
import org.weka.model.CaminhoArquivo;
import org.weka.model.Modelo;
import org.weka.service.CaminhoArquivoService;
import org.weka.service.ModeloService;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.classifiers.Evaluation;
@Controller
public class TreinarAvaliarModeloController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private CaminhoArquivoService caminhoArquivoService;
    
    @Autowired
    private ModeloService modeloService;

    /** 
     * Treina e avalia o modelo tendo como parametro arquivo para criação do modelo e arquivo para rodar o test set
     * */
    @RequestMapping(value = "/treinarAvaliarModelo/{caminhoArquivoModeloNome},{caminhoArquivoTesteNome}", method = RequestMethod.GET)
    public @ResponseBody String treinarAvaliarModelo(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome) {
        
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        String caminhoArquivoTeste = encontrarNomeCaminho(caminhoArquivoTesteNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Arquivo arq = new Arquivo();
        Instances instanciaDadosModelo = arq.lerArquivoTransformarEmInstancias(caminhoArquivoModelo);
        //Pego arquivo para teste de modelo gerado e transformo em instancia
        Instances instanciaDadosTeste = arq.lerArquivoTransformarEmInstancias(caminhoArquivoTeste);

        //Seto classe (atributo) que será usado para classificar
        ArvoreDecisao arvore = new ArvoreDecisao();
        arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        arvore.setarIndexClasse(instanciaDadosTeste,  instanciaDadosTeste.numAttributes() - 1);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo);
        
        //Faço predição dos dados inseridos baseado no modelo gerado
        Instances dadosClassificados = arvore.classificarDados(modelo, instanciaDadosTeste);
        
        return "Dados de previsão" + "\n" + dadosClassificados.toString();
    }
    
    /** 
     * Treina e avalia o modelo tendo como parametro arquivo para criação do modelo e arquivo para rodar o test set
     * Faz output do modelo gerado, da matriz de confusão e do resultado da avaliação
     * @param caminhoArquivoModeloNome - caminho do arquivo de modelo
     * @param caminhoArquivoTesteNome - caminho do arquivo de validação
     * @param tipoArquivo - tipo do arquivo (csv, arff)
     * */
    @RequestMapping(value = "/avaliarModeloMatriz/{caminhoArquivoModeloNome},{caminhoArquivoTesteNome}", method = RequestMethod.GET)
    public @ResponseBody String avaliarModeloMatriz(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome) {
        
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        String caminhoArquivoTeste = encontrarNomeCaminho(caminhoArquivoTesteNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Instances instanciaDadosModelo = loadArquivo(caminhoArquivoModelo);
        
        //Pego arquivo para teste de modelo gerado e transformo em instancia
        Instances instanciaDadosTeste = loadArquivo(caminhoArquivoTeste);

        //Seto classe (atributo) que será usado para classificar
        ArvoreDecisao arvore = new ArvoreDecisao();
        arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        arvore.setarIndexClasse(instanciaDadosTeste,  instanciaDadosTeste.numAttributes() - 1);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo);
        
        String resultados = arvore.sumarioResultados(instanciaDadosModelo, instanciaDadosTeste, modelo, false);
        
        String matriz = arvore.matrizConfusao(instanciaDadosModelo, instanciaDadosTeste, modelo);
        
        String modeloString = modelo.toString();
        
        String output = "Dados de avaliação com validador:" + "\n" + modeloString +"\n" + resultados + "\n" + matriz;
        
        return output;
    }
    
    /** 
     * Treina e avalia o modelo tendo como parametro arquivo para criação do modelo, mas ao invés de colocar um
     * arquivo de test set, roda cross fold para avaliação do modelo construindo.
     * Output do modelo gerado, da matriz de confusão e dos resultados da avaliação
     * @param caminhoArquivoModeloNome - caminho do arquivo de modelo
     * @param tipoArquivo - tipo do arquivo (csv, arff)
     * */
    @RequestMapping(value = "/avaliarModeloMatrizCrossValidation/{caminhoArquivoModeloNome},{tipoArquivo}", method = RequestMethod.GET)
    public @ResponseBody String avaliarModeloMatrizCrossValidation(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("tipoArquivo") String tipoArquivo) {
        
        String output = null;
        try {
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Arquivo arq = new Arquivo();
        Instances instanciaDadosModelo = defineTipoArquivo(tipoArquivo, caminhoArquivoModelo);
        
        //Seto classe (atributo) que será usado para classificar
        ArvoreDecisao arvore = new ArvoreDecisao();
        instanciaDadosModelo = arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo);
        
        Evaluation crossResult = arvore.crossValidator(instanciaDadosModelo, new J48());
        
        String modeloString = modelo.toString();
        
            output = "Dados de avaliação com cross validation:" + "\n" + "Modelo: " + "\n" + modeloString + "\n" + crossResult.toMatrixString() + "\nResultados: \n" + crossResult.toSummaryString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return output;
    }
    
    /** 
     * Treina o modelo tendo como parametro arquivo para criação do modelo
     * Output do mensagem de sucesso
     * @param caminhoArquivoModeloNome - caminho do arquivo de modelo
     * */
    @RequestMapping(value = "/treinarModelo/{caminhoArquivoModeloNome}", method = RequestMethod.GET)
    public @ResponseBody String treinarModelo(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome) {
        
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Arquivo arq = new Arquivo();
        Instances instanciaDadosModelo = arq.lerArquivoTransformarEmInstancias(caminhoArquivoModelo);

        ArvoreDecisao arvore = new ArvoreDecisao();
        
        //Seto classe (atributo) que será usado para classificar
        arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo);
        
        String modeloString = arvore.classificadorParaModeloString(modelo);
        
        salvarModelo("teste7", "teste2", "teste3", "teste4", modeloString);
        
        return "Modelo treinado com sucessos";
    }
    
    /** 
     * Realiza previsão do modelo gerado, utilizando como parametro arquivo onde tuplas serão geradas com previsão
     * Output do mensagem de sucesso e tuplas do arquivo com a previsão
     * @param modeloNome - nome do modelo gravado no banco
     * @param caminhoArquivoTesteNome - caminho do arquivo de validação
     * */
    @RequestMapping(value = "/previsaoModelo/{modeloNome},{caminhoArquivoTesteNome}", method = RequestMethod.GET)
    public @ResponseBody String previsaoModelo(@PathVariable("modeloNome") String modeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome) {

        String caminhoArquivoTeste = encontrarNomeCaminho(caminhoArquivoTesteNome);
        
        //Pego arquivo para teste de modelo gerado e transformo em instancia
        Arquivo arq = new Arquivo();
        Instances instanciaDadosTeste = arq.lerArquivoTransformarEmInstancias(caminhoArquivoTeste);

        //Seto classe (atributo) que será usado para classificar
        ArvoreDecisao arvore = new ArvoreDecisao();
        arvore.setarIndexClasse(instanciaDadosTeste,  instanciaDadosTeste.numAttributes() - 1);
        
        String modeloString = encontrarStringModelo(modeloNome);
        
        //Pego o modelo do banco
        Classifier modeloClassificador = arvore.modeloStringParaClassificador(modeloString);
        
        //Faço predição dos dados inseridos baseado no modelo gerado
        Instances dadosClassificados = arvore.classificarDados(modeloClassificador, instanciaDadosTeste);
        
        return "Modelo utilizado com sucessos" + "\n" + dadosClassificados.toString();
    }
    
    public String encontrarNomeCaminho(String name){
        CaminhoArquivo caminhoArquivo = caminhoArquivoService.findByNomeArquivo(name);
        
        if (caminhoArquivo != null){
            return caminhoArquivo.getNomeCaminho();
        }
        return null;
    }
    
    public void salvarModelo(String nomeModelo, String anoModelo, String cursoModelo, String semestreModelo, String modeloString){
        
        Modelo salvarModelo = new Modelo();
        salvarModelo.setNomeModelo(nomeModelo);
        salvarModelo.setAnoModelo(anoModelo);
        salvarModelo.setCursoModelo(cursoModelo);
        salvarModelo.setSemestreModelo(semestreModelo);
        salvarModelo.setModeloModeloString(modeloString);
        
        modeloService.save(salvarModelo);
    }
    
    public String encontrarStringModelo(String nomeModelo){
        Modelo m = modeloService.findByNomeModelo(nomeModelo);
        return m.getModeloModeloString();
    }
    
    public Instances defineTipoArquivo(String tipoArquivo, String caminhoArquivo){
        Arquivo arq = new Arquivo();
        Instances instanciaDados = null;
        
        if(tipoArquivo.equals("arff")){
            instanciaDados = arq.lerArquivoTransformarEmInstancias(caminhoArquivo);
        }
        else if(tipoArquivo.equals("csv")){
            instanciaDados = arq.lerArquivoCSVTransformarEmInstancias(caminhoArquivo);
        }
       return instanciaDados;
    }
    
    public Instances tipoArquivo(String extensao, String caminhoArquivo){
        Arquivo arq = new Arquivo();
        Instances instanciaDados = null;
        
        if(extensao.equals("arff")){
            instanciaDados = arq.lerArquivoTransformarEmInstancias(caminhoArquivo);
        }
        else if(extensao.equals("csv")){
            instanciaDados = arq.lerArquivoCSVTransformarEmInstancias(caminhoArquivo);
        }
       return instanciaDados;
    }

    public Instances loadArquivo(String caminhoArquivo){
        Arquivo arq = new Arquivo();
        String extensaoArquivo = arq.descubraExtensaoString(caminhoArquivo);
        Instances instanciaDados = tipoArquivo(extensaoArquivo, caminhoArquivo);
        return instanciaDados;
    }

}
