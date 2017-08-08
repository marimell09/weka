package org.weka.service;

import org.weka.model.CaminhoArquivo;

public interface CaminhoArquivoService {
    
    CaminhoArquivo save(CaminhoArquivo caminhoArquivo);

    CaminhoArquivo findByNomeArquivo(String nomeArquivo);

}
