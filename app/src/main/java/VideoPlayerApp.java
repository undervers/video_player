import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class VideoPlayerApp extends Application{


    public static final int MAX_CACHE_FILE_COUNT = 25;

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
        builder.diskCacheFileCount(MAX_CACHE_FILE_COUNT);

        ImageLoader.getInstance().init(builder.build());
    }
}
