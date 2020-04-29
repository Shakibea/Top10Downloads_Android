package com.example.top10downloads

import android.nfc.Tag
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

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
            Release Date =$releaseDate
            Summary = $summary
            Image Url = $imageUrl
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate Called")
        val downloadFilesTask = DownloadFilesTask()
        downloadFilesTask.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG, "onCreate Done")
    }


    companion object {
        private class DownloadFilesTask : AsyncTask<String, Double, String>(){

            val TAG = "DownloadTask"
            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//                Log.d(TAG, "onPostExecute: parameter is $result")
                val parseApplication = ParseApplication()
                parseApplication.parse(result)
            }

            override fun doInBackground(vararg params: String?): String {
                Log.d(TAG, "DIG: ${params[0]}")
                val rssFeed = DownloadXML(params[0])
                if(rssFeed.isEmpty()){
                    Log.e(TAG, "doInBackground: Error connection")
                }
                return rssFeed
            }

            private fun DownloadXML(urlPath: String?) : String {
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

