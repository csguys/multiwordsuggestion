package com.csguys.multiwordsuggestion.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Pawan Bhardwaj on 11/04/18.
 */
public class Utils {

    private Utils() {
    }

    public static int indexOfFirstWord(final String source, final String target){
        int index = -1;
        while (index < source.length()) {
            if (source.indexOf(target) == 0) {
                return 0;
            } else {
                int currentindex = source.indexOf(target, index + 1);
                index = currentindex;
                if (currentindex == -1) {
                    return -1;
                }
                if (!Character.isLetterOrDigit(source.charAt(currentindex - 1))) {
                    return currentindex;
                }
            }
        }
        return index;
    }

    /**
     * remove all not Alphanumeric char from string
     * @param string input string
     * @return filtered string
     */
    public static String filterString(final String string) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char val = string.charAt(i);
            if (Character.isLetterOrDigit(val)) {
                builder.append(val);
            }
        }
        return builder.toString();
    }

    /**
     * find intersection between two list
     * @param listA
     * @param listB
     * @return listA intersection listB
     */
    public static List<String> findIntersection(final List<String> listA, final List<String> listB) {
        int count = 0;
        List<String> resultList = new ArrayList<>();
        List<String> smallList = listA.size() <= listB.size() ? listA :listB;
        List<String> bigList = listA.size() > listB.size() ? listA :listB;
        Map<String, Integer> map = new HashMap<>();
        for (String keys : smallList) {
            map.put(keys, count);
        }
        for (String keys : bigList) {
            if (map.containsKey(keys)) {
                resultList.add(keys);
            }
        }
        return resultList;
    }

    /**
     * set text on text view with background spannable
     * @param textView
     * @param searchKey
     * @param resultKey
     * @param spanColor
     */
    public static void setResultOnView(final TextView textView, final String searchKey, final String resultKey, final int spanColor) {
        Log.e("utils", "keys :" + searchKey);
        if (textView == null || resultKey == null || searchKey == null || searchKey.length() == 0) {
            return;
        }
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(resultKey);
        for (String str : searchKey.split(" ")) {
            final BackgroundColorSpan span = new BackgroundColorSpan(spanColor);
            int startIndex = indexOfFirstWord(resultKey.toLowerCase(), str.toLowerCase());
            if (startIndex != -1) {
                spannableStringBuilder.setSpan(span, startIndex
                        , startIndex + str.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    /**
     * generate Radix search tree from given suggestion lists
     * @param trie
     * @param inputList
     */
    public static void buildSearchTree(final Trie trie, final List<String> inputList) {
        if (trie == null) {
            return;
        }
        if (inputList == null) {
            return;
        }
        for (int i = 0; i < inputList.size(); i++) {
            for (String s : inputList.get(i).split(" ")) {
                trie.insert(filterString(s.toLowerCase()), i);
            }
        }
    }

    /**
     * find search result from Radix tree
     * @param searchKey
     * @param trie
     * @param suggestionList
     * @return
     */
    public static List<String> getSearchSuggestion(final String searchKey, final Trie trie, final List<String> suggestionList) {
        List<String> stringList = new ArrayList<>();
        if (searchKey.equalsIgnoreCase("") || trie == null) {
            return stringList;
        }
        for (Integer index : trie.subStringMatcher(searchKey.toLowerCase())) {
            stringList.add(suggestionList.get(index));
        }
        return stringList;
    }

    /**
     * this method perform the searching in the Trie object tree
     * @param keyword keyword to search
     * @param suggestionList list of all possible outputs
     * @param trie trie object
     * @param queryCache cache to use
     * @return list of suggested keywords
     */
    public static List<String> searchKeyWord(final String keyword,final List<String> suggestionList, final Trie trie ,final Map<String, List<String>> queryCache) {
        boolean flag = false;
        List<String> temp = new ArrayList<>();
        for (String singleWord : keyword.split(" ")) {
            singleWord = filterString(singleWord);
            if (!(singleWord.equalsIgnoreCase(" ") || singleWord.equalsIgnoreCase(""))){
                if (!queryCache.containsKey(singleWord)) {
                    queryCache.put(singleWord, getSearchSuggestion(singleWord,trie, suggestionList));
//                    Log.i(TAG, singleWord + "-->" + queryCache.get(singleWord).size());
                }
                if (!flag) {
                    temp.addAll(queryCache.get(singleWord));
                    flag = true;
                } else {
                    temp = findIntersection(temp, queryCache.get(singleWord));
                }
            }
        }
        Set<String> stringSet = new HashSet<>(temp);
        return new ArrayList<>(stringSet);
    }
}
