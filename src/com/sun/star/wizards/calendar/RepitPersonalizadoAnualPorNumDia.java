/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;

/**
 *
 * @author adriam
 */
public class RepitPersonalizadoAnualPorNumDia extends RepitPersonalizadoMensual{
  private  int mounth; 

    public RepitPersonalizadoAnualPorNumDia(int mounth, NumDiaDeSemana numDiaDeSemana, String diaDeSemana, int repitCada, int cantRepeticiones, Calendar repetirHastaDia, int conFechaFinal) {
        super(numDiaDeSemana, diaDeSemana, repitCada, cantRepeticiones, repetirHastaDia, conFechaFinal);
        this.mounth = mounth;
    }

    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    } 
}
