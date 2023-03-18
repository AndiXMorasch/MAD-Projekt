package de.hsos.findyourdoc.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hsos.findyourdoc.R;

public class DocModelRecyclerViewAdapter extends RecyclerView.Adapter<DocModelRecyclerViewAdapter.MyViewHolder> {
    private final Context context;
    private final List<DocModel> docModelList;
    DocRecyclerViewClickListener onClickListener;
    DocRecyclerViewLongClickListener onLongClickListener;

    public DocModelRecyclerViewAdapter(Context context, List<DocModel> docModelList,
                                       DocRecyclerViewClickListener onClickListener,
                                       DocRecyclerViewLongClickListener onLongClickListener) {
        this.context = context;
        this.docModelList = docModelList;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public DocModelRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout and gives a look into the rows
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.recycler_view_doc_row, parent, false);
        return new DocModelRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocModelRecyclerViewAdapter.MyViewHolder holder, int position) {
        // Assigning values to the created views in the recycler_view_row layout XML based on the position of
        // the recycler view

        holder.docName.setText(docModelList.get(position).getDocName());
        holder.date.setText(docModelList.get(position).getDate());
        holder.imageView.setImageResource(docModelList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        // Number of items the recycler view should display on users screen
        return this.docModelList.size();
    }

    public interface DocRecyclerViewClickListener {
        void onClick(View v, int position);
    }

    public interface DocRecyclerViewLongClickListener {
        void onLongClick(View v, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // Getting the views from the recycler_view_row XML file and assigning
        // them to variables

        ImageView imageView;
        TextView docName;
        TextView date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.docLogoImageView);
            this.docName = itemView.findViewById(R.id.docNameTextView);
            this.date = itemView.findViewById(R.id.dateTextViewDocCreation);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onLongClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}
