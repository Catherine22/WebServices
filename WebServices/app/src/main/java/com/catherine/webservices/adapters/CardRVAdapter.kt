package com.catherine.webservices.adapters

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catherine.webservices.MyApplication
import com.catherine.webservices.R
import com.catherine.webservices.entities.ProgressBarInfo
import com.catherine.webservices.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.rv_card.view.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class CardRVAdapter(private var ctx: Context, var images: List<String>?, var titles: List<String>, var subtitles: List<String>?, private var onClickListener: OnItemClickListener) : RecyclerView.Adapter<CardRVAdapter.MainRvHolder>() {
    companion object {
        val TAG = "CardRVAdapter"
    }

    private val handler = Handler(MyApplication.INSTANCE.calHandlerThread.looper)
    private val activity = ctx as Activity
    private var progressList = arrayOfNulls<ProgressBarInfo>(titles.size)

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

        if (images != null) {
            holder.itemView.iv_main.visibility = View.VISIBLE
            handler.post {
                try {
                    val url = URL(images!![position])
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    val input = connection.inputStream
                    val b = BitmapFactory.decodeStream(input)
                    activity.runOnUiThread { holder.itemView.iv_main.setImageBitmap(b) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            holder.itemView.iv_main.visibility = View.GONE
        }

        holder.itemView.tv_title.text = titles[position]

        if (subtitles != null)
            holder.itemView.tv_subtitle.text = subtitles!![position]
        holder.itemView.cv.setOnClickListener(listener1)
        holder.itemView.cv.setOnLongClickListener(listener2)

        if (progressList[position] == null || progressList[position]?.MAX_PROGRESS == -1 || progressList[position]?.cur_progress == -1)
            holder.itemView.pb.visibility = View.INVISIBLE
        else {
            holder.itemView.pb.visibility = View.VISIBLE
            holder.itemView.pb.max = progressList[position]!!.MAX_PROGRESS
            holder.itemView.pb.progress = progressList[position]!!.cur_progress

            if (progressList[position]!!.cur_progress == progressList[position]!!.MAX_PROGRESS)
                holder.itemView.pb.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainRvHolder {
        return MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_card, parent, false))
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    fun updateProgress(pos: Int, MAX: Int, cur: Int) {
        if (progressList[pos] == null)
            progressList[pos] = ProgressBarInfo(MAX, cur)

        progressList[pos]?.MAX_PROGRESS = MAX
        progressList[pos]?.cur_progress = cur


    }

    class MainRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}