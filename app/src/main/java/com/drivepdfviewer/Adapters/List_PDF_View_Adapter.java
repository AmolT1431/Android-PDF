package com.drivepdfviewer.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.drivepdfviewer.R;

public class List_PDF_View_Adapter extends RecyclerView.Adapter<List_PDF_View_Adapter.ViewHolder> {

    private Context context;
    private PdfRenderer pdfRenderer;




    public List_PDF_View_Adapter(Context context, PdfRenderer pdfRenderer) {
        this.context = context;
        this.pdfRenderer = pdfRenderer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_page_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap bitmap = getBitmapForPage(position);
        holder.imageView.setImageBitmap(bitmap);


    }

    @Override
    public int getItemCount() {
        return pdfRenderer.getPageCount();
    }

    private Bitmap getBitmapForPage(int pageIndex) {
        PdfRenderer.Page page = pdfRenderer.openPage(pageIndex);
        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
        page.close();
        return bitmap;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pdfPageImageView);

        }
    }
}