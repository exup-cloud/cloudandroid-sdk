package com.bmtc.sdk.contract.uiLogic;

import android.content.Context;


import com.bmtc.sdk.contract.data.Collect;
import com.bmtc.sdk.contract.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/4/8.
 */

public class LogicCollects {

    private static LogicCollects instance = null;

    public interface ICollectsListener {
        void onCollectChange(boolean empty);
    }

    public static LogicCollects getInstance(){
        if (null == instance)
            instance = new LogicCollects();
        return instance;
    }

    private List<ICollectsListener> mListeners = new ArrayList<>();
    
    private Context mContext;
    private List<Collect> collects = new ArrayList<>();

    public void init(Context context) {
        mContext = context;
        load();
    }

    public boolean hasContractTicker() {
        for (int i=0; i<collects.size(); i++) {
            if (!collects.get(i).getName().contains("/")) {
                return true;
            }
        }
        return false;
    }

    public List<Collect> getCollects() {
        return collects;
    }

    public Collect get(String name) {
        for (int i=0; i<collects.size(); i++) {
            if (collects.get(i).getName().equals(name)) {
                return collects.get(i);
            }
        }
        return null;
    }

    public void add(Collect collect) {

        boolean ready = false;
        if (collects.size() == 0) {
            ready = true;
        }

        for (int i=0; i<collects.size(); i++) {
            if (collects.get(i).getName().equals(collect.getName())) {
                return;
            }
        }

        collects.add(collect);
        save();
        if (ready && collects.size() > 0) {
            collectChange(false);
        }
    }

    public void remove(String name) {
        for (int i=0; i<collects.size(); i++) {
            if (collects.get(i).getName().equals(name)) {
                collects.remove(i);
                save();
                break;
            }
        }

        if (collects.size() <= 0) {
            collectChange(true);
        }
        return;
    }

    private void load() {
        String jsonCollects = PreferenceManager.getInstance(mContext).getSharedString(PreferenceManager.PREF_COLLECTS, "");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonCollects);

            if (collects == null) {
                collects = new ArrayList<>();
            }

            JSONArray array_collects = jsonObject.optJSONArray("collects");
            if (array_collects != null) {
                for (int i = 0; i < array_collects.length(); i++) {
                    JSONObject obj = array_collects.getJSONObject(i);
                    if (obj == null) {
                        continue;
                    }

                    Collect item = new Collect();
                    item.fromJson(obj);
                    collects.add(item);
                }
            }

        } catch (JSONException ignored) {
        }

    }

    private void save() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray array_collects = new JSONArray();
            for (int i = 0; i < collects.size(); i++) {
                array_collects.put(collects.get(i).toJson());
            }
            jsonObject.put("collects", array_collects);

        } catch (JSONException ignored) {
        }

        PreferenceManager.getInstance(mContext).putSharedString(PreferenceManager.PREF_COLLECTS, jsonObject.toString());
    }

    //添加到这个listener
    public void registListener(ICollectsListener listener){

        if (listener == null) return;

        int iCount;
        for (iCount = 0; iCount<mListeners.size(); iCount++){
            if(listener.equals(mListeners.get(iCount)))
                break;
        }

        if(iCount >= mListeners.size())
            mListeners.add(listener);
    }


    public void unregistListener(ICollectsListener listener){

        if (listener == null) return;

        int iCount;
        for (iCount = 0; iCount<mListeners.size(); iCount++){
            if(listener.equals(mListeners.get(iCount))){
                mListeners.remove(mListeners.get(iCount));
                return;
            }
        }
    }

    public void collectChange(boolean empty){
        for (int i = 0; i<mListeners.size(); i++){
            if (mListeners.get(i) != null){
                mListeners.get(i).onCollectChange(empty);
            }
        }
    }
}
