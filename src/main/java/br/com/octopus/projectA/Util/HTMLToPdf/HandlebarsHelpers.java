package br.com.octopus.projectA.Util.HTMLToPdf;

import java.time.LocalDateTime;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandlebarsHelpers {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public HandlebarsHelpers() {
    }

    public CharSequence formatarDataHora(LocalDateTime date) {
        return this.formatter.format(date);
    }

    public CharSequence getAno(LocalDate date) {
        return String.valueOf(date.getYear());
    }

    public CharSequence formatarDinheiro(Double value) {
        return value == null ? "" : NumberFormat.getCurrencyInstance().format(value);
    }

    public boolean itensIguais(Object obj1, Object obj2) {
        List<Object> collect = (List)Stream.of(obj1, obj2).filter(Objects::nonNull).collect(Collectors.toList());
        boolean todosOsItensSaoIguais = collect.stream().allMatch((o) -> {
            return o.equals(obj1);
        });
        return collect.isEmpty() || collect.size() != Integer.parseInt("1") && todosOsItensSaoIguais;
    }
    
}
