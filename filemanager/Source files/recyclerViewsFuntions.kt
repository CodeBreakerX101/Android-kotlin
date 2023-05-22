package com.example.filemanager

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.filemanager.MainActivity.Companion.layoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

fun addThumbnailInImageView(context : Activity, file : File, imageViewR : ImageView,adapterPos : Int,itemView : View)
    {
        var fileType: String = ""
        val fileTypeWithExtension = android.webkit.MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(file.extension).toString()

        for (i in 0 until  fileTypeWithExtension.length - 1) {

            if (fileTypeWithExtension[i] == '/') { //Get the file type like( text/plain to text)
                for (j in 0 until i) {

                    fileType += fileTypeWithExtension[j]
                }
                break
            }
        }
        //holder.itemView.doOnLayout{
        /*val imageFile = File(context.externalCacheDir,file.nameWithoutExtension + ".jpg")
        if(!imageFile.exists())
            imageFile.createNewFile()*/



        try {
            val imageView = imageViewR
            val adapterPosition = adapterPos
            val recyclerView: RecyclerView = context.findViewById(R.id.recyclerView)


            /*val currentView : View = layoutManager.findViewByPosition(adapter.MyViewHolder(itemView).adapterPosition)!!
            currentView.findViewById<ImageView>()*/
            when (fileType) {
                "" -> imageView.setImageResource(R.drawable.unknown_file_image)

                "text" ->
                    when (fileTypeWithExtension) {
                        "text/plain" -> imageView.setImageResource(R.drawable.text_file_image)
                        "text/xml" -> imageView.setImageResource(R.drawable.xml_image)
                    }
                "image" -> {

                    try {
                        //if(imageFile.length() == 0.toLong())
                        Glide.with(context).asBitmap().load(file).centerCrop()
                            .into(customTarget(imageView))
                        //itemView.doOnLayout {

                        //CoroutineScope(Main)


                        //recyclerView.adapter?.notifyItemChanged(layoutManager.getPosition(imageView))
                    } catch (e: java.lang.Exception) {
                    }
                }
                "audio" -> {
                    val image = getAlbumArt(file.toString())
                    Glide.with(context).load(image).placeholder(R.drawable.music_image)
                        .centerCrop().into(imageView)
                }
                "video" ->
                    Glide.with(context).load(file).centerCrop().into(imageView)

                "application" -> {

                    if (fileTypeWithExtension == "application/pdf") {

                        imageView.setImageResource(R.drawable.pdf_image)
                    } else if (isMsWord(fileTypeWithExtension)) {

                        imageView.setImageResource(R.drawable.msword_image)
                    } else if (fileTypeWithExtension == "application/vnd.ms-powerpoint") {

                        imageView.setImageResource(R.drawable.powerpoint_image)
                    }
                }
            }

        } catch (e: java.lang.Exception) { }

    }

    class customTarget(val imageView: ImageView) : CustomTarget<Bitmap>(250,250){
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

            imageView.setImageBitmap(resource)

        }

        override fun onLoadCleared(placeholder: Drawable?) {
            TODO("Not yet implemented")
        }

    }
    fun selectAll(selectedCheckBox: ArrayList<Boolean>){

        for(i in 0 until  selectedCheckBox.size)
        {
            selectedCheckBox[i] = true
        }
    }
    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        return retriever.embeddedPicture
    }
    private fun isMsWord(fileTypeWithExtension: String) : Boolean
    {
        val context = MainActivity().applicationContext

        if(fileTypeWithExtension == "application/msword" || fileTypeWithExtension == context.getString(R.string.docx))
            return true

        return false
    }
    private fun isPowerPoint(context: Activity, fileTypeWithExtension : String) : Boolean{

        Log.i("ppt fileType",fileTypeWithExtension)
        when(fileTypeWithExtension)
        {
            context.getString(R.string.ppt)->return true
            context.getString(R.string.pot)->return true
            context.getString(R.string.pps)->return true
            context.getString(R.string.ppa)->return true
        }
        return false
    }