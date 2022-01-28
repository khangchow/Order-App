package com.foodorder.fooder.restaurantorder.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.*


object FileUtils {

    private const val FILE_NAME = "InternalStorage"

    private fun getInternalStorage(context: Context): File {
        val internalDirectory = context.getDir(FILE_NAME, Context.MODE_PRIVATE)
        if (!internalDirectory.exists()) {
            internalDirectory.mkdirs()
        }
        return internalDirectory
    }
    
    fun getPathFile(context: Context, fileName: String) =
        getInternalStorage(context).path + File.separatorChar + fileName
    
    fun deleteFile(path: String): Boolean {
        val file = File(path)
        if (file.exists()) {
            return file.delete()
        }
        return true
    }
    
    fun getBitmapImageFromUri(context: Context, selectedPhotoUri: Uri): Bitmap {
//        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//            MediaStore.Images.Media.getBitmap(
//                context.contentResolver,
//                selectedPhotoUri
//            )
//        } else {
//            val source = ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
//            ImageDecoder.decodeBitmap(source)
//        }
        return MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            selectedPhotoUri
        )
    }
    
    fun saveToInternalStorage(
        context: Context,
        bitmapInput: Bitmap,
        outputDirection: String,
        outputFileName: String? = null
    ): String? {
        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir(outputDirection, Context.MODE_PRIVATE)
        // Create imageDir
        val output = File(
            directory,
            if (outputFileName.isNullOrEmpty())
                "${System.currentTimeMillis()}.png" else outputFileName
        )
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(output)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapInput.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return output.path
    }
    
    fun saveToInternalStorage(
        bitmapInput: Bitmap,
        outputFile : File
    ): String? {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(outputFile)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapInput.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return outputFile.path
    }
    
    fun copyFile(pathOutput: String, inputPath: String): Boolean {
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(inputPath))
            bos = BufferedOutputStream(FileOutputStream(pathOutput, false))
            val buf = ByteArray(1024)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) !== -1)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                bis?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }
        return true
    }

}