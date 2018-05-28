package site.iway.androidrpcframework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import site.iway.androidrpcframework.network.RPCBaseReq;
import site.iway.androidrpcframework.network.RPCCallback;
import site.iway.androidrpcframework.network.mymusic.models.ListSongsReq;
import site.iway.androidrpcframework.network.mymusic.models.ListSongsRes;

public class MainActivity extends Activity implements RPCCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ListSongsReq listSongsReq = new ListSongsReq();
                listSongsReq.filter = "周杰伦";
                listSongsReq.start(MainActivity.this);
            }
        });
    }

    @Override
    public void onRequestOK(RPCBaseReq req) {
        ListSongsRes listSongsRes = (ListSongsRes) req.response;
        TextView textView = findViewById(R.id.textView);
        textView.setText(Arrays.toString(listSongsRes.fileNames.toArray()));
    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        Toast.makeText(this, req.error.toString(), Toast.LENGTH_SHORT).show();
    }

}
