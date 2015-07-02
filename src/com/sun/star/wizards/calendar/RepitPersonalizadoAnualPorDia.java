/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;
import java.util.List;



/**
 *
 * @author adriam
 */
public class RepitPersonalizadoAnualPorDia extends RepitPersonalizado{
   int repitElDia;
  int mounth;

    public RepitPersonalizadoAnualPorDia(int repitElDia, int mounth, int repitCada, int cantRepeticiones, Calendar repetirHastaDia, int conFechaFinal) {
        super(repitCada, cantRepeticiones, repetirHastaDia, conFechaFinal);
        this.repitElDia = repitElDia;
        this.mounth = mounth;
    }

    public int getRepitElDia() {
        return repitElDia;
    }

    public void setRepitElDia(int repitElDia) {
        this.repitElDia = repitElDia;
    }

    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    }

    @Override
    public void ReetirFechasPersonalizado(List<Calendar> fechaIni, Calendar fechaFin) {
          int instancias = 0;
        Calendar copy = (Calendar) fechaIni.get(instancias).clone();
        boolean prinera = true;
        copy.add(Calendar.DAY_OF_MONTH, 1);  
        while (true) {
      copy.set(copy.get(Calendar.YEAR), mounth, repitElDia);
                if (copy.compareTo(fechaFin) > 0) {
                    return;
                }
                if (conFechaFinal == 1 && instancias == cantRepeticiones) {
                    return;
                }
                if (conFechaFinal == 2 && copy.compareTo(repetirHastaDia) > 0) {
                    return;
                }
         if (copy.compareTo(fechaIni.get(instancias))>0) {
                     fechaIni.add((Calendar) copy.clone());
                    instancias++;
                }  
          copy.add(Calendar.YEAR, repitCada);
        }
    }
  
}
