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
import weka.core.Instances;

@Controller
public class TreinarAvaliarModeloController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private CaminhoArquivoService caminhoArquivoService;
    
    @Autowired
    private ModeloService modeloService;

    @RequestMapping(value = "/treinarAvaliarModelo/{caminhoArquivoModeloNome},{caminhoArquivoTesteNome}", method = RequestMethod.GET)
    public @ResponseBody String treinarAvaliarModelo(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome) {
        
        String caminhoArquivoModelo = findPath(caminhoArquivoModeloNome);
        String caminhoArquivoTeste = findPath(caminhoArquivoTesteNome);
        
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
        
        return "Dados de previsão" + "/n" + dadosClassificados.toString();
    }
    
    @RequestMapping(value = "/treinarModelo/{caminhoArquivoModeloNome}", method = RequestMethod.GET)
    public @ResponseBody String treinarModelo(@PathVariable("caminhoArquivoModeloNome") String caminhoArquivoModeloNome) {
        
        String caminhoArquivoModelo = findPath(caminhoArquivoModeloNome);
        
        //Pego arquivo para geração do modelo e transformo na instancia
        Arquivo arq = new Arquivo();
        Instances instanciaDadosModelo = arq.lerArquivoTransformarEmInstancias(caminhoArquivoModelo);

        ArvoreDecisao arvore = new ArvoreDecisao();
        
        //Seto classe (atributo) que será usado para classificar
        arvore.setarIndexClasse(instanciaDadosModelo, instanciaDadosModelo.numAttributes() - 1);
        
        //Com a instancia gerada acima, construo um modelo
        Classifier modelo = arvore.construirModelo(instanciaDadosModelo);
        
        String modeloString = arvore.classificadorParaModeloString(modelo);
        
        salvarModelo("teste1", "teste2", "teste3", "teste4", modeloString);
        
        return "Modelo treinado com sucessos";
    }
    
    @RequestMapping(value = "/previsaoModelo/{modeloNome},{caminhoArquivoTesteNome}", method = RequestMethod.GET)
    public @ResponseBody String previsaoModelo(@PathVariable("modeloNome") String modeloNome, @PathVariable("caminhoArquivoTesteNome") String caminhoArquivoTesteNome) {

        String caminhoArquivoTeste = findPath(caminhoArquivoTesteNome);
        
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
        
        return "Modelo utilizado com sucessos" + "/n" + dadosClassificados.toString();
    }
    
    public String findPath(String name){
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

}
