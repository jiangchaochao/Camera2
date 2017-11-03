package jiangc.cn.yview.camera2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private String TAG = "Preview";
    private HandlerThread mhandlerThread;  //接收消息在线程中处理耗时操作
    private Handler mhandler;   //用来发消息的handler
    private static Context mcontext;
    CaptureRequest.Builder mCaptureBuilder;
    private TextureView textureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mhandlerThread = new HandlerThread("CAMERA2");//创建一个线程
        mhandlerThread.start();//开启一个线程
        mhandler = new Handler(mhandlerThread.getLooper());
        textureView = new TextureView(this);
        textureView.setSurfaceTextureListener(this);
        setContentView(textureView);
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "获得相机服务");
        /*获得相机管理服务*/
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            /*获得可用的相机列表*/
            String[] CameraList = manager.getCameraIdList();
            for (String s : CameraList) {
                Log.i(TAG, "可用相机：" + s);
            }
            /*CameraCharacteristics 相机属性类,可以获得相机的一些属性,如果相机不支持该功能则返回NULL*/
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(CameraList[0]);
            characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            /*
            * 打开相机
            * openCamera: 三个参数
            * 1：CameraID
            * 2: CameraDevice.StateCallback ID为CameraID的相机状态更新会回调该方法
            * 3：Handler
            * */
            try {
                manager.openCamera(CameraList[0], mCameraDeviceStateCallback, mhandler);
            }catch (Exception e)
            {
                e.printStackTrace();
            }


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.i(TAG, "打开相机成功");
            /*这里是打开相机后做的事情*/
            /*1.获得surfaceTexture 对象 用来预览*/
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);
            try {
                /*这里创建一个请求*/
                mCaptureBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mCaptureBuilder.addTarget(surface);
            try {
                /*创建这个会话用来接收请求的数据*/
                camera.createCaptureSession(Arrays.asList(surface), CameraStateCallback, mhandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            /*关闭相机后做的事*/
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
             /*出错时*/
            Log.e(TAG, "相机出错");
        }
    };


    /*用于接收有关摄像机捕获会话状态的更新的回调对象*/
    CameraCaptureSession.StateCallback CameraStateCallback = new CameraCaptureSession.StateCallback(){
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                session.setRepeatingRequest(mCaptureBuilder.build(), CameraCaptureCallback, mhandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    /**/
    CameraCaptureSession.CaptureCallback CameraCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            /*这里照片已经处理完了，最终结果*/

        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            /*对照片做修改处理什么的在这里做操作，例如美颜*/
        }
    };

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
