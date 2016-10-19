package com.codepath.android.booksearch.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.model.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Book> mBooks;
    private listener mListener;

    public BookAdapter() {
        this.mBooks = new ArrayList<>();
    }

    public void setBooks(List<Book> books) {
        mBooks.clear();
        mBooks.addAll(books);
        notifyDataSetChanged();
    }

    public void setListener(listener mListener) {
        this.mListener = mListener;
    }

    public void addBooks(List<Book> books) {
        // int startPosition = getItemCount();
        mBooks.addAll(books);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Book book = mBooks.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        Picasso.with(viewHolder.itemView.getContext()).load(book.getCoverUrl())
                .placeholder(R.drawable.ic_nocover)
                .fit()
                .centerCrop()
                .into(viewHolder.ivCover);

        viewHolder.tvTitle.setText(book.getTitle());
        viewHolder.tvAuthor.setText(book.getAuthor());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onItemClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivCover)
        ImageView ivCover;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvAuthor)
        TextView tvAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface listener {
        void onItemClick(Book book);
    }
}
