package com.example.Lync.ReusablePackage;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class StringAndDateUtils {

    // Method to check if the target string partially matches the start of the source string
    public boolean isPartialMatch(String source, String target) {
        if (source.length() < target.length()) {
            return false;
        }

        for (int i = 0; i < target.length(); i++) {
            if (source.charAt(i) != target.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    // Method to convert and format date strings from 'yyyy-MM-dd' to 'dd-MMM-yyyy'
    public String convertAndFormatDate(String dateString) {
        LocalDate localDate = LocalDate.parse(dateString);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return localDate.format(formatter);
    }
}

