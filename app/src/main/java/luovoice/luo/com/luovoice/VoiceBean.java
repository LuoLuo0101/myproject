package luovoice.luo.com.luovoice;

import java.util.List;

/**
 * 语音识别的Json
 * <p>
 * Created by Luo on 2017/4/22 0022.
 */
public class VoiceBean {
    private ContentBean content;
    private ResultBean result;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ContentBean {
        private String json_res;
        private List<String> item;

        public String getJson_res() {
            return json_res;
        }

        public void setJson_res(String json_res) {
            this.json_res = json_res;
        }

        public List<String> getItem() {
            return item;
        }

        public void setItem(List<String> item) {
            this.item = item;
        }
    }

    public static class ResultBean {
        private long corpus_no;
        private int err_no;
        private int idx;
        private int res_type;
        private String sn;

        public long getCorpus_no() {
            return corpus_no;
        }

        public void setCorpus_no(long corpus_no) {
            this.corpus_no = corpus_no;
        }

        public int getErr_no() {
            return err_no;
        }

        public void setErr_no(int err_no) {
            this.err_no = err_no;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        public int getRes_type() {
            return res_type;
        }

        public void setRes_type(int res_type) {
            this.res_type = res_type;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }
    }
}
