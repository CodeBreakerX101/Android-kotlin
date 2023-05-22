package com.example.filemanager

import android.app.Activity
import android.app.AlertDialog
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.DuplicateItemsRecyclerView.Companion.selectedDestinationCheckBox
import com.example.filemanager.DuplicateItemsRecyclerView.Companion.selectedSourceCheckBox
import com.example.filemanager.MainActivity.Companion.saveProcessedFilesSize
import com.example.filemanager.SetFilesFoldersInLayout.Companion.childDirectory

import com.example.filemanager.SetFilesFoldersInLayout.Companion.selectedDuplicateFiles
import java.io.File


    fun set_options_for_selected_item(context : Activity,showPasteBtnOnly: Boolean = false)
    {
        val optionsForSelectedItem = LayoutInflater.from(context).inflate(R.layout.options_for_selected_item,null)
        val options_constraint_as_parent  : ConstraintLayout = optionsForSelectedItem.findViewById(R.id.options_constraint_as_parent)

        val options_linearLayoutImg : LinearLayout = optionsForSelectedItem.findViewById(R.id.options_linearLayoutImg)
        val options_deleteImg : ImageView = optionsForSelectedItem.findViewById(R.id.delete_img)
        val options_linearLayoutText : LinearLayout = optionsForSelectedItem.findViewById(R.id.options_linearLayoutText)

        val main_constraint_as_parent : ConstraintLayout = context.findViewById(R.id.main_constraintView)


        options_constraint_as_parent.removeView(options_linearLayoutImg) //starting with options keyword means the view is from options_for_selected_item.xml file
        options_constraint_as_parent.removeView(options_linearLayoutText)

        main_constraint_as_parent.addView(options_linearLayoutImg) //starting with main keyword means the view is from main_activity.xml file
        main_constraint_as_parent.addView(options_linearLayoutText)



        if(showPasteBtnOnly) {
            context.findViewById<ImageView>(R.id.delete_img).visibility = View.INVISIBLE
            context.findViewById<TextView>(R.id.delete_textView).visibility = View.INVISIBLE
            context.findViewById<ImageView>(R.id.cut_img).visibility = View.INVISIBLE
            context.findViewById<TextView>(R.id.cut_textView).visibility = View.INVISIBLE
            context.findViewById<ImageView>(R.id.copy_img).visibility = View.INVISIBLE
            context.findViewById<TextView>(R.id.copy_textView).visibility = View.INVISIBLE
            context.findViewById<ImageView>(R.id.options_img).visibility = View.INVISIBLE
            context.findViewById<TextView>(R.id.options_textView).visibility = View.INVISIBLE

            context.findViewById<ImageView>(R.id.paste_img).visibility = View.VISIBLE
            context.findViewById<TextView>(R.id.paste_textView).visibility = View.VISIBLE
        }
        //main_constraint_as_parent.addView(options_deleteImg)
        /*main_constraint_as_parent.findViewById<ImageView>(R.id.delete_img).setOnClickListener {
            //onClickDeleteButton(context, selectedFiles)
            Log.i("deleteImg","working")
        }*/

    }
    fun hide_options_for_selected_item(context: Activity)
    {
        try {

            val main_linearLayoutImg: LinearLayout =
                context.findViewById(R.id.options_linearLayoutImg)
            val main_linearLayoutText: LinearLayout =
                context.findViewById(R.id.options_linearLayoutText)

            val main_constraint_as_parent: ConstraintLayout =
                context.findViewById(R.id.main_constraintView)



            main_constraint_as_parent.removeView(main_linearLayoutImg)
            main_constraint_as_parent.removeView(main_linearLayoutText)


        }catch (e : java.lang.Exception){}

    }
    fun ShowPasteBtnOnly(context: Activity)
    {

    }

    fun onClickDeleteButton(context: Activity, selectedFiles: ArrayList<File>,allFiles : ArrayList<File>, selectedCheckBox : Array<Boolean>) {

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete")
        if(selectedFiles.size == 1 )
        {
            if(selectedFiles[0].isFile)
                alertDialog.setMessage("Do you want to delete this file")
            else
                alertDialog.setMessage("Do you want to delete this folder")
        }
        else
        {
            var foldersCount = 0
            var selectedFilesCount = 0
            for(i in selectedFiles)
            {
                if(i.isDirectory)
                    foldersCount+=1
                else
                    selectedFilesCount+=1
            }
            if(foldersCount > 1 && selectedFilesCount > 1)
                alertDialog.setMessage("Do you want to delete $foldersCount folders & $selectedFilesCount selectedFiles?")
            else if(foldersCount == 1 && selectedFilesCount == 1)
                alertDialog.setMessage("Do you want to delete $foldersCount folders & $selectedFilesCount selectedFiles?")
            else if(foldersCount == 0 && selectedFilesCount > 1)
                alertDialog.setMessage("Do you want to delete $selectedFilesCount selectedFiles?")
            else if(foldersCount > 1 && selectedFilesCount == 0)
                alertDialog.setMessage("Do you want to delete $foldersCount folders ?")

        }
        alertDialog.setPositiveButton("Ok") { dialog, which ->
            val parentDirectory = allFiles[0].parentFile

            for (i in selectedFiles) {
                var isDeleted = false
                Toast.makeText(context,i.name,Toast.LENGTH_SHORT).show()
                if(i.isDirectory)
                    i.deleteRecursively()
                else {

                    isDeleted = i.delete()
                }
                if(!isDeleted)
                {
                    Log.i("failed to delete",i.name)
                }

                //no need to do selectedFiles.removeAll(selectedFiles) in here already doing in onBackPressed, which is being called at the bottom side this function

                val deletedFilePos = allFiles.indexOf(i)
                saveProcessedFilesSize[saveProcessedFilesSize.lastIndex].removeAt(deletedFilePos)
                allFiles.removeAt(deletedFilePos)
            }

            var folderSize = ""

            folderSize = SortAndHideFolders().convertToSpecificStorageUnit(SortAndHideFolders().folderSize(parentDirectory!!))
            if(parentDirectory != Environment.getExternalStorageDirectory())
            {
                if (saveProcessedFilesSize.size > 1)
                    saveProcessedFilesSize[saveProcessedFilesSize.size - 2][childDirectory[childDirectory.lastIndex]] =
                        folderSize
                else
                    saveProcessedFilesSize[saveProcessedFilesSize.lastIndex][childDirectory[childDirectory.lastIndex]] =
                        folderSize
                childDirectory.remove(childDirectory.lastIndex)
            }

            selectedFiles.removeAll(SetFilesFoldersInLayout.selectedFiles)
            for(i in 0..selectedCheckBox.lastIndex)
            {
                selectedCheckBox[i] = false
            }

            context.onBackPressed()
        }
        alertDialog.setNegativeButton("No") { dialog, which ->

        }
        alertDialog.create().show()

    }
    fun onClickCopyButton(context: Activity, selectedFiles: ArrayList<File>,allFiles : ArrayList<File>) {


    }

    fun onClickCutButton(context: Activity, selectedFiles: ArrayList<Int>,allFiles : ArrayList<File>) {

    }

    fun onClickPasteButton(context: Activity, allFiles: ArrayList<File>, pasteDirectory : File) {
        /*if(selectedFilesToCopy.isNotEmpty())
        {
            for(i in selectedFilesToCopy)
            {
                if(allFiles.contains(i))
                {

                }

                //if(allFiles.contains(i))
                //i.copyTo(dir)
                val newFile = File(pasteDirectory,i.name)
                newFile.createNewFile()

                //i.copyTo(newFile)
            }
        }*/
    }
    fun onClickOptionButton(context : Activity, file : File)
    {
        val options : ImageView = context.findViewById(R.id.options_img)
        val popUpMenu = PopupMenu(context,options)

        popUpMenu.menuInflater.inflate(R.menu.pop_up_menu,popUpMenu.menu)

        popUpMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            item ->

            when(item.itemId) {

                R.id.share-> {

                }
                R.id.compress->{

                }
                R.id.copy_also->{

                }
                R.id.cut_also->{

                }
            }
            return@OnMenuItemClickListener true

        })
        popUpMenu.show()

    }
    fun duplicateItemsFoundPopUpMenu(context: Activity,files : ArrayList<File>)
    {
        val popUpWindow = LayoutInflater.from(context).inflate(R.layout.duplicate_item_present_popupmenu,null)

        
        ////// recyclerView Adapter

        val recyclerView : RecyclerView = popUpWindow.findViewById(R.id.showDuplicateFiles_recyclerView)
                
        val recyclerViewAdapter = DuplicateItemsRecyclerView(context,files)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewAdapter

        /// recyclerView Adapter End
        
        //Set Duplicate files Custom Pop up Window in activity_main
        layoutManager.onLayoutCompleted(LayoutCompletedState(context,files,popUpWindow))

        ///////Pop Window End
        
        
    }
    class LayoutCompletedState(context: Activity, files: ArrayList<File>, popUpWindow: View) : RecyclerView.State(){

        init {
            val mainConstraintAsParent: ConstraintLayout =
                context.findViewById(R.id.main_constraintView)
            val duplicateConstraintAsParent = popUpWindow.findViewById<ConstraintLayout>(R.id.duplicateItemParent)
            val duplicateConstraintChild = popUpWindow.findViewById<ConstraintLayout>(R.id.duplicateItemChild)


            duplicateConstraintAsParent.removeView(duplicateConstraintChild)
            mainConstraintAsParent.addView(duplicateConstraintChild)

            val replaceTextView : TextView = mainConstraintAsParent.findViewById(R.id.replace_textViewForDuplicate)
            val deleteTextView : TextView = mainConstraintAsParent.findViewById(R.id.delete_textViewForDuplicate)
            val cancelTextView : TextView = mainConstraintAsParent.findViewById(R.id.cancel_textViewForDuplicate)

            replaceTextView.setOnClickListener {


            }
            if(selectedSourceCheckBox.isNotEmpty() || selectedDestinationCheckBox.isNotEmpty()) {

                deleteTextView.isClickable = true
                deleteTextView.alpha = 1.0f
                if(selectedSourceCheckBox.isNotEmpty()) {

                    deleteTextView.setOnClickListener {

                        for( i in selectedSourceCheckBox) {
                            if(selectedDuplicateFiles[i].isDirectory)
                                selectedDuplicateFiles[i].deleteRecursively()
                            else
                                selectedDuplicateFiles[i].delete()

                        }
                    }
                }
                else //(selectedDestinationCheckBox.isNotEmpty())
                {
                    deleteTextView.setOnClickListener {

                        for(i in selectedDestinationCheckBox)
                        {
                            if(files[i].isDirectory)
                                files[i].deleteRecursively()
                            else
                                files[i].delete()
                        }

                    }
                }
            }


            cancelTextView.setOnClickListener {

            }
        }
    }
    fun dealWithDuplicateFile(context: Activity)
    {
        


    }


