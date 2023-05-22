package com.example.filemanager

import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import java.io.File

fun SortByName(files : ArrayList<File>, files_size : ArrayList<Int>)
{

}
fun SortBySize(files : ArrayList<File>, files_size : ArrayList<Int>)
{

}
fun SortByDate(files : ArrayList<File>, files_size : ArrayList<Int>)
{

}

fun merging(files : ArrayList<File>, files_size : ArrayList<Int>, files_size_holder : ArrayList<Int>, low : Int, mid : Int, high : Int)
{
    var l1 : Int = low; var l2 = mid + 1; var i = low;

    while ( l1 <= mid && l2 <= high)
    {
        if (files_size[l1] >= files_size[l2])
            files_size_holder[i] = files_size[l1++];
        else
            files_size_holder[i] = files_size[l2++];

        i++;
    }
    
    while (l1 <= mid)
        files_size_holder[i++] = files_size[l1++];

    while (l2 <= high)
        files_size_holder[i++] = files_size[l2++];

    for (i in low..high)
        files_size[i] = files_size_holder[i];

}

fun sort( files : ArrayList<File>, files_size : ArrayList<Int>, files_size_holder : ArrayList<Int>, low : Int, high : Int)
{

    var mid : Int = 0;

    if (low < high)
    {
        mid = (low + high) / 2;


        sort(files, files_size, files_size_holder, low, mid);

        sort(files, files_size, files_size_holder, mid + 1, high);

        merging(files, files_size, files_size_holder, low, mid, high);

    }
    else
    {
        //std::cout << "returning\n";
        return;
    }

}
fun SortBy_PopUpMenu(context : Activity) {

    val sortBy = context.findViewById<ImageButton>(R.id.sort)

    sortBy.setOnClickListener {

        val popUpMenu: PopupMenu = PopupMenu(context, sortBy)

        popUpMenu.menuInflater.inflate(R.menu.pop_up_menu, popUpMenu.menu)

        popUpMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.name -> {
                    Toast.makeText(context, "Sorting by name", Toast.LENGTH_SHORT).show()
                }
                R.id.size -> {

                }
                R.id.date -> {

                }
            }
            true
        })


    }
}
    fun popUpMenu(context : Activity, option : ImageButton)
    {
        val popupMenu: PopupMenu = PopupMenu(context.applicationContext, option)

        popupMenu.menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)
        val ToastByName : Toast =  Toast.makeText(context.applicationContext,"Sorting By Name",Toast.LENGTH_SHORT)
        val ToastBySize : Toast = Toast.makeText(context.applicationContext, "Sorting By Size", Toast.LENGTH_SHORT)
        val ToastByDate : Toast = Toast.makeText(context.applicationContext, "Sorting By Date", Toast.LENGTH_SHORT)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener
        { item ->
            when (item.itemId) {

                R.id.name -> {
                    ToastBySize.cancel()
                    ToastByDate.cancel()
                    ToastByName.show()
                }
                R.id.size -> {
                    ToastByName.cancel()
                    ToastByDate.cancel()
                    ToastBySize.show()
                }
                R.id.date -> {
                    ToastByName.cancel()
                    ToastBySize.cancel()
                    ToastByDate.show()

                }

            }
            true
        }
        )
        popupMenu.show()
    }
