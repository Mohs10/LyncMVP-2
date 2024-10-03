package com.example.Lync.ReusablePackage;

import java.util.Comparator;

public class BinarySearch<T> {

    public int binarySearch(T[] arr, T target, Comparator<T> comparator) {
        int low = 0, high = arr.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (comparator.compare(arr[mid], target) == 0) {
                return mid;
            } else if (comparator.compare(arr[mid], target) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;  // Target not found
    }
}
