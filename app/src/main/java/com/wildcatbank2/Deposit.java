package com.wildcatbank2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Deposit.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Deposit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Deposit extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String currentImageName = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private byte[] currentBitmapData;
    private byte[] currentThumbnailData;

    private OnFragmentInteractionListener mListener;

    public Deposit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Deposit.
     */
    // TODO: Rename and change types and number of parameters
    public static Deposit newInstance(String param1, String param2) {
        Deposit fragment = new Deposit();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_deposit, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onResume() {
        super.onResume();
        // Inflate the layout for this fragment

        Button getCheckImageFrontBtn = (Button)getActivity().findViewById(R.id.button4);
        getCheckImageFrontBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){ getCheckImage(R.id.imageView3);}
        });

        Button getCheckImageBackBtn = (Button)getActivity().findViewById(R.id.button5);
        getCheckImageBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){ getCheckImage(R.id.imageView2);}
        });
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // check if the image was created and is not empty otherwise clean it up

            int imageName = (int)getActivity().getIntent().getExtras().get("image_name");
            ImageView imgView = (ImageView)getActivity().findViewById(imageName);
            // Get the arbitrary dimensions to scale the image to
            int targetW = 1280;
            int targetH = 720;

            try {
                ExifInterface exif = new ExifInterface(currentImageName);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int rotate = 0;
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(currentImageName, bmOptions);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inPurgeable = true;

                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(currentImageName, bmOptions);

                // Calculate inSampleSize
                bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetW, targetH);

                // Decode bitmap with inSampleSize set
                bmOptions.inJustDecodeBounds = false;
                Bitmap bmp = BitmapFactory.decodeFile(currentImageName, bmOptions);

                // Rotate it if we need to
                if (rotate != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    Bitmap rotBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                    bmp = rotBitmap;
                }

                imgView.setImageBitmap(bmp);

                // save this data off so we can push it up to Parse later on
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                currentBitmapData = stream.toByteArray();

                // create a thumbnail size image to display in the list
                int thumbW = 200;
                int thumbH = thumbW * bmp.getHeight()/bmp.getWidth();
                Bitmap thumb = Bitmap.createScaledBitmap(bmp, thumbW, thumbH, false);
                stream = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                currentThumbnailData = stream.toByteArray();

            } catch (Exception error) {
                //Utils.doError(getString(R.string.img_create_error)+ error, PhotoCaptureActivity.this);
            }
        }

    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2; // start out with 2 to keep the images small and memory to a minimum

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public void getCheckImage (int imgViewId) {
        PackageManager pm = this.getActivity().getPackageManager();
        File photoFile = null;
        if (ContextCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            getActivity().getIntent().putExtra("image_name", imgViewId);
            if (takePictureIntent.resolveActivity(pm) != null){
                try {
                    photoFile = getImageFile();
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }catch (IOException ex){
                    Log.e("Wildcat", "Failed to create a file to store your image");
                }
            }
        }
    }
    private File getImageFile() throws IOException {
        // create a filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "ZoVegas";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentImageName = image.getAbsolutePath();
        return image;
    }
}
