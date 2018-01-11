package tv.vanriper.fconnect;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 *
 * The Utility class hosts a few static functions used to display pictures,
 * convert Strings, and calculate the number of columns for grid views.
 */
public class Utility
{
    public static int calculateColumns(Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int columns = (int) (width / 100);
        return columns;
    }

    public static void displayPicture(ImageView imageView, String url)
    {
        Transformation transformation = new RoundedTransformationBuilder()
                .oval(false)
                .build();
        Picasso.with(imageView.getContext())
                .load(url)
                .transform(transformation)
                .into(imageView);
    }

    public static String formatDate(String date)
    {
        SimpleDateFormat convertedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        try
        {
            Date datetime = convertedDate.parse(date);
            SimpleDateFormat formattedDate = new SimpleDateFormat("MMMM dd, yyyy");
            return formattedDate.format(datetime);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatTime(String date)
    {
        SimpleDateFormat convertedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        try
        {
            Date datetime = convertedDate.parse(date);
            SimpleDateFormat formattedTime = new SimpleDateFormat("hh:mm a");
            return formattedTime.format(datetime);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
