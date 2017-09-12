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
import com.catherine.webservices.network.MyHttpURLConnection
import kotlinx.android.synthetic.main.rv_short_card.view.*
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import com.catherine.webservices.security.Encryption
import com.catherine.webservices.toolkits.CLog
import com.jakewharton.disklrucache.DiskLruCache
import java.io.BufferedOutputStream
import java.util.*


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
    private var diskLruCache: DiskLruCache? = null

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

        if (subtitles != null)
            holder.itemView.tv_subtitle.text = subtitles!![position]

        if (images != null) {
            handler.post {
                try {
                    val snapShot = diskLruCache?.get(Encryption.doMd5Safely(images!![position].byteInputStream()))
                    if (snapShot != null) {
                        if (subtitles != null) {
                            val bitmap = BitmapFactory.decodeStream(snapShot.getInputStream(0))
                            activity.runOnUiThread {
                                CLog.d(TAG,"cache")
                                if (subtitles != null) {
                                    (subtitles as ArrayList)[position] = "cache"
                                    holder.itemView.tv_subtitle.text = subtitles!![position]
                                }
                                holder.itemView.iv_main.setImageBitmap(bitmap)
                            }
                        }
                    } else {
                        CLog.d(TAG,"fresh")
                        val url = URL(images!![position])
                        val conn = url.openConnection() as HttpURLConnection
                        conn.connect()
                        val input = conn.inputStream
                        val bitmap = BitmapFactory.decodeStream(input)
                        activity.runOnUiThread {
                            holder.itemView.iv_main.setImageBitmap(bitmap)
                        }


                        val bis = BufferedInputStream(input, MyHttpURLConnection.MAX_CACHE_SIZE)
                        val editor: DiskLruCache.Editor? = diskLruCache?.edit(Encryption.doMd5Safely(images!![position].byteInputStream()))
                        val bos = BufferedOutputStream(editor?.newOutputStream(0), MyHttpURLConnection.MAX_CACHE_SIZE)
                        var b = -1
                        while (true) {
                            b = bis.read()
                            if (b != -1)
                                bos.write(b)
                            else
                                break
                        }
                        editor?.commit()
                        bis.close()
                        bos.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                activity.runOnUiThread {
                    if (subtitles != null) {
                        (subtitles as ArrayList)[position] = "fresh"
                        holder.itemView.tv_subtitle.text = subtitles!![position]
                    }
                }
            }
        }
        holder.itemView.cv.setOnClickListener(onClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainRvHolder {
        val version = ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionCode
        diskLruCache = DiskLruCache.open(MyApplication.INSTANCE.getDiskCacheDir("image"), version, 1, MyHttpURLConnection.MAX_CACHE_SIZE.toLong())
        return MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_short_card, parent, false))
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class MainRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}