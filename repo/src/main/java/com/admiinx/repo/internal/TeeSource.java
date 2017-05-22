package com.admiinx.repo.internal;


import okio.*;

import java.io.IOException;

/**
 * A Wrapper Takes a {@linkplain Source} and {@linkplain Sink}.
 * Whenever {@linkplain #read(Buffer, long)} called it would copy the same bytes to sink
 */
public class TeeSource implements Source {

    private final Source source;
    private final BufferedSink copySink;

    public TeeSource(Source source, Sink copySink) {
        this.source = source;
        this.copySink = Okio.buffer(copySink);
    }

    @Override
    public long read(Buffer sink, long byteCount) throws IOException {
        long bytesRead = source.read(sink, byteCount);

        if (bytesRead <= 0) {
            return bytesRead;
        }
        try {
            sink.copyTo(copySink.buffer(), sink.size() - bytesRead, bytesRead);
            copySink.emitCompleteSegments();
        } catch (Exception ignored) {
        }

        return bytesRead;
    }

    @Override
    public Timeout timeout() {
        return source.timeout();
    }

    @Override
    public void close() throws IOException {
        copySink.close();
        source.close();
    }
}
