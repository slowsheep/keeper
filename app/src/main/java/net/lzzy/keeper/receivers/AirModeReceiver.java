package net.lzzy.keeper.receivers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * @author lzzy_gxy
 * @date 2017/12/22
 * Description:
 */

public class AirModeReceiver extends BroadcastReceiver {
    private static final String EXTRA_AIR_MODE_STATE="state";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())){
            if (intent.getBooleanExtra(EXTRA_AIR_MODE_STATE, false)) {
                new AlertDialog.Builder(context).setTitle("提示")
                        .setMessage("网络不可用，是否继续？")
                        .setPositiveButton("退出", (dialog, which) ->System.exit(0) )
                        .setNegativeButton("确定", null)
                        .show();
            }
        }
    }
}
