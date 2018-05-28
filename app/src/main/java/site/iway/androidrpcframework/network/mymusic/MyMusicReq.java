package site.iway.androidrpcframework.network.mymusic;

import java.net.HttpURLConnection;

import site.iway.androidrpcframework.network.RPCBaseReq;

public class MyMusicReq extends RPCBaseReq {

    public MyMusicReq() {
        url = "http://home.iway.site:8888/mm/";
    }

    @Override
    protected void onPrepare() throws Exception {
        super.onPrepare();
        String query = buildQuery();
        if (!query.isEmpty()) {
            mOutputData = query.getBytes(CHARSET);
        }
    }

    @Override
    protected void onConnect(HttpURLConnection connection) throws Exception {
        super.onConnect(connection);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + CHARSET.name());
        connection.setRequestProperty("Content-Length", String.valueOf(mOutputData == null ? 0 : mOutputData.length));
    }

}
