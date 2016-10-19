package com.codepath.android.booksearch.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.adapter.BookAdapter;
import com.codepath.android.booksearch.api.BookApi;
import com.codepath.android.booksearch.model.Book;
import com.codepath.android.booksearch.model.SearchRequest;
import com.codepath.android.booksearch.model.SearchResult;
import com.codepath.android.booksearch.utils.RetrofitUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListActivity extends AppCompatActivity {
    private SearchRequest mSearchRequest;
    private BookAdapter mBookAdapter;
    private BookApi mBookApi;
    private LinearLayoutManager mLayoutManager;

    private MenuItem miProgress;

    @BindView(R.id.lvBooks)
    RecyclerView lvBooks;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);
        setUpApi();
        setUpViews();
//        fetchBooks();

    }

    private void setUpApi() {
        mSearchRequest = new SearchRequest();
        mBookApi = RetrofitUtils.get().create(BookApi.class);
    }

    private void setUpViews() {
        setSupportActionBar(toolbar);
        mBookAdapter = new BookAdapter();
        mBookAdapter.setListener(new BookAdapter.listener() {
            @Override
            public void onItemClick(Book book) {
                Intent i = new Intent(BookListActivity.this, BookDetailActivity.class);
                i.putExtra("data",book);
                startActivity(i);
            }
        });
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lvBooks.setAdapter(new SlideInBottomAnimationAdapter(mBookAdapter));
        lvBooks.setLayoutManager(mLayoutManager);
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private void fetchBooks() {
        miProgress.setVisible(true);
        mBookApi.search(mSearchRequest.toQueryMay()).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.body() != null) {
                    handleResponse(response.body());
                }
                miProgress.setVisible(false);
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void handleResponse(SearchResult searchResult) {
        mBookAdapter.setBooks(searchResult.getBooks());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_list, menu);
        miProgress = menu.findItem(R.id.miActionProgress);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchRequest.setQuery(query);
                fetchBooks();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search);

        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.WHITE);
        et.setHint("Search...");
        et.setHintTextColor(Color.parseColor("#50FFFFFF"));

        //Can fetchBook in here

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        fetchBooks();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
