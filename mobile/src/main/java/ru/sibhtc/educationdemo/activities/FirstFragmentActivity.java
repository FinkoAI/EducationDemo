package ru.sibhtc.educationdemo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import ru.sibhtc.educationdemo.R;
import ru.sibhtc.educationdemo.constants.MessagePaths;
import ru.sibhtc.educationdemo.helpers.BytesHelper;
import ru.sibhtc.educationdemo.models.EventModel;
import ru.sibhtc.educationdemo.models.ProgressObject;
import ru.sibhtc.educationdemo.models.TestSendModel;

/**
 * Created by Антон on 15.09.2015.
 **/
public class FirstFragmentActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {
    private EditText sendMessage;

    private GoogleApiClient apiClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_one);

        init();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        apiClient.connect();
    }

    private void init() {
        //тестовые кнопули
        Button sendButton = (Button) findViewById(R.id.test_btn);
        Button sendObject = (Button) findViewById(R.id.object_btn);
        Button sendProgress = (Button) findViewById(R.id.progress_btn);
        sendMessage = (EditText) findViewById(R.id.editText);
        Button sendExam = (Button) findViewById(R.id.exam_start_btn);
        Button getInfoFromServerButton = (Button) findViewById(R.id.get_info_from_server_btn);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = sendMessage.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    sendMessage(MessagePaths.TEST_MESSAGE_PATH, text.getBytes());
                }
            }
        });

        sendObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestSendModel test = new TestSendModel(1, "Object");
                byte[] data;
                String path;

                try {
                    data = BytesHelper.toByteArray(test);
                    path = MessagePaths.OBJECT_MESSAGE_PATH;
                } catch (Exception ex) {
                    data = ex.getMessage().getBytes();
                    path = MessagePaths.ERROR_MESSAGE_PATH;
                }
                sendMessage(path, data);
            }
        });

        sendProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressObject progress = new ProgressObject("Lorem Ipsum", "Lorem Lorem Lorem Lorem Lorem Lorem Lorem Lorem Lorem Lorem Lorem", 80, 100, "кг");
                byte[] data;
                String path;

                try {
                    data = BytesHelper.toByteArray(progress);
                    path = MessagePaths.PROGRESS_MESSAGE_PATH;
                } catch (Exception ex) {
                    data = ex.getMessage().getBytes();
                    path = MessagePaths.ERROR_MESSAGE_PATH;
                }
                sendMessage(path, data);
            }
        });

        sendExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventModel exam = new EventModel("Иванов", "Вывод на режим");
                byte[] data;
                String path;

                try {
                    data = BytesHelper.toByteArray(exam);
                    path = MessagePaths.EXAM_MESSAGE_PATH;
                } catch (Exception ex) {
                    data = ex.getMessage().getBytes();
                    path = MessagePaths.ERROR_MESSAGE_PATH;
                }
                sendMessage(path, data);
            }
        });

    }

    private void sendMessage(final String path, final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(apiClient).await();
                for (Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            apiClient, node.getId(), path, data).await();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage.setText("");
                    }
                });
            }
        }).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendMessage(MessagePaths.START_ACTIVITY, "".getBytes());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apiClient.disconnect();
    }
}
