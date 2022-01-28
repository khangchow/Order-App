package com.foodorder.fooder.restaurantorder.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.BuildConfig
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import com.foodorder.fooder.restaurantorder.databinding.ActivityPictureTakeBinding
import com.foodorder.fooder.restaurantorder.utils.Logger
import java.io.File

class PictureTakeActivity : BaseActivity() {
    companion object {
        const val RESULT_PICTURE_IMAGE_PATH = "RESULT_PICTURE_IMAGE_PATH"
        private const val PICTURE_IMAGE_PATH = "PICTURE_IMAGE_PATH"
        fun getIntent(context: Context, imagePath: String? = null) =
            Intent(context, PictureTakeActivity::class.java).apply {
                putExtra(PICTURE_IMAGE_PATH, imagePath)
            }
    }

    lateinit var binding: ActivityPictureTakeBinding

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    Logger.e("path uri take picture: $uri")
                    Glide.with(this@PictureTakeActivity).load(uri).error(R.drawable.ic_icon)
                        .into(binding.imagePreview)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                latestTmpUri = it
                Logger.e("path uri get content: $uri")
                Glide.with(this@PictureTakeActivity).load(it).error(R.drawable.ic_icon)
                    .into(binding.imagePreview)
            }
        }

    private var latestTmpUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureTakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            takeImageButton.setOnClickListener { takeImage() }
            selectImageButton.setOnClickListener { selectImageFromGallery() }

            val imagePath = intent.getStringExtra(PICTURE_IMAGE_PATH)
            Glide.with(this@PictureTakeActivity).load(imagePath).error(R.drawable.ic_icon)
                .into(imagePreview)

            toolbarCustom.setBackOnClick {
                setResult(Activity.RESULT_CANCELED, null)
                finish()
            }

            btnActionDone.setOnClickListener {
                if (latestTmpUri == null) {
                    setResult(Activity.RESULT_CANCELED, null)
                    finish()
                } else {
                    val intent = Intent().apply {
                        putExtra(
                            RESULT_PICTURE_IMAGE_PATH,
                            latestTmpUri.toString()
                        )
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch(arrayOf("image/*"))

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }


}