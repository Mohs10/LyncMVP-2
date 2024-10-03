package com.example.Lync.ReusablePackage;

import java.util.Comparator;

public class Sorting<T> {

    public void quickSort(T[] arr, Comparator<T> comparator, int low, int high) {
        if (low < high)
        {
            int pi = partition(arr, comparator, low, high);
            quickSort(arr, comparator, low, pi - 1);
            quickSort(arr, comparator, pi + 1, high);
        }
    }

    private int partition(T[] arr, Comparator<T> comparator, int low, int high) {
        T pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(arr[j], pivot) <= 0) {
                i++;
                T temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        T temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }
}

