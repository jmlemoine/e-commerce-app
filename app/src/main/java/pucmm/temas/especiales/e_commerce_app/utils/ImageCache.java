package pucmm.temas.especiales.e_commerce_app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.collection.LruCache;

import java.io.File;
import java.io.IOException;

public class ImageCache {
    private LruCache<String, Bitmap> memoryCache;
    private DiskLruCache diskLruCache;
    private final Object diskCacheLock = new Object();
    private boolean diskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    public ImageCache(Context context) {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        // Initialize disk cache on background thread
        File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);

    }


    public void deleteBitmapFromCache(String key)  {
        // Add to memory cache as before
        if (getBitmapFromCache(key) == null) {
            memoryCache.remove(key);
        }

        // Also add to disk cache
        synchronized (diskCacheLock) {
            try {
                if (diskLruCache != null && diskLruCache.get(key) == null) {
                    memoryCache.remove(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return memoryCache.get(key);
    }

    public void addBitmapToCache(String key, Bitmap bitmap)  {
        // Add to memory cache as before
        if (getBitmapFromCache(key) == null) {
            memoryCache.put(key, bitmap);
        }

        // Also add to disk cache
        synchronized (diskCacheLock) {
            try {
                if (diskLruCache != null && diskLruCache.get(key) == null) {
                    memoryCache.put(key, bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public DiskLruCache.Snapshot getBitmapFromDiskCache(String key) throws IOException {
        synchronized (diskCacheLock) {
            // Wait while disk cache is started from background thread
            while (diskCacheStarting) {
                try {
                    diskCacheLock.wait();
                } catch (InterruptedException e) {
                }
            }
            if (diskLruCache != null) {
                return diskLruCache.get(key);
            }
        }
        return null;
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath = context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (diskCacheLock) {
                File cacheDir = params[0];
                try {
                    diskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                }
                diskCacheStarting = false; // Finished initialization
                diskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }
}
