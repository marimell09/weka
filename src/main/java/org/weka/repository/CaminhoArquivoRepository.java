package org.weka.repository;

import org.springframework.data.repository.CrudRepository;
import org.weka.model.CaminhoArquivo;
import org.weka.model.Modelo;

public interface CaminhoArquivoRepository extends CrudRepository<CaminhoArquivo, Integer>{
    
    public CaminhoArquivo findByNomeArquivo(String nomeArquivo);

}
