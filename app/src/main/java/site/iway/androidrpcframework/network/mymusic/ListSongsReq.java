package site.iway.androidrpcframework.network.mymusic;

import com.google.gson.annotations.Expose;

public class ListSongsReq extends MyMusicReq {

    public ListSongsReq() {
        url += "ListSongs";
        responseClass = ListSongsRes.class;
    }

    @Expose
    public String filter;

}
