package com.example.proyectointegradodef.musica.filtros

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.proyectointegradodef.models.ReadAutor


class ListAutorAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val autor: ArrayList<ReadAutor>):
    ArrayAdapter<ReadAutor>(context, layoutResource, autor), Filterable {

    private var mAutor:ArrayList<ReadAutor> = autor

    override fun getCount(): Int {
        return mAutor.size
    }

    override fun getItemId(p0: Int): Long {
        // Or just return p0
        return mAutor.get(p0).id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        Log.d("------------------------", mAutor.toString())
        view.text = mAutor[position].toString()
        return view
    }

    override fun getItem(p0: Int): ReadAutor? {
        return mAutor.get(p0)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                mAutor = filterResults.values as ArrayList<ReadAutor>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                    autor
                else
                    autor.filter {
                        it.nombre.toLowerCase().contains(queryString)
                    }
                return filterResults
            }
        }
    }
}