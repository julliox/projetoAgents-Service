package br.com.octopus.projectA.Util;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class FormatadorUtil {

    public static String formatarYearMonth(YearMonth yearMonth) {
        // Obter o mês por extenso
        String mesPorExtenso = yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

        // Obter o ano
        int ano = yearMonth.getYear();

        // Retornar a data formatada
        return mesPorExtenso.substring(0, 1).toUpperCase() + mesPorExtenso.substring(1) + " de " + ano;
    }

}
