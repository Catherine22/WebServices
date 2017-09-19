package com.catherine.webservices.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catherine.webservices.R
import com.catherine.webservices.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.rv_item.view.*

/**
 * Created by Catherine on 2017/7/31.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class RVAdapter(private var ctx: Context, var data: List<String>, private var onClickListener: OnItemClickListener) : RecyclerView.Adapter<RVAdapter.MainRvHolder>() {


    override fun onBindViewHolder(holder: MainRvHolder, position: Int) {
        val onClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.tv_title -> onClickListener.onItemClick(view, position)
            }
        }
        holder.itemView.tv_title.text = data[position]
        holder.itemView.tv_title.setOnClickListener(onClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainRvHolder {
        return MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class MainRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}