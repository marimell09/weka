package org.weka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.weka.model.Arquivo;
import org.weka.model.ArvoreDecisao;

import weka.classifiers.Classifier;
import weka.core.Instances;

@Controller
public class TreinarAvaliarModeloController {

    @RequestMapping(value = "/treinarAvaliarModelo", method = RequestMethod.POST)
    public @ResponseBody String uploadFileHandler(@RequestParam("path") String caminhoArquivoModelo, @RequestParam("path") String caminhoArquivoTeste) {
        
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
        
        return "Modelo treinado com sucessos<br />" + dadosClassificados.toString();
    }

}
