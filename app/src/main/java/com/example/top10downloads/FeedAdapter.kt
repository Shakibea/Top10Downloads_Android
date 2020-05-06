package com.example.top10downloads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_list.view.*

class ViewHolder(view: View) {

    val appName: TextView = view.findViewById(R.id.tvName)
    val appArtist: TextView = view.findViewById(R.id.tvArtist)
    val appSummary: TextView = view.findViewById(R.id.tvSummary)

}

class FeedAdapter(
    context: Context,
    private val resource: Int,
    private val applications: List<FeedEntry>
) : ArrayAdapter<FeedEntry>(context, resource) {

    val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(resource, parent, false)
            //using ViewHolder
            viewHolder = ViewHolder(view)
            view.tag =
                viewHolder        // tag use as a sign to.. knock android that here is something
        } else {
            view = convertView
            //using ViewHolder
            viewHolder = view.tag as ViewHolder
        }

//        val appName: TextView = view.findViewById(R.id.tvName)
//        val appArtist: TextView = view.findViewById(R.id.tvArtist)
//        val appSummary: TextView = view.findViewById(R.id.tvSummary)

        fun abbreviateString(input: String, maxLength: Int): String? {
            return if (input.length <= maxLength) input else input.substring(
                0,
                maxLength - 3
            ) + "..."
        }

        val getPosition = applications[position]
        viewHolder.appName.text = getPosition.name
        viewHolder.appArtist.text = getPosition.artist
        val shortenSummary = getPosition.summary
        viewHolder.appSummary.text = abbreviateString(shortenSummary, 250)

        return view
    }

    override fun getCount(): Int {
        return applications.size
    }
}