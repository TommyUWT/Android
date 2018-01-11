package tv.vanriper.fconnect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder>
{
    private final List<PhotoItem> mValues;

    public PhotosAdapter(List<PhotoItem> items)
    {
        mValues = items;
    }

    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_photo, parent, false);
        return new PhotosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotosAdapter.ViewHolder holder, int position)
    {
        final PhotosAdapter.PhotoItem photoItem = mValues.get(position);
        Utility.displayPicture(holder.mPhoto, photoItem.url);
        holder.mPhoto.setTag(photoItem.id);
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public static class PhotoItem
    {
        final String date;
        final String id;
        final String url;

        public PhotoItem(String date, String id, String url)
        {
            this.date = date;
            this.id = id;
            this.url = url;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final View mView;
        final ImageView mPhoto;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mPhoto = (ImageView) view.findViewById(R.id.photo_view);
        }
    }
}
