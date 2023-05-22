package com.example.filemanager



import com.example.filemanager.MainActivity.Companion.filesFirstPos
import com.example.filemanager.MainActivity.Companion.foldersLastPos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File

class LoadFolders(){



    fun getThefoldersAndFiles(folder_address : File, allFolders : Boolean = false) : ArrayList<File> // Here is decided which folders or files will be taken or even if only folders
    {
        val folders = ArrayList<File>()
        val files = ArrayList<File>()

        for (i in folder_address.listFiles()!!)
        {
            if (i.isDirectory)
                folders.add(i)
            else if(i.isFile)
                files.add(i)
            }


         // using folders.size-1 actually initializing folders + files size

            return if (allFolders) {//For all Folders

                SortAndHideFolders().sortByName(folders)
                SortAndHideFolders().sortByName(files)


                foldersLastPos.add(folders.lastIndex)
                if(files.isNotEmpty())
                {
                    filesFirstPos.add(folders.size) //if no folder available in folders then the size will be 0
                }

                for(i in files)  //adding files in one var
                    folders.add(i)

                    folders // Also using to collect the files

            } else { //For only Basic Folders

                SortAndHideFolders().hideHiddenFolders(folders)
                SortAndHideFolders().hideHiddenFolders(files)

                foldersLastPos.add(folders.lastIndex)
                if(files.isNotEmpty())
                {
                    filesFirstPos.add(folders.size)
                }

                val sortingFoldersJob = CoroutineScope(IO).launch {
                    if (folders.size != 0)
                        SortAndHideFolders().sortByName(folders)

                }
                val sortingFilesJob = CoroutineScope(IO).launch {
                    if (files.size != 0)
                        SortAndHideFolders().sortByName(files)


                }

                while (sortingFoldersJob.isActive || sortingFilesJob.isActive) {
                        //
                }
                for (i in files)  //adding files in one var
                        folders.add(i)


                folders // Also using to collect the files
            }
    }



}