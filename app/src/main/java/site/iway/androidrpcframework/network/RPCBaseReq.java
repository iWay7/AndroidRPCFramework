package site.iway.androidrpcframework.network;

/**
 * Created by iWay on 2018/3/25.
 */

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import site.iway.androidhelpers.RPCReq;
import site.iway.androidrpcframework.BuildConfig;
import site.iway.javahelpers.GsonHelper;
import site.iway.javahelpers.StreamReader;
import site.iway.javahelpers.StringHelper;

import static site.iway.javahelpers.GsonHelper.TYPE_NORMAL_AND_EXPOSE_BASED_FIELDS;
import static site.iway.javahelpers.GsonHelper.TYPE_ONLY_EXPOSE_BASED_FIELDS;

/**
 * Created by iWay on 2018/1/11.
 */

public abstract class RPCBaseReq extends RPCReq {

    protected static final Gson GSON_REQ;
    protected static final Gson GSON_RES;
    protected static final Charset CHARSET;
    protected static final String TAG;

    static {
        GSON_REQ = GsonHelper.create(TYPE_ONLY_EXPOSE_BASED_FIELDS);
        GSON_RES = GsonHelper.create(TYPE_NORMAL_AND_EXPOSE_BASED_FIELDS);
        CHARSET = Charset.forName("utf-8");
        TAG = "RPC";
    }

    public int connectTimeout = 20 * 1000;
    public int readTimeout = 20 * 1000;
    public Class responseClass;
    public Object response;
    public Exception error;

    protected byte[] mOutputData;
    protected RPCCallback mCallback;

    public void start(RPCCallback callback) {
        mCallback = callback;
        start();
    }

    protected String buildQuery() {
        JsonObject jsonObject = (JsonObject) GSON_REQ.toJsonTree(this);
        Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
        List<Entry<String, JsonElement>> items = new ArrayList<>();
        items.addAll(set);
        Collections.sort(items, new Comparator<Entry<String, JsonElement>>() {
            @Override
            public int compare(Entry<String, JsonElement> o1, Entry<String, JsonElement> o2) {
                String o1Key = o1.getKey();
                String o2Key = o2.getKey();
                return o1Key.compareTo(o2Key);
            }
        });
        StringBuilder query = new StringBuilder();
        for (Entry<String, JsonElement> entry : items) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (value instanceof JsonPrimitive) {
                query.append("&");
                String keyEncoded = StringHelper.urlEncode(key);
                query.append(keyEncoded);
                query.append("=");
                String valueString = value.getAsString();
                String valueStringEncoded = StringHelper.urlEncode(valueString);
                query.append(valueStringEncoded);
            }
        }
        if (TextUtils.isEmpty(query)) {
            return "";
        } else {
            return query.substring(1);
        }
    }

    @Override
    protected void onPrepare() throws Exception {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "C: " + url);
        }
    }

    @Override
    protected void onConnect(HttpURLConnection connection) throws Exception {
        connection.setUseCaches(false);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setRequestProperty("Accept-Charset", CHARSET.name());
        connection.setRequestProperty("Accept-Encoding", "identity");
    }

    private OutputStream mOutputStream;

    protected void doOutput(HttpURLConnection connection) throws Exception {
        if (connection.getDoOutput() && mOutputData != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "S: " + new String(mOutputData, CHARSET));
            }
            mOutputStream = connection.getOutputStream();
            mOutputStream.write(mOutputData);
            mOutputStream.flush();
        }
    }

    private InputStream mInputStream;

    protected String transformContentString(String contentString) throws Exception {
        return contentString;
    }

    protected void doInput(HttpURLConnection connection) throws Exception {
        if (connection.getDoInput() && responseClass != null) {
            mInputStream = connection.getInputStream();
            byte[] contentData = StreamReader.readAllBytes(mInputStream);
            String contentString = new String(contentData, CHARSET);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "R: " + contentString);
            }
            contentString = transformContentString(contentString);
            response = GSON_RES.fromJson(contentString, responseClass);
        }
    }

    @Override
    protected void onConnected(HttpURLConnection connection) throws Exception {
        doOutput(connection);
        doInput(connection);
    }

    @Override
    protected void onFinish() {
        // nothing
    }

    @Override
    protected void onFinishUI() {
        if (mCallback != null) {
            mCallback.onRequestOK(this);
        }
    }

    @Override
    protected void onError(Exception e) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "E: " + e);
        }
        error = e;
    }

    @Override
    protected void onErrorUI() {
        if (mCallback != null) {
            mCallback.onRequestER(this);
        }
    }

    @Override
    protected void onFinally() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (Exception e) {
                // nothing
            }
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (Exception e) {
                // nothing
            }
        }
    }

}
