package org.weka.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import weka.core.Instances;

public class Arquivo {
	
	
	public Instances lerArquivoTransformarEmInstancias(String caminho){
		Instances instanciaDados = null;
		
		try {
			BufferedReader readerValidator = new BufferedReader(
					new FileReader(caminho));
			instanciaDados = new Instances(readerValidator);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instanciaDados;
	}
	
	public void escreveNoArquivo(String caminho, Instances instanciaDados){
		 try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(caminho));
			writer.write(instanciaDados.toString());
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
