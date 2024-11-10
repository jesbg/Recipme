package com.example.recipme

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import android.widget.Toast
import java.io.File
import java.security.SecureRandom
import java.util.*

fun Any?.toast(context: Context, length: Int = Toast.LENGTH_SHORT) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, toString(), length).show()
    }
}

private const val AUTO_ID_LENGTH = 20

private const val AUTO_ID_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

private val rand: Random = SecureRandom()

fun autoId(): String {
    // val rand: Random = SecureRandom()
    val builder = StringBuilder()
    val maxRandom = AUTO_ID_ALPHABET.length
    for (i in 0 until AUTO_ID_LENGTH) {
        builder.append(AUTO_ID_ALPHABET[rand.nextInt(maxRandom)])
    }
    return builder.toString()
}

fun getMimeType(context: Context, uri: Uri): String? {
    if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {

        val mime = MimeTypeMap.getSingleton()
        return mime?.getExtensionFromMimeType(context.contentResolver.getType(uri))
    } else {
        return MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
    }
}