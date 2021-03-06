package ru.sibhtc.educationdemo.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ru.sibhtc.educationdemo.R;
import ru.sibhtc.educationdemo.activities.ExamActivity;
import ru.sibhtc.educationdemo.activities.LearningActivity;
import ru.sibhtc.educationdemo.helpers.GlobalHelper;
import ru.sibhtc.educationdemo.mock.AppMode;

/**
 * Created by Антон on 16.09.2015.
 **/
public class StudentsActivity extends AppCompatActivity implements ActionBar.OnNavigationListener{
    final String LOG_TAG = "myLogs";
    private static final String TAG = "junk";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.application_name);
        bar.setSubtitle(Html.fromHtml("<font color='#ff0000'>" + getString(R.string.application_name_demo) + "</font>"));
        bar.setDisplayHomeAsUpEnabled(true);
    }

    //NEW
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalHelper.CurrentAppMode = AppMode.INFORMATION_SENDER;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.itemLearning:{
                Intent intent =  new Intent(this, LearningActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case R.id.itemExam:{
                Intent intent =  new Intent(this, ExamActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }
}
