package swj.swj.common;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import swj.swj.R;

/**
 * Created by shw on 2015/9/18.
 */
public class ImageLoaderCfg {
    public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.abc)
            .showImageForEmptyUri(R.drawable.abc)
            .showImageOnFail(R.drawable.abc)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)//是否识别图片的方向
            .displayer(new FadeInBitmapDisplayer(500)).build();//图片渐现动画
//	.displayer(new RoundedBitmapDisplayer(20)).build();//图片设置圆角
}
