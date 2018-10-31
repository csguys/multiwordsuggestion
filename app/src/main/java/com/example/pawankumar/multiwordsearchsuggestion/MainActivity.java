package com.example.pawankumar.multiwordsearchsuggestion;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.csguys.multiwordsuggestion.util.PrefixSearchAutoCompleteAdapter;
import com.csguys.multiwordsuggestion.util.RecyclerPrefixSearchAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AutoCompleteTextView");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        list.add("Manger");
        list.add("Account manager");
        list.add("Engineer motor");
        list.add("Sales manager");
        list.add("Sound engineer");
        list.add("Engine repair");
        list.add("Shop fruits");
        list.add("Electronics repair");
        list.add("House cleaning");
        list.add("Teacher");
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSupportActionBar().setTitle("AutoCompleteTextView");
                }
                if (position == 1) {
                    getSupportActionBar().setTitle("RecyclerView");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return position == 0 ? FragmentAutoComplete.newInstance() : FragmentRecyclerView.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FragmentAutoComplete extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private Context context;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public FragmentAutoComplete() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FragmentAutoComplete newInstance() {
            return new FragmentAutoComplete();
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            this.context =context;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_autocomplete_text, container, false);
            init(rootView);
            return rootView;
        }

        private void init(View rootView) {
            AutoCompleteTextView autoCompleteTextView = rootView.findViewById(R.id.autoCompleteTextView2);
            PrefixSearchAutoCompleteAdapter adapter = new PrefixSearchAutoCompleteAdapter(context, list, Color.YELLOW);
            autoCompleteTextView.setAdapter(adapter);
        }
    }


    public static class FragmentRecyclerView extends Fragment implements RecyclerPrefixSearchAdapter.SearchSelectListener{

        private Context context;
        EditText editText;
        RecyclerView recyclerView;
        public FragmentRecyclerView() {
        }

        public static FragmentRecyclerView newInstance() {
            return new FragmentRecyclerView();
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            this.context = context;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
            init(rootView);
            return rootView;
        }

        private void init(View rootView) {
            recyclerView = rootView.findViewById(R.id.recycler);
            final RecyclerPrefixSearchAdapter adapter = new RecyclerPrefixSearchAdapter(this, list, Color.YELLOW);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            editText = rootView.findViewById(R.id.editText);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    recyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    adapter.searchKeyWord(s.toString());
                }
            });
        }

        @Override
        public void onTagSelected(String tagName, int position) {
            editText.setText(tagName);
            recyclerView.setVisibility(View.GONE);
        }
    }
}

