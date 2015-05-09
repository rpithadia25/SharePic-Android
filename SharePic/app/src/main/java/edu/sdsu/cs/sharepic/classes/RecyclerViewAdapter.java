package edu.sdsu.cs.sharepic.classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.sdsu.cs.sharepic.R;

/**
 * Created by Rakshit Pithadia on 5/7/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RowViewHolder> {
    Context context;
    ArrayList<RowItems> itemsList;

    public RecyclerViewAdapter(Context context, ArrayList<RowItems> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    @Override
    public int getItemCount() {
        if (itemsList == null) {
            return 0;
        } else {
            return itemsList.size();
        }
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.single_row, null);
        RowViewHolder viewHolder = new RowViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RowViewHolder rowViewHolder, int position) {
        RowItems items = itemsList.get(position);
        rowViewHolder.textView.setText(String.valueOf(items.getTitle()));
        rowViewHolder.imageView.setBackgroundResource(items.getImgIcon());
    }
}
