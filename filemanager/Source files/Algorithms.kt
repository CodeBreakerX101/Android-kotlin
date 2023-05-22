package com.example.filemanager

import java.io.File

class Algorithms {

    public
    fun QuickSortInDescendingOrder(data : ArrayList<File>,  left : Int, right : Int)
    {
        if (right - left <= 0)
            return;

        val pivot : Long = data[right].length();
        val partition = PartitionFuncInDescendingOrder(data, left, right, pivot);
        QuickSortInDescendingOrder(data, left, partition - 1);
        QuickSortInDescendingOrder(data, partition + 1, right);

    }

    private
    fun PartitionFuncInDescendingOrder(data : ArrayList<File>,  left : Int, right : Int, pivot : Long): Int
    {

        var leftPointer = left - 1;
        var rightPointer = right;

        while (true)
        {
            while (data[++leftPointer].length() > pivot) {}

            while (rightPointer > 0 && data[--rightPointer].length() < pivot) {}

            if (leftPointer >= rightPointer)
                break;
            else
                swap(data, leftPointer, rightPointer);
        }

        swap(data, leftPointer, right);

        return leftPointer;
    }

    private
    fun swap(data : ArrayList<File>, num1 : Int, num2 : Int)
    {
        val temp = data[num1];
        data[num1] = data[num2];
        data[num2] = temp;
    }

}