package com.example.top10downloads

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name = ""
    var artist = ""
    var releaseDate = ""
    var summary = ""
    var imageUrl = ""

    override fun toString(): String {
        return """
            Name = $name
            Artist = $artist
            Release Date = $releaseDate
            Summary = $summary
            Image Url = $imageUrl
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    //    private val downloadFilesTask by lazy { DownloadFilesTask(this, xmlListView) }
    private var downloadFilesTask: DownloadFilesTask? = null

    private var feedURL: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10


    private var feedCacheUrl = "INVALIDATED"
    private val STATE_URL = "feedUrl"
    private val STATE_LIMIT = "feedLimit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate Called")

        if (savedInstanceState != null) {
            feedURL = savedInstanceState.getString(STATE_URL)!!
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }

        downloadUrl(feedURL.format(feedLimit))
        Log.d(TAG, "onCreate Done")

        //used before downloadUrl method created
//        Log.d(TAG, "onCreate Called")
//        downloadFilesTask.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
//        Log.d(TAG, "onCreate Done")
    }

    private fun downloadUrl(feedUrl: String) {
        if (feedUrl != feedCacheUrl) {
            Log.d(TAG, "downloadUrl Starting AsyncTask")
            downloadFilesTask = DownloadFilesTask(this, xmlListView)
            downloadFilesTask?.execute(feedUrl)
            feedCacheUrl =
                feedUrl  //storing, cos if user again check the same url, then else block will work
            Log.d(TAG, "downloadUrl Done")
        } else {
            Log.d(TAG, "donwloadUrl- Url not Downloaded")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedURL)
        outState.putInt(STATE_LIMIT, feedLimit)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feed_menu, menu)
        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10apps)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25apps)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mnuFree ->
                feedURL =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid ->
                feedURL =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedURL =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnu10apps, R.id.mnu25apps -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected ${item.title} setting $feedLimit")
                }
                Log.d(TAG, "onOptionsItemSelected ${item.title} setting unchange")
            }
            R.id.mnuRefresh ->
                feedCacheUrl = "INVALIDATED"
            else ->
                return super.onOptionsItemSelected(item)
        }
        downloadUrl(feedURL.format(feedLimit))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadFilesTask?.cancel(true)
    }

    companion object {
        private class DownloadFilesTask(context: Context, listView: ListView) :
            AsyncTask<String, Double, String>() {

            var propContext: Context by Delegates.notNull()
            var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            val TAG = "DownloadTask"
            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//                Log.d(TAG, "onPostExecute: parameter is $result")
                val parseApplication = ParseApplication()
                parseApplication.parse(result)

//                val arrayAdapter = ArrayAdapter(propContext, R.layout.item_list, parseApplication.applications)
//                propListView.adapter = arrayAdapter

                val feedAdapter =
                    FeedAdapter(propContext, R.layout.list_record, parseApplication.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg params: String?): String {
                Log.d(TAG, "DIG: ${params[0]}")
                val rssFeed = downloadXML(params[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error connection")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
//            private fun DownloadXML(urlPath: String?) : String {
//                val xml = StringBuilder()
//
//                try {
//                    val url = URL(urlPath)
//                    val connection : HttpURLConnection = url.openConnection() as HttpURLConnection
//                    val response = connection.responseCode
//                    Log.d(TAG, "DownloadXML: your response code was $response")
//
////            val inputStream = connection.inputStream
////            val inputStreamReader = InputStreamReader(inputStream)
////            val bufferReader = BufferedReader(inputStreamReader)
////                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
////
////                    val inputArray = CharArray(600)
////                    var CharReader = 0
////                    while(CharReader >= 0){
////                        CharReader = reader.read(inputArray)
////                        if(CharReader > 0){
////                            xml.append(String(inputArray, 0, CharReader))
////                        }
////                    }
////                    reader.close()
//                    //functional way to use buffer reader......
//                    connection.inputStream.buffered().reader().use { xml.append(it.readText()) }
//
//                    Log.d(TAG, "Receive ${xml.length} byte")
//                    return xml.toString()
//
////                } catch (e: MalformedURLException){
////                    Log.e(TAG, "DownloadXML: Invalid URL ${e.message}")
////                } catch (e: IOException){
////                    Log.e(TAG, "DownloadXML: IO exception Reading data ${e.message}")
////                } catch (e: SecurityException){
////                    Log.e(TAG, "Security Exception. Need Permission? ${e.message}")
////                } catch (e: Exception){
////                    Log.e(TAG, "Unknown Exception ${e.message}")
//                } catch (e: Exception){
//                    val errorException: String? = when(e){
//                        is MalformedURLException ->  "DownloadXML: Invalid Url ${e.message}"
//                        is IOException -> "DownloadXML: IO exception ${e.message}"
//                        is SecurityException -> {e.printStackTrace()
//                            "DownloadXML: Security Exception. Need Permission? ${e.message}"
//                        }
//                        else -> "Unkown Error: ${e.message}"
//                    }
//                }
//
//                return "" //if error then it will return
//            }
        }
    }
}

