package com.deadrudolph.common_utils.extension

fun <T> MutableList<T>.setOrAdd(index: Int, element: T) {
    if(index in indices) set(index, element)
    else add(element)
}
