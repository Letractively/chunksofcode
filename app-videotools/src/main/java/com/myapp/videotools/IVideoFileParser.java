package com.myapp.videotools;

import java.io.IOException;

public interface IVideoFileParser {

    void parse(VideoFile vidfile) throws IOException;
}
