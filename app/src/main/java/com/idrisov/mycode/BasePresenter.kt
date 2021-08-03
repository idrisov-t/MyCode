package com.idrisov.mycode

import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import moxy.MvpPresenter
import moxy.MvpView
import kotlin.coroutines.CoroutineContext

abstract class BasePresenter<V : MvpView> : MvpPresenter<V>() {

    protected val compositeDisposable by lazy { CompositeDisposable() }

    open fun onBackPressed() {}
    open fun onResume() {}

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}