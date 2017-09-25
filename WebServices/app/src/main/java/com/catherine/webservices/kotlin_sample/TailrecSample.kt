package com.catherine.webservices.kotlin_sample

import java.math.BigInteger

/**
 * Created by Catherine on 2017/7/28.
 */

/**
 * 尾递归：只有递没有归，用tailrec关键字
 */
class TailrecSample {
    /**
     * 原始递归方法求阶乘，比如num代入1000000，得StackOverflowError
     */
    fun factorial1(num: Int): BigInteger {
        if (num == 0) return BigInteger.valueOf(1L)
        else return BigInteger.valueOf(num.toLong()).times(factorial1(num - 1))
        //等同于else return BigInteger.valueOf(num.toLong()) * factorial1(num - 1)
    }

    /**
     * 尾递归方法求阶乘
     */
    tailrec fun factorial2(num: Int, result: TailrecResult) {
        if (num == 0)
            result.value = result.value.times(BigInteger.valueOf(1L))
        else {
            result.value = result.value.times(BigInteger.valueOf(num.toLong()))
            factorial2(num - 1, result)
        }
    }
}