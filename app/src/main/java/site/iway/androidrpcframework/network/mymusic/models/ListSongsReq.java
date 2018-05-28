package site.iway.androidrpcframework.network.mymusic.models;

import com.google.gson.annotations.Expose;

import site.iway.androidrpcframework.network.mymusic.MyMusicReq;

public class ListSongsReq extends MyMusicReq {

    public ListSongsReq() {
        url += "ListSongs";
        responseClass = ListSongsRes.class;
    }

    @Expose
    public String filter;

}
