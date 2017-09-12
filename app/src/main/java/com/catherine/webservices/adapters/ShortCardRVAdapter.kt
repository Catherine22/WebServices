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
import com.catherine.webservices.toolkits.CLog
import kotlinx.android.synthetic.main.rv_short_card.view.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class ShortCardRVAdapter(private var ctx: Context, var images: List<String>?, var titles: List<String>, var subtitles: List<String>?, private var onClickListener: OnItemClickListener) : RecyclerView.Adapter<ShortCardRVAdapter.MainRvHolder>() {
    companion object {
        val TAG = "ShortCardRVAdapter"
    }

    private val handler = Handler(MyApplication.INSTANCE.calHandlerThread.looper)
    private val activity = ctx as Activity

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

        if (images != null) {
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
        }

        holder.itemView.tv_title.text = titles[position]

        if (subtitles != null)
            holder.itemView.tv_subtitle.text = subtitles!![position]
        holder.itemView.cv.setOnClickListener(onClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainRvHolder {
        return MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_short_card, parent, false))
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class MainRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}