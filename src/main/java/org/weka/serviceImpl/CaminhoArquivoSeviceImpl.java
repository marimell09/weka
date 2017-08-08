package org.weka.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weka.model.CaminhoArquivo;
import org.weka.repository.CaminhoArquivoRepository;
import org.weka.service.CaminhoArquivoService;



@Service("caminhoArquivoService")
public class CaminhoArquivoSeviceImpl implements CaminhoArquivoService{
    
    @Autowired
    private CaminhoArquivoRepository caminhoArquivoRepository;


    public CaminhoArquivo findByNomeArquivo(String nomeArquivo) {

        return caminhoArquivoRepository.findByNomeArquivo(nomeArquivo);

    }

    public CaminhoArquivo save(CaminhoArquivo caminhoArquivo) {
        
        return caminhoArquivoRepository.save(caminhoArquivo);
    }

}
