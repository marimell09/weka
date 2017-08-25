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
import org.weka.model.Algoritmo;
import org.weka.model.CaminhoArquivo;
import org.weka.model.Instancias;
import org.weka.model.Modelo;
import org.weka.service.CaminhoArquivoService;
import org.weka.service.ModeloService;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
@Controller
public class TreinarAvaliarModeloController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private CaminhoArquivoService caminhoArquivoService;
    
    @Autowired
    private ModeloService modeloService;

    /** 
     * Treina e avalia o modelo tendo como parametro arquivo para criação do modelo e arquivo para rodar a predicao.
     * Arquivo de modelo deve conter na ultima coluna o atributo classificador (situacao do curso)
     * Arquivo de predicao deve conter na ultima coluna interrogacoes (situacao do curso a ser prevista)
     * @param caminhoArquivoModeloNome - nome do arquivo de modelo
     * @param caminhoArquivoTesteNome - nome do arquivo que sera realizada a predicao
     * @param algoritmo - algoritmo para criar o modelo (j48 ou naive bayes)
     * @return Output do mensagem de sucesso e tuplas do arquivo com a previsão
     * */
    @RequestMapping(value = "/treinarPredirModelo/{caminhoArquivoModeloNome},{caminhoArquivoTesteNome},{algoritmo}", method = RequestMethod.GET)
    public @ResponseBody String treinarPredirModelo(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome, @PathVariable("algoritmo") String algoritmo) {
        
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        String caminhoArquivoTeste = encontrarNomeCaminho(caminhoArquivoTesteNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Instances instanciaDadosModelo = loadArquivo(caminhoArquivoModelo);
        
        //Pego arquivo para teste de modelo gerado e transformo em instancia
        Instances instanciaDadosTeste = loadArquivo(caminhoArquivoTeste);
        
        //Seto classe (atributo) que será usado para classificar
        Algoritmo arvore = new Algoritmo();
        arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        arvore.setarIndexClasse(instanciaDadosTeste,  instanciaDadosTeste.numAttributes() - 1);
        
        //Balanceamento de dados com SMOTE
        Instancias instancias = new Instancias();
        instanciaDadosModelo = instancias.aplicarSmote(instanciaDadosModelo);
        String balanceamentoAtributos = instancias.getBalanceamentoAtributos(instanciaDadosModelo);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo, algoritmo);
        
        //Faço predição dos dados inseridos baseado no modelo gerado
        Instances dadosClassificados = null;
        try {
            dadosClassificados = arvore.classificarDados(modelo, instanciaDadosTeste);
            System.out.println("passou");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "Dados de previsao \n" + dadosClassificados.toString();
    }
    
    
    /** 
     *  Treina e avalia o modelo tendo como parametro arquivo para criação do modelo e arquivo para rodar o test set
     * Arquivo de modelo e de test set devem conter na ultima coluna o atributo classificador (situacao do curso)
     * Arquivo de predicao deve conter na ultima coluna interrogacoes (situacao do curso a ser prevista)
     * @param caminhoArquivoModeloNome - nome do arquivo de modelo
     * @param caminhoArquivoTesteNome - nome do arquivo de teste
     * @param algoritmo - algoritmo para criar o modelo (j48 ou naive bayes)
     * @return Dados de avaliacao, resultados da avaliacao e matriz de confusao
     * */
    @RequestMapping(value = "/treinarAvaliarModelo/{caminhoArquivoModeloNome},{caminhoArquivoTesteNome},{algoritmo}", method = RequestMethod.GET)
    public @ResponseBody String treinarAvaliarModelo(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome, @PathVariable("algoritmo") String algoritmo) {
        
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        String caminhoArquivoTeste = encontrarNomeCaminho(caminhoArquivoTesteNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Instances instanciaDadosModelo = loadArquivo(caminhoArquivoModelo);
        
        //Pego arquivo para teste de modelo gerado e transformo em instancia
        Instances instanciaDadosTeste = loadArquivo(caminhoArquivoTeste);

        //Seto classe (atributo) que será usado para classificar
        Algoritmo arvore = new Algoritmo();
        arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        arvore.setarIndexClasse(instanciaDadosTeste,  instanciaDadosTeste.numAttributes() - 1);
        
        //Balanceamento de dados com SMOTE
        Instancias instancias = new Instancias();
        instanciaDadosModelo = instancias.aplicarSmote(instanciaDadosModelo);
        String balanceamentoAtributos = instancias.getBalanceamentoAtributos(instanciaDadosModelo);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo, algoritmo);
        
        String resultados = arvore.sumarioResultados(instanciaDadosModelo, instanciaDadosTeste, modelo, false);
        
        String matriz = arvore.matrizConfusao(instanciaDadosModelo, instanciaDadosTeste, modelo);
        
        String modeloString = modelo.toString();
        
        String output = "Dados de avaliacao com validador:" + "\n" + modeloString +"\n" + resultados + "\n" + matriz;
        
        return output;
    }
    
    /** 
     * Treina e avalia o modelo tendo como parametro arquivo para criação do modelo, mas ao invés de colocar um
     * arquivo de test set, roda cross fold para avaliação do modelo construido.
     * Arquivo de modelo deve conter na ultima coluna o atributo classificador (situacao do curso)
     * @param caminhoArquivoModeloNome - nome do arquivo de modelo
     * @param algoritmo - algoritmo para criar o modelo (j48 ou naive bayes)
     * @return Output da decisão de avaliacao do algoritmo, do modelo gerado, da matriz de confusão, dos resultados da avaliação, 
     * da quantidade de atributos para cada valor de classificador (em curso, cancelado) depois do balanceamento do modelo
     * e da porcentagem de balanceamento usada para criação do modelo
     * */
    @RequestMapping(value = "/treinarAvaliarModeloCrossValidation/{caminhoArquivoModeloNome},{algoritmo}", method = RequestMethod.GET)
    public @ResponseBody String treinarAvaliarModeloCrossValidation(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("algoritmo") String algoritmo) {
        
        String output = null;
        try {
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Instances instanciaDadosModelo = loadArquivo(caminhoArquivoModelo);
        
        //Seto classe (atributo) que será usado para classificar
        Algoritmo alg = new Algoritmo();
        instanciaDadosModelo = alg.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        
        //Balanceamento de dados com SMOTE
        Instancias instancias = new Instancias();
        instanciaDadosModelo = instancias.aplicarSmote(instanciaDadosModelo);
        String balanceamentoAtributos = instancias.getBalanceamentoAtributos(instanciaDadosModelo);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = alg.construirModelo(instanciaDadosModelo, algoritmo);
        
        Evaluation crossResult = alg.crossValidator(instanciaDadosModelo, alg.escolherAlgoritmo(algoritmo));
        
        String modeloString = modelo.toString();
        
            output = "Dados de avaliacao com cross validation:" + "\n" + "Modelo: " + "\n" + modeloString + "\n";
            output = output + crossResult.toMatrixString() + "\nResultados: \n" + crossResult.toSummaryString();
            output = output + balanceamentoAtributos + "\n";
            output = output + "Porcentagem de balanceamento: " + instancias.getPorcentagem() + "%\n";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return output;
    }
    
    /** 
     * Treina o modelo tendo como parametro arquivo para criação do modelo, do tipo de algoritmo e o nome do modelo.
     * Arquivo de modelo deve conter na ultima coluna o atributo classificador (situacao do curso).
     * @param caminhoArquivoModeloNome - nome do arquivo de modelo
     * @param algoritmo - algoritmo para criar o modelo (j48 ou naive bayes)
     * @param nomeModelo - nome do modelo a ser gravado no banco para posterior utilizacao
     * @return Output do mensagem de sucesso
     * */
    @RequestMapping(value = "/treinarModelo/{caminhoArquivoModeloNome},{algoritmo},{nomeModelo}", method = RequestMethod.GET)
    public @ResponseBody String treinarModelo(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("algoritmo") String algoritmo, @PathVariable("nomeModelo") String nomeModelo) {
        
        String caminhoArquivoModelo = encontrarNomeCaminho(caminhoArquivoModeloNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Instances instanciaDadosModelo = loadArquivo(caminhoArquivoModelo);
        
        Algoritmo arvore = new Algoritmo();
        
        //Seto classe (atributo) que será usado para classificar
        arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        
        //Balanceamento de dados com SMOTE
        Instancias instancias = new Instancias();
        instanciaDadosModelo = instancias.aplicarSmote(instanciaDadosModelo);
        String balanceamentoAtributos = instancias.getBalanceamentoAtributos(instanciaDadosModelo);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo, algoritmo);
        
        String modeloString = arvore.classificadorParaModeloString(modelo);
        
        salvarModelo(nomeModelo, "teste", "teste", "teste", modeloString);
        
        return "Modelo treinado com sucessos: " + nomeModelo;
    }
    
    
    /** 
     * Realiza previsão do modelo gerado, utilizando como parametro arquivo onde tuplas serão geradas com previsão
     * Arquivo de predicao deve conter na ultima coluna interrogacoes (situacao do curso a ser prevista)
     * @param modeloNome - nome do modelo gravado no banco
     * @param caminhoArquivoTesteNome - nome do arquivo que sera realizada a predicao
     * @return Output do mensagem de sucesso e tuplas do arquivo com a previsão
     * */
    @RequestMapping(value = "/predirModelo/{modeloNome},{caminhoArquivoTesteNome}", method = RequestMethod.GET)
    public @ResponseBody String predirModelo(@PathVariable("modeloNome") String modeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome) {

        String caminhoArquivoTeste = encontrarNomeCaminho(caminhoArquivoTesteNome);
        
        //Pego arquivo para teste de modelo gerado e transformo em instancia
        Instances instanciaDadosTeste = loadArquivo(caminhoArquivoTeste);

        //Seto classe (atributo) que será usado para classificar
        Algoritmo arvore = new Algoritmo();
        arvore.setarIndexClasse(instanciaDadosTeste,  instanciaDadosTeste.numAttributes() - 1);
        
        String modeloString = encontrarStringModelo(modeloNome);
        
        //Pego o modelo do banco
        Classifier modeloClassificador = arvore.modeloStringParaClassificador(modeloString);
        
        //Faço predição dos dados inseridos baseado no modelo gerado
        Instances dadosClassificados = null;
        try {
            dadosClassificados = arvore.classificarDados(modeloClassificador, instanciaDadosTeste);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "Modelo utilizado com sucessos!\nDados de previsao:" + "\n" + dadosClassificados.toString();
    }
    
    /** 
     * Método utilizado para encontrar o nome do caminho do arquivo (path)
     * Servico do Arquivo utilizado
     * @param nomeArquivo - nome do arquivo ao qual o caminho será retornado
     * @return Caso arquivo encontrado, retorna-se o nome do caminho,
     * caso nao encontrado, retorna-se nulo
     * */
    public String encontrarNomeCaminho(String nomeArquivo){
        CaminhoArquivo caminhoArquivo = caminhoArquivoService.findByNomeArquivo(nomeArquivo);
        
        if (caminhoArquivo != null){
            return caminhoArquivo.getNomeCaminho();
        }
        return null;
    }
    
    /** 
     * Método para salvar modelo utilizando servico do modelo
     * Servico do Modelo utilizado
     * @param nomeModelo - nome do modelo a ser gravado
     * @param anoModelo - ano do modelo a ser gravado
     * @param cursoModelo - curso do modelo a ser gravado
     * @param semestreModelo - semestre do modelo a ser gravado
     * @param modelo - modeloString do modelo a ser gravado (string gerada do modelo treinado)
     * */
    public void salvarModelo(String nomeModelo, String anoModelo, String cursoModelo, String semestreModelo, String modeloString){
        
        Modelo salvarModelo = new Modelo();
        salvarModelo.setNomeModelo(nomeModelo);
        salvarModelo.setAnoModelo(anoModelo);
        salvarModelo.setCursoModelo(cursoModelo);
        salvarModelo.setSemestreModelo(semestreModelo);
        salvarModelo.setModeloModeloString(modeloString);
        
        modeloService.save(salvarModelo);
    }
    
    /** 
     * Método para encontrar a string gerada do modelo treinado no banco
     * Servico do Modelo utilizado
     * @param nomeModelo - nome do modelo a ser gravado
     * @return string do modelo treinado
     * */
    public String encontrarStringModelo(String nomeModelo){
        Modelo m = modeloService.findByNomeModelo(nomeModelo);
        return m.getModeloModeloString();
    }
    
    /** 
     * Método para carregar arquivo por tipo. Possibilita carregar arquivos arff e csv e transforma-los em instancias.
     * @param extensao - extensao do arquivo, utilizada para chamar metodos especificos de carregamento
     * @param caminhoArquivo - nome do caminho do arquivo (path)
     * @return instanciaDados - instancia de dados carregada
     * */
    public Instances lerArquivoTipoTransformarEmInstancia(String extensao, String caminhoArquivo){
        Arquivo arq = new Arquivo();
        Instances instanciaDados = null;
        
        if(extensao.equals("arff")){
            instanciaDados = arq.lerArquivoArffTransformarEmInstancias(caminhoArquivo);
        }
        else if(extensao.equals("csv")){
            instanciaDados = arq.lerArquivoCSVTransformarEmInstancias(caminhoArquivo);
        }
       return instanciaDados;
    }

    /** 
     * Método chama carregador de arquivo por tipo depois de descobrir o tipo da extensao do arquivo
     * @param caminhoArquivo - nome do caminho do arquivo (path)
     * @return instanciaDados - instancia de dados carregada
     * */
    public Instances loadArquivo(String caminhoArquivo){
        Arquivo arq = new Arquivo();
        String extensaoArquivo = arq.descubraExtensaoString(caminhoArquivo);
        Instances instanciaDados = lerArquivoTipoTransformarEmInstancia(extensaoArquivo, caminhoArquivo);
        return instanciaDados;
    }
    
}
