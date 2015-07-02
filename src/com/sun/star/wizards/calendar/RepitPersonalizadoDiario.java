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
public class RepitPersonalizadoDiario extends RepitPersonalizado {

    private boolean soloDiaLaborable;

    public RepitPersonalizadoDiario(boolean soloDiaLaborable, int repitCada, int cantRepeticiones, Calendar repetirHastaDia, int conFechaFinal) {
        super(repitCada, cantRepeticiones, repetirHastaDia, conFechaFinal);
        this.soloDiaLaborable = soloDiaLaborable;
    }

    public boolean isSoloDiaLaborable() {
        return soloDiaLaborable;
    }

    public void setSoloDiaLaborable(boolean soloDiaLaborable) {
        this.soloDiaLaborable = soloDiaLaborable;
    }

    @Override
    public void ReetirFechasPersonalizado(List<Calendar> fechaIni, Calendar fechaFin) {
        int i = 0;
        while (true) {
            Calendar copy = (Calendar) fechaIni.get(i).clone();
          
            if (soloDiaLaborable) {
                  copy.add(Calendar.DAY_OF_MONTH, 1);
                if (copy.get(Calendar.DAY_OF_WEEK) == 7) {
                    copy.add(Calendar.DAY_OF_MONTH, 1);
                }if (copy.get(Calendar.DAY_OF_WEEK) == 1) {
                    copy.add(Calendar.DAY_OF_MONTH, 1);
                }
            } else {
                copy.add(Calendar.DAY_OF_MONTH, repitCada);
            }
            if (copy.compareTo(fechaFin) > 0) {
                return;
            }if (conFechaFinal == 1 && i == cantRepeticiones) {
                return;
            }if (conFechaFinal == 2 && copy.compareTo(repetirHastaDia) > 0) {
                return;
            }
            fechaIni.add( (Calendar)copy.clone());
            i++;
        }
    }
}
