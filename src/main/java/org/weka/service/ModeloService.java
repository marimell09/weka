package org.weka.service;

import org.weka.model.Modelo;

public interface ModeloService {

    Modelo save(Modelo modelo);

    Modelo findByNomeModelo(String nomeModelo);

}
