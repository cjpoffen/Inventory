package app.se329.project2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.se329.project2.R;

public class ListItemView extends LinearLayout{
	
	View root;
	TextView textView;
	TextView subTextView;
	TextView textViewRight;
	ImageView iconImageView;
	
	String attrText;
	
	public ListItemView(Context context) {
		super(context);
		sharedConstructor(context);
	}
	public ListItemView(Context context, String title, String subTitle, int resource) {
		super(context);
		sharedConstructor(context);
	}
	
	public ListItemView(Context context, AttributeSet attrs){
		super(context,attrs);
		sharedConstructor(context);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ListItemView);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.ListItemView_text:
                    String text = a.getString(attr);
                    textView.setText(text);
                    break;
            }
        }
        a.recycle();

    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	private void sharedConstructor(Context context){
		root = LayoutInflater.from(context).inflate(R.layout.view_list_item, this);
        if(root!=null) {
            textView = (TextView) root.findViewById(R.id.list_item_textview);
            subTextView = (TextView) root.findViewById(R.id.list_item_subtextview);
            iconImageView = (ImageView) root.findViewById(R.id.list_item_icon);
            textViewRight = (TextView) root.findViewById(R.id.list_item_text_right);
        }
	}
	
	public void setItemName(String name){
		textView.setText(name);
	}

	public String getItemName() {
		return textView.getText().toString();
	}
	
	public void setItemIcon(Bitmap bitmap){
		if(bitmap == null)return;
		iconImageView.setImageBitmap(bitmap);
		iconImageView.setVisibility(VISIBLE);
	}
	
	public void setItemTextRight(String text)
	{
		textViewRight.setText(text);
		textViewRight.setVisibility(VISIBLE);
	}
	
	public void setItemSubName(String sub){
		subTextView.setVisibility(VISIBLE);
		subTextView.setText(sub);
	}
	
}
