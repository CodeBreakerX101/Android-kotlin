package com.example.filemanager

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class CreateCheckBox(val checkBoxId : Int) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.item_list)

        val checkBox : CheckBox = CheckBox(this)
        //val constraintLayout = ConstraintLayout(this)

        val listConstraintLayout : ConstraintLayout = findViewById(R.id.listView)

        checkBox.id = checkBoxId

        //if(checkBoxVisibility)
        checkBox.visibility = View.VISIBLE
        /*else
            GcheckBox.visibility = View.INVISIBLE*/

        checkBox.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT).apply { endToEnd = listConstraintLayout.paddingEnd }

        listConstraintLayout.addView(checkBox)
    }
}