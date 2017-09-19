package com.catherine.webservices.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catherine.webservices.R
import com.catherine.webservices.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.rv_text_card.view.*

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class TextCardRVAdapter(private var ctx: Context, var contents: List<String>?, var titles: List<String>, var subtitles: List<String>?, private var onClickListener: OnItemClickListener) : RecyclerView.Adapter<TextCardRVAdapter.MainRvHolder>() {
    companion object {
        val TAG = "TextCardRVAdapter"
    }

    override fun onBindViewHolder(holder: MainRvHolder, position: Int) {
        val listener1 = View.OnClickListener { view ->
            when (view.id) {
                R.id.cv -> onClickListener.onItemClick(view, position)
            }
        }
        val listener2 = View.OnLongClickListener { view ->
            when (view.id) {
                R.id.cv -> onClickListener.onItemLongClick(view, position)
            }
            false
        }

        holder.itemView.tv_title.text = titles[position]

        if (contents != null && contents!!.size > position && !TextUtils.isEmpty(contents!![position])) {
            holder.itemView.tv_main.visibility = View.VISIBLE
            holder.itemView.tv_main.text = contents!![position]
        } else
            holder.itemView.tv_main.visibility = View.GONE

        if (subtitles != null && subtitles!!.size > position && !TextUtils.isEmpty(subtitles!![position]))
            holder.itemView.tv_subtitle.text = subtitles!![position]
        holder.itemView.cv.setOnClickListener(listener1)
        holder.itemView.cv.setOnLongClickListener(listener2)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainRvHolder {
        return MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_text_card, parent, false))
    }

    override fun getItemCount(): Int {
        return titles.size
    }


    class MainRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}