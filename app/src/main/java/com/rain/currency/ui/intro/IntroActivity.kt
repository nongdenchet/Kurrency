package com.rain.currency.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.OnClick
import com.rain.currency.R
import com.rain.currency.ui.setting.SettingActivity
import com.rain.currency.utils.hasOverlayPermission
import com.rain.currency.utils.toOverlayPermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class IntroActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        if (hasOverlayPermission(this)) {
            finishIntro()
        } else {
            setContentView(R.layout.activity_intro)
            ButterKnife.bind(this)
        }
    }

    override fun onResume() {
        super.onResume()
        disposables.add(Observable.interval(500, TimeUnit.MILLISECONDS)
            .startWith(0)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (hasOverlayPermission(this)) {
                    finishIntro()
                }
            }, Timber::e)
        )
    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    private fun finishIntro() {
        startActivity(Intent(this, SettingActivity::class.java))
        finish()
    }

    @OnClick(R.id.btnGrandPermission)
    fun onGrandPermissionClick() {
        toOverlayPermission(this)
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
