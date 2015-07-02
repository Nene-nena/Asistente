/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author adriam
 */
public class RepitPersonalizadoMensual extends RepitPersonalizado {

    NumDiaDeSemana numDiaDeSemana;
    String diaDeSemana;

    public RepitPersonalizadoMensual(NumDiaDeSemana numDiaDeSemana, String diaDeSemana, int repitCada, int cantRepeticiones, Calendar repetirHastaDia, int conFechaFinal) {
        super(repitCada, cantRepeticiones, repetirHastaDia, conFechaFinal);
        this.numDiaDeSemana = numDiaDeSemana;
        this.diaDeSemana = diaDeSemana;
    }

    public NumDiaDeSemana getNumDiaDeSemana() {
        return numDiaDeSemana;
    }

    public void setNumDiaDeSemana(NumDiaDeSemana numDiaDeSemana) {
        this.numDiaDeSemana = numDiaDeSemana;
    }

    public String getDiaDeSemana() {
        return diaDeSemana;
    }

    public void setDiaDeSemana(String diaDeSemana) {
        this.diaDeSemana = diaDeSemana;
    }

    @Override
    public void ReetirFechasPersonalizado(List<Calendar> fechaIni, Calendar fechaFin) {
        int instancias = 0;
        Calendar copy = (Calendar) fechaIni.get(instancias).clone();
       copy.add(Calendar.DAY_OF_MONTH, 1);
        RepitPersonalizadoAnualPorNumDia re = null;
       if(this instanceof RepitPersonalizadoAnualPorNumDia){
         re = (RepitPersonalizadoAnualPorNumDia) this;
        copy.set(Calendar.MONTH,re.getMounth());
       } 
       while (true) {
            if (numDiaDeSemana.equals(NumDiaDeSemana.Todos)) {
            copy = (Calendar) XdiaDeSemYdeMesZ(1, copy).clone();
            int month= copy.get(Calendar.MONTH);
            while(month== copy.get(Calendar.MONTH)){ 
            if (copy.compareTo(fechaFin) > 0) {
                    return;
                }
                if (conFechaFinal == 1 && instancias == cantRepeticiones) {
                    return;
                }
                if (conFechaFinal == 2 && copy.compareTo(repetirHastaDia) > 0) {
                    return;
                }
                if (copy.compareTo(fechaIni.get(instancias)) > 0) {
                    fechaIni.add((Calendar) copy.clone());
                    instancias++;
                }
            copy.add(Calendar.DAY_OF_MONTH,7);
            }
             if(this instanceof RepitPersonalizadoAnualPorNumDia){
                 copy.add(Calendar.YEAR, repitCada); 
                 copy.add(Calendar.MONTH,-1); 
             }else
             copy.add(Calendar.MONTH, repitCada-1);
            }
            else{  int posdia=0;
                if(numDiaDeSemana.equals(NumDiaDeSemana.Último)) {
                    posdia= CantDiasDeSemEspTieneMes(copy);
                }else{
                  posdia=numDiaDeSemana.ordinal()  ;
                }
               
               copy = (Calendar) XdiaDeSemYdeMesZ(posdia, copy).clone();
                   if (copy.compareTo(fechaFin) > 0) {
                    return;
                }
                if (conFechaFinal == 1 && instancias == cantRepeticiones) {
                    return;
                }
                if (conFechaFinal == 2 && copy.compareTo(repetirHastaDia) > 0) {
                    return;
                }
                if (copy.compareTo(fechaIni.get(instancias)) > 0) {
                    fechaIni.add((Calendar) copy.clone());
                    instancias++;
                }
            if(this instanceof RepitPersonalizadoAnualPorNumDia){
                 copy.add(Calendar.YEAR, repitCada); 
             }else
                copy.add(Calendar.MONTH, repitCada);
            }
           
        }

    }

    public Calendar XdiaDeSemYdeMesZ(int xDia, Calendar cal) {
        Calendar calen = (Calendar) cal.clone();
        if (calen.get(Calendar.DAY_OF_MONTH) > 1) {
            calen.add(Calendar.DAY_OF_MONTH, -(calen.get(Calendar.DAY_OF_MONTH) - 1));
        }
        int cont = 0;
        while (true) {
            if (calen.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                    .equals(diaDeSemana)) {
                if (++cont == xDia) {
                    return calen;
                }
            }
            calen.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    private int CantDiasDeSemEspTieneMes(Calendar copy1) {
        int cant = 0;
        Calendar copy2 = (Calendar) copy1.clone();
        copy2.add(Calendar.DAY_OF_MONTH, -(copy2.get(Calendar.DAY_OF_MONTH) - 1));
        int month = copy2.get(Calendar.MONTH);
        while (month == copy2.get(Calendar.MONTH)) {
            if (copy2.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                    .equals(diaDeSemana)) {
                cant++;
            }
            copy2.add(Calendar.DAY_OF_WEEK, 1);
        }
        return cant;
    }
}
