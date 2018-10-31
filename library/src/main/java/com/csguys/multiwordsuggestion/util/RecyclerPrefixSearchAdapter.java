package com.csguys.multiwordsuggestion.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ********** CSGuys ********
 * **********(16/10/17)*******
 */

public class RecyclerPrefixSearchAdapter extends RecyclerView.Adapter<RecyclerPrefixSearchAdapter.Holder> {

    private static final String TAG = RecyclerPrefixSearchAdapter.class.getSimpleName();

    /**
     * store the list resulted suggestions
     */
    private List<String> searchResultList;

    /**
     * search keywords
     */
    private String keyWord;
    private String[] multiWord;

    /**
     * listener interface for selection callback
     */
    private SearchSelectListener listener;

    /**
     * local cache
     */
    private Map<String , List<String>> queryCache = new HashMap<>();

    /**
     * search tree object
     */
    private Trie trie;

    /**
     * store the list of all suggestions
     */
    private List<String> inputList;

    /**
     * syntax highlight color for matching string
     */
    private int spanColor;


    public RecyclerPrefixSearchAdapter(final SearchSelectListener listener, final List<String> inputList, final int spanColor) {
        this.searchResultList = new ArrayList<>();
        this.listener = listener;
        this.inputList = inputList;
        this.spanColor = spanColor;
        trie = new Trie();
        Utils.buildSearchTree(trie, inputList);
        Log.i(TAG, "len :" + searchResultList.size());
    }

    public void setSuggestionListData(List<String> inputList) {
        this.inputList = inputList;
        if (trie != null) {
            trie.resetTree();
        }
        Utils.buildSearchTree(trie, inputList);
    }

    /**
     * this method search keyword in search tree and generate suggested list of
     * result
     * @param keyword
     */
    public void searchKeyWord(final String keyword) {
        if (keyword == null) {
            return;
        }
        if (keyword.trim().length() == 0) {
            searchResultList.clear();
            notifyDataSetChanged();
        }
        searchResultList.clear();
        searchResultList = Utils.searchKeyWord(keyword, inputList, trie, queryCache);
        notifyDataSetChanged();
        this.keyWord = keyword;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        Utils.setResultOnView(holder.textView, keyWord, searchResultList.get(position), spanColor);
    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        public Holder(final View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            if (listener != null) {
                listener.onTagSelected(searchResultList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }

    /**
     * on key selection listener callback
     */
    public interface SearchSelectListener{
        void onTagSelected(String tagName, int position);
    }
}
