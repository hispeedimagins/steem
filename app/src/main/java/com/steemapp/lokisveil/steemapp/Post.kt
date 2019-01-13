package com.steemapp.lokisveil.steemapp

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.github.clans.fab.FloatingActionMenu
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.ImageUploadedUrls
import com.steemapp.lokisveil.steemapp.Databases.beneficiariesDatabase
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Fragments.PreviewPost
import com.steemapp.lokisveil.steemapp.Fragments.WritePost
import com.steemapp.lokisveil.steemapp.HelperClasses.AddABeneficiary
import com.steemapp.lokisveil.steemapp.HelperClasses.GetDynamicAndBlock
import com.steemapp.lokisveil.steemapp.HelperClasses.ImagePickersWithHelpers
import com.steemapp.lokisveil.steemapp.HelperClasses.ImagePickersWithHelpers.Companion.RC_CAMERA_STORAGE_PERMS
import com.steemapp.lokisveil.steemapp.HelperClasses.ImagePickersWithHelpers.Companion.REQUEST_IMAGE_CAPTURE
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import com.steemapp.lokisveil.steemapp.Interfaces.BeneficiaryAddInterface
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.ImageUpload.SteemImageUpload
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.bottom_sheet_beneficiaries_view.*
import kotlinx.android.synthetic.main.content_post.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*
import java.util.regex.Pattern


class Post : AppCompatActivity() , GlobalInterface, BeneficiaryAddInterface,
ImagePickersWithHelpers.onTakePick{
    override fun startActivityForResults(takePictureIntent: Intent, requesT_IMAGE_CAPTURE: Int) {
        startActivityForResult(takePictureIntent, requesT_IMAGE_CAPTURE)
    }

    override fun setURI(mCurrentPhotoUri: Uri?) {
        imageUri = mCurrentPhotoUri
    }

    //open the beneficiary page after adding it to the db
    override fun AddedSuccessfully(dbid: Long) {
        beneficiaryBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
        beneficiaryrecycler?.smoothScrollToPosition(beneficiaryAdapter?.getSize()!! - 1)
    }

    override fun notifyRequestMadeSuccess() {

    }

    override fun notifyRequestMadeError() {

    }

    override fun getObjectMine(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContextMine(): Context {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityMine(): Activity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFabM(): FloatingActionMenu? {
        return fabmenu
    }

    //listener for changes
    override fun attachCheckboxListner(box:CheckBox?) {

        //checkdevs(box?.isChecked!!)
        box?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener({buttonView, isChecked ->

            checkdevs(isChecked)
        }))
    }

    //Use or remove develpers are beneficiaries
    fun checkdevs(isChecked:Boolean){
        var ben = getList(false)
        var i = 0
        for(item in ben!!){
            /*var tagsi = item.tags?.trim()?.split(" ")
            if(tags?.contains(item.tags)!!){
                item.usenow = 1
            }*/
            if(item.isdeveloper == 1){
                item.uncheckedbyuser = 0
                if(isChecked){
                    item.usenow = 1
                } else{
                    item.usenow = 0
                }

                beneficiaryAdapter?.notifyitemcchanged(i,item)
            }
            i++
        }
    }

    companion object {
        val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 100
    }

    var username : String? = null
    var key : String? = null
    /*internal var tabLayout: TabLayout? = null
    internal var viewPager: ViewPager? = null*/
    internal var writePost : WritePost? = null
    internal var previewPost : PreviewPost? = null
    internal var viewPagerAdapteradapter: ViewPagerAdapter? = null
    internal var beneficiaryrecycler : RecyclerView? = null
    internal var beneficiaryrecyclerswipecommon : swipecommonactionsclass? = null
    internal var beneficiaryAdapter : AllRecyclerViewAdapter? = null
    internal var beneficiaryBottomSheet : BottomSheetBehavior<LinearLayout>? = null
    val PICK_IMAGE_REQUEST = 1
    var filePath: String? = null
    var dbid : Int = -1
    var isedit: Boolean = false
    var permlinkedit:String? = null
    var categoryedit:String? = null
    var edittitle:String? = null
    var edittags :String? = null
    var editpost:String? = null
    var imageUri:Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@Post)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(toolbar)


        //create the bottom sheet
        beneficiaryBottomSheet = BottomSheetBehavior.from(findViewById<LinearLayout>(R.id.bottomsheetmain))

        beneficiaryrecycler = findViewById(R.id.beneficiaryrecycler)
        //beneficiaryrecyclerswipecommon = swipecommonactionsclass(v.findViewById(R.id.beneficiaryrecycler_swipe_refresh_layout))
        beneficiaryAdapter = AllRecyclerViewAdapter(this@Post, ArrayList(), beneficiaryrecycler!!, null, AdapterToUseFor.beneficiaries)
        beneficiaryrecycler?.itemAnimator = DefaultItemAnimator()
        beneficiaryrecycler?.adapter = beneficiaryAdapter
        beneficiaryBottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
        //beneficiaryAdapter?.beneficiaryHelperFunctionsOb?.addDummies()
        var sd = beneficiariesDatabase(this@Post)
        beneficiaryAdapter?.beneficiaryHelperFunctionsOb?.add(sd.GetAllQuestions())
        beneficiary_add_fab.setOnClickListener { view ->
            //var add = AddABeneficiary(this@Post,this@Post)
            beneficiaryBottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
        }


        //events for buttons
        Add_menu_item.setOnClickListener { view ->
            var add = AddABeneficiary(this@Post,this@Post)
            fabmenu.close(true)
        }

        View_menu_item.setOnClickListener { view ->
            beneficiaryBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
            fabmenu.close(true)
        }

        post_menu_item.setOnClickListener { view ->
            fabmenu.close(true)
            //val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(MiscConstants.ApplyMyThemePopUp(this@Post))
            val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(this@Post))
            alertDialogBuilder.setTitle("Post the article?")
            alertDialogBuilder.setMessage("This action is not reversible")

            alertDialogBuilder.setPositiveButton("ok") { diin, num ->


                val mine = MakeOperationsMine()
                var tags = writePost?.gettags()?.trim()
                var title = writePost?.gettitle()?.trim()
                var content = writePost?.getedittext()?.trim()
                var ben = getList()
                var sd = beneficiariesDatabase(this@Post)
                for(item in ben!!){
                    if(item.isbuiltin == 0){
                        sd.update(item)
                    }

                }
                var posttr = true
                if(!(content != null && !content.isNullOrEmpty() && content != "")){
                    posttr = false
                    Toast.makeText(applicationContext,"Content cannot be empty",Toast.LENGTH_LONG).show()
                }
                if(!(title != null && !title.isNullOrEmpty() && title != "")){
                    posttr = false
                    Toast.makeText(applicationContext,"Title cannot be empty",Toast.LENGTH_LONG).show()
                }
                if(!(tags != null && !tags.isNullOrEmpty() && tags != "")){
                    posttr = false
                    Toast.makeText(applicationContext,"Tags cannot be empty",Toast.LENGTH_LONG).show()
                }
                if(posttr){

                    try{
                        if(isedit){
                            //a different function called if it is for editing a post
                            //different enum used for GetDynamicAndBlock
                            var ops = mine.updatePost(AccountName(username),Permlink(permlinkedit),title,content, tags?.split(" ")?.toTypedArray())
                            val block = GetDynamicAndBlock(applicationContext, null, 0, ops, "posted $title", MyOperationTypes.edit_comment, writePost?.progressBar, this@Post)
                            block.GetDynamicGlobalProperties()
                        } else{
                            val ops = mine.createPost(AccountName(username), title,content,tags?.split(" ")?.toTypedArray(),ben)

                            val block = GetDynamicAndBlock(applicationContext, null, 0, ops, "posted $title", MyOperationTypes.post, writePost?.progressBar, this@Post)
                            block.GetDynamicGlobalProperties()
                        }
                    } catch(ex:Exception){
                        //display any errors occured while signing and broadcasting the operation instead of crashing
                        Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
                    }


                }


            }

            alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

            })
            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }

        val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences.getString(CentralConstants.username, null)
        key = sharedPreferences.getString(CentralConstants.key, null)

        if(intent != null && intent.extras != null){
            dbid = intent.extras.getInt("db",-1)
            if(dbid == -1){
                dbid = intent.extras.getLong("db",-1).toInt()
            }
            isedit = intent.extras.getBoolean("isedit",false)
            permlinkedit = intent.extras.getString("permlink",null)
            categoryedit = intent.extras.getString("category",null)

            //edittitle =
            //useOtherGuy = intent.extras.getBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,false)
        }

        if(savedInstanceState != null){
            dbid = savedInstanceState.getInt("db",-1)
            isedit = savedInstanceState.getBoolean("isedit",false)
            permlinkedit = savedInstanceState.getString("permlink",null)
            categoryedit = savedInstanceState.getString("category",null)
        }



        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.setCurrentItem(tab.position)
                if(tab.position == 1){
                    previewPost?.display(writePost?.getedittext() as String,writePost?.gettitle())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        mysetup()


        //Call back for modal bottom sheet states
        beneficiaryBottomSheet?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //check the tags and see if we can enable a tag beneficiary
                        var vtag = writePost?.gettags()?.trim()
                        if(vtag?.isNotEmpty()!! && vtag?.isNotBlank()!!){
                            var ben = getList(false)
                            var tags = "\\b(?:${writePost?.gettags()?.trim()?.replace(" ","|")})\\b"
                            val pattern = Pattern.compile(
                                    tags,
                                    Pattern.CASE_INSENSITIVE)
                            var i = 0
                            for(item in ben!!){
                                /*var tagsi = item.tags?.trim()?.split(" ")
                                if(tags?.contains(item.tags)!!){
                                    item.usenow = 1
                                }*/
                                if(pattern.matcher(item.tags?.trim()).find() && item.uncheckedbyuser == 0){
                                    item.usenow = 1
                                    beneficiaryAdapter?.notifyitemcchanged(i,item)
                                }
                                i++
                            }
                        }

                        //fab.hide()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //fab.show()
                        beneficiaryBottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {

            }
        })


        if (ContextCompat.checkSelfPermission(this@Post, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this@Post,
                    arrayOf(READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE)
        }

    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("dbid",dbid)
        outState.putBoolean("isedit",isedit)
        outState.putString("permlink",permlinkedit)
        outState.putString("category",categoryedit)
    }


    //sort the list by default and return it, alphabetically
    fun getList(getSorted:Boolean = true):List<FeedArticleDataHolder.beneficiariesDataHolder>?{
        var arl = ArrayList<FeedArticleDataHolder.beneficiariesDataHolder>()
        for(item in beneficiaryAdapter?.getList()!!){
            arl.add(item as FeedArticleDataHolder.beneficiariesDataHolder)
        }
        if(getSorted) return arl.sortedWith(compareBy({it.username}))
        else return arl
        //return beneficiaryAdapter?.getList()?.sortBy(compareBy<FeedArticleDataHolder.beneficiariesDataHolder> { it.username })
    }


    private fun imageBrowse() {
        //we make a call to our common picker
        ImagePickersWithHelpers.createImagePickDialog(applicationContext,this,this@Post)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //check if activity result is ok
        if (resultCode == Activity.RESULT_OK) {
            //if pickimage request then we open the image editor
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                //we create our own temp file
                imageUri = Uri.fromFile(ImagePickersWithHelpers.createTempFile(this))
                //pass data to ucrop
                UCrop.of(data.data, imageUri!!)
                        .withOptions(ImagePickersWithHelpers.getUcropOptions(this@Post))
                        .start(this)
            } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
                //else we begin the upload dialog
                imageUri = UCrop.getOutput(data)
                uploadImage()
            }

        }

        //check if it is a image click request code, then launch the editor again
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //pass data to ucrop
            //pass data to ucrop
            UCrop.of(imageUri!!, imageUri!!)
                    .withOptions(ImagePickersWithHelpers.getUcropOptions(this@Post))
                    .start(this)
        }
         else if (resultCode == UCrop.RESULT_ERROR) {
            if(data != null){
                val cropError = UCrop.getError(data);
            }
        }




    }


    fun uploadImage(){
        if(imageUri == null) return
        val picUri : Uri   = imageUri!! //data.data

        filePath = imageUri?.path
        var file = File(filePath)

        //val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(this@Post)
        val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(this@Post))
        alertDialogBuilder.setTitle("Upload this image?")

        val inflater = layoutInflater
        val dialogView : View = inflater.inflate(R.layout.popup_image_upload, null)
        alertDialogBuilder.setView(dialogView)
        val imagev = dialogView.findViewById<ImageView>(R.id.image)
        imagev.setImageURI(picUri)
        //val edittext = dialogView.findViewById<EditText>(R.id.name)
        alertDialogBuilder.setPositiveButton("ok") { diin, num ->
            writePost?.progress(View.VISIBLE)



            class someTask() : AsyncTask<Void, Void, String>() {
                override fun doInBackground(vararg params: Void?): String? {
                    //catch errors while signing and uploading the image and display them
                    try{
                        var result = SteemImageUpload.uploadImage(AccountName(username),key,file,filePath )

                        return result
                    } catch (ex:Exception){
                        runOnUiThread {
                            Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
                        }
                    }
                    return null
                }

                override fun onPostExecute(result: String?) {


                    //check if the reult is not null before adding it to the database
                    if(result != null){
                        var db = ImageUploadedUrls(applicationContext)
                        var ins = db.Insert(result!!)
                        writePost?.addtexturl(result,file.name)
                        writePost?.progress(View.GONE)
                    }

                    super.onPostExecute(result)
                }
            }
            someTask().execute()


        }

        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

        })
        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

        //imageView.setImageURI(picUri)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.post, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.post_add_image -> {
                imageBrowse()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Get Path of selected image
    private fun getPath(contentUri: Uri?): String {
        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(applicationContext, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }


    fun mysetup(){


        setupViewPager(pager)
        //tabLayout = findViewById(R.id.tabs)
        tabs?.setupWithViewPager(pager)

        setTabViewItems()


    }



    /*fun beforerequesting(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(thisActivity,
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                            Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(thisActivity,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

    }*/


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                //pass on the permissions to easy permissions
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
            }
        }
    }


    /**
     * Called when easy permissions has got all the permissions
     * Then we check for our usecase and either proceed for request again
     */
    @AfterPermissionGranted(RC_CAMERA_STORAGE_PERMS)
    private fun methodRequiresTwoPermission() {
        val perms = ImagePickersWithHelpers.getCameraPermissions()
        if (EasyPermissions.hasPermissions(this, *perms)) {
            ImagePickersWithHelpers.dispatchTakePictureIntent(applicationContext, this,this@Post)
        } else {
            // Do not have permissions, request them now
            ImagePickersWithHelpers.getCameraPermission(applicationContext,this,this@Post)
            return
        }
    }




    private fun setTabViewItems() {
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_chat_white);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_question_answer_white);
        //tabLayout?.getTabAt(2)!!.setIcon(R.drawable.ic_person_pin_white_48dp)
        // tabLayout?.getTabAt(3)!!.setIcon(R.drawable.ic_notifications_none_white_24dp)
    }

    private fun makeFragmentName(viewPagerId: Int?, index: Int): String {
        return "android:switcher:$viewPagerId:$index"
    }

    private fun setupViewPager(viewPager: ViewPager?) {

        val vid = viewPager?.id

        val fragmentManager = supportFragmentManager

        val fo = fragmentManager.findFragmentByTag(makeFragmentName(vid, 0))
        if (fo != null && fo is WritePost) {
            writePost = fo
        }

        val fi = fragmentManager.findFragmentByTag(makeFragmentName(vid,1))
        if(fi != null && fi is PreviewPost){
            previewPost = fi
        }

        /*val ft = fragmentManager.findFragmentByTag(makeFragmentName(vid, 1))
        if (ft != null && ft is questionslistFragment) {
            questionslistFragmentf = ft as questionslistFragment
        }

        val fth = fragmentManager.findFragmentByTag(makeFragmentName(vid, 2))
        if (fth != null && fth is MyPeopleFragment) {
            myPeopleFragmentf = fth as MyPeopleFragment
        }

        val ff = fragmentManager.findFragmentByTag(makeFragmentName(vid, 3))
        if (ff != null && ff is NotificationsFragment) {
            notificationsFragment = ff as NotificationsFragment
        }*/

        val args = Bundle()
        args.putInt("db",dbid)
        args.putBoolean("isedit",isedit)
        args.putString("category",categoryedit)
        //boolean tokenisrefreshingHoldon = false;
        //args.putBoolean("isrefreshing",tokenisrefreshingHoldon)
        if (writePost == null) {
            writePost = WritePost()
            writePost?.arguments = args
        }
        if( previewPost == null){
            previewPost = PreviewPost()
            previewPost?.arguments = args
        }


        /*if (myChatMessageFragmentf == null) {
            myChatMessageFragmentf = MyChatMessageFragment()
            myChatMessageFragmentf.setArguments(args)
        }


        if (myPeopleFragmentf == null) {
            myPeopleFragmentf = MyPeopleFragment()
            myPeopleFragmentf.setArguments(args)
        }


        if (notificationsFragment == null) {
            notificationsFragment = NotificationsFragment()
            notificationsFragment.setArguments(args)
        }*/


        if (viewPagerAdapteradapter == null) {

            viewPagerAdapteradapter = ViewPagerAdapter(supportFragmentManager)
            //Chat
            viewPagerAdapteradapter?.addFragment(writePost as WritePost, "Type", CentralConstants.FragmentTagFeed)
            viewPagerAdapteradapter?.addFragment(previewPost as PreviewPost,"Preview",CentralConstants.FragmentTagBlog)

            //Questions
            /*viewPagerAdapteradapter.addFragment(questionslistFragmentf, "Discuss", CentralConstantsRepository.FragmentTagQuestions)

            //People
            viewPagerAdapteradapter.addFragment(myPeopleFragmentf, "", CentralConstantsRepository.FragmentTagPeople)

            viewPagerAdapteradapter.addFragment(notificationsFragment, "", CentralConstantsRepository.FragmentTagNotifications)*/

            /*adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");*/
            viewPager?.adapter = viewPagerAdapteradapter
        }

        /*if (frag1 == null) frag1 = viewPagerAdapteradapter.mFragmentList.get(1) as questionslistFragment
        if (frag2 == null) frag2 = viewPagerAdapteradapter.mFragmentList.get(0) as MyChatMessageFragment

        if (fragnotification == null) fragnotification = viewPagerAdapteradapter.mFragmentList.get(3) as NotificationsFragment

        frag2.SetShareText(SharedTextToMe)*/


        /* if (usechatpage) {
             viewPager.setCurrentItem(0, true)
         } else {
             viewPager.setCurrentItem(1, true)
         }*/


    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList = ArrayList<String>()
        private var managers: FragmentManager? = null

        private val mFragmentTags = HashMap<Int, String>()

        init {
            managers = manager
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String, tag: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)

            //managers.beginTransaction().remove(fragment);
            // managers.beginTransaction().add(fragment,tag);
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        /*@Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }*/


    }


}
