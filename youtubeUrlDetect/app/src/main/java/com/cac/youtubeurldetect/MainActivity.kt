package com.cac.youtubeurldetect

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.cac.youtubeurldetect.adapter.ItemListAdapter
import com.commit451.youtubeextractor.YouTubeExtraction
import com.commit451.youtubeextractor.YouTubeExtractor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins.onError
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val YOUTUBE_ID_KEY = "v"
    val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    val adapter: ItemListAdapter by lazy {
        ItemListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()

        generate_button.setOnClickListener {
            var yotubeLink = youtube_link_edit_text.text.toString()
            getYoutubeLinkId(yotubeLink)
        }
    }

    fun initRecyclerView() {
        var linearLayoutManager = LinearLayoutManager(this@MainActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
    }

    fun getYoutubeLinkId(link: String) {
        try {
            val youtubeurl = Uri.parse(link)
            var youtubeId = youtubeurl.getQueryParameter(YOUTUBE_ID_KEY)
            getYoutubeLinkVideoPath(youtubeId)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("id not found")
        }
    }

    fun getYoutubeLinkVideoPath(id : String) {
        val extractor = YouTubeExtractor.Builder()
            .build()

        compositeDisposable.add(
            extractor.extract(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ extraction ->
                    bindVideoResult(extraction)
                }, { t ->
                    onError(t)
                    showToast(t.message!!)
                })
        )

    }

    fun showToast(message : String) {
        Toast.makeText(applicationContext, "id not found", Toast.LENGTH_SHORT).show()
    }

    fun bindVideoResult(extractor: YouTubeExtraction) {
        extractor.videoStreams.let {
            var urlList = Observable.fromIterable(it).map { it.url }.toList().blockingGet()
            adapter.setList(urlList)
        }

        adapter.itemClick.subscribe { it!!.let {
            triggerBrowser(it)
        } }
    }

    fun triggerBrowser(link : String) {
        var browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(link)
        startActivity(browserIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
