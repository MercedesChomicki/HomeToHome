package com.hometohome.pet_service.util;

import java.time.LocalDate;
import java.time.Period;

public class DateUtils {

    public static String formatAge(LocalDate birthDate) {
        if (birthDate == null) {
            return "Fecha desconocida";
        }

        Period period = Period.between(birthDate, LocalDate.now());

        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        StringBuilder ageString = new StringBuilder();

        if (years > 0) {
            ageString.append(years).append(years == 1 ? " año" : " años");
        }

        if (months > 0) {
            if (!ageString.isEmpty()) {
                ageString.append(" y ");
            }
            ageString.append(months).append(months == 1 ? " mes" : " meses");
        }

        if (days > 0 && years == 0) { // Solo mostramos días si tiene menos de un año
            if (!ageString.isEmpty()) {
                ageString.append(" y ");
            }
            ageString.append(days).append(days == 1 ? " día" : " días");
        }

        return ageString.toString();
    }
}