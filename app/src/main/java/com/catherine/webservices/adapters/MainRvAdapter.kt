package com.catherine.webservices.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catherine.webservices.R
import kotlinx.android.synthetic.main.rv_main_item.view.*

/**
 * Created by Catherine on 2017/7/31.
 */
class MainRvAdapter(var ctx: Context, var data: List<String>, var onClickListener: OnItemClickListener) : RecyclerView.Adapter<MainRvAdapter.MainRvHolder>() {
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }


    override fun onBindViewHolder(holder: MainRvHolder, position: Int) {
        val onClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.tv_title -> onClickListener?.onItemClick(view, position)
            }
        }
        holder.itemView.tv_title.text = data[position]
        holder.itemView.tv_title.setOnClickListener(onClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainRvHolder {
        val holder = MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_main_item, parent, false))
        return holder
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class MainRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}