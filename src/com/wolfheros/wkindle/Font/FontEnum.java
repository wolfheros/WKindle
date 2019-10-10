package com.wolfheros.wkindle.Font;

import java.io.File;

public enum FontEnum {
    NotoSans_Light{
        public File getFont(){
            return new File("./src/com/wolfheros/wkindle/Font/NotoSans-Light.ttf").getAbsoluteFile();
        }
    },
    NotoSans_Italic{
        @Override
        public File getFont() {
            return new File("./src/com/wolfheros/wkindle/Font/NotoSans-Italic.ttf").getAbsoluteFile();
        }
    };

    public abstract File getFont();
}
