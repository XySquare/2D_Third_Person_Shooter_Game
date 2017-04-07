package com.xyy.game.ai.Screen;

import android.content.Intent;
import android.net.Uri;

import com.xyy.game.ai.Assets;
import com.xyy.game.component.CircleButton;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 错误报告界面，
 * 当检测的目录中存在错误报告时将跳转至次页面，
 * 提供用户选择“发送错误报告”或“忽略，并删除错误报告”，
 * 选择“发送错误报告”将调用邮件客户端，将错误报告以附件的形式发送，
 * 选择“忽略，并删除错误报告”，将删除错误报告并继续
 * Created by ${XYY} on ${2016/11/8}.
 */
public class CrashReportScreen extends Screen {
    private CircleButton okBt, cancelBt;
    private ArrayList<File> crashReports;
    private int offline = 0;//背景绘线偏移量
    public CrashReportScreen(Game game, ArrayList<File> crashReports) {
        super(game);
        okBt = new CircleButton(1280/2-300,720/2+20,80,0xFFF3318A,Assets.tickIco);
        cancelBt = new CircleButton(1280/2-300,720/2+200,80,0xFF30547C,Assets.crossIco);
        this.crashReports = crashReports;
    }

    @Override
    public void update(float deltaTime) {
        offline = offline + 1;
        if(offline==80)
            offline = 0;

        okBt.update(deltaTime);
        cancelBt.update(deltaTime);

        List<Input.Touch> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            if(cancelBt.isClicked(event)){
                for(int j=0;j<crashReports.size();j++)
                    game.getFileIO().deleteExternalStorage(crashReports.get(j));
                game.setScreen(new LogoScreen(game));
                break;
            }
            else if(okBt.isClicked(event)){

                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                String[] tos = { "computernetworkx@sina.com" };
                ArrayList<Uri> Uris = new ArrayList<>();
                for(int j=0;j<crashReports.size();j++)
                    Uris.add(Uri.fromFile(crashReports.get(j)));
                intent.putExtra(Intent.EXTRA_EMAIL, tos)
                        .putExtra(Intent.EXTRA_TEXT, "It would be better if you can provide us with more detail information.")
                        .putExtra(Intent.EXTRA_SUBJECT, "AI Game Bug Report")
                        .putParcelableArrayListExtra(Intent.EXTRA_STREAM, Uris)
                        .setType("application/octet-stream")
                        .setType("message/rfc882");
                game.newActivity(Intent.createChooser(intent, "Please Choose Email Client"));

                game.setScreen(new LogoScreen(game));
                break;
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        g.fill(0xff202d42);
        for(int i = 0;i<16;i++){
            g.drawLine(i*80+offline,0,i*80+offline,720,0xff2d415f);
        }
        for(int i = 9;i>0;i--){
            g.drawLine(0,i*80-offline,1280,i*80-offline,0xff2d415f);
        }

        g.drawText("检测到您上次可能未正常退出",1280/2-330,720/2-160,0xFFFFFFFF,50);
        g.drawText("建议将错误报告发送给开发者以协助改善软件。",1280/2-480,720/2-100,0xFFFFFFFF,50);
        g.drawText("发送错误报告",1280/2-200,720/2+50,0xFFFFFFFF,50);
        g.drawText("忽略，并删除错误报告",1280/2-200,720/2+230,0xFFFFFFFF,50);
        okBt.present(g);
        cancelBt.present(g);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        okBt.initialize(0);
        cancelBt.initialize(0.1f);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBack() {
        return false;
    }
}
