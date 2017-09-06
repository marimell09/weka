package org.weka.model;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class Instancias {
    
    private int porcentagem = 0;
    
    /** 
     * Método aplicarSmote chama os métodos necessários para calcular a porcentagem 
     * necessária a ser aplicada pelo filtro SMOTE, e em seguida aplica o filtro
     * SMOTE na instância dos dados.
     * @param disbalancedData - instância de dados desbalanceada
     * @return retorna a instância de dados balanceada, caso haja porcentagem a ser aplicada,
     * ou retorna dados sem filtro caso não haja porcentagem a ser aplicada
     * */
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
    
    /** 
     * Método calcularPorcentagemSmote reconhece os valores da último atributo do arquivo (em curso, evadido)
     * e chama o método porcentagemEspecifica, passando o menor valor primeiro como parametro.
     * @param instanciaDadosModelo - instancia de dados
     * @return porcentagem a ser aplicada no filtro SMOTE
     * */
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
    
    /** 
     * Método porcentagemEspecifica realiza a regra de três para calcular a porcentagem
     * exata para realização do balanceamento entre os valores do atributo
     * @param menor - numero de instancias do menor atributo (ex: evadido - 4)
     * @param maior - numero de instancias do maior atributo (ex: em curso - 26)
     * @return resultado da porcentagem a ser aplicada no filtro SMOTE
     * */
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
