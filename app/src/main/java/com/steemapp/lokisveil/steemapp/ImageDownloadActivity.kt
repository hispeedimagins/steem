package com.steemapp.lokisveil.steemapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.steemapp.lokisveil.steemapp.DataHolders.ImageDownloadDataHolder
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.ImagePickersWithHelpers
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface

import kotlinx.android.synthetic.main.activity_image_download.*
import kotlinx.android.synthetic.main.content_image_download.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class ImageDownloadActivity : AppCompatActivity(),GlobalInterface {

    var currentMainImage: ImageDownloadDataHolder? = null
    var animatedVec : AnimatedVectorDrawableCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@ImageDownloadActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_download)
        setSupportActionBar(toolbar)
        animatedVec = AnimatedVectorDrawableCompat.create(this,R.drawable.animated_loader)
        val adapter = AllRecyclerViewAdapter(this,ArrayList(),bottomList,null,
                AdapterToUseFor.imageDownload,this)
        bottomList.adapter = adapter
        bottomList.itemAnimator = DefaultItemAnimator()


        adapter.add(createList(CentralConstantsOfSteem.getInstance().urls))
        setMainImage(createImageDownloadClass(intent.getStringExtra(CentralConstants.ImageDownloadUrlPasser)))
    }

    fun createImageDownloadClass(url:String):ImageDownloadDataHolder{
        return ImageDownloadDataHolder(url,"","")
    }

    fun createList(data:List<String>):List<ImageDownloadDataHolder>{
        val arl = ArrayList<ImageDownloadDataHolder>()
        for(item in data){
            arl.add(createImageDownloadClass(item))
        }
        return arl
    }

    fun setMainImage(data:ImageDownloadDataHolder){
        currentMainImage = data
        val optionss = RequestOptions()
                .placeholder(animatedVec)
                .centerInside()
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
        animatedVec?.start()
        Glide.with(this).load(data.url).apply(optionss)
                .into(main_image)
    }


    override fun getObjectMine(): Any {
        return 0
    }

    override fun getContextMine(): Context {
        return this
    }

    override fun getActivityMine(): Activity {
        return this@ImageDownloadActivity
    }

    override fun objectClicked(data: Any?) {
        if(data != null && data is ImageDownloadDataHolder){
            setMainImage(data)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    /**
     * Called when easy permissions has got all the permissions
     * Then we check for our usecase and either proceed for request again
     */
    @AfterPermissionGranted(ImagePickersWithHelpers.RC_CAMERA_STORAGE_PERMS)
    private fun methodRequiresTwoPermission() {
        val perms = ImagePickersWithHelpers.getExternalStoragePermissions()
        if (EasyPermissions.hasPermissions(this, *perms)) {

        } else {
            // Do not have permissions, request them now
            ImagePickersWithHelpers.getCameraPermission(applicationContext,this,this@ImageDownloadActivity,perms)
            return
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.image_download_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_download ->{
                if(currentMainImage != null){
                    ImagePickersWithHelpers.saveImage(applicationContext,this,this@ImageDownloadActivity,currentMainImage?.url!!,Date().time.toString())

                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
