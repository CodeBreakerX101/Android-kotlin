package com.example.filemanager


import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.SetFilesFoldersInLayout.Companion.selectedFilesToCopyOrCut
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.File
import java.lang.Exception


/*
 1. Ask For Permission()
 2. LoadFoldersAndFiles() { GetInternalStorageAddress(), Set Directory And Files Address to a var}
 3. Get The Folders And Files Sizes()
 4. Attach Folders, Files Names and Sizes to a RecyclerView

 */

class MainActivity : AppCompatActivity() {

    private val REQUESTCODE = 455
    private val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    var job : Job = Job()

    lateinit var recyclerView : RecyclerView
    private lateinit var internalStorageDirectory : File

    var files = ArrayList<File>()
    var foldersSizes = ArrayList<String>()



    companion object{

        //var recyclerViewId = 0

        var saveProcessedFiles = ArrayList<ArrayList<File>>()
        var saveProcessedFilesSize = ArrayList<ArrayList<String>>()

        var checkBoxVisibility : Boolean = false
        var hasScrolled = false
        var forOneTime = true

        var layoutState : Parcelable? = null
        var allActivityStates = ArrayList<Parcelable>()

        lateinit var layoutManager : LinearLayoutManager
        var showCheckJob : Job = Job()

        var getAndSetFilesSize : Job = Job()

        var foldersLastPos = ArrayList<Int>()
        var filesFirstPos = ArrayList<Int>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestPermissions(permission,REQUESTCODE)

        recyclerView = findViewById(R.id.recyclerView)
        //checkBox = findViewById(R.id.checkBox)

        layoutManager = LinearLayoutManager(this@MainActivity)

        showCheckBox()
        recyclerView.addOnScrollListener(ScrollChanged(this@MainActivity))
        val sortBtn : ImageButton = findViewById(R.id.sort)

        sortBtn.setOnClickListener {
            popUpMenu(this,sortBtn)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(REQUESTCODE == requestCode) {
           if(grantResults.isNotEmpty() && grantResults[0] and grantResults[1] == PackageManager.PERMISSION_GRANTED)
           {

               getInternalStorageAddress()
               // this will only work if any folder has been clicked
               getTheClickedFolderAddressToOpenInNewActivity()

               //Load Folders And Files
               files = LoadFolders().getThefoldersAndFiles(internalStorageDirectory)

               getFoldersAndFilesSizes()
               loadFilesInAnotherActivity()
           }
           else {
               Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
               finish()
               //////////////////////// Show a Massage to Show the permission again or Quit
           }
        }
        else {
            Toast.makeText(this, "Permission Code Did Not Matched", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun getInternalStorageAddress()
    {
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            internalStorageDirectory = Environment.getExternalStorageDirectory()
        else {
            Toast.makeText(this, "Internal Storage Not Available", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun getFoldersAndFilesSizes()
    {
        getAndSetFilesSize = CoroutineScope(IO).launch {

            for (i in files) {
                foldersSizes.add(SortAndHideFolders().convertToSpecificStorageUnit(
                    SortAndHideFolders().folderSize(i)))
            }

            saveProcessedFiles.add(files)
            saveProcessedFilesSize.add(foldersSizes)
        }
    }

    override fun onBackPressed() { //use of onBackPressed() called - state : RecyclerView.State in SetFilesFoldersInLayout, onClickDeleteBtn() in Options Function.kt

        if(checkBoxVisibility)
        {
            checkBoxVisibility = false

            /*for(i in 0 until SetFilesFoldersInLayout.selectedCheckBox.size)
                SetFilesFoldersInLayout.selectedCheckBox[i] = false*/



            showCheckBox()

            if(selectedFilesToCopyOrCut.isEmpty()){
                hide_options_for_selected_item(this@MainActivity)
                val selectAllCheckBox : CheckBox = this.findViewById(R.id.selectAllCheckBox)
                selectAllCheckBox.isChecked = false

            }

            if(allActivityStates.isNotEmpty()) {
                layoutState = allActivityStates[allActivityStates.lastIndex]
                allActivityStates.removeAt(allActivityStates.size-1)
            }

            //Invisible checkbox and textview
            findViewById<CheckBox>(R.id.selectAllCheckBox).visibility = View.INVISIBLE;
            findViewById<TextView>(R.id.selectAllTextView).visibility = View.INVISIBLE;
            findViewById<ImageButton>(R.id.sort).visibility = View.VISIBLE
            loadFilesInAnotherActivity()
        }
        else if(Environment.getExternalStorageDirectory() == internalStorageDirectory)
        {
            finishAffinity()
        }
        else {

            //finish()
            files.removeAll(files)
            saveProcessedFiles.removeAt(saveProcessedFiles.lastIndex)
            files = saveProcessedFiles[saveProcessedFiles.size-1] //

            foldersSizes.removeAll(foldersSizes)
            saveProcessedFilesSize.removeAt(saveProcessedFilesSize.lastIndex)

            foldersSizes = saveProcessedFilesSize[saveProcessedFilesSize.lastIndex]

            internalStorageDirectory = internalStorageDirectory.parentFile!!

            if(foldersLastPos.isNotEmpty())
                foldersLastPos.removeAt(foldersLastPos.lastIndex)

            if(filesFirstPos.isNotEmpty())
                filesFirstPos.removeAt(filesFirstPos.lastIndex)

            if(hasScrolled) {
                allActivityStates.removeAt(allActivityStates.lastIndex) //also saving onSaveInstance in onScrollChanged by default this function runs at least one so removing the last var
                forOneTime = true // to make only one variable while saving onSaveInstance in allActivityStates[]
            }
            if(allActivityStates.isNotEmpty()) {

                layoutState = allActivityStates[allActivityStates.lastIndex]
                allActivityStates.removeAt(allActivityStates.lastIndex)//to use the same position again
            }

            loadFilesInAnotherActivity()
        }

    }
    private fun showCheckBox()
    {
        showCheckJob = CoroutineScope(IO).launch {
            while (!checkBoxVisibility) {
                delay(20)
            }

            //set_options_for_selected_item(this@MainActivity) // bottom options like copy,delete
            if(allActivityStates.isNotEmpty())
                layoutState = allActivityStates[allActivityStates.size - 1]

            CoroutineScope(Dispatchers.Main).launch {
                loadFilesInAnotherActivity()
            }
        }
    }    
    private fun getTheClickedFolderAddressToOpenInNewActivity()
    {
        try {
            val bundle : Bundle = intent.extras!!
            val directory : File = bundle.get("directory") as File
            if(directory.exists())
            {
                internalStorageDirectory = directory
            }

        }catch (exception : Exception) { }
    }

    fun loadFilesInAnotherActivity()
    {
        val adapter = SetFilesFoldersInLayout(this@MainActivity,files,foldersSizes)

        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        if(layoutState != null )
            layoutManager.onRestoreInstanceState(layoutState)

    }

    class ScrollChanged(val context : MainActivity) : RecyclerView.OnScrollListener() {


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //Log.i("scroll position",dy.toString())
            //scrollUp = dy < 0

            if(forOneTime) {


                allActivityStates.add(layoutManager.onSaveInstanceState()!!)
                hasScrolled = true
                forOneTime = false
            }
            else
            {

                if(allActivityStates.isNotEmpty())
                    allActivityStates[allActivityStates.lastIndex] = layoutManager.onSaveInstanceState()!!
            }

        }
    }




}

