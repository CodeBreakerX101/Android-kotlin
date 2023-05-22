package com.example.filemanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.MainActivity.Companion.filesFirstPos
import com.example.filemanager.MainActivity.Companion.foldersLastPos
import com.example.filemanager.MainActivity.Companion.allActivityStates
import com.example.filemanager.MainActivity.Companion.checkBoxVisibility
import com.example.filemanager.MainActivity.Companion.forOneTime
import com.example.filemanager.MainActivity.Companion.hasScrolled
import com.example.filemanager.MainActivity.Companion.layoutManager
import com.example.filemanager.MainActivity.Companion.showCheckJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class SetFilesFoldersInLayout(private val context: Activity, private var files : ArrayList<File>,
                              private val foldersSizes : ArrayList<String>) : RecyclerView.Adapter<SetFilesFoldersInLayout.MyViewHolder>()
{

    lateinit var allViews : ViewGroup

    var foldersSizeJob : Job = Job()
    var selectedCheckBox = Array<Boolean>(files.size){false}

    var selectedFilesToDelete = ArrayList<File>()

    companion object{

        var firstSelectedCheckBoxPos : Int = -1
        val selectedFiles = ArrayList<File>() //all selected files
        val duplicateInSelectedFilesPos = ArrayList<Int>() //this is to delete,replace in source directory
        val duplicateInAllFilesPos = ArrayList<Int>()//this is to delete,replace in destination directory


        var selectedFilesToCopyOrCut = ArrayList<File>()

        val selectedDuplicateFiles = ArrayList<File>()

        var childDirectory = ArrayList<Int>()

    }

    init {


        //name of directory(Header Name)
        if(files.isNotEmpty()) {
            val directory: File = files[0].parentFile!!

            val directoryName: TextView = context.findViewById(R.id.directoryName)
            if (directory.exists()) {
                if (Environment.getExternalStorageDirectory() == directory) {
                    directoryName.text = context.getString(R.string.internalstorage)
                } else {
                    directoryName.text = directory.name
                }
            }
        }
        layoutManager.onLayoutCompleted(state()) // pasteBtn,selectAllCheckBox initialized

        /*if(selectedCheckBox.isEmpty()) {
            for (i in 0 until files.size) {
                selectedCheckBox.add(false) //To prevent other views checkView being checked automatically by checking only one
            }
        }*/
        if(firstSelectedCheckBoxPos != -1)
            selectedCheckBox[firstSelectedCheckBoxPos] = true

        if(selectedFilesToCopyOrCut.isNotEmpty()) {

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = context.layoutInflater.inflate(R.layout.item_list, parent, false)
        allViews = parent
        createCheckBox(view)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        if (checkBoxVisibility) {

            holder.checkBox.isChecked = selectedCheckBox[position]
            //Log.i("isChecked OnBind",position.toString() + "  "+selectedCheckBox[position].toString())
        }

        val file = files[position]

        holder.setData(file, position,holder)
    }

    override fun getItemCount(): Int {

        return files.size
    }

    @SuppressLint("ClickableViewAccessibility", "UsableSpace")
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val checkBox : CheckBox = itemView.findViewById(R.id.checkBox)
        private val showFileSize : TextView = itemView.findViewById(R.id.fileSize)


        fun setData(file: File, pos: Int, holder : MyViewHolder) {

            val txvTitle: TextView = itemView.findViewById(R.id.txvTitle)
            val imageView : ImageView = itemView.findViewById(R.id.imageView)

            if(file.isDirectory)
            {
                //txvTitle.text = file.name.toString()
                imageView.setImageResource(R.drawable.folder_image)

            }
            else {

                imageView.setImageResource(R.drawable.file_image)
                addThumbnailInImageView(context,file,imageView,adapterPosition,itemView)
            }

            txvTitle.text = file.name.toString()

            try {
                showFileSize.text = foldersSizes[adapterPosition]

            }catch (e : java.lang.Exception){}

        }
        init {

            itemView.setOnClickListener {

                if (checkBoxVisibility) {

                    selectedCheckBox[adapterPosition] = !checkBox.isChecked//storing checkBox checked states to check the checkBox onBind //without this var checkBox is checking random checkBox while scrolling upside
                    checkBox.isChecked = !checkBox.isChecked //assigning directly
                    if(checkBox.isChecked)
                    {
                        selectedFiles.add(files[adapterPosition])
                    }else{
                        selectedFiles.remove(files[adapterPosition])
                    }

                } else
                    openFileOrFolder()
            }
            itemView.setOnLongClickListener {

                allActivityStates.add(layoutManager.onSaveInstanceState()!!) //for when onBackPressed

                firstSelectedCheckBoxPos = adapterPosition
                selectedCheckBox[adapterPosition] =
                    true
                selectedFiles.add(files[adapterPosition])
                //selectedFilesToCopyOrCut.add(files[adapterPosition])
                context.findViewById<CheckBox>(R.id.selectAllCheckBox).visibility = View.VISIBLE;
                context.findViewById<TextView>(R.id.selectAllTextView).visibility = View.VISIBLE;
                context.findViewById<ImageButton>(R.id.sort).visibility = View.INVISIBLE
                checkBoxVisibility =
                    true //checkBoxVisisbility = true causing showCheckBox() in MainActivity to Start, selectedCheckBox is static


                true
            }
            checkBox.setOnTouchListener { v, event -> // when clicked on checkBox. using setOnTouchListener instead of onCheckedChangeListener cause checking one view causing it to check random view while scrolling to upside

                selectedCheckBox[adapterPosition] = !checkBox.isChecked

                if(checkBox.isChecked)
                {
                    selectedFiles.add(files[adapterPosition])
                }else{
                    selectedFiles.remove(files[adapterPosition])
                }

                false
            }
            /*checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked && !selectedFilesToCopyOrCut.contains(files[adapterPosition])) {
                    selectedFiles.add(files[adapterPosition])
                }
                else if (!isChecked) {
                    selectedFiles.remove(files[adapterPosition])
                }
            }*/
           /* if(selectedCheckBox.size > adapterPosition) {
                *//*if (selectedCheckBox[adapterPosition] ) {
                    selectedFiles.add(files[adapterPosition])
                }*//*
                Log.i("selectedCheckBox ${selectedCheckBox.size}", "position $adapterPosition")
            }*/
            if(checkBoxVisibility) {

                val deleteImgBtn: ImageView =  context.findViewById(R.id.delete_img)
                val copyImgBtn: ImageView   =  context.findViewById(R.id.copy_img)
                val cutImgBtn: ImageView    =  context.findViewById(R.id.cut_img)
                val pasteImgBtn: ImageView  =  context.findViewById(R.id.paste_img)
                val pasteTextBtn: TextView  =  context.findViewById(R.id.paste_textView)

                deleteImgBtn.setOnClickListener {

                    /*if(selectedFilesToDelete.isNotEmpty())//what if select an item pressed delete but cancel the delete, the files are still added in selectedFilesToDelete
                    {
                        selectedFilesToDelete.removeAll(selectedFilesToDelete)
                    }
                    selectedFilesToDelete.addAll(selectedFiles)*/
                    selectedFilesToDelete = selectedFiles
                    onClickDeleteButton(context, selectedFilesToDelete, files, selectedCheckBox)

                    val selectAllCheckBox : CheckBox = context.findViewById(R.id.selectAllCheckBox)
                    if(selectAllCheckBox.isChecked)
                        selectAllCheckBox.isChecked = false
                }

                copyImgBtn.setOnClickListener {

                    /*if(selectedFilesToCopyOrCut.isNotEmpty())
                    {
                        selectedFilesToCopyOrCut.removeAll(selectedFilesToCopyOrCut)
                    }*/
                    selectedFilesToCopyOrCut.addAll(selectedFiles)

                    //onClickCopyButton(context, selectedFilesToCopyOrCut, files)

                    pasteImgBtn.isClickable = true
                    pasteImgBtn.alpha = 1.0f

                    pasteTextBtn.isClickable = true
                    pasteTextBtn.alpha = 1.0f
                }
            }
        }

        private fun openFileOrFolder()
        {
            if (files[adapterPosition].isDirectory) {

                if (hasScrolled) {
                    if(allActivityStates.isNotEmpty())
                        allActivityStates.removeAt(allActivityStates.size - 1)//removing var which has onSaveInstance cause we will be adding onScroll Changed By this way we only using one var
                    hasScrolled = false
                    forOneTime = true
                }
                //selectedCheckBox.removeAll(selectedCheckBox)

                allActivityStates.add(layoutManager.onSaveInstanceState()!!)

                childDirectory.add(adapterPosition)

                showCheckJob.cancel() //in MainActivity()

                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("directory", files[adapterPosition])
                context.startActivity(intent)

            } else if (files[adapterPosition].isFile) {

                val fileExtension = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(files[adapterPosition].extension)

                val fileIntent = Intent(Intent.ACTION_VIEW)
                fileIntent.setDataAndType(Uri.fromFile(files[adapterPosition]), fileExtension)

                try {
                    if (files[adapterPosition].extension == "") {
                        ////
                    } else
                        context.startActivity(fileIntent)

                } catch (e: Exception) {
                    CoroutineScope(Main).launch {
                        Toast.makeText(context, "No App Found To Open This File", Toast.LENGTH_SHORT) /*use create chooser*/
                            .show()
                    }
                }
            }
        }
    }

    private fun createCheckBox(view : View){

        //val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //val view = inflater.inflate(R.layout.item_list,null)
        val checkBox = CheckBox(context)
        val constraintLayout : ConstraintLayout = view.findViewById(R.id.listView)

        checkBox.id = R.id.checkBox
        if(checkBoxVisibility) //if checkBoxVisible then in MainActivity showCheckBox() starts, makes the adapter load again and as checkBoxVisibility true we initialize checkBox.visibility = view.VISIBLE
            checkBox.visibility = View.VISIBLE
        else
            checkBox.visibility = View.INVISIBLE

        checkBox.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT).apply { endToEnd = constraintLayout.paddingEnd}
        constraintLayout.addView(checkBox)

    }
    inner class state() : RecyclerView.State(){

        init {

            Log.i("folder lastPos $foldersLastPos", "filesFirstPos $filesFirstPos")
            if(checkBoxVisibility || selectedFilesToCopyOrCut.isNotEmpty()) {

                set_options_for_selected_item(context) //show bottom options like copy,delete

                if(selectedFilesToCopyOrCut.isNotEmpty())
                {
                    val pasteImgBtn : ImageView = context.findViewById(R.id.paste_img)
                    val pasteTextBtn : TextView = context.findViewById(R.id.paste_textView)
                    pasteImgBtn.isClickable = true
                    pasteImgBtn.alpha = 1.0f

                    pasteTextBtn.isClickable = true
                    pasteTextBtn.alpha = 1.0f

                    pasteImgBtn.setOnClickListener {
                        Log.i("copied file", selectedFilesToCopyOrCut.size.toString())
                        SortAndHideFolders().insertInSortedFiles(context,files,foldersSizes,
                            this@SetFilesFoldersInLayout,foldersLastPos[foldersLastPos.lastIndex],filesFirstPos[filesFirstPos.lastIndex])
                        //selectedFilesToCopyOrCut[0].copyTo()
                        if(duplicateInSelectedFilesPos.isNotEmpty())//if duplicate files present
                        {

                        }

                        selectedFilesToCopyOrCut.removeAll(selectedFilesToCopyOrCut)
                        selectedFiles.removeAll(selectedFiles)
                        hide_options_for_selected_item(context)

                    }
                }
            }

            val selectAllCheckBox : CheckBox = context.findViewById(R.id.selectAllCheckBox)
            val selectAllTextView : TextView = context.findViewById(R.id.selectAllTextView)
            selectAllTextView.setOnClickListener {
                selectAllCheckBox.isChecked = !selectAllCheckBox.isChecked
            }

            selectAllCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->

                if(isChecked)
                {

                    for(i in 0..selectedCheckBox.lastIndex)
                    {
                        selectedCheckBox[i] = true
                    }
                    selectedFiles.addAll(files)

                    if(checkBoxVisibility)
                    {
                        notifyItemRangeChanged(layoutManager.findFirstCompletelyVisibleItemPosition(),
                            files.lastIndex)
                    }else {
                        if (allActivityStates.isNotEmpty())
                            allActivityStates.removeAt(allActivityStates.size - 1)

                        allActivityStates.add(layoutManager.onSaveInstanceState()!!)

                        checkBoxVisibility =
                            true // showCheckBox() will be called from MainActivity()
                    }
                }
                else
                {
                    for(i in 0..selectedCheckBox.lastIndex)
                    {
                        selectedCheckBox[i] = false
                    }
                    selectedFiles.removeAll(selectedFiles)
                    context.onBackPressed()
                }
            }

            /*if(checkBoxVisibility )
            {
                val scale = context.resources.displayMetrics.density
                Log.i("density",scale.toString())
                val recyclerView : RecyclerView = context.findViewById(R.id.recyclerView)
                recyclerView.setPadding(0,0,0, (60 * scale + 0.5f).toInt())
                recyclerView.clipToPadding = false

            }*/
            //getAndSetFilesSize.invokeOnCompletion {
            //notifyItemRangeChanged(layoutManager.findFirstCompletelyVisibleItemPosition(), layoutManager.findLastCompletelyVisibleItemPosition())
            //}

            Log.i("state","completed")
        }
    }

}


