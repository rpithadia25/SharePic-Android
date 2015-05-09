package edu.sdsu.cs.sharepic.classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.sdsu.cs.sharepic.R;

/**
 * Created by Rakshit Pithadia on 5/7/15.
 */
public class RowViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView textView;

    public RowViewHolder(View view) {
        super(view);
        this.textView = (TextView) view.findViewById(R.id.title);
        this.imageView = (ImageView) view.findViewById(R.id.image);
    }
}
