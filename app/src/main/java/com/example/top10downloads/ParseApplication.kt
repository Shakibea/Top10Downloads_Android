package com.example.top10downloads

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.lang.Exception

class ParseApplication {
    private val TAG = "ParseApplication"
    val applications = ArrayList<FeedEntry>()

    private var status = true
    private var inEntry = false
    private var textValue = ""
    private var gotImage = false

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "Parse is Called")

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()

            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.toLowerCase()

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        Log.d(TAG, "Parse: Starting Tag for $tagName")
                        if (tagName == "entry") {
                            inEntry = true

                            //setting image
                        } else if ((tagName == "image") && inEntry) {
                            val imageResolution = xpp.getAttributeValue(null, "height")
                            if (imageResolution.isNotEmpty()) {
                                gotImage = imageResolution == "53"
                            }
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
                        Log.d(TAG, "Parse: Ending tag for $tagName")
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()
                                }

                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> if (gotImage) currentRecord.imageUrl = textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }

            //show in logcat separation
//            for (app in applications) {
//                Log.d(TAG, "****************")
//                Log.d(TAG, app.toString())
//            }

        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return true
    }
}