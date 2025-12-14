package com.example.amap_sim.base

interface BaseView {
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
}