package com.csguys.multiwordsuggestion.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pawan Bhardwaj on 11/04/18.
 */
public class PrefixSearchAutoCompleteAdapter extends ArrayAdapter<String> {

    private Context context;
    private String searchKey;
    /**
     * store the list of all suggestions
     */
    private List<String> suggestionList;
    /**
     * store result of queried suggestion for given serach key
     */
    private List<String> resultList = new ArrayList<>();

    /**
     * syntax highlight color for matching string
     */
    private int spanColor;

    /**
     * root trie node of search tree
     */
    private Trie trie;

    /**
     * cache of result map with search keys
     */
    final Map<String, List<String>> queryCache = new HashMap<>();

    public PrefixSearchAutoCompleteAdapter(@NonNull Context context, final List<String> suggestionList, int spanColor) {
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
        this.suggestionList = suggestionList;
        this.spanColor = spanColor;
        trie = new Trie();
        Utils.buildSearchTree(trie, suggestionList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        Utils.setResultOnView((TextView) view, searchKey, resultList.get(position), spanColor);
        return view;
    }

    /**
     * generate filter object of resulted suggestions
     */
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
               return filterResults;
            }
            searchKey = constraint.toString();
            List<String> result = Utils.searchKeyWord(constraint.toString().trim(), suggestionList, trie, queryCache);
            resultList = result;
            filterResults.values = result;
            filterResults.count = result.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> result = (List<String>) results.values;
            if (result != null && result.size() >0) {
                clear();
                addAll(result);
                notifyDataSetChanged();
            }
        }
    };
}
