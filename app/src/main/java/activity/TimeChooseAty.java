
package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.health_community.R;

import java.util.Calendar;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

public class TimeChooseAty extends Activity {
    public static final String TAG = "TimeChooseAty";
    public static final int TIME_RETURN = 1;
    public static final int CANCEL = 0;

    private DatePicker daPicker;
	private Button confirmBt, cancelBt;
	private String date;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.date_select);
		confirmBt =  (Button) findViewById(R.id.date_confirmBt);
        cancelBt = (Button) findViewById(R.id.date_cancelBt);
        daPicker = (DatePicker) findViewById(R.id.date_picker);

		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		int monthOfYear=calendar.get(Calendar.MONTH);
		int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);

		String dd;
		if (dayOfMonth - 10 < 0) {
			dd = "0" + dayOfMonth;
		}
		else {
			dd = Integer.toString(dayOfMonth);
		}
		date = year+"-"+(monthOfYear+1)+"-"+dd;

        daPicker.setDate(year, monthOfYear + 1);
        daPicker.setMode(DPMode.SINGLE);
        daPicker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                TimeChooseAty.this.date = date;
            }
        });
		confirmBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				returnDate();
			}
		});

        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

    private void returnDate(){
        Intent data = new Intent();
        data.putExtra("date", date);
        setResult(TIME_RETURN, data);
        finish();
    }
}
