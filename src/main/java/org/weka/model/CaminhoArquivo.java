package org.weka.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "CAMINHO_ARQUIVO", catalog = "wekadatabase")
public class CaminhoArquivo implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAMINHO_ID", unique = true, nullable = false)
    private Integer idCaminho;

    @Column(name = "CAMINHO_NOME")
    private String nomeCaminho;
    
    @Column(name = "ARQUIVO_NOME")
    private String nomeArquivo;

    public Integer getIdModelo() {
        return idCaminho;
    }

    public void setIdModelo(Integer idModelo) {
        this.idCaminho = idModelo;
    }

    public String getNomeCaminho() {
        return nomeCaminho;
    }

    public void setNomeCaminho(String nomeCaminho) {
        this.nomeCaminho = nomeCaminho;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

}
