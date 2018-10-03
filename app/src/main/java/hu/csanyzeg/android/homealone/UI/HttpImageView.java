package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import hu.csanyzeg.android.homealone.R;
import hu.csanyzeg.android.homealone.Utils.HttpByteArrayDownloadUtil;

public class HttpImageView extends ImageView {
    public HttpImageView(Context context) {
        super(context);
        setImageResource(R.drawable.imagesensorview);
    }

    public HttpImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.imagesensorview);
    }

    public HttpImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.imagesensorview);
    }

    public void setURL(String url){
        new HttpByteArrayDownloadUtil(){
            @Override
            protected void onPostExecute(Result bytes) {
                super.onPostExecute(bytes);
                if (bytes.errorCode == ErrorCode.OK) {
                    onImageDownloadComplete(bytes);
                }else{
                    onImageDownloadFail();
                }
            }
        }.execute(url);
    }

    public void onImageDownloadComplete(HttpByteArrayDownloadUtil.Result bytes){
        setImageBitmap(BitmapFactory.decodeByteArray(bytes.bytes, 0, bytes.bytes.length));
    }


    public void onImageDownloadFail(){
        setImageResource(R.drawable.imagesensorviewerror);
    }
}
