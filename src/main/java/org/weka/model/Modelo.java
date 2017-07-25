package org.weka.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "MODELO", catalog = "wekadatabase")
public class Modelo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODELO_ID", unique = true, nullable = false)
	private Integer idModelo;

	@Column(name = "MODELO_NOME")
	private String nomeModelo;
	
	@Column(name = "MODELO_CURSO")
	private String cursoModelo;
	
	@Column(name = "MODELO_ANO")
	private String anoModelo;
	
	@Column(name = "MODELO_SEMESTRE")
	private String semestreModelo;
	
	@Column(name = "MODELO_MODELO_STRING")
	private String modeloModeloString;
	
	

	public Integer getIdModelo() {
		return idModelo;
	}

	public void setIdModelo(Integer idModelo) {
		this.idModelo = idModelo;
	}

	public String getNomeModelo() {
		return nomeModelo;
	}

	public void setNomeModelo(String nomeModelo) {
		this.nomeModelo = nomeModelo;
	}
	
	public String getCursoModelo() {
		return cursoModelo;
	}

	public void setCursoModelo(String cursoModelo) {
		this.cursoModelo = cursoModelo;
	}

	public String getAnoModelo() {
		return anoModelo;
	}

	public void setAnoModelo(String anoModelo) {
		this.anoModelo = anoModelo;
	}
	
	public String getSemestreModelo() {
		return semestreModelo;
	}

	public void setSemestreModelo(String semestreModelo) {
		this.semestreModelo = semestreModelo;
	}
	
	public String getModeloModeloString() {
		return modeloModeloString;
	}

	public void setModeloModeloString(String modeloModeloString) {
		this.modeloModeloString = modeloModeloString;
	}
}