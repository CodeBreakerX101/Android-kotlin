package com.example.filemanager


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.SetFilesFoldersInLayout.Companion.duplicateInAllFilesPos
import com.example.filemanager.SetFilesFoldersInLayout.Companion.duplicateInSelectedFilesPos
import com.example.filemanager.SetFilesFoldersInLayout.Companion.selectedFilesToCopyOrCut
import java.io.File

class SortAndHideFolders {

    private val namesInLowerCase = ArrayList<String>()
    private val foldersAndFilesSize = ArrayList<Long>()

    fun sortByName(foldersOrFiles: ArrayList<File>)
    {

        for(i in foldersOrFiles)
        {
            namesInLowerCase.add(i.name.toString().lowercase())
        }

        for (h in 0 until foldersOrFiles.size) {

            var hasSwaped = false
            for (i in 0 until (foldersOrFiles.size- 1)) {

                if (namesInLowerCase[i] > namesInLowerCase[i+1]) {

                    swap(foldersOrFiles,namesInLowerCase,i, i + 1)

                    hasSwaped = true
                }
            }
            if(!hasSwaped)
                break
        }
    }
    fun swap(foldersOrFiles: ArrayList<File>, namesInLowerCase : ArrayList<String>, first : Int , second : Int)
    {
        val temp = foldersOrFiles[first]
        foldersOrFiles[first] = foldersOrFiles[second]
        foldersOrFiles[second] = temp

        val tempName = namesInLowerCase[first]
        namesInLowerCase[first] = namesInLowerCase[second]
        namesInLowerCase[second] = tempName


    }
    fun insertInSortedFiles(context: Activity,files : ArrayList<File>,
                            foldersSizes : ArrayList<String>, adapter : SetFilesFoldersInLayout, 
                            foldersLastPosition : Int,filesFirstPosition : Int)
    {
        var foldersLastPos = foldersLastPosition
        var filesFirstPos = filesFirstPosition


        for(i in selectedFilesToCopyOrCut)
        {
            if (i.isDirectory) {


                if (foldersLastPos == -1) //No folder available in destination path
                {
                    copyFileOrFolder(context,files, foldersSizes, i, adapter, 0)

                    filesFirstPos += 1
                }
                else if( i.name.lowercase() < files[0].name.lowercase())
                {
                    copyFileOrFolder(context, files, foldersSizes, i, adapter, 0)
                }
                else if(i.name.lowercase() > files[foldersLastPos].name.lowercase())
                {
                    copyFileOrFolder(context, files, foldersSizes, i, adapter, foldersLastPosition+1) //foldersLastPos+1 is > files.lastIndex so file will be added without any use of position see in copyFileOrFolder(context, )
                }
                else if (i.name.lowercase() > files[0].name.lowercase() && i.name.lowercase() < files[foldersLastPos].name.lowercase()) {

                    val sortedPosition = getThePosition(files, i, 0, foldersLastPos)
                    if(i.name.lowercase() == files[sortedPosition].name.lowercase())
                    {
                        duplicateInAllFilesPos.add(sortedPosition)
                        duplicateInSelectedFilesPos.add(selectedFilesToCopyOrCut.indexOf(i))
                    }else{
                        copyFileOrFolder(context, files, foldersSizes, i, adapter, sortedPosition)
                        foldersLastPos += 1// don't know if folder is created and i will check for it later for now let's assume its created the folder
                        filesFirstPos += 1
                    }
                }
                else if (i.name.lowercase() > files[0].name.lowercase() && i.name.lowercase() < files[foldersLastPos].name.lowercase())
                {
                    val sortedPosition = getThePosition(files,i,0,foldersLastPos)
                    if(i.name.lowercase() == files[sortedPosition].name.lowercase()) {

                        duplicateInAllFilesPos.add(sortedPosition)
                        duplicateInSelectedFilesPos.add(selectedFilesToCopyOrCut.indexOf(i))

                    }else{
                        copyFileOrFolder(context, files,foldersSizes,i,adapter,sortedPosition)
                        foldersLastPos += 1// don't know if folder is created and i will check for it later for now let's assume its created the folder
                        filesFirstPos += 1
                    }
                }
                else if(i.name.lowercase() == files[0].name.lowercase()){
                    duplicateInAllFilesPos.add(0)
                    duplicateInSelectedFilesPos.add(selectedFilesToCopyOrCut.indexOf(i))
                }
                else if(i.name.lowercase() == files[foldersLastPos].name.lowercase()){
                    duplicateInAllFilesPos.add(foldersLastPos)
                    duplicateInSelectedFilesPos.add(selectedFilesToCopyOrCut.indexOf(i))
                }
            }
            else { //selectedFile.isFile


                if(filesFirstPos == -1){

                    if(foldersLastPos == -1)
                    {

                        copyFileOrFolder(context, files, foldersSizes, i, adapter, 0)//dont worry about sortPosition == 0 , 0 is > files.lastIndex which is -1 as no file or folder available so file will be added without any use of sortPosition
                    }
                    else{ //if folder available
                        copyFileOrFolder(context, files, foldersSizes, i, adapter, foldersLastPosition+1) //foldersLastPos+1 is > files.lastIndex so file will be added without any use of position see in copyFileOrFolder(context, )
                    }
                }
                else if( i.name.lowercase() < files[filesFirstPos].name.lowercase())
                {
                    if(foldersLastPos == -1)
                    {
                        copyFileOrFolder(context, files, foldersSizes, i, adapter, 0)//dont worry about sortPosition == 0 , 0 is > files.lastIndex which is -1 as no file or folder available so file will be added without any use of sortPosition
                    }
                    else {
                        copyFileOrFolder(context, files, foldersSizes, i, adapter, foldersLastPosition+1) //foldersLastPos+1 is > files.lastIndex so file will be added without any use of position see in copyFileOrFolder(context, )
                    }
                }
                else if(i.name.lowercase() > files[files.lastIndex].name.lowercase()){

                    copyFileOrFolder(context, files, foldersSizes, i, adapter, files.size)//files.size means files.lastIndex + 1
                }
                else if(i.name.lowercase() > files[filesFirstPos].name.lowercase() && i.name.lowercase() < files[files.lastIndex].name.lowercase()){

                    val sortedPosition = getThePosition(files,i,filesFirstPos,files.lastIndex)

                    if(sortedPosition <= files.lastIndex) {

                        if (i.name.lowercase() == files[sortedPosition].name.lowercase()) {
                            duplicateInAllFilesPos.add(sortedPosition)
                            duplicateInSelectedFilesPos.add(selectedFilesToCopyOrCut.indexOf(i))
                        } else {
                            copyFileOrFolder(context, files, foldersSizes, i, adapter
                                , sortedPosition)
                        }
                    }else {
                        copyFileOrFolder(context, files, foldersSizes, i, adapter
                            , sortedPosition)
                    }

                }
                else if(i.name.lowercase() == files[filesFirstPos].name.lowercase()){
                    duplicateInAllFilesPos.add(filesFirstPos)
                    duplicateInSelectedFilesPos.add(selectedFilesToCopyOrCut.indexOf(i))
                }
                else if(i.name.lowercase() == files[files.lastIndex].name.lowercase()){
                    duplicateInAllFilesPos.add(files.lastIndex)
                    duplicateInSelectedFilesPos.add(selectedFilesToCopyOrCut.indexOf(i))
                }
            }
        }
    }

    private fun copyFileOrFolder( context : Activity, files: ArrayList<File>, foldersSizes: ArrayList<String>, selectedFile: File, adapter : SetFilesFoldersInLayout, sortedPosition: Int = -1) {

        val fileCreate : File
        val folderCreate : File
        val parentDirectory : File
        val bundle : Bundle = context.intent.extras!!

        parentDirectory = if(files.isNotEmpty())
            files[0].parentFile!! //onBackPressed() bundle is empty, we can use files and it can't be empty cause we are going back to the parent directory
        else
            bundle.get("directory") as File // while opening new directory we don't know if files.isNotEmpty() so can't trust on files[0],but now bundle has the opened directory parentFile address, but onBackPressed() yes this is empty

        val recyclerView : RecyclerView = context.findViewById(R.id.recyclerView)

        if(selectedFile.isDirectory)
        {
            folderCreate = File(parentDirectory,selectedFile.name.lowercase())

            folderCreate.mkdirs()

            if(folderCreate.exists())
            {
                val folderLength = folderSize(folderCreate)
                if (folderLength < folderCreate.freeSpace) //folderCreate.freeSpace is same as memory card free space
                {
                    selectedFile.copyRecursively(folderCreate,true)

                    if (sortedPosition < files.lastIndex) { //if files.size == 0 means Not a singel file or folder available, then files.lastIndex == -1

                        files.add(sortedPosition, folderCreate)
                        foldersSizes.add(sortedPosition, convertToSpecificStorageUnit(folderLength))

                        adapter.notifyItemInserted(sortedPosition)

                        recyclerView.scrollToPosition(sortedPosition)

                    } else {

                        files.add(folderCreate)
                        foldersSizes.add(convertToSpecificStorageUnit(folderLength))

                        adapter.notifyItemInserted(files.lastIndex)

                        recyclerView.scrollToPosition(files.lastIndex)
                    }

                } else { Toast.makeText(context, "Low Memory", Toast.LENGTH_SHORT).show() }

            }else{ Toast.makeText(context,"Failed to copy",Toast.LENGTH_SHORT).show() }
        }
        else {

            fileCreate = File(parentDirectory, selectedFile.name.lowercase())
            fileCreate.createNewFile()

            if (fileCreate.exists()) {
                //val freeSpace = convertToSpecificStorageUnit(selectedFile.freeSpace)
                //Toast.makeText(context,"free space" + freeSpace,Toast.LENGTH_SHORT).show()

                val fileLength = selectedFile.length()
                if (fileLength < selectedFile.freeSpace) { // here i.freeSpace is same as any directory its the free space of the memory card

                    selectedFile.copyTo(fileCreate,true)

                    if (sortedPosition < files.lastIndex) {
                        // adding in files : Arraylist<File>
                        files.add(sortedPosition, fileCreate)
                        foldersSizes.add(sortedPosition,
                            convertToSpecificStorageUnit(fileLength))
                        //no need to change filesFirstPos when adding file in (files :ArrayList<File>)
                        // change only when adding folder in files first position will change

                        adapter.notifyItemInserted(sortedPosition)
                        recyclerView.scrollToPosition(sortedPosition)

                    } else {

                        files.add(fileCreate)
                        foldersSizes.add(convertToSpecificStorageUnit(fileLength))

                        adapter.notifyItemInserted(files.lastIndex)
                        recyclerView.scrollToPosition(sortedPosition)
                    }

                } else { Toast.makeText(context,"Low Memory",Toast.LENGTH_SHORT).show() }

            }else { Toast.makeText(context,"Failed to copy",Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getThePosition(files : ArrayList<File>,selectedFile: File,firstPosition : Int,lastPosition : Int) : Int
    {
        var firstPos = firstPosition
        var lastPos = lastPosition

        while(true) {

            if (lastPos - firstPos <= 10) {   //compare up to 10 files or folders
                for (i in lastPos downTo (firstPos ))//selectedFile is greater than firstPos and less than lastPos
                {
                    if (selectedFile.name.lowercase() >= files[i].name.lowercase()) {//unless the selectedFile is greater than files[i] keep going

                        return if(selectedFile.name.lowercase() == files[i].name.lowercase()) {
                            i
                        } else{
                            i+1
                        }//+1 is if we have A,B,D,E and selectedFile is C than C is greater than B(condition met) but B pos is i and we wanna put after B which is i+1
                    }
                }
            }
            else
            {
                if(selectedFile.name.lowercase() < files[firstPos + (lastPos - firstPos) / 2].name.lowercase()) //75% between first to last Pos
                    lastPos = firstPos +  (lastPos - firstPos)/2
                else
                    firstPos += (lastPos - firstPos)/2
            }
        }
    }

    fun sortBySize(foldersAndFiles: ArrayList<File>)
    {

        val obj : Algorithms = Algorithms()
                obj.QuickSortInDescendingOrder(foldersAndFiles, 0,  foldersAndFiles.size )

    }

    private var totalSize : Long = 0


    fun folderSize(directory : File) : Long//in here directory is folder or file
    {

        val childFiles = ArrayList<File>()

        if(directory.isDirectory) {

            for (i in directory.listFiles()!!) {
                childFiles.add(i)
            }

            for (i in 0 until childFiles.size) {

                if (childFiles[i].isDirectory) {

                    folderSize(childFiles[i])

                } else {
                    totalSize += childFiles[i].length()
                }
            }
        }
        else // if Directory.isFile
            totalSize = directory.length()

        return totalSize
    }

    fun convertToSpecificStorageUnit(SizeOfFile : Long) : String
    {
        //totalSize = 0

        var fileSize : Double = SizeOfFile.toDouble() //in Byte
        var fileSizeWithType = ""
        val digitalStorageTypes = listOf<String>(" KB"," MB"," GB"," TB"," PB")
        var nextUnit = 0

        if(fileSize > 1024) {
            while (fileSize > 1024) // KByte
            {

                fileSize /= 1024.0f

                fileSizeWithType = if(nextUnit > 0)
                    String.format("%.2f", fileSize) + digitalStorageTypes[nextUnit]

                else
                    String.format("%.2f", fileSize) + digitalStorageTypes[nextUnit]

                if (nextUnit < 5)
                    nextUnit += 1
            }

        }else if(fileSize > 0)
            fileSizeWithType = (fileSize.toInt()).toString() + " B"
        else
            fileSizeWithType = "0 B"



        return fileSizeWithType
    }

    fun hideHiddenFolders(foldersOrFiles: ArrayList<File>)
    {
        var nextFile  = 0
        for (i in 0 until foldersOrFiles.size) {

            if (!(foldersOrFiles[i].isHidden))
            {
                foldersOrFiles[nextFile] = foldersOrFiles[i]
                nextFile += 1
            }
        }
        if(nextFile < foldersOrFiles.size && nextFile != 0)      /// guess if theres are no dot in any folder then nextFile
            for (i in (foldersOrFiles.size - 1) downTo nextFile )// is 0 and thus all foldersOrFiles will be removed
                foldersOrFiles.removeAt(i)                       // if nextFile is at least 1 means other foldersOrFiles have dot so removing them is OK
        // the logic in NO : 1 is -> nextFile is only increased if theres no dot file
        // means except all the files are doted so we can remove them
    }

}