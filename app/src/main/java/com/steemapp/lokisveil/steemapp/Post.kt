package com.steemapp.lokisveil.steemapp

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.steemapp.lokisveil.steemapp.Fragments.FeedFragment
import com.steemapp.lokisveil.steemapp.Fragments.MyFeedFragment
import com.steemapp.lokisveil.steemapp.Fragments.PreviewPost
import com.steemapp.lokisveil.steemapp.Fragments.WritePost
import com.steemapp.lokisveil.steemapp.R

import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.content_post.*
import java.util.ArrayList
import java.util.HashMap
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.steemapp.lokisveil.steemapp.Databases.ImageUploadedUrls
import com.steemapp.lokisveil.steemapp.Databases.drafts
import com.steemapp.lokisveil.steemapp.HelperClasses.GetDynamicAndBlock
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.ImageUpload.SteemImageUpload
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import java.io.File


class Post : AppCompatActivity() , GlobalInterface {
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
    val PICK_IMAGE_REQUEST = 1
    var filePath: String? = null
    var dbid : Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(this@Post)
            alertDialogBuilder.setTitle("Post the article?")
            alertDialogBuilder.setMessage("This action is not reversible")
            alertDialogBuilder.setPositiveButton("ok", DialogInterface.OnClickListener{ diin, num ->


                val mine = MakeOperationsMine()
                var tags = writePost?.gettags()?.trim()
                var title = writePost?.gettitle()?.trim()
                var content = writePost?.getedittext()?.trim()
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
                    val ops = mine.createPost(AccountName(username), title,content,tags?.split(" ")?.toTypedArray())

                    val block = GetDynamicAndBlock(applicationContext, null, 0, ops, "posted $title", MyOperationTypes.post, writePost?.progressBar, this@Post)
                    block.GetDynamicGlobalProperties()
                }


            })

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
            //useOtherGuy = intent.extras.getBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,false)
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
        if (ContextCompat.checkSelfPermission(this@Post, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this@Post,
                    arrayOf(READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE)
        }

    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("body",univBody)
    }*/




    private fun imageBrowse() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null){
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {

                if (requestCode == PICK_IMAGE_REQUEST) {
                    val picUri : Uri   =  data.data

                    filePath = getPath(picUri)
                    var file = File(filePath)

                    val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(this@Post)
                    alertDialogBuilder.setTitle("Upload this image?")

                    val inflater = layoutInflater
                    val dialogView : View = inflater.inflate(R.layout.popup_image_upload, null)
                    alertDialogBuilder.setView(dialogView)
                    val imagev = dialogView.findViewById<ImageView>(R.id.image)
                    imagev.setImageURI(picUri)
                    //val edittext = dialogView.findViewById<EditText>(R.id.name)
                    alertDialogBuilder.setPositiveButton("ok", DialogInterface.OnClickListener{ diin, num ->
                        writePost?.progress(View.VISIBLE)
                        //vote.weight = numberPicker.value as Short
                        /*if(edittext.text != null){
                            //val u : Int = edittext.text
                            val i = Intent(this, OpenOtherGuyBlog::class.java)
                            i.putExtra(CentralConstants.OtherGuyNamePasser,edittext.text.toString())
                            this.startActivity(i)
                        }*/
                        class someTask() : AsyncTask<Void, Void, String>() {
                            override fun doInBackground(vararg params: Void?): String? {
                                var result = SteemImageUpload.uploadImage(AccountName(username),key,file,filePath )

                                return result
                            }

                            override fun onPostExecute(result: String?) {
                                var db = ImageUploadedUrls(applicationContext)
                                var ins = db.Insert(result!!)
                                writePost?.addtexturl(result,file.name)
                                writePost?.progress(View.GONE)
                                super.onPostExecute(result)
                            }
                        }
                        someTask().execute()



                    })

                    alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

                    })
                    val alertDialog = alertDialogBuilder.create()

                    alertDialog.show()

                    //imageView.setImageURI(picUri)

                }

            }
        }


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
                // Ignore all other requests.
            }
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
