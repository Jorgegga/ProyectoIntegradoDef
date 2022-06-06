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
import com.example.proyectointegradodef.models.ReadGenero

class ListGeneroAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val genero: ArrayList<ReadGenero>):
    ArrayAdapter<ReadGenero>(context, layoutResource, genero), Filterable {

    private var mGenero:ArrayList<ReadGenero> = genero

    override fun getCount(): Int {
        return mGenero.size
    }

    override fun getItemId(p0: Int): Long {
        // Or just return p0
        return mGenero.get(p0).id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = mGenero[position].nombre
        return view
    }

    override fun getItem(p0: Int): ReadGenero? {
        return mGenero.get(p0)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                mGenero = filterResults.values as ArrayList<ReadGenero>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                    genero
                else
                    genero.filter {
                        it.nombre.toLowerCase().contains(queryString)
                    }
                return filterResults
            }
        }
    }
}