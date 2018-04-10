package com.example.admin.pintu.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**异步任务基类
 * Created by MFLZ on 2017-03-08.
 */
public abstract class BaseTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected Context context;
    private ProgressDialog pd;

    public BaseTask(Context context) {this.context = context;}

    protected void showProgressDialog(String msg) {
        pd = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();
    }

    protected void diessDialog(){
        if (pd != null && pd.isShowing()) pd.dismiss();
    }

    protected void setDialogMsg(String msg) {
        if (pd != null) pd.setMessage(msg);
    }

    @Override
    protected abstract Result doInBackground(Params... params);

    @Override
    protected final void onPostExecute(Result result) {
        diessDialog();
        onPostResult(result);
    }

    protected  void onPostResult(Result result){}
}
