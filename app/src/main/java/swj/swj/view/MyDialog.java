package swj.swj.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import swj.swj.R;

/**
 * Created by syb on 2015/9/21.
 */
public class MyDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tv1, tv2;
    private LeaveMyDialogListener listener;


    public interface LeaveMyDialogListener {
        public void onClick(View view);
    }

    public MyDialog(Context context, int activity_dialog) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public MyDialog(Context context, int theme, LeaveMyDialogListener listener) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        tv1 = (TextView) findViewById(R.id.tv_1);
        tv2 = (TextView) findViewById(R.id.tv_2);
        tv1.setText("怕怕");
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }
}
