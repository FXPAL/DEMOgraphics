package com.fxpal.demographics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MusicListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private ArrayList<ArtistData> myDataset;
    private ArrayList<ArtistData>[] currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        currentList = ArtistData.allOrders.get("elderly");
        myDataset = getListForArray(currentList);

        mAdapter = new MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    private ArrayList<ArtistData> getListForArray(ArrayList<ArtistData>[] list) {
        ArrayList<ArtistData> ret = new ArrayList<>();

        for(ArrayList<ArtistData> a : list)
            ret.addAll(a);

        return ret;
    }

    public void itemClicked(View v){
//        myDataset.add(myDataset.remove(0));
//        myDataset.add(myDataset.remove(0));
//        myDataset.add(myDataset.remove(0));
//        myDataset.add(myDataset.remove(0));
//        myDataset.add(myDataset.remove(0));
//
//        mAdapter.notifyItemMoved(0, myDataset.size() - 1);
//        mAdapter.notifyItemMoved(0, myDataset.size() - 1);
//        mAdapter.notifyItemMoved(0, myDataset.size() - 1);
//        mAdapter.notifyItemMoved(0, myDataset.size() - 1);
//        mAdapter.notifyItemMoved(0, myDataset.size() - 1);

        ArrayList<ArtistData>[] newList = (ArrayList<ArtistData>[]) ArtistData.allOrders.values().toArray()[(int) (new Date().getTime() % ArtistData.allOrders.size())];


        myDataset.clear();
        myDataset.addAll(getListForArray(newList));
        updateFromCurrentToNew(newList);


//        mAdapter.notifyDataSetChanged();
    }

    private void updateFromCurrentToNew(ArrayList<ArtistData>[] newList) {
        int subIdx = 0;

        for(int i = 0; i< newList.length; i++){
            if(newList[i] != currentList[i]){
                int currListIdx = i+1;
                int moveFromIdx = subIdx + currentList[i].size();
                while (newList[i] != currentList[currListIdx]){
                    moveFromIdx += currentList[currListIdx].size();
                    currListIdx++;
                }

                for(ArtistData item : newList[i]){
                    mAdapter.notifyItemMoved(moveFromIdx + currentList[currListIdx].size() -1, subIdx);
                }

                ArrayList<ArtistData> tmp, itemToInsert = newList[i];
                currListIdx = i;
                boolean foundItem = false;
                while (!foundItem){
                    foundItem = newList[i] == currentList[currListIdx];
                    tmp = currentList[currListIdx];
                    currentList[currListIdx] = itemToInsert;
                    itemToInsert = tmp;
                    currListIdx++;
                }
            }
            subIdx += newList[i].size();

        }


    }

}
