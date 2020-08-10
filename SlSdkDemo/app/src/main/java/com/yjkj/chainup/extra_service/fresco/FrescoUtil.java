package com.yjkj.chainup.extra_service.fresco;

import android.content.Context;
import android.net.Uri;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FrescoUtil {

    private static final String baseDirectoryName = "dk_dection";
    private static final String webpSupport = "image/webp,image/apng,image/*,*/*;q=0.8";

    private static final long maxCacheSize = 100 * 1024 * 1024;

    public static void initialize(Context context) {
        // 通过 OKHttp Client支持 http2.0协议
        /*final OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor(){
            @Override
            public Response intercept(Chain chain) throws IOException {
                // 通过增加accept, 告知服务器我们支持webp格式
                return chain.proceed(chain.request().newBuilder().addHeader("accept", webpSupport).build());
            }
        }).build();

        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, okHttpClient)
                //.setDownsampleEnabled(true)
                .setRequestListeners(listeners)
                .build();*/

        //Fresco.initialize(context);
      //  Fresco.initialize(context);
    }

    public static void setDiskCache(Context context) {
        ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(context);
        imagePipelineConfigBuilder.setMainDiskCacheConfig(DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getExternalCacheDir())//设置磁盘缓存的路径
                .setBaseDirectoryName(baseDirectoryName)//设置磁盘缓存文件夹的名称
                .setMaxCacheSize(maxCacheSize)//设置磁盘缓存的大小
                .build());
    }

    public static void setMemoryCache(Context context) {
        ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(context);
        imagePipelineConfigBuilder.setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
            public MemoryCacheParams get() {
                int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
                int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 5;//取手机内存最大值的五分之一作为可用的最大内存数

                MemoryCacheParams bitmapCacheParams = new MemoryCacheParams( //
                        // 可用最大内存数，以字节为单位
                        MAX_MEMORY_CACHE_SIZE,
                        // 内存中允许的最多图片数量
                        Integer.MAX_VALUE,
                        // 内存中准备清理但是尚未删除的总图片所可用的最大内存数，以字节为单位
                        MAX_MEMORY_CACHE_SIZE,
                        // 内存中准备清除的图片最大数量
                        Integer.MAX_VALUE,
                        // 内存中单图片的最大大小
                        Integer.MAX_VALUE);
                return bitmapCacheParams;
            }
        });
    }

    public static void loadImg(String filePath, SimpleDraweeView imageview) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_FILE_SCHEME)
                .path(filePath)
                .build();
        imageview.setImageURI(uri);
    }

    /**
     * 异步
     *
     * @param context
     * @param picUrl
     * @return
     */
    public static void getBitmap(Context context, String picUrl, DataSubscriber dataSubscriber) {
        Uri uri = Uri.parse(picUrl);
        ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
                .build();
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setImageDecodeOptions(decodeOptions)
                .setAutoRotateEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(dataSubscriber, UiThreadImmediateExecutorService.getInstance());
    }

    private static void download(Context context, String url) {

        ImageRequest request = ImageRequestBuilder.
                newBuilderWithSource(Uri.parse(url))
                .setAutoRotateEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.prefetchToDiskCache(request, context);
    }

    /**
     * 图片拷贝
     *
     * @param imgUrl
     * @param newPath
     * @param fileName
     * @return
     */
    public static boolean copyPicFile(String imgUrl, String newPath, String fileName) {
        FileBinaryResource fileBinaryResource = (FileBinaryResource) Fresco.getImagePipelineFactory()
                .getMainFileCache().getResource(new SimpleCacheKey(imgUrl));
        if (fileBinaryResource == null) {
            return false;
        }
        File oldfile = fileBinaryResource.getFile();
        boolean isok = true;
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldfile); //读入原文件
                if (!new File(newPath).exists()) {
                    new File(newPath).mkdirs();
                }
                String myPath = newPath + File.separator + fileName;
                FileOutputStream fs = new FileOutputStream(myPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
            } else {
                isok = false;
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }
}
