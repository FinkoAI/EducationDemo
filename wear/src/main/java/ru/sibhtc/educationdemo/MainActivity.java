package ru.sibhtc.educationdemo;

import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.sibhtc.educationdemo.constants.IntentTypes;
import ru.sibhtc.educationdemo.constants.MessagePaths;
import ru.sibhtc.educationdemo.fragments.ExamWearFragment;
import ru.sibhtc.educationdemo.fragments.InfoFragment;
import ru.sibhtc.educationdemo.fragments.LearningWearFragment;
import ru.sibhtc.educationdemo.fragments.LogicalFragment;
import ru.sibhtc.educationdemo.fragments.ProgressFragment;
import ru.sibhtc.educationdemo.fragments.WaitingFragment;
import ru.sibhtc.educationdemo.helpers.BytesHelper;
import ru.sibhtc.educationdemo.helpers.GlobalHelper;
import ru.sibhtc.educationdemo.mock.LabelsMock;
import ru.sibhtc.educationdemo.models.MessageModel;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks {
    private int selectedLabelId;

    String LOG_TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient apiClient;
    private Spinner spinner;
    private FrameLayout frameLayout;
    private String nodeId;
    private FragmentManager fragmentManager;

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final String MOBILE_PATH = "/mobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();


        Fragment fragment = new WaitingFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentById(R.id.watchDataFrame) != null) {
            fragmentTransaction.replace(R.id.watchDataFrame, fragment, "INFO");
        } else {
            fragmentTransaction.add(R.id.watchDataFrame, fragment, "INFO");
        }
        fragmentTransaction.commit();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initGoogleApiClient();
        GlobalHelper.mainActivity = this;
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type == null)
                type = IntentTypes.Info;

            switch (type) {
                case IntentTypes.Info: {
                    Fragment fragment;
                    Bundle bundle = intent.getExtras();
                    byte[] bytes = null;
                    if (bundle != null)
                        bytes = bundle.getByteArray("infoArray");

                    Bundle fragBundle = new Bundle();
                    if (bundle != null) {
                        fragBundle.putByteArray("info", bytes);
                        fragment = new InfoFragment();
                    } else {
                        //если первый запуск то окно ожидания сигнала метки
                        fragment = new WaitingFragment();
                    }

                    fragment.setArguments(fragBundle);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    if (fragmentManager.findFragmentById(R.id.watchDataFrame) != null) {
                        fragmentTransaction.replace(R.id.watchDataFrame, fragment, "INFO");
                    } else {
                        fragmentTransaction.add(R.id.watchDataFrame, fragment, "INFO");
                    }
                    fragmentTransaction.commit();
                    break;
                }
                case IntentTypes.Exam: {
                    Fragment fragment = new ExamWearFragment();
                    Bundle bundle = intent.getExtras();
                    byte[] bytes = bundle.getByteArray("infoArray");
                    Bundle fragBundle = new Bundle();
                    fragBundle.putByteArray("info", bytes);
                    fragment.setArguments(fragBundle);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (fragmentManager.findFragmentById(R.id.watchDataFrame) != null) {
                        fragmentTransaction.replace(R.id.watchDataFrame, fragment, "EXAM");
                    } else {
                        fragmentTransaction.add(R.id.watchDataFrame, fragment, "EXAM");
                    }
                    fragmentTransaction.commit();
                    break;
                }
                case IntentTypes.Learning: {
                    Fragment fragment = new LearningWearFragment();
                    Bundle bundle = intent.getExtras();
                    byte[] bytes = bundle.getByteArray("infoArray");
                    Bundle fragBundle = new Bundle();
                    fragBundle.putByteArray("info", bytes);
                    fragment.setArguments(fragBundle);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (fragmentManager.findFragmentById(R.id.watchDataFrame) != null) {
                        fragmentTransaction.replace(R.id.watchDataFrame, fragment, "LEARNING");
                    } else {
                        fragmentTransaction.add(R.id.watchDataFrame, fragment, "LEARNING");
                    }
                    fragmentTransaction.commit();
                    break;
                }

            }

        }
    }

    private void initGoogleApiClient() {
        apiClient = getGoogleApiClient(this);
        apiClient.connect();
        retrieveDeviceNode();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(apiClient).await();

                List<Node> nodes = result.getNodes();

                if (nodes.size() > 0)
                    nodeId = nodes.get(0).getId();

                if (nodeId != null && nodeId.equals("cloud") && nodes.size() > 1) {
                    nodeId = nodes.get(1).getId();
                }

                Log.v(LOG_TAG, "Node ID of phone: " + nodeId);
            }
        }).start();
    }

    private void init() {
        spinner = (Spinner) findViewById(R.id.spinner);

        frameLayout = (FrameLayout) findViewById(R.id.watchDataFrame);

        ArrayList<String> data;
        data = new ArrayList<>();

        for (int index = 0; index < LabelsMock.labels.length; index++) {
            data.add(LabelsMock.labels[index].LabelName);
        }

        final ArrayAdapter labelsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, data);
        labelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(labelsAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLabelId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing
            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageModel messageModel = new MessageModel(selectedLabelId, LabelsMock.getCodeById(selectedLabelId), null, LabelsMock.getById(selectedLabelId).IsValued);
                try {
                    //провибрировать на приложенную метку
                    Vibrator vibratorNFCCheck = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibratorNFCCheck.vibrate(100);

                    GlobalHelper.nodeId = nodeId;
                    GlobalHelper.apiClient = apiClient;
                    GlobalHelper.sendMessage(MessagePaths.LABEL_MESSAGE_PATH, BytesHelper.toByteArray(messageModel));
                } catch (IOException e) {
                    //
                }
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        //sendMessage(START_ACTIVITY, "");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apiClient.disconnect();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //метод будет заменять инфу в открытом фрагменте или же сменит фрагмент
    //если сменился режим приложения
    public void changeInformation(byte[] data) {
        if (this.fragmentManager.getFragments().get(0) instanceof WaitingFragment) {

            Bundle fragBundle = new Bundle();
            fragBundle.putByteArray("info", data);
            Fragment fragment = new InfoFragment();
            fragment.setArguments(fragBundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (fragmentManager.findFragmentById(R.id.watchDataFrame) != null) {
                fragmentTransaction.replace(R.id.watchDataFrame, fragment, "INFO");
            } else {
                fragmentTransaction.add(R.id.watchDataFrame, fragment, "INFO");
            }
            fragmentTransaction.commit();
        } else if (this.fragmentManager.getFragments().get(0) instanceof InfoFragment) {
            ((InfoFragment) this.fragmentManager.getFragments().get(0)).changeInformation(data);
        }
    }

}
