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

public class FileModelRecyclerViewAdapter extends RecyclerView.Adapter<FileModelRecyclerViewAdapter.MyViewHolder> {

    private final Context context;
    private final List<FileModel> fileModelList;
    PDFRecyclerViewClickListener onClickListener;
    PDFRecyclerViewLongClickListener onLongClickListener;

    public FileModelRecyclerViewAdapter(Context context, List<FileModel> fileModelList,
                                        PDFRecyclerViewClickListener onClickListener,
                                        PDFRecyclerViewLongClickListener onLongClickListener) {
        this.context = context;
        this.fileModelList = fileModelList;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public FileModelRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_pdf_row, parent, false);
        return new FileModelRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileModelRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.filenameTextView.setText(fileModelList.get(position).getFileName());
        holder.imageView.setImageResource(fileModelList.get(position).getFileIcon());
    }

    @Override
    public int getItemCount() {
        return this.fileModelList.size();
    }

    public interface PDFRecyclerViewClickListener {
        void onClick(View v, int position);
    }

    public interface PDFRecyclerViewLongClickListener {
        void onLongClick(View v, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView filenameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.pdfImageView);
            this.filenameTextView = itemView.findViewById(R.id.pdfTextView);
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
