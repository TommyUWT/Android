package tv.vanriper.fconnect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder>
{
    private final List<AlbumItem> mValues;

    public AlbumsAdapter(List<AlbumItem> items)
    {
        mValues = items;
    }

    @Override
    public AlbumsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album, parent, false);
        return new AlbumsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumsAdapter.ViewHolder holder, int position)
    {
        final AlbumItem albumItem = mValues.get(position);
        holder.mAlbum.setText(albumItem.name);
        holder.mAlbum.setTag(albumItem.id);
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public static class AlbumItem
    {
        final String id;
        final String name;

        public AlbumItem(String id, String name)
        {
            this.id = id;
            this.name = name;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final View mView;
        final Button mAlbum;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mAlbum = (Button) view.findViewById(R.id.album_view);
        }
    }
}
