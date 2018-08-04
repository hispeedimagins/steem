package com.steemapp.lokisveil.steemapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder;
import com.steemapp.lokisveil.steemapp.HelperClasses.GetDynamicAndBlock;
import com.steemapp.lokisveil.steemapp.HelperClasses.TextInputLayoutErrorHandler;
import com.steemapp.lokisveil.steemapp.Interfaces.ArticleActivityInterface;
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface;
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder;
import com.steemapp.lokisveil.steemapp.MyViewHolders.CommentViewHolder;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boot on 3/4/2018.
 */

public class ModalBottomSheetMy  extends BottomSheetDialogFragment {

    //TextView tv;
    CardView cardviewOne;
    CardView cardviewTwo;
    CardView cardviewThree;
    CardView cardviewFour;


    EditText EditTextMainOne;
    EditText EditTextMainTwo;
    EditText EditTextMainThree;
    CheckBox CheckBoxMainOne;

    TextInputLayoutErrorHandler EditTextMainOnehandler;
    TextInputLayoutErrorHandler EditTextMainTwohandler;
    TextInputLayoutErrorHandler EditTextMainThreehandler;

    FeedArticleDataHolder.FeedArticleHolder articleViewHolder = null;
    FeedArticleDataHolder.CommentHolder commentViewHolder = null;


    MyOperationTypes myOperationTypes = null;

    /*EditText editText;
    EditText editTextTitle;
    EditText editTextSummary;
    TextInputLayoutErrorHandler handler;*/


    int questionIdUniversal = 0;
    public boolean dataHasChanged = false;
    //EditText AddAnAnswerAnswer;
    internalDecider decider;


    boolean useAnswerComment = true;
    final int defaultmatcher = 0;
    int answerId = defaultmatcher;
    int questionId = defaultmatcher;
    int IdToPostTo;
    //EditText et;


    //OpenAQuestionClassForShowingData data;
    /*private EditText editTextForMainData;
    private CheckBox checkBoxForClosedQuestion;
    private EditText editTextForReasonForQuestionClosed;*/

    View view = null;
    Activity activity;
    TextView tvv;
    Context context;
    //android.support.v4.widget.ContentLoadingProgressBar progressBar;
    ProgressBar progressBar;
    TextView titleholder;
    GlobalInterface globalInterface = null;
    String username = null;

    String articlepermlink = null;
    String comment = null;
    String tags = null;
    String title = null;
    String parentau = null;
    //Activity activity;
    //BottomSheetBehavior bottomSheetBehavior;

    /*public static ModalBottomSheetMy getModalInstance(int typeToMake){

        ModalBottomSheetMy bottomSheetMy = new ModalBottomSheetMy();
        Bundle b = new Bundle();
        b.putInt(CentralConstantsRepository.deflateValueHolder,typeToMake);
        bottomSheetMy.setArguments(b);
        return bottomSheetMy;
    }*/

    public ModalBottomSheetMy() {
        // Required empty public constructor
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }


        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    /*static BottomSheetDialogFragment newInstance() {
        return new BottomSheetDialogFragment();
    }
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //MiscConstants.Companion.ApplyMyTheme(context);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        super.onCreateView(inflater,container,savedInstanceState);
        //int th = getTheme();
        view = inflater.inflate(R.layout.addaquestionbottom, container, false);


        //view = v;

        setUpCommons(view);
        if(myOperationTypes != null){
            switch (myOperationTypes){
                case comment:
                    setUpComments();
                    break;
                case edit_comment:
                    setUpComments();
                    setUpCommentsEdit();
                    break;
            }
        }
        /*makeCommentRequest();*/
        return view;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface){
        super.onCancel(dialogInterface);
        /*if(dataHasChanged && activity != null && activity instanceof openAQestion){


            ((openAQestion)activity).refreshmini();

        }*/
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface){
        super.onDismiss(dialogInterface);
        /*if(dataHasChanged && activity != null && activity instanceof openAQestion){
            ((openAQestion)activity).refreshmini();

        }*/
    }

    public void setArticleViewHolder(FeedArticleDataHolder.FeedArticleHolder holder){
        this.articleViewHolder = holder;
    }

    public void setCommentViewHolder(FeedArticleDataHolder.CommentHolder holder){
        this.commentViewHolder = holder;
    }
    public void setInterface(GlobalInterface interfaces){
        this.globalInterface = interfaces;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setUsername(String name){
        this.username = name;
    }

    public void setMyOperationTypes(MyOperationTypes type){
        this.myOperationTypes = type;
    }

    public void setEditStuff(String title,String author,String tags,String permlink,String content){
        this.title = title;
        this.parentau = author;
        this.tags = tags;
        this.articlepermlink = permlink;
        this.comment = content;
    }

    /**
     * check of the comment field is not null before signing
     * @return boolean
     */
    public boolean checkforempty(){
        String con = EditTextMainTwo.getText().toString();
        if(con.isEmpty()){
            Toast.makeText(context,"Comment cannot be empty.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void makeCommentRequest(){
        //MakeOperationsMine mine = new MakeOperationsMine();

        //String[] tags = tagsd.split(" ");
        String content = EditTextMainTwo.getText().toString();
        //String tagsd = "holy shit";

        //String content = "the words of the wise are to test this time.";
        if(articleViewHolder != null){
            //String tagsd = articleViewHolder.getTags(); //EditTextMainThree.getText().toString();
            String[] tags = new String[articleViewHolder.getTags().size()];
            tags = articleViewHolder.getTags().toArray(tags); //tagsd.split(" ");
            MakeOperationsMine mine = new MakeOperationsMine();
            List<Operation> ops = mine.createComment(new AccountName(username),new AccountName(articleViewHolder.getAuthor()),new Permlink(articleViewHolder.getPermlink()),content,tags,CheckBoxMainOne.isChecked());
            /*Gson gson = new Gson();
            Gson gsons = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String jsons = gsons.toJson(ops);
            List<Object> obs = new ArrayList<>();
            //String von = MyOperationTypes.comment_options.name().toString();
            obs.add(MyOperationTypes.comment.name().toString());
            obs.add(ops.get(0));

            List<Object> obs2 = new ArrayList<>();
            obs2.add(MyOperationTypes.comment_options.name().toString());
            obs2.add(ops.get(1));
            List<Object> obss = new ArrayList<>();
            obss.add(obs);
            obss.add(obs2);
            String json = gsons.toJson(obss);*/

            //String json = gson.toJson(ops);
            GetDynamicAndBlock block = new GetDynamicAndBlock(context,null,0,ops,"Commented on " +articleViewHolder.getAuthor() , MyOperationTypes.comment,progressBar,globalInterface);
            block.GetDynamicGlobalProperties();

        }
        else if(commentViewHolder != null){
            String[] tags = new String[commentViewHolder.getTags().size()];
            tags = commentViewHolder.getTags().toArray(tags); //tagsd.split(" ");
            MakeOperationsMine mine = new MakeOperationsMine();
            List<Operation> ops = mine.createComment(new AccountName(username),new AccountName(commentViewHolder.getAuthor()),new Permlink(commentViewHolder.getPermlink()),content,tags,CheckBoxMainOne.isChecked());
            /*Gson gson = new Gson();
            String json = gson.toJson(ops);*/
            GetDynamicAndBlock block = new GetDynamicAndBlock(context,null,0,ops,"Comment on " +commentViewHolder.getAuthor() , MyOperationTypes.comment,progressBar,globalInterface);
            block.GetDynamicGlobalProperties();

        }
    }


    public void makeCommentEditRequest(){
        //MakeOperationsMine mine = new MakeOperationsMine();
        String tagsd = tags; //EditTextMainThree.getText().toString();
        if(tagsd == null){
            tagsd = "";
        }
        //String[] tags = tagsd.split(" ");
        String content = EditTextMainTwo.getText().toString();
        //String tagsd = "holy shit";
        String[] tags = tagsd.split(" ");
        //String content = "the words of the wise are to test this time.";
        if(articleViewHolder != null){
            MakeOperationsMine mine = new MakeOperationsMine();
            List<Operation> ops = mine.updateComment(new AccountName(username),new AccountName(parentau),new Permlink(articlepermlink ),new Permlink(articleViewHolder.getPermlink()),content,tags);

            GetDynamicAndBlock block = new GetDynamicAndBlock(context,null,0,ops,"Edited " +articleViewHolder.getAuthor() , MyOperationTypes.edit_comment,progressBar,globalInterface);
            block.GetDynamicGlobalProperties();

        }
        else if(commentViewHolder != null){
            MakeOperationsMine mine = new MakeOperationsMine();
            //List<Operation> ops = mine.updateComment(new AccountName(username),new AccountName(commentViewHolder.getAuthor()),new Permlink(articlepermlink ),new Permlink(commentViewHolder.getPermlink()),content,tags);
            List<Operation> ops = mine.updateComment(new AccountName(username),new AccountName(parentau),new Permlink(articlepermlink ),new Permlink(commentViewHolder.getPermlink()),content,tags);
            /*Gson gson = new Gson();
            String json = gson.toJson(ops);*/
            GetDynamicAndBlock block = new GetDynamicAndBlock(context,null,0,ops,"Edited " +commentViewHolder.getAuthor() , MyOperationTypes.edit_comment,progressBar,globalInterface);
            block.GetDynamicGlobalProperties();

        }
    }

    /*@Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }*/

    private void setUpCommons(View v){
        titleholder = (TextView)v.findViewById(R.id.titleholder);
        tvv = (TextView)v.findViewById(R.id.putErrorForQuestionHere);
        progressBar = v.findViewById(R.id.mprogressbar);
        //progressBar = (android.support.v4.widget.ContentLoadingProgressBar)v.findViewById(R.id.toolbar_progress_bar_flat);
        //progressBar.hide();
        FloatingActionButton dab = (FloatingActionButton)v.findViewById(R.id.addaquestionfabminebc);
        dab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FabIntermediary(view);
            }
        });
        activity = getActivity();
        context = activity.getApplicationContext();

        EditTextMainOne = (EditText) v.findViewById(R.id.TextMainOne);
        EditTextMainTwo = (EditText) v.findViewById(R.id.EditMainTextTwo);
        EditTextMainThree = (EditText) v.findViewById(R.id.EditMainTextThree);

        CheckBoxMainOne = (CheckBox)v.findViewById(R.id.CheckboxMainOne);

        EditTextMainOnehandler = new TextInputLayoutErrorHandler((TextInputLayout) v.findViewById(R.id.TextMainOneTextLayout));
        EditTextMainTwohandler = new TextInputLayoutErrorHandler((TextInputLayout) v.findViewById(R.id.EditMainTextTwoTextLayout));
        EditTextMainThreehandler = new TextInputLayoutErrorHandler((TextInputLayout) v.findViewById(R.id.EditMainTextThreeTextLayout));


        cardviewOne = (CardView)v.findViewById(R.id.cardviewOne);
        cardviewTwo = (CardView)v.findViewById(R.id.cardviewTwo);
        cardviewThree = (CardView)v.findViewById(R.id.cardviewThree);
        cardviewFour = (CardView)v.findViewById(R.id.cardviewFour);
    }

    public void setUpComments(){
        cardviewOne.setVisibility(View.GONE);
        //cardviewTwo.setVisibility(View.GONE);
        cardviewFour.setVisibility(View.GONE);
        //added a different text for comment placeholder
        EditTextMainTwo.setHint("Comment (Make it witty)");
        if(articleViewHolder != null){
            titleholder.setText("Reply to "+articleViewHolder.getAuthor());
        }
        else if(commentViewHolder != null) {
            titleholder.setText("Reply to "+commentViewHolder.getAuthor());
        }

    }


    public void setUpCommentsEdit(){
        cardviewTwo.setVisibility(View.GONE);
        //EditTextMainTwo.setText(Html.fromHtml(this.comment));
        EditTextMainTwo.setText(this.comment);

    }



    private void FabIntermediary(View view){
        if(checkforempty()){
            switch (myOperationTypes){
                case comment:
                    makeCommentRequest();
                    break;
                case edit_comment:
                    makeCommentEditRequest();
            }
        }

        //makeCommentRequest();
        /*switch (decider){
            case addQuestion:
                //this.postTheQuestion();
                break;
            case addAnswer:
                //this.submitTheQuestion();
                break;
            case addComment:

                //this.submitTheComment();
                break;
            case editAll:
                //this.SubmitToTheServer();
            default:
                break;
        }*/
    }

    enum internalDecider{
        addQuestion,
        addAnswer,
        addComment,
        editAll
    }

    /*private void runOnUiThread(Runnable runner){
        runner.run();
    }*/
}
