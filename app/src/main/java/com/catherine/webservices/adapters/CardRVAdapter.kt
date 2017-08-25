package com.catherine.webservices.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catherine.webservices.R
import kotlinx.android.synthetic.main.rv_card.view.*

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class CardRVAdapter(private var ctx: Context, private var titles: List<String>, private var subtitles: List<String>, private var onClickListener: OnItemClickListener) : RecyclerView.Adapter<CardRVAdapter.MainRvHolder>() {
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }


    override fun onBindViewHolder(holder: MainRvHolder, position: Int) {
        val onClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.cv -> onClickListener.onItemClick(view, position)
            }
        }
        holder.itemView.tv_title.text = titles[position]
        holder.itemView.tv_subtitle.text = subtitles[position]
        holder.itemView.cv.setOnClickListener(onClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainRvHolder {
        return MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_card, parent, false))
    }

    override fun getItemCount(): Int {
        return titles.size
    }


    class MainRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}