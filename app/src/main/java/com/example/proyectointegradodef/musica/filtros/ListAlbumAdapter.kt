package com.example.proyectointegradodef.musica.filtros

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadMusica

/**
 * List album adapter
 *
 * @property layoutResource
 * @property album
 * @constructor
 *
 * @param context
 */
class ListAlbumAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val album: ArrayList<ReadAlbum>):
ArrayAdapter<ReadAlbum>(context, layoutResource, album), Filterable{
    private var mAlbum: ArrayList<ReadAlbum> = album

    override fun getCount(): Int {
        return mAlbum.size
    }

    override fun getItemId(position: Int): Long {
        return mAlbum.get(position).id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = mAlbum[position].titulo
        return view
    }

    override fun getItem(position: Int): ReadAlbum? {
        return mAlbum.get(position)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                mAlbum = filterResults.values as ArrayList<ReadAlbum>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                    album
                else
                    album.filter {
                        it.titulo.toLowerCase().contains(queryString)
                    }
                return filterResults
            }
        }
    }
}