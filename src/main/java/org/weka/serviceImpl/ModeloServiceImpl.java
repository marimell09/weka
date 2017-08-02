package org.weka.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weka.model.Modelo;
import org.weka.repository.ModeloRepository;
import org.weka.service.ModeloService;

@Service("modeloService")
public class ModeloServiceImpl implements ModeloService {

    @Autowired
    private ModeloRepository modeloRepository;

    public Modelo save(Modelo modelo) {

        Modelo modeloSearched = modeloRepository.findByNomeModelo(modelo.getNomeModelo());
        if (modeloSearched == null) {
            modeloSearched = modeloRepository.save(modelo);
        }
        return modeloSearched;
    }

    public Modelo findByNomeModelo(String nomeModelo) {

        return modeloRepository.findByNomeModelo(nomeModelo);

    }

}
