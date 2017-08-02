package org.weka.repository;

import org.weka.model.Modelo;
import org.springframework.data.repository.CrudRepository;

public interface ModeloRepository extends CrudRepository<Modelo, Integer> {

    public Modelo findByNomeModelo(String nomeModelo);

}
