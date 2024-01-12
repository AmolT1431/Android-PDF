package com.drivepdfviewer.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.drivepdfviewer.R;

public class PdfPagesAdapter extends BaseAdapter {

    private Context context;
    private PdfRenderer pdfRenderer;
    private Matrix matrix = new Matrix(); // Matrix for transformations
    private float scaleFactor = 1.0f;

    public PdfPagesAdapter(Context context, PdfRenderer pdfRenderer) {
        this.context = context;
        this.pdfRenderer = pdfRenderer;
    }

    @Override
    public int getCount() {
        return pdfRenderer.getPageCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pdf_page_view, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.pdfPageImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bitmap bitmap = getBitmapForPage(position);
        holder.imageView.setImageBitmap(bitmap);

        // Apply transformations to the ImageView
        holder.imageView.setScaleType(ImageView.ScaleType.MATRIX);
        holder.imageView.setImageMatrix(matrix);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    private Bitmap getBitmapForPage(int pageIndex) {
        PdfRenderer.Page page = pdfRenderer.openPage(pageIndex);
        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();
        return bitmap;
    }


}
