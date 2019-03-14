package com.insteem.ipfreely.steem.HelperClasses

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputLayout
import com.insteem.ipfreely.steem.Databases.ImageUploadedUrls
import com.insteem.ipfreely.steem.MiscConstants
import com.insteem.ipfreely.steem.R
import com.insteem.ipfreely.steem.SteemBackend.Config.ImageUpload.SteemImageUpload
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.AccountName
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import pub.devrel.easypermissions.EasyPermissions
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ImagePickersWithHelpers{
    companion object {

        // To check image capture event
        const val PICK_IMAGE_REQUEST = 1
        const val REQUEST_IMAGE_CAPTURE = 198
        const val RC_CAMERA_STORAGE_PERMS = 193

        // Reference of temp image file for changing profile photo.
        private var mCurrentPhotoUri: Uri? = null

        var onTakePick: onTakePick? = null

        fun setOnTakePicks(onTakePick: onTakePick?) {
            this.onTakePick = onTakePick
        }


        /**
         * Converts date,month and year to string
         * @param date date in int
         * @param month month in int
         * @param year year in int
         * @return string in format (date month year) month name in string
         */
        fun getDateData(date:Int,month:Int,year: Int):String{
            var fromMonthStr = getMonth(date,month,year)
            return "$date $fromMonthStr $year"

        }


        /**
         * converts the month in int to its corresponding name
         * @param date in int
         * @param month in int
         * @param year in int
         * @returns month name
         */
        fun getMonth(date:Int,month:Int,year: Int):String{
            var calIns = Calendar.getInstance()
            calIns.set(year,month,date)
            return calIns.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        }

        /**
         * Converts a url string to a browser intent
         * @param url the url to send to
         * @return Intent which redirects to the browser
         */
        fun getBrowserIntent(url:String):Intent{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            //i.action = Intent.ACTION_VIEW
            i.addCategory(Intent.CATEGORY_BROWSABLE)
            //i.data = Uri.parse(url)
            return i
        }



        /**
         * encode the image to base64 string for uploading
         * @param path the path of the image on the device
         */
        fun encodeImage(path:String):String
        {
            val imagefile = File(path)
            var fis : FileInputStream? = null
            try{
                fis = FileInputStream(imagefile)
            }catch(e: FileNotFoundException){
                e.printStackTrace()
            }
            val bm = BitmapFactory.decodeStream(fis)
            var baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG,100,baos)
            var b = baos.toByteArray()
            val encodedImage = android.util.Base64.encodeToString(b,android.util.Base64.DEFAULT) //(baos, Base64.DEFAULT)
            return encodedImage

        }




        fun GetPx(dp : Float,metrics: DisplayMetrics) : Int{

            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    metrics
            ).toInt()
        }

        /**
         * fetches the themed colour
         * @param context the current context
         * @param color the id of the colour to fetch
         * @return the themed colour id
         */
        fun getThemedColor(context:Context,color:Int):Int{
            var attrs  = intArrayOf(color)
            var ta = context?.obtainStyledAttributes(attrs)
            var textColorMineThemeint = ta?.getResourceId(0, android.R.color.black)
            ta?.recycle()
            return ContextCompat.getColor(context, textColorMineThemeint!!)
        }


        /**
         * fetches ucrop options
         * @param context the context to add colours to the options. nullable
         * @param allowRotate if rotation is to be blocked. default value is false
         * @return Ucrop.Options which have all the data set
         */
        fun getUcropOptions(context:Context,allowRotate:Boolean = false): UCrop.Options{
            var ur = UCrop.Options()
            if(!allowRotate) ur.setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
            if(context != null){
                ur.setCropFrameColor(getThemedColor(context,R.color.colorAccent))
                ur.setStatusBarColor(getThemedColor(context,R.color.colorPrimaryDark))
                ur.setToolbarColor(getThemedColor(context,R.color.colorPrimary))
            }

            return ur
        }

        /**
         * clears errors from a textinputlayout
         *
         */
        fun clearError(til: TextInputLayout, text: Editable? = null, lengthCheck:Int? = null, clear:Boolean = false){
            if(text != null && text.isNotEmpty()){
                if(lengthCheck == null) {
                    til.error = null
                    til.isErrorEnabled = false
                } else if(text.length >= lengthCheck) {
                    til.error = null
                    til.isErrorEnabled = false
                }
            } else if(clear){
                til.error = null
                til.isErrorEnabled = false
            }
        }

        fun setErrorTil(til: TextInputLayout, error:String){
            if(!til.isErrorEnabled) til.isErrorEnabled = true
            til.error = error
        }





        /**
         * Create a alert imagePickDialog which asks the selectingUserImage from where he'd
         * like to pick the image
         * @param applicationContext the application context
         * @param context the acitvity context
         * @param activity the acitvity
         * @return AlertDialog
         */
        fun createImagePickDialog(applicationContext: Context, context: Context,activity: Activity): AlertDialog {

            val items = arrayOf("Take from camera", "Select from gallery")
            if(activity is onTakePick) onTakePick = activity
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    context,
                    //android.R.layout.select_dialog_item,
                    R.layout.select_dialog_item_new,
                    items
            )

            val builder = AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(context))
            builder.setTitle("Select Image")
            builder.setAdapter(adapter) { dialog, item ->
                if (item == 0) {
                    // enableUpdateButton()
                    dispatchTakePictureIntent(applicationContext, context,activity)
                } else if (item == 1) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    // Start the Intent
                    activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
                }
            }

            return builder.show()
        }

        /**
         * fetches the permission array required for taking a picture
         * @return Array<String> with permissions to request.
         */
        fun getCameraPermissions():Array<String>{
            //return arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        /**
         * checks with easy permissions wether the user has granted the permission.
         * if not then we request them.
         * @param applicationContext the applicationContext is the main application context
         * @param context the context being run on
         * @param activity the activity which is requesting it
         * @return returns false if permissions need to be request else true
         */
        fun getCameraPermission(applicationContext: Context, context: Context,activity: Activity,
                                perm:Array<String> = getCameraPermissions()):Boolean{
            if (!EasyPermissions.hasPermissions(applicationContext, *perm)) {
                EasyPermissions.requestPermissions(activity, context.getString(R.string.camera_permission),
                        RC_CAMERA_STORAGE_PERMS, *perm)
                return false
            }
            return true
        }

        /**
         * fetches the permission array required for saving a picture
         * @return Array<String> with permissions to request.
         */
        fun getExternalStoragePermissions():Array<String>{
            return arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }



        /**
         * Starts the camera for taking a photo
         * @param applicationContext the application context
         * @param context the acitvity context
         * @param activity the acitvity
         */
        fun dispatchTakePictureIntent(applicationContext: Context, context: Context,activity: Activity) {
            // Check that we have permission to read images from external storage.
            //String perm = Manifest.permission.CAMERA;
            if(!getCameraPermission(applicationContext,context,activity)){
                return
            }

            var file = createTempFile(context)
            if(file == null) return
            mCurrentPhotoUri = FileProvider.getUriForFile(
                    context,
                    applicationContext.packageName + ".provider", file)//Uri.fromFile(file)

            // Create and launch the intent
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri)
            //takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Grant permission to camera (this is required on KitKat and below)
            val resolveInfos = applicationContext
                    .packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (info in resolveInfos) {
                val packageName = info.activityInfo.packageName
                applicationContext.grantUriPermission(packageName, mCurrentPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            //we create a uri from a file so it points to the correct location
            onTakePick?.setURI(Uri.fromFile(file))

            // Start picture-taking intent
            onTakePick?.startActivityForResults(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }


        /**
         * Creates a temporary image file.
         *
         * @return File
         * @throws IOException
         */
        @Throws(IOException::class)
        fun createTempFile(context: Context): File? {
            // Create an image file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(Date())

            val imageFileName = "JPEG_" + timeStamp + "_"

            val externalStorageState = Environment.getExternalStorageState()

            var image: File? = null
            if (externalStorageState == Environment.MEDIA_MOUNTED) {
                try {
                    val storageDir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    image = File.createTempFile(
                            imageFileName, /* prefix */
                            ".jpg", /* suffix */
                            storageDir      /* directory */
                    )
                } catch (e: Exception) {
                    image = File.createTempFile(imageFileName, ".jpg",
                            context.cacheDir)

                }

            }
            return image
        }





        /**
         * Starts the camera for taking a photo
         * @param applicationContext the application context
         * @param context the acitvity context
         * @param activity the acitvity
         */
        fun saveImage(applicationContext: Context, context: Context,activity: Activity,url:String,title:String) {
            // Check that we have permission to read images from external storage.
            //String perm = Manifest.permission.CAMERA;
            if(!getCameraPermission(applicationContext,context,activity, getExternalStoragePermissions())){
                return
            }

            startImageDownload(context,url,title)
        }



        fun getPublicAlbumStorageDir(albumName: String): File? {
            // Get the directory for the user's public pictures directory.
            val file = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName)
            if (!file?.mkdirs()) {
                Log.e("firecreation", "Directory not created")
                //return null
            }
            return file
        }


        /**
         * Checks the download progress in the android system and displays
         * a toast to the user which shows to progress of the download in percentage
         */
        fun startImageDownload(context: Context,url:String,title:String) {
            class someTask : AsyncTask<Void, Void, Long>() {
                override fun doInBackground(vararg params: Void?): Long {
                    var downloadRef = 0L
                    try{
                        val fileurl = url
                        val dl  = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val fileDir = getPublicAlbumStorageDir("steemer")
                        if(fileDir != null){
                            val file = File(fileDir, "$title.jpg")
                            val req = DownloadManager.Request(Uri.parse(fileurl))
                            req.setDestinationUri(Uri.fromFile(file))
                            req.setTitle(title)
                            req.setDescription("Image")
                            downloadRef = dl.enqueue(req)
                        }

                    } catch (ex:Exception){
                        Log.d("download ex",ex.message)
                    }


                    return downloadRef
                }

                override fun onPostExecute(result: Long) {
                    if(result != 0L){
                        Toast.makeText(context,context.getString(R.string.download_request_made), Toast.LENGTH_LONG).show()
                    }

                    super.onPostExecute(result)
                }

            }
            someTask().execute()


        }


        fun uploadImage(imageUri:Uri?,activity: Activity,inflater: LayoutInflater,username:String?,key:String?,editText: EditText?,comms:onTakePick){
            if(imageUri == null) return
            val picUri : Uri   = imageUri //data.data

            val filePath = imageUri.path
            val file = File(filePath)

            //val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(this@Post)
            val alertDialogBuilder = AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(activity))
            alertDialogBuilder.setTitle("Upload this image?")
            val dialogView : View = inflater.inflate(R.layout.popup_image_upload, null)
            alertDialogBuilder.setView(dialogView)
            val imagev = dialogView.findViewById<ImageView>(R.id.image)
            imagev.setImageURI(picUri)
            //val edittext = dialogView.findViewById<EditText>(R.id.name)
            alertDialogBuilder.setPositiveButton("ok") { diin, num ->
                comms.progress(View.VISIBLE)
                comms.imageOkClicked()


                class someTask() : AsyncTask<Void, Void, String>() {
                    override fun doInBackground(vararg params: Void?): String? {
                        //catch errors while signing and uploading the image and display them
                        try{
                            val result = SteemImageUpload.uploadImage(AccountName(username),key,file,filePath )
                            val db = ImageUploadedUrls(activity.applicationContext)
                            db.Insert(result)
                            return result
                        } catch (ex:Exception){
                            ex.printStackTrace()
                            activity.runOnUiThread {
                                Toast.makeText(activity.applicationContext,ex.message,Toast.LENGTH_LONG).show()
                            }
                        }
                        return null
                    }

                    override fun onPostExecute(result: String?) {


                        //check if the reult is not null before adding it to the database
                        if(result != null){
                            if(editText == null)
                                comms.imageProcessed(result)
                            else
                                addtexturl(editText,result,file.name)

                            comms.progress(View.GONE)
                        }
                        comms.imageProcessDoneClearVariables(result)
                        super.onPostExecute(result)
                    }
                }
                someTask().execute()


            }

            alertDialogBuilder.setNegativeButton("No") { diin, num ->

            }
            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()

            //imageView.setImageURI(picUri)
        }

        fun addtexturl(editText: EditText?, url:String, name:String){
            if(editText == null) return
            val image = "![$name]($url)"
            val start = Math.max(editText.selectionStart, 0)
            val end = Math.max(editText.selectionEnd, 0)
            editText.text.replace(Math.min(start, end), Math.max(start, end),
                    image, 0, image.length)
        }


    }



    interface onTakePick {
        /**
         * callback for the activity to start a camer intent
         * @param takePictureIntent the intent with which to start taking an image
         * @param requesT_IMAGE_CAPTURE the request id
         */
        fun startActivityForResults(takePictureIntent: Intent, requesT_IMAGE_CAPTURE: Int)

        /**
         * sets the image uri to the master activity
         * @param mCurrentPhotoUri the uri to deliver the the master activity
         */
        fun setURI(mCurrentPhotoUri: Uri?)

        fun progress(visibility:Int)

        fun imageOkClicked(){}
        fun imageProcessed(url:String){}
        fun imageProcessDoneClearVariables(url:String?){}
    }
}