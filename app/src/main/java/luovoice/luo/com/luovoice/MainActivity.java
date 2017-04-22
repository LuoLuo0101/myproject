package luovoice.luo.com.luovoice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.VoiceRecognitionService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener {
    private SpeechRecognizer speechRecognizer;
    private TextView voiceTxt;
    private EventManager wakeupManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        // 注册监听器
        speechRecognizer.setRecognitionListener(this);

        // 语音唤醒
        wakeupManager = EventManagerFactory.create(MainActivity.this, "wp");
        wakeupManager.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                try {
                    Log.e("自定义标签", "类名==MainActivity" + "方法名==onEvent=====name:" + name + "params:" + params);
                    //解析json文件
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word"); // 唤醒词
                        /*
                         *  这里大家可以根据自己的需求实现唤醒后的功能，这里我们简单打印出唤醒词
                         */
                        Log.e("自定义标签", "类名==MainActivity" + "方法名==onEvent=====:" + word);
                        Toast.makeText(MainActivity.this, word, Toast.LENGTH_SHORT).show();
                        // 开始语音识别
                        startASR();
                    } else if ("wp.exit".equals(name)) {
                        // 唤醒已经停止
                        Log.e("自定义标签", "类名==MainActivity" + "方法名==onEvent=====:" + "唤醒停止");
                    }
                } catch (JSONException e) {
                    throw new AndroidRuntimeException(e);
                }
            }
        });

        // 显示接收到的东西
        voiceTxt = (TextView) findViewById(R.id.voiceTxt);
        // 三个监听
        findViewById(R.id.voice).setOnClickListener(this);
        findViewById(R.id.wakeUp).setOnClickListener(this);
        findViewById(R.id.wakeUpOff).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voice:
                startASR();
                break;
            case R.id.wakeUp:
                HashMap<String, String> params = new HashMap<>();
                params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
                wakeupManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
                Log.e("自定义标签", "类名==MainActivity" + "方法名==onClick=====:" + "唤醒已经开始");
                break;
            case R.id.wakeUpOff:
                wakeupManager.send("wp.stop", null, null, 0, 0);
                break;
        }
    }

    /**
     * 开始识别
     */
    public void startASR() {
        Intent intent = new Intent();
        bindParams(intent);
        speechRecognizer.startListening(intent);
//        speechRecognizer.cancel();
//        speechRecognizer.stopListening();
//        speechRecognizer.destroy();
    }

    /**
     * 设置识别参数
     *
     * @param intent 设置语音识别的一些认证信息
     */
    public void bindParams(Intent intent) {
        // 设置一些提示音有关的常量
        intent.putExtra(ConstantVoice.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start); // 开始说话提示音
        intent.putExtra(ConstantVoice.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);          // 解析语音完成提示音
        intent.putExtra(ConstantVoice.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success); // 解析成功的提示音
        intent.putExtra(ConstantVoice.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);     // 解析错误的提示音
        intent.putExtra(ConstantVoice.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);   // 解析取消的提示音
        // 设置自定义离线语意解析
        intent.putExtra(ConstantVoice.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        // 语言设置
        intent.putExtra(ConstantVoice.EXTRA_LANGUAGE, "cmn-Hans-CN");        // 普通话
//        intent.putExtra(ConstantVoice.EXTRA_LANGUAGE,"sichuan-Hans-CN");  // 四川话
//        intent.putExtra(ConstantVoice.EXTRA_LANGUAGE,"yue-Hans-CN");      // 粤语
//        intent.putExtra(ConstantVoice.EXTRA_LANGUAGE,"en-GB");            // 英语
        // 保存录音，这里可以用不同的文件，记得改文件名，记得定时删除就行了，不要内存爆炸
        intent.putExtra(ConstantVoice.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        // 语意解析
//        intent.putExtra(ConstantVoice.EXTRA_NLU, "");       // 默认
//        intent.putExtra(ConstantVoice.EXTRA_NLU, "disable");// 禁用语义解析
        intent.putExtra(ConstantVoice.EXTRA_NLU, "enable"); // 开启语义解析
        // 去掉关键词
        intent.putExtra(ConstantVoice.EXTRA_KEYWORD, "小王你好"); // 去掉关键词，这个不知道为什么没用

    }

    /**
     * 准备就绪回调
     *
     * @param params
     */
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onReadyForSpeech=====:" + "");
    }

    /**
     * 开始说话回调
     */
    @Override
    public void onBeginningOfSpeech() {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onBeginningOfSpeech=====:" + "");
    }

    /**
     * 音量变化处理
     *
     * @param rmsdB 音量
     */
    @Override
    public void onRmsChanged(float rmsdB) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onRmsChanged=====:" + rmsdB);
    }

    /**
     * 录音数据传出处理
     *
     * @param buffer 这一块的不知道咋办，有可能写入一个文件中，形成一个语音
     */
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onBufferReceived=====:" + "");
    }

    /**
     * 说话结束回调
     */
    @Override
    public void onEndOfSpeech() {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onEndOfSpeech=====:" + "");
    }

    /**
     * 出错回调
     *
     * @param error 错误代码
     */
    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        voiceTxt.setText(String.format(Locale.CHINA, voiceTxt.getText().toString() + "%s", sb.toString()));
    }

    /**
     * 最终结果回调
     *
     * @param results 一些回调的信息
     */
    @Override
    public void onResults(Bundle results) {
        // 获取截取到的词的集合
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest != null) {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====nbest:" + nbest);
        }

        // 获取到不知道干嘛的东西,好像是认证的分数，但是一直为空
        float[] array = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if (array != null) {
            String str = "";
            for (float anArray : array) {
                str += anArray;
            }
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====array:" + str);
        } else {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====array:" + "为空");
        }

        // 获取到Json数据
        String json = results.getString("origin_result");
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====:" + json);

        voiceTxt.setText(json);
    }

    /**
     * 临时结果处理,这里可以截取到一些关键词
     *
     * @param results 这里保存着一些说话的关键词
     */
    @Override
    public void onPartialResults(Bundle results) {
        // 获取截取到的词的集合
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest != null) {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====nbest:" + nbest);
        }

        // 获取到不知道干嘛的东西，好像是认证的分数，但是一直为空
        float[] array = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if (array != null) {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====array:" + array.toString());
        } else {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====array:" + "为空");
        }

        // 获取到Json数据
        String json = results.getString("origin_result");
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====:" + json);
    }

    /**
     * 处理事件回调,为将来的一些事件保留的一些东西
     *
     * @param eventType 事件类型
     * @param params    这个可能和上面回调结果的一样，用同样的key去获取
     */
    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onEvent=====:" + eventType);
    }

}
