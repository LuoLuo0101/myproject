package luovoice.luo.com.luovoice;

/**
 * 有关于语音设置的一些常量
 *
 * Created by Luo on 2017/4/22 0022.
 */
public interface ConstantVoice {
    // 提示音的组合
    String EXTRA_SOUND_START = "sound_start";
    String EXTRA_SOUND_END = "sound_end";
    String EXTRA_SOUND_SUCCESS = "sound_success";
    String EXTRA_SOUND_ERROR = "sound_error";
    String EXTRA_SOUND_CANCEL = "sound_cancel";
    // 外部存储语音
    String EXTRA_OUTFILE = "outfile";
    // 语意解析
    String EXTRA_GRAMMAR = "grammar";
    // 支持的语言
    String EXTRA_LANGUAGE = "language";
    // 语意解析
    String EXTRA_NLU = "nlu";
    String EXTRA_KEYWORD = "keyword";
}
