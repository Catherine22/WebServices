package com.catherine.webservices.kotlin_sample.player

import com.catherine.webservices.toolkits.CLog
import kotlin.properties.Delegates

/**
 * Created by Catherine on 2017/7/28.
 */
class Player {
    companion object {
        val TAG = "Player"
    }

    interface OnPlayStateChangedListener {
        fun OnPlayStateChanged(prevState: State, newState: State)
    }

    //nullable
    var onPlayStateChangedListener: OnPlayStateChangedListener? = null

    /**
     * 通过Delegates.observable可以捕捉状态的变化，并会叫回调接口
     */
    private var state: State by Delegates.observable(State.IDLE, { prop, old, new ->
        CLog.w(TAG, "$old -> $new")
        //?.表示只有在onPlayStateChangedListener不为空时才调用方法，Java中就是if(onPlayStateChangedListener!=null)...
        onPlayStateChangedListener?.OnPlayStateChanged(old, new)
    })

    private fun executeCmd(cmd: PlayerCmd) {
        when (cmd) {
            is PlayerCmd.Play -> {
                CLog.v(TAG, "Play ${cmd.url} from ${cmd.position}ms")
                state = State.PLAYING
            }
            is PlayerCmd.Pause -> {
                CLog.v(TAG, "Pause")
                state = State.PAUSED
            }
            is PlayerCmd.Resume -> {
                CLog.v(TAG, "Resume")
                state = State.PLAYING
            }
            is PlayerCmd.Stop -> {
                CLog.v(TAG, "Stop")
                state = State.IDLE
            }
            is PlayerCmd.Seek -> {
                CLog.v(TAG, "Seek to ${cmd.position}ms, state: $state")
            }
        }
    }

    /**
     * 给position一个默认值0，呼叫方法时就可以只带url一个参数
     */
    fun play(url: String, position: Long = 0) {
        val cmd = PlayerCmd.Play(url, position)
        executeCmd(cmd)
    }

    fun seekTo(position: Long) {
        val cmd = PlayerCmd.Seek(position)
        executeCmd(cmd)
    }

    fun pause() {
        executeCmd(PlayerCmd.Pause)
    }

    fun resume() {
        executeCmd(PlayerCmd.Resume)
    }

    fun stop() {
        executeCmd(PlayerCmd.Stop)
    }
}