package tv.vanriper.fconnect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>
{
    private final List<CommentItem> mValues;

    public CommentsAdapter(List<CommentItem> items)
    {
        mValues = items;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comments, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder holder, int position)
    {
        final CommentItem item = mValues.get(position);
        holder.mDate.setText(item.date);
        holder.mText.setText(item.comment);
        holder.mTitle.setText(item.name);
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public static class CommentItem
    {
        final String comment;
        private String date;
        final String name;

        public CommentItem(String comment, String date, String name)
        {
            this.comment = comment;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Utility.formatDate(date));
            stringBuilder.append(" at ");
            stringBuilder.append(Utility.formatTime(date));
            this.date = stringBuilder.toString();
            SimpleDateFormat convertedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            this.name = name;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final View mView;
        final TextView mDate;
        final TextView mText;
        final TextView mTitle;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mDate = (TextView) view.findViewById(R.id.comment_date);
            mText = (TextView) view.findViewById(R.id.comment_text);
            mTitle = (TextView) view.findViewById(R.id.comment_title);
        }
    }
}
