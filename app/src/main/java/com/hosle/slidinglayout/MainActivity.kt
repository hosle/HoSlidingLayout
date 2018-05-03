package com.hosle.slidinglayout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hosle.slidinglayout.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_preview.setOnClickListener {
            sliding_view.openSlideBoard()
        }

        sliding_view.addOnSlidingListener(object : HoSlidingLayout.OnSlidingListener {
            override fun onStart(isOpenNow: Boolean) {
                tv_preview.text =if(isOpenNow){
                    "Start to close !"
                }else {
                    "Start to open !"
                }
            }

            override fun onSliding() {
                tv_preview.text = "You are sliding!"
            }

            override fun onOpen() {
                tv_preview.text = "Side panel is open!"
            }

            override fun onClose() {
                tv_preview.text = resources.getString(R.string.hint_close)
            }

        })
    }
}
