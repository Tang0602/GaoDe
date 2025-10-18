package com.example.GaoDe.base

interface BaseView {
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
}