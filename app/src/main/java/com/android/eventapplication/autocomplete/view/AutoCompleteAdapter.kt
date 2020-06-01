package com.android.eventapplication.autocomplete.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.android.eventapplication.autocomplete.model.preditictions.Prediction

class AutoCompleteAdapter(
    context: Context?, @LayoutRes private val layoutResource: Int,
    predictionList: List<Prediction>
) : ArrayAdapter<Prediction>(context!!, layoutResource, predictionList), Filterable {
    private var mpredictionList = predictionList


    override fun getCount() = mpredictionList.size
    override fun getItem(i: Int) = mpredictionList[i]
    override fun getItemId(i: Int) = i.toLong()

    fun replaceData(newRepos: List<Prediction>) {
        mpredictionList = newRepos
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(
            layoutResource,
            parent,
            false
        ) as TextView
        view.setTextColor(Color.BLUE)
        if (!mpredictionList[position].description.isNullOrEmpty()) {
            view.text =
                "(${mpredictionList[position].description})"
        }

        return view
    }
}
