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
public class RepitPersonalizadoSemanal extends RepitPersonalizado {

    private boolean[] selectedDay;

    public RepitPersonalizadoSemanal(boolean[] selectedDay, int repitCada, int cantRepeticiones, Calendar repetirHastaDia, int conFechaFinal) {
        super(repitCada, cantRepeticiones, repetirHastaDia, conFechaFinal);
        this.selectedDay = selectedDay;
    }

    public boolean[] getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(boolean[] selectedDay) {
        this.selectedDay = selectedDay;
    }

    @Override
    public void ReetirFechasPersonalizado(List<Calendar> fechaIni, Calendar fechaFin) {
        int instancias = 0;
        Calendar copy = (Calendar) fechaIni.get(instancias).clone();
        boolean prinera = true;
        copy.add(Calendar.DAY_OF_MONTH, 1);  
        while (true) {
 
            
            int semana = copy.get(Calendar.WEEK_OF_MONTH);
            prinera = false;
          while (semana == copy.get(Calendar.WEEK_OF_MONTH)){
                if (copy.compareTo(fechaFin) > 0) {
                    return;
                }
                if (conFechaFinal == 1 && instancias == cantRepeticiones) {
                    return;
                }
                if (conFechaFinal == 2 && copy.compareTo(repetirHastaDia) > 0) {
                    return;
                }
                boolean esdia = false;
                for (int j = 0; j < selectedDay.length; j++) {
                    if (copy.get(Calendar.DAY_OF_WEEK)-1 == j && selectedDay[j]) {
                        esdia = true;
                        break;
                    }
                }
                if (esdia) {
                    fechaIni.add((Calendar) copy.clone());
                    instancias++;
                }
                copy.add(Calendar.DAY_OF_MONTH, 1);
            }
           
           if(repitCada-1!=0)
            copy.add(Calendar.DAY_OF_MONTH, 7 * repitCada);


        }
    }

    public Calendar FechaParaDia() {

        return null;
    }
}
