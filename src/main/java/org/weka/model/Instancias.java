package org.weka.model;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class Instancias {
    
    private int porcentagem = 0;
    
    public Instances aplicarSmote(Instances disbalancedData){
        try {
            porcentagem = calcularPorcentagemSmote(disbalancedData);
            System.out.println(porcentagem);
            if (porcentagem > 0){
                SMOTE filters =new SMOTE();
                filters.setInputFormat(disbalancedData); // Instances instances;
                filters.setPercentage(porcentagem);
                Instances subSamplingInstances = Filter.useFilter(disbalancedData, filters);
                return subSamplingInstances;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disbalancedData;
    }
    
    public int calcularPorcentagemSmote(Instances instanciaDadosModelo){
        int finalAttributeValue[] = instanciaDadosModelo.attributeStats(instanciaDadosModelo.numAttributes() - 1).nominalCounts;
        
        int firstNumber = finalAttributeValue[0];
        int secondNumber = finalAttributeValue[1];
        //retorna 0 caso os valores sejam iguais
        porcentagem = 0;
        
        if (firstNumber < secondNumber){
            porcentagem = porcentagemEspecifica(firstNumber, secondNumber);
        }
        else if (firstNumber > secondNumber){
            porcentagem = porcentagemEspecifica(secondNumber, firstNumber);
        }
        
        return porcentagem;
        
    }
     //regra de 3
    public int porcentagemEspecifica(int menor, int maior){
        int diferenca = maior - menor;
        int resultado = ((diferenca * 100)/menor);
        return resultado;
    }
    
    public String getBalanceamentoAtributos(Instances instanciaDadosModelo){
        int ultimoAtributo = instanciaDadosModelo.numAttributes() - 1;
        int finalAttributeValue[] = instanciaDadosModelo.attributeStats(ultimoAtributo).nominalCounts;
        String nomePrmAtributo = instanciaDadosModelo.attribute(ultimoAtributo).value(0);
        String nomeSegAtributo = instanciaDadosModelo.attribute(ultimoAtributo).value(1);
        String atributos = "\nAtributos balanceados: \n" + nomePrmAtributo + " : " + finalAttributeValue[0] + "\n" +  nomeSegAtributo + " : " +finalAttributeValue[1];
        return atributos;
    }

    public int getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(int porcentagem) {
        this.porcentagem = porcentagem;
    }

}
