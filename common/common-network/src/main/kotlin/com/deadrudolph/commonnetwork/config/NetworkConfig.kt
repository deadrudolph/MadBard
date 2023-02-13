package com.deadrudolph.commonnetwork.config

internal interface NetworkConfig {
    val connectTimeOutSeconds: Long
    val readTimeoutSeconds: Long
    val writeTimeoutSeconds: Long
}