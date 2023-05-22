package com.example.filemanager

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.SetFilesFoldersInLayout.Companion.duplicateInAllFilesPos
import com.example.filemanager.SetFilesFoldersInLayout.Companion.duplicateInSelectedFilesPos
import com.example.filemanager.SetFilesFoldersInLayout.Companion.selectedDuplicateFiles
import java.io.File

class DuplicateItemsRecyclerView(val context : Activity,private val files : ArrayList<File>) : RecyclerView.Adapter<DuplicateItemsRecyclerView.DuplicateViewHolder>() {

    companion object{
        val selectedSourceCheckBox = ArrayList<Int>()
        val selectedDestinationCheckBox = ArrayList<Int>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuplicateViewHolder {
        val view = context.layoutInflater.inflate(R.layout.duplicate_items_details_for_recyclerview,parent,false)

        return DuplicateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DuplicateViewHolder, position: Int) {

        holder.setData(holder,position)
    }

    override fun getItemCount(): Int {

        return selectedDuplicateFiles.size
    }

    inner class DuplicateViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        private val sourceDirName : TextView = itemView.findViewById(R.id.sourceDirName)
        private val sourceCheckBox : CheckBox = itemView.findViewById(R.id.sourceCheckBox)
        private val sourceImageView : ImageView = itemView.findViewById(R.id.sourceImageView)
        private val sourceFileSize : TextView = itemView.findViewById(R.id.sourceFileSize)

        private val destinationDirName : TextView = itemView.findViewById(R.id.destinationDirName)
        private val destinationCheckBox : CheckBox = itemView.findViewById(R.id.destinationCheckBox)
        private val destinationImageView : ImageView = itemView.findViewById(R.id.destinationImageView)
        private val destinationFileSize : TextView = itemView.findViewById(R.id.destinationFileSize)

        fun setData(holder : DuplicateViewHolder, pos : Int)
        {

            val filesPosition = duplicateInAllFilesPos[pos]
            val selectedFilePosition = duplicateInSelectedFilesPos[pos]

            /*
                source -> selectedFiles,selectedFilesPosition
                destination -> files,filesPosition
             */

            sourceDirName.text = selectedDuplicateFiles[selectedFilePosition].parentFile!!.name
            sourceCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    selectedSourceCheckBox.add(selectedFilePosition)
                else
                    selectedSourceCheckBox.remove(selectedFilePosition)
            }
            addThumbnailInImageView(context,selectedDuplicateFiles[selectedFilePosition],sourceImageView,adapterPosition,itemView) // adding image in imageView
            sourceFileSize.text = SortAndHideFolders().convertToSpecificStorageUnit(
                selectedDuplicateFiles[selectedFilePosition].length())

            destinationDirName.text = files[filesPosition].parentFile!!.name
            destinationCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    selectedDestinationCheckBox.add(filesPosition)
                else
                    selectedDestinationCheckBox.remove(filesPosition)
            }
            addThumbnailInImageView(context,files[filesPosition],destinationImageView,adapterPosition,itemView)
            destinationFileSize.text = SortAndHideFolders().convertToSpecificStorageUnit(files[filesPosition].length())

        }

    }

}