package com.daniil.shevtsov.fightsimulator.common.util

import com.soywiz.kbignum.BigNum
import com.soywiz.kbignum.bi
import com.soywiz.kbignum.bn

fun Int.toBigInt() = bi
fun Long.toBigInt() = bi

fun Int.toBigNum() = bn
fun Long.toBigNum() = bn
fun Double.toBigNum() = bn

fun BigNum.toBigInt() = convertToScale(0).int
