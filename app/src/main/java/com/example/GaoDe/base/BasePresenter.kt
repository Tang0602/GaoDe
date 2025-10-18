package com.example.GaoDe.base

abstract class BasePresenter<V : BaseView> {
    protected var view: V? = null
    
    fun attachView(view: V) {
        this.view = view
    }
    
    fun detachView() {
        this.view = null
    }
    
    protected fun isViewAttached(): Boolean {
        return view != null
    }
}