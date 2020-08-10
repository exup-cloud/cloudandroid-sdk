package com.follow.order.net.interceptor;


import com.follow.order.utils.LogUtil;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final String mUrl;
    private BufferedSource bufferedSource;
    private long lastTime = 0;
    private long postTime = 500;

    public ProgressResponseBody(String url, ResponseBody responseBody) {
        this.mUrl = url;
        this.responseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
//                long t = System.currentTimeMillis();
//                if (lastTime-t>=postTime){
//                    RxBus.getInstance().post(new DownLoadEvent(mUrl,totalBytesRead,responseBody.contentLength(),bytesRead==-1));
//                }
//                progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                LogUtil.e(totalBytesRead+"__"+contentLength());
                return bytesRead;
            }
        };
    }

}